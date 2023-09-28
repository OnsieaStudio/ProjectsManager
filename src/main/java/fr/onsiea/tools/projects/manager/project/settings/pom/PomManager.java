package fr.onsiea.tools.projects.manager.project.settings.pom;

import fr.onsiea.tools.projects.manager.project.ProjectModule;
import fr.onsiea.tools.projects.manager.project.settings.pom.details.IPomDetails;
import fr.onsiea.tools.projects.manager.project.settings.pom.details.PomDetailsBuilder;
import fr.onsiea.tools.utils.function.IIFunction;
import fr.onsiea.tools.utils.function.IOIFunction;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class PomManager
{
	private final IPomDetails[] details;
	private final PomFormatter  formatter;

	public PomManager(IPomDetails[] detailsIn)
	{
		details   = detailsIn;
		formatter = new PomFormatter();
	}

	public final String details()
	{
		return formatter.formatAll(details);
	}


	public interface IChild<R>
	{
		default int level()
		{
			return 0;
		}

		String shortcutOf(String idIn);

		ProjectModule.Builder module();

		default PomDetailsBuilder currentChild()
		{
			return null;
		}

		PomManager.Builder pomManager();

		R parent();

		default PomDetailsBuilder of(String idIn)
		{
			var shortcut = shortcutOf(idIn);
			if (shortcut == null)
			{
				shortcut = pomManager() != null ? pomManager().globalShortcutOf(idIn) : null;
			}

			if (shortcut == null)
			{
				return strictGet(idIn);
			}

			var split = shortcut.split("\\.");

			if (split == null || split.length == 0)
			{
				return strictGet(idIn);
			}

			PomDetailsBuilder current = null;
			int               toStart = 1;
			if (currentChild() != null)
			{
				for (int i = 0; i < split.length; i++)
				{
					var id = split[i];
					if (id.contentEquals(currentChild().id()))
					{
						current = currentChild();
						toStart = i;
						break;
					}
				}
			}
			else
			{
				current = strictGet(split[0]);
			}

			if (current == null)
			{
				return strictGet(idIn);
			}

			for (int i = toStart; i < split.length; i++)
			{
				current = current.childMap().get(split[i]);
				if (current == null)
				{
					return strictGet(idIn);
				}
			}

			return current;
		}

		PomDetailsBuilder strictGet(String idIn);

		default boolean containsChild(String idIn)
		{
			return strictGet(idIn) != null;
		}

		int childCount();

		void put(String idIn, PomDetailsBuilder builderIn);

		default R pomDetailsMultiples(String idIn, IIFunction<PomDetailsBuilder> functionIn, String... detailsIn)
		{
			var id      = idIn;
			var builder = of(id);

			if (builder != null && builder.canHadMultiples())
			{
				IChild<?> previous = builder.previous() != null ? builder.previous() : this;

				int i = 0;
				for (var details : detailsIn)
				{
					id = idIn + "-" + i;
					i++;
					while (containsChild(id))
					{
						id = idIn + "-" + i;

						i++;
					}

					builder = new PomDetailsBuilder(module(), currentChild(), pomManager(), id).name(idIn).details(details).level(previous.level() + 1);
					previous.put(id, builder);
					if (functionIn != null)
					{
						functionIn.execute(builder);
					}
				}
			}
			else
			{
				builder = new PomDetailsBuilder(module(), currentChild(), pomManager(), id).name(idIn).details(detailsIn).level(level() + 1);
				put(id, builder);
				if (functionIn != null)
				{
					functionIn.execute(builder);
				}
			}

			return parent();
		}

		default R pomDetailsMultiples(String idIn, String... detailsIn)
		{
			return pomDetailsMultiples(idIn, null, detailsIn);
		}

		default PomDetailsBuilder pomDetails()
		{
			return pomDetails("<LOCAL_" + childCount() + ">");
		}

		default PomDetailsBuilder pomDetails(String idIn)
		{
			var id      = idIn;
			var builder = of(id);

			if (builder != null)
			{
				if (builder.canHadMultiples())
				{
					if (builder.isEmpty())
					{
						return builder;
					}

					return builder.copy();
				}
				return builder;
			}

			builder = new PomDetailsBuilder(module(), currentChild(), pomManager(), id).name(idIn).level(level() + 1);

			put(id, builder);

			return builder;
		}

		default R pomBlocTagMultiples(String idIn, String... detailsIn)
		{
			return pomDetailsMultiples(idIn, (builderIn) ->
			{
				builderIn.enableBlocTag();
			}, detailsIn);
		}

		default PomDetailsBuilder pomBlocTag()
		{
			return pomBlocTag("<LOCAL_" + childCount() + ">");
		}

		default PomDetailsBuilder pomBlocTag(String idIn)
		{
			return pomDetails(idIn).enableBlocTag();
		}

		default R pomLineTagMultiples(String idIn, String... detailsIn)
		{
			return pomDetailsMultiples(idIn, (builderIn) ->
			{
				builderIn.enableLineTag();
			}, detailsIn);
		}

		default PomDetailsBuilder pomLineTag()
		{
			return pomLineTag("<LOCAL_" + childCount() + ">");
		}

		default PomDetailsBuilder pomLineTag(String idIn)
		{
			return pomDetails(idIn).enableLineTag();
		}
	}

	public final static class Builder implements IChild<Builder>
	{
		private final         Map<String, PomDetailsBuilder> childMap;
		private final         Map<String, String>            shortcuts;
		private final         Map<String, String>            globalShortcuts;
		private final @Getter ProjectModule.Builder          module;
		private               PomManager                     built;

		public Builder(ProjectModule.Builder moduleIn)
		{
			module = moduleIn;

			childMap        = new LinkedHashMap<>();
			shortcuts       = new LinkedHashMap<>();
			globalShortcuts = new LinkedHashMap<>();

			IOIFunction<String, IPomDetails> afterFunction = (detailsAfterIn) -> detailsAfterIn == null || detailsAfterIn.name().contentEquals("project") ? null : System.lineSeparator();

			this.pomDetails("xml").details("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").replaceExisting(true);

			var pomArtifactDetailsGroupFunction = lineSeparatorIfNoEquals("groupId",
					"artifactId",
					"version",
					"packaging",
					"name",
					"url",
					"description",
					"inceptionYear",
					"modules",
					"project");

			// @Formatter:off
			pomBlocTag("project").before("\r\n").startTagContent("""
							project xmlns="http://maven.apache.org/POM/4.0.0"
							xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
							xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"""").replaceExisting(true)
						.pomLineTag("modelVersion").after(System.lineSeparator()).details("4.0.0").replaceExisting(true).previous()
						.pomBlocTag("parent").afterFunction(afterFunction)
							//.pomLineTag("groupId").previous() REMOVED because is UNUSED and to DETECT if defined during build(...)
							//.pomLineTag("artifactId").previous()
							//.pomLineTag("version").previous()
						.previous()
						.pomLineTag("groupId").afterFunction(pomArtifactDetailsGroupFunction).previous()
						.pomLineTag("artifactId").afterFunction(pomArtifactDetailsGroupFunction).previous()
						.pomLineTag("version").afterFunction(pomArtifactDetailsGroupFunction).previous()
						.pomLineTag("packaging").afterFunction(pomArtifactDetailsGroupFunction).previous()
						.pomLineTag("name").afterFunction(pomArtifactDetailsGroupFunction).previous()
						.pomLineTag("url").afterFunction(pomArtifactDetailsGroupFunction).previous()
						.pomLineTag("description").afterFunction(pomArtifactDetailsGroupFunction).previous()
						.pomLineTag("inceptionYear").afterFunction(pomArtifactDetailsGroupFunction).previous()
						.pomBlocTag("licenses").afterFunction(pomArtifactDetailsGroupFunction)
							.pomBlocTag("license").replaceExisting(true).canHadMultiples(true)
								.pomLineTag("name").previous()
								.pomLineTag("url").previous()
								.pomLineTag("distribution").previous()
							.previous()
						.previous()
						.pomBlocTag("organization").afterFunction(afterFunction)
							.pomLineTag("name").previous()
							.pomLineTag("url").previous()
						.previous()
						.pomBlocTag("developers").afterFunction(afterFunction)
							.pomBlocTag("developer").replaceExisting(true).canHadMultiples(true)
								.pomLineTag("name").previous()
								.pomLineTag("id").previous()
								.pomLineTag("email").previous()
								.pomLineTag("timezone").previous()
								.pomLineTag("organization").previous()
								.pomLineTag("organizationUrl").previous()
								.pomBlocTag("roles")
									.pomLineTag("role").canHadMultiples(true).previous()
								.previous()
							.previous()
						.previous()
						.pomBlocTag("contributors").afterFunction(afterFunction)
							.pomBlocTag("contributor").replaceExisting(true).canHadMultiples(true)
								.pomLineTag("name").previous()
								.pomLineTag("id").previous()
								.pomLineTag("email").previous()
								.pomLineTag("timezone").previous()
								.pomLineTag("organization").previous()
								.pomLineTag("organizationUrl").previous()
								.pomBlocTag("roles")
									.pomLineTag("role").canHadMultiples(true).previous()
								.previous()
							.previous()
						.previous()
						.pomBlocTag("issueManagement").afterFunction(afterFunction)
							.pomBlocTag("url").previous()
							.pomBlocTag("system").previous()
						.previous()
						.pomBlocTag("scm").afterFunction(afterFunction)
							.pomBlocTag("url").previous()
							.pomBlocTag("connection").previous()
							.pomBlocTag("developerConnection").previous()
						.previous()
						.pomBlocTag("modules").afterFunction(afterFunction)
							.pomLineTag("module").replaceExisting(true).canHadMultiples(true).previous()
						.previous()
						.pomBlocTag("properties").afterFunction(afterFunction)
							.pomLineTag("maven.compiler.source").details("20").replaceExisting(true).previous()
							.pomLineTag("maven.compiler.target").details("20").replaceExisting(true).previous()
							.pomLineTag("project.build.sourceEncoding").details("UTF-8").replaceExisting(true).previous()
						.previous()
						.pomBlocTag("repositories").afterFunction(afterFunction)
							.pomBlocTag("repository").replaceExisting(true).canHadMultiples(true).previous()
						.previous()
						.pomBlocTag("dependencyManagement").afterFunction(afterFunction)
							.pomBlocTag("dependencies")
								.pomBlocTag("dependency").replaceExisting(true).canHadMultiples(true).previous()
							.previous()
						.previous()
						.pomBlocTag("dependencies").afterFunction(afterFunction)
							.pomBlocTag("dependency").betweenChild(System.lineSeparator()).replaceExisting(true).canHadMultiples(true).previous()
						.previous()
						.pomBlocTag("distributionManagement").afterFunction(afterFunction).replaceExisting(true)
							.pomBlocTag("repository").replaceExisting(true).canHadMultiples(true).previous()
						.previous()
						.pomBlocTag("build").afterFunction(afterFunction).replaceExisting(true)
							.pomBlocTag("plugins").replaceExisting(true)
								.pomBlocTag("plugin").replaceExisting(true).canHadMultiples(true).previous()
							.previous()
						.previous();
			// @Formatter:on

			globalShortcut("parent", "project.parent");
			globalShortcut("groupId", "project.groupId");
			globalShortcut("artifactId", "project.artifactId");
			globalShortcut("version", "project.version");
			globalShortcut("packaging", "project.packaging");
			globalShortcut("name", "project.name");
			globalShortcut("url", "project.url");
			globalShortcut("description", "project.description");
			globalShortcut("inceptionYear", "project.inceptionYear");
			globalShortcut("licenses", "project.licenses");
			globalShortcut("organization", "project.organization");
			globalShortcut("developers", "project.developers");
			globalShortcut("contributors", "project.contributors");
			globalShortcut("issueManagement", "project.issueManagement");
			globalShortcut("scm", "project.scm");
			globalShortcut("modules", "project.modules");
			globalShortcut("packaging", "project.packaging");
			globalShortcut("properties", "project.properties");
			globalShortcut("dependencies", "project.dependencies");
			globalShortcut("dependencyManagement", "project.dependencyManagement");
			globalShortcut("repositories", "project.repositories");
			globalShortcut("distributionManagement", "project.distributionManagement");
			globalShortcut("build", "project.build");
		}

		public void show()
		{
			System.out.println("POM MANAGER SHOW :");
			show("\t", this);
		}

		public void show(String prefixIn, IChild child)
		{
			System.out.println(prefixIn + "- " + (child.currentChild() != null ? child.currentChild().name() : "NO CURRENT CHILD") + "(" + (child.module() != null ? "M" : "") + (child.parent() != null ? "P" : "") + (child.currentChild() != null ? "C" : "") + ")[" + child.level() + "][" + child.childCount() + " childs]");

			if (child instanceof PomManager.Builder)
			{
				System.out.println(prefixIn + "\t_IS POM MANAGER !");
				for (var subChild : ((PomManager.Builder) child).childMap.values())
				{
					show(prefixIn + "\t", subChild);
				}
			}
			else if (child instanceof PomDetailsBuilder)
			{
				for (var subChild : ((PomDetailsBuilder) child).childMap.values())
				{
					show(prefixIn + "\t", subChild);
				}
			}
		}

		public static IOIFunction<String, IPomDetails> lineSeparatorIfNoEquals(String... namesIn)
		{
			return (detailsIn) ->
			{
				if (namesIn == null)
				{
					return null;
				}

				for (var name : namesIn)
				{
					if (name != null && detailsIn.name().contentEquals(name))
					{
						return null;
					}
				}

				return System.lineSeparator();
			};
		}

		public Builder compile(String contentIn)
		{
			PomCompiler.compile(contentIn, this);

			return this;
		}

		public PomDetailsBuilder strictGet(String idIn)
		{
			return childMap.get(idIn);
		}

		/**
		 * Add shortcut to idIn from pathIn<br>
		 * Examples :<br>
		 * parent -> project.parent);<br>
		 * properties -> project.properties);<br>
		 * dependencies -> project.dependencies);<br>
		 * dependency -> project.dependencies.dependency);<br>
		 * dependency -> project.dependencies.dependency);<br>
		 *
		 * @return this builder current instance
		 */
		public Builder shortcut(String idIn, String pathIn)
		{
			shortcuts.put(idIn, pathIn);

			return this;
		}

		public Builder globalShortcut(String idIn, String pathIn)
		{
			globalShortcuts.put(idIn, pathIn);

			return this;
		}

		public String shortcutOf(String idIn)
		{
			return shortcuts.get(idIn);
		}

		public String globalShortcutOf(String idIn)
		{
			return globalShortcuts.get(idIn);
		}

		@Override
		public void put(String idIn, PomDetailsBuilder builderIn)
		{
			childMap.put(idIn, builderIn);
		}

		@Override
		public int childCount()
		{
			return childMap.size();
		}

		@Override
		public Builder pomManager()
		{
			return this;
		}

		@Override
		public Builder parent()
		{
			return this;
		}

		public PomManager build()
		{
			if (built != null)
			{
				return built;
			}

			var parentBuilder = of("parent");

			if (module.currentParent() != null && parentBuilder.childMap().isEmpty()) // -> if is already defined
			{
				parentBuilder.pomLineTag("groupId").details(module.parent().groupId());
				parentBuilder.pomLineTag("artifactId").details(module.parent().artifactId());
				parentBuilder.pomLineTag("version").details(module.parent().version());
			}

			pomLineTag("groupId").details(module.groupId());
			pomLineTag("artifactId").details(module.artifactId());
			pomLineTag("version").details(module.version());
			if (!module.name().contentEquals(module.artifactId()))
			{
				pomLineTag("name").details(module.name());
			}
			pomLineTag("inceptionYear").details(new SimpleDateFormat("yyyy").format(new Date()));

			if (module.modulesCount() > 0)
			{
				pomLineTag("packaging").details("pom");
				var modulesBlocTag = pomBlocTag("modules");
				for (var subModule : module.modules())
				{
					modulesBlocTag.pomLineTag("module").details(subModule.name());
				}
			}

			if (module.dependenciesCount() > 0)
			{
				var modulesBlocTag = pomBlocTag("dependencies");
				for (var dependencies : module.dependencies())
				{
					modulesBlocTag.pomBlocTag("dependency")
							.pomLineTag("groupId").details(dependencies.groupId()).previous()
							.pomLineTag("artifactId").details(dependencies.artifactId()).previous()
							.pomLineTag("version").details(dependencies.version());
				}
			}

			final var detailsArray = new IPomDetails[childMap.size()];
			built = new PomManager(detailsArray);

			for (var detail : childMap.values())
			{
				if (detail.index() >= detailsArray.length)
				{
					throw new RuntimeException("[ERROR] PomManager.Builder : index of details/tag > detailsArray length !");
				}

				detailsArray[detail.index()] = detail.build();
			}

			return built;
		}
	}
}