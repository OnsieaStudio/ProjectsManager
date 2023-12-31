package fr.onsiea.tools.projects.manager.project;

import fr.onsiea.tools.projects.manager.project.settings.MavenSettings;
import fr.onsiea.tools.projects.manager.project.settings.pom.details.PomDetailsBuilder;
import fr.onsiea.tools.project.initializer.project.settings.resources.*;
import fr.onsiea.tools.projects.manager.project.settings.resources.Resource.IResourceFunction;
import fr.onsiea.tools.projects.manager.project.settings.resources.*;
import fr.onsiea.tools.utils.string.StringUtils;
import fr.onsiea.tools.utils.stringbuilder.CachedStringBuilder;
import fr.onsiea.tools.utils.stringbuilder.StringBuilderCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectModule
{
	private final           ProjectModule    parent;
	private final           String           name;
	private final           String           path;
	private final           ProjectModule[]  modules;
	private final           ProjectModule[]  dependencies;
	private final @Delegate ResourcesManager resourcesManager;
	private final @Delegate MavenSettings    mavenSettings;

	public ProjectModule(ProjectModule parentIn, String nameIn, String pathIn, ProjectModule[] modulesIn, ProjectModule[] dependenciesIn, ResourcesManager resourcesManagerIn, MavenSettings.Builder mavenSettingsIn)
	{
		parent           = parentIn;
		name             = nameIn;
		path             = pathIn;
		modules          = modulesIn;
		dependencies     = dependenciesIn;
		resourcesManager = resourcesManagerIn;
		mavenSettings    = mavenSettingsIn.build(this);
	}

	public final int modulesCount()
	{
		return modules.length;
	}

	public final boolean hasModules()
	{
		return modules.length > 0;
	}

	public final ProjectModule[] modules()
	{
		return modules;
	}

	public final int dependenciesCount()
	{
		return dependencies.length;
	}

	public final boolean hasDependencies()
	{
		return dependencies.length > 0;
	}

	public final ProjectModule[] dependencies()
	{
		return dependencies;
	}

	public String firstName()
	{
		if (parent != null)
		{
			return parent.firstName();
		}

		return name;
	}

	public ProjectModule show()
	{
		System.out.println(this);

		return this;
	}

	public String toString()
	{
		var stringBuilder = StringBuilderCache.use();

		var toString = toString("", stringBuilder);

		stringBuilder.free();

		return toString;
	}

	public String toString(String prefixIn, CachedStringBuilder stringBuilderIn)
	{
		stringBuilderIn.append(prefixIn + "- " + name() + " : " + mavenSettings().groupId() + ":" + mavenSettings().artifactId() + ":" + mavenSettings().version() + " (" + filePath() + ")" + (parent() != null ?
				                                                                                                                                                                                        "[" + parent().groupId() +
				                                                                                                                                                                                        ":" + parent().artifactId() +
				                                                                                                                                                                                        ":" + parent().version() +
				                                                                                                                                                                                        "]" :
				                                                                                                                                                                                        "") + "\r\n");
		if (modules().length > 0)
		{
			stringBuilderIn.append(prefixIn + "-> Modules [" + modules().length + "] : \r\n");
			stringBuilderIn.append(prefixIn + "{\r\n");
			for (var module : modules())
			{
				module.toString(prefixIn + "\t", stringBuilderIn);
			}
			stringBuilderIn.append(prefixIn + "}\r\n");
		}
		if (dependencies().length > 0)
		{
			stringBuilderIn.append(prefixIn + "-> Dependencies [" + dependencies().length + "] : \r\n");
			stringBuilderIn.append(prefixIn + "{\r\n");
			for (var dependency : dependencies())
			{
				stringBuilderIn.append(prefixIn + "\t- " + dependency.name() + " : " + dependency.mavenSettings().groupId() + ":" + dependency.mavenSettings().artifactId() + ":" + dependency.mavenSettings().version() + " (" + dependency.filePath() + ")\r\n");
			}
			stringBuilderIn.append(prefixIn + "}\r\n");
		}
		stringBuilderIn.append(prefixIn + "- Pom :\r\n");
		stringBuilderIn.append(prefixIn + "[\r\n");
		stringBuilderIn.append(prefixIn + "\t" + pomManager().details().replaceAll("\n", "\n" + prefixIn + "\t") + "\r\n");
		stringBuilderIn.append(prefixIn + "]\r\n");

		return stringBuilderIn.toString();
	}

	public String filePath()
	{
		var path = "";
		if (parent != null)
		{
			var parentPath = parent.filePath();
			if (parentPath != null)
			{
				path += parentPath;
				if (path.endsWith("\\"))
				{
					path = path.substring(0, path.length() - 1);
				}
			}


			return parentPath + (this.path != null ? (parentPath.endsWith("\\") || path.startsWith("\\") ? "" : "\\") + this.path : "");
		}

		if (this.path != null)
		{
			if (path != null)
			{
			}

			if (this.path.startsWith("\\"))
			{
				path += this.path.substring(1, path.length());
			}
			else
			{
				path += this.path;
			}

			if (path.endsWith("\\"))
			{
				path = path.substring(0, path.length() - 1);
			}
		}

		return path;
	}

	public final Resource resourceOf(EnumResourceType typeIn)
	{
		var resource = resourcesManager.of(typeIn);

		if (resource != null && resource.isScope(EnumResourceScope.GLOBAL, EnumResourceScope.CHILD))
		{
			return resource;
		}

		var current = parent;
		while (current != null)
		{
			resource = current.resourcesManager.of(typeIn);

			if (resource.scope().equals(EnumResourceScope.GLOBAL))
			{
				return resource;
			}

			current = current.parent();
		}

		return null;
	}

	public void runtime()
	{
		for (var resourceType : EnumResourceType.values())
		{
			var resource = resourcesManager.of(resourceType);

			if (resource == null)
			{
				if (parent != null)
				{
					resource = parent.resourceOf(resourceType);
				}
				else
				{
					continue;
				}
			}

			if (resource == null)
			{
				continue;
			}

			var pathArray = resource.make(this);

			if (pathArray != null && pathArray.length > 0)
			{
				loaded(resource, pathArray);
			}
		}

		for (var module : modules)
		{
			module.runtime();
		}
	}

	@Getter
	@Setter
	public final static class Builder
	{
		private final List<ProjectModule.Builder> modules;
		private final List<ProjectModule.Builder> dependencies;
		private final ResourcesManager.Builder    resourcesManager;
		private final MavenSettings.Builder       mavenSettings;
		private       String                      name;
		private       ProjectModule.Builder       parent;
		private       ProjectModule.Builder       previous;
		private       String                      path;
		private       ProjectModule               built;

		public Builder(String artifactIdIn)
		{
			name = StringUtils.firstUpper(artifactIdIn);

			modules      = new ArrayList<>();
			dependencies = new ArrayList<>();

			resourcesManager = new ResourcesManager.Builder();
			mavenSettings    = new MavenSettings.Builder(artifactIdIn, this);
		}

		public int modulesCount()
		{
			return modules.size();
		}

		public Collection<Builder> modules()
		{
			return modules;
		}

		/**
		 * @return current builder instance
		 */
		public Builder modules(String... modulesArtifactsIdsIn)
		{
			if (modulesArtifactsIdsIn == null)
			{
				return this;
			}

			for (var moduleArtifactId : modulesArtifactsIdsIn)
			{
				if (moduleArtifactId == null)
				{
					continue;
				}

				module(moduleArtifactId);
			}

			return this;
		}

		public int dependenciesCount()
		{
			return dependencies.size();
		}

		public Collection<Builder> dependencies()
		{
			return dependencies;
		}


		/**
		 * @return new builder instance
		 */
		public Builder module(String artifactIdIn)
		{
			var builder = new ProjectModule.Builder(artifactIdIn);
			builder.parent(this);

			modules.add(builder);

			return builder;
		}

		/**
		 * @return current builder instance
		 */
		public Builder modules(ProjectModule.Builder... modulesIn)
		{
			if (modulesIn == null)
			{
				return this;
			}

			for (var module : modulesIn)
			{
				if (module == null)
				{
					continue;
				}

				module(module);
			}

			return this;
		}

		/**
		 * @return current builder instance
		 */
		public Builder module(ProjectModule.Builder moduleIn)
		{
			modules.add(moduleIn);
			moduleIn.parent(this);

			return this;
		}

		/**
		 * @return current builder instance
		 */
		public Builder dependencies(String... modulesNamesIn)
		{
			if (modulesNamesIn == null)
			{
				return this;
			}

			for (var module : modulesNamesIn)
			{
				if (module == null)
				{
					continue;
				}

				dependency(module);
			}

			return this;
		}

		/**
		 * @return new builder instance
		 */
		public Builder dependency(String artifactIdIn)
		{
			var builder = new ProjectModule.Builder(artifactIdIn);

			dependencies.add(builder);

			return builder;
		}

		/**
		 * @return current builder instance
		 */
		public Builder dependencies(ProjectModule.Builder... modulesIn)
		{
			if (modulesIn == null)
			{
				return this;
			}

			for (var module : modulesIn)
			{
				if (module == null)
				{
					continue;
				}

				dependency(module);
			}

			return this;
		}

		/**
		 * @return current builder instance
		 */
		public Builder dependency(ProjectModule.Builder moduleIn)
		{
			dependencies.add(moduleIn);

			return this;
		}

		/**
		 * @return current builder instance
		 */
		public Builder dependants(String... modulesNamesIn)
		{
			if (modulesNamesIn == null)
			{
				return this;
			}

			for (var module : modulesNamesIn)
			{
				if (module == null)
				{
					continue;
				}

				dependant(module);
			}

			return this;
		}

		/**
		 * @return new builder instance
		 */
		public Builder dependant(String moduleNameIn)
		{
			ProjectModule.Builder builder;

			if (parent != null)
			{
				builder = parent.module(moduleNameIn);
			}
			else
			{
				builder = new ProjectModule.Builder(moduleNameIn);
			}

			builder.dependency(this).previous(this);

			return builder;
		}

		/**
		 * @return current builder instance
		 */
		public Builder dependants(ProjectModule.Builder... modulesIn)
		{
			if (modulesIn == null)
			{
				return this;
			}

			for (var module : modulesIn)
			{
				if (module == null)
				{
					continue;
				}

				dependant(module);
			}

			return this;
		}

		/**
		 * @return current builder instance
		 */
		public Builder dependant(ProjectModule.Builder moduleIn)
		{
			moduleIn.dependency(this);

			return this;
		}

		public ProjectModule.Builder previous()
		{
			if (previous == null)
			{
				throw new RuntimeException("[ERROR] ProjectModule.Builder : cannot return previous, this builder not has previous ! ");
			}

			return previous;
		}

		public ProjectModule.Builder currentParent()
		{
			return parent;
		}

		public ProjectModule.Builder parent()
		{
			if (parent == null)
			{
				throw new RuntimeException("[ERROR] ProjectModule.Builder : cannot return parent, this builder not has parent ! ");
			}

			return parent;
		}


		public ProjectModule.Builder first()
		{
			var current = parent;
			if (current == null)
			{
				System.err.println("[WARN] ProjectModule.Builder : end module without parent do nothing !");

				return this;
			}

			while (current.parent != null)
			{
				current = current.parent;
			}

			return current;
		}

		public ProjectModule buildOnlyCurrent()
		{
			if (built != null)
			{
				return built;
			}
			if (path == null)
			{
				path = name;
			}

			var modulesArray      = new ProjectModule[modules.size()];
			var dependenciesArray = new ProjectModule[dependencies.size()];

			built = new ProjectModule(parent != null ? parent.buildOnlyCurrent() : null, name, path, modulesArray, dependenciesArray, resourcesManager.build(), mavenSettings);

			int i = 0;
			for (var module : modules)
			{
				modulesArray[i] = module.buildOnlyCurrent();

				i++;
			}

			i = 0;
			for (var dependency : dependencies)
			{
				dependenciesArray[i] = dependency.buildOnlyCurrent();

				i++;
			}

			return built;
		}

		public ProjectModule build()
		{
			if (built != null)
			{
				return built;
			}

			var builder = this;

			while (builder.parent != null)
			{
				builder = builder.parent;
			}

			return builder.buildOnlyCurrent();
		}

		// Delegated

		public Resource.Builder add(EnumResourceType typeIn)
		{
			return resourcesManager.add(this, typeIn);
		}

		public Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn)
		{
			resourcesManager.add(this, typeIn, basePathIn, functionIn);

			return this;
		}

		public Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, boolean moduleConditionIn)
		{
			var builder = resourcesManager.add(this, typeIn, basePathIn, functionIn, moduleConditionIn);

			return this;
		}

		public Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, EnumResourceFilesCollisionAction actionIn)
		{
			var builder = resourcesManager.add(this, typeIn, basePathIn, functionIn, actionIn);

			return this;
		}

		public Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, EnumResourceFilesCollisionAction actionIn, boolean moduleConditionIn)
		{
			var builder = resourcesManager.add(this, typeIn, basePathIn, functionIn, actionIn, moduleConditionIn);

			return this;
		}


		public Builder add(Resource resourceIn)
		{
			resourcesManager.add(resourceIn);

			return this;
		}

		public String artifactId()
		{
			return mavenSettings.artifactId();
		}

		public Builder pomManager()
		{
			mavenSettings.pomManager();

			return this;
		}

		public String prefix()
		{
			return mavenSettings.prefix();
		}

		public String groupId()
		{
			return mavenSettings.groupId();
		}

		public Builder version(String version)
		{
			mavenSettings.version(version);

			return this;
		}

		public String version()
		{
			return mavenSettings.version();
		}

		public Builder prefix(String prefix)
		{
			mavenSettings.prefix(prefix);

			return this;
		}

		public Builder groupId(String groupId)
		{
			mavenSettings.groupId(groupId);

			return this;
		}

		public Builder pomCompile(String contentIn)
		{
			mavenSettings.pomManager().compile(contentIn);

			return this;
		}

		public Builder pomDetailsMultiples(String idIn, String... detailsIn)
		{
			mavenSettings.pomManager().pomDetailsMultiples(idIn, detailsIn);

			return this;
		}

		public PomDetailsBuilder pomDetails()
		{
			return mavenSettings.pomManager().pomDetails();
		}

		public PomDetailsBuilder pomDetails(String idIn)
		{
			return mavenSettings.pomManager().pomDetails(idIn);
		}

		public Builder pomBlocTagMultiples(String idIn, String... detailsIn)
		{
			mavenSettings.pomManager().pomBlocTagMultiples(idIn, detailsIn);

			return this;
		}

		public PomDetailsBuilder pomBlocTag()
		{
			return mavenSettings.pomManager().pomBlocTag();
		}

		public PomDetailsBuilder pomBlocTag(String idIn)
		{
			return mavenSettings.pomManager().pomBlocTag(idIn);
		}

		public Builder pomLineTagMultiples(String idIn, String... detailsIn)
		{
			mavenSettings.pomManager().pomBlocTagMultiples(idIn, detailsIn);

			return this;
		}

		public PomDetailsBuilder pomLineTag()
		{
			return mavenSettings.pomManager().pomLineTag();
		}

		public PomDetailsBuilder pomLineTag(String idIn)
		{
			return mavenSettings.pomManager().pomLineTag(idIn);
		}
	}
}