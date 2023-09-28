package fr.onsiea.tools.projects.manager.project.settings;

import fr.onsiea.tools.projects.manager.project.ProjectModule;
import fr.onsiea.tools.projects.manager.project.settings.pom.PomManager;
import fr.onsiea.tools.utils.stringbuilder.StringBuilderCache;
import lombok.Getter;
import lombok.Setter;

public record MavenSettings(ProjectModule module, String prefix, String groupId, String artifactId, String version, PomManager pomManager)
{
	@Override
	public String groupId()
	{
		var stringBuilder = StringBuilderCache.use();

		boolean hasAdd = false;
		if (module.parent() != null)
		{
			var groupId = module.parent().groupId();
			if (groupId != null && !groupId.isEmpty() && !groupId.isBlank())
			{
				hasAdd = true;
				stringBuilder.append(groupId);
			}

			var artifactId = module.parent().artifactId();
			if (artifactId != null && !artifactId.isEmpty() && !artifactId.isBlank())
			{
				if (hasAdd)
				{
					stringBuilder.append(".");
				}

				hasAdd = true;
				stringBuilder.append(artifactId);
			}
		}


		if (prefix != null && !prefix.isEmpty() && !prefix.isBlank())
		{
			if (hasAdd)
			{
				stringBuilder.append(".");
			}
			hasAdd = true;

			stringBuilder.append(prefix);
		}

		if (groupId != null && !groupId.isEmpty() && !groupId.isBlank())
		{
			if (hasAdd)
			{
				stringBuilder.append(".");
			}
			hasAdd = true;

			stringBuilder.append(groupId);
		}

		if (!hasAdd)
		{
			stringBuilder.free();

			return null;
		}

		var toString = stringBuilder.toString();
		stringBuilder.free();
		return toString;
	}

	@Getter
	@Setter
	public final static class Builder
	{
		private final String             artifactId;
		private final PomManager.Builder pomManager;
		private       String             prefix;
		private       String             groupId;
		private       String             version;

		public Builder(String artifactIdIn, ProjectModule.Builder moduleIn)
		{
			artifactId = artifactIdIn;
			version    = "1.0-SNAPSHOT";
			pomManager = new PomManager.Builder(moduleIn);
		}

		public String groupId()
		{
			var stringBuilder = StringBuilderCache.use();

			boolean hasAdd = false;
			if (pomManager.module().currentParent() != null)
			{
				var groupId = pomManager.module().parent().groupId();
				if (groupId != null && !groupId.isEmpty() && !groupId.isBlank())
				{
					hasAdd = true;
					stringBuilder.append(groupId);
				}

				var artifactId = pomManager.module().parent().artifactId();
				if (artifactId != null && !artifactId.isEmpty() && !artifactId.isBlank())
				{
					if (hasAdd)
					{
						stringBuilder.append(".");
					}

					hasAdd = true;
					stringBuilder.append(artifactId);
				}
			}


			if (prefix != null && !prefix.isEmpty() && !prefix.isBlank())
			{
				if (hasAdd)
				{
					stringBuilder.append(".");
				}
				hasAdd = true;

				stringBuilder.append(prefix);
			}

			if (groupId != null && !groupId.isEmpty() && !groupId.isBlank())
			{
				if (hasAdd)
				{
					stringBuilder.append(".");
				}
				stringBuilder.append(groupId);
			}

			var toString = stringBuilder.toString();
			stringBuilder.free();
			return toString;
		}

		public MavenSettings build(ProjectModule moduleIn)
		{
			return new MavenSettings(moduleIn, prefix, groupId, artifactId, version, pomManager.build());
		}
	}
}