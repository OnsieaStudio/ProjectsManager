package fr.onsiea.tools.projects.manager.project.settings.resources;

import fr.onsiea.tools.projects.manager.project.ProjectModule;
import fr.onsiea.tools.projects.manager.project.settings.resources.Resource.IResourceFunction;
import fr.onsiea.tools.projects.manager.project.settings.resources.Resource.Loaded;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResourcesManager
{
	private final Map<EnumResourceType, Resource>        resourceMap;
	private final Map<EnumResourceType, Resource.Loaded> loadedMap;

	private final @Getter FilesManager filesManager;

	public ResourcesManager()
	{
		resourceMap = new LinkedHashMap<>();
		loadedMap   = new LinkedHashMap<>();

		filesManager = new FilesManager();
	}

	public int resourceCount()
	{
		return resourceMap.size();
	}

	public Resource.Loaded loadedOf(EnumResourceType typeIn)
	{
		return loadedMap.get(typeIn);
	}

	public ResourcesManager loaded(Resource resourceIn, String[] pathArrayIn)
	{
		if (resourceIn == null || pathArrayIn == null)
		{
			throw new RuntimeException("[ERROR] Cannot put loaded from null resourceIn and pathArrayIn !");
		}

		loadedMap.put(resourceIn.type(), new Loaded(resourceIn, pathArrayIn));

		return this;
	}

	public ResourcesManager add(Resource resourceIn)
	{
		resourceMap.put(resourceIn.type(), resourceIn);

		return this;
	}

	public Resource of(EnumResourceType typeIn)
	{
		return resourceMap.get(typeIn);
	}

	public final static class Builder
	{
		private final @Getter ResourcesManager       resourcesManager;
		private final         List<Resource.Builder> resources;

		public Builder()
		{
			resourcesManager = new ResourcesManager();
			resources        = new ArrayList<>();
		}

		public int resourceCount()
		{
			return resources.size();
		}

		public Resource.Builder add(ProjectModule.Builder moduleIn, EnumResourceType typeIn)
		{
			var builder = new Resource.Builder(moduleIn, this, typeIn);

			resources.add(builder);

			return builder;
		}

		public Builder add(ProjectModule.Builder moduleIn, EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn)
		{
			add(moduleIn, typeIn).basePath(basePathIn).function(functionIn);

			return this;
		}

		public Builder add(ProjectModule.Builder moduleIn, EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, boolean moduleConditionIn)
		{
			var builder = add(moduleIn, typeIn);
			builder.basePath(basePathIn).function(functionIn);

			if (moduleConditionIn)
			{
				builder.hasntModulesCondition();
			}

			return this;
		}

		public Builder add(ProjectModule.Builder moduleIn, EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, EnumResourceFilesCollisionAction actionIn)
		{
			var builder = add(moduleIn, typeIn);
			builder.basePath(basePathIn).function(functionIn).action(actionIn);

			return this;
		}

		public Builder add(ProjectModule.Builder moduleIn, EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, EnumResourceFilesCollisionAction actionIn, boolean moduleConditionIn)
		{
			var builder = add(moduleIn, typeIn);
			builder.basePath(basePathIn).function(functionIn).action(actionIn);

			if (moduleConditionIn)
			{
				builder.hasntModulesCondition();
			}

			return this;
		}

		public ResourcesManager.Builder add(Resource resourceIn)
		{
			resourcesManager.add(resourceIn);

			return this;
		}

		public Resource of(EnumResourceType typeIn)
		{
			return resourcesManager.of(typeIn);
		}

		public ResourcesManager build()
		{
			for (var resource : resources)
			{
				resource.build();
			}
			resources.clear();

			return resourcesManager;
		}

		// Deletaged

		public FilesManager filesManager()
		{
			return resourcesManager.filesManager;
		}
	}
}
