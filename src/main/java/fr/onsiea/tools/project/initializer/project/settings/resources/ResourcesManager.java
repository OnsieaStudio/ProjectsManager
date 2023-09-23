package fr.onsiea.tools.project.initializer.project.settings.resources;

import fr.onsiea.tools.project.initializer.project.ProjectModule;
import fr.onsiea.tools.project.initializer.project.settings.resources.Resource.IResourceFunction;
import fr.onsiea.tools.project.initializer.project.settings.resources.Resource.Loaded;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResourcesManager
{
	private final Map<EnumResourceType, Resource>        resources;
	private final Map<EnumResourceType, Resource.Loaded> loadeds;

	public ResourcesManager()
	{
		resources = new LinkedHashMap<>();
		loadeds   = new LinkedHashMap<>();
	}

	public int resourceCount()
	{
		return resources.size();
	}

	public Resource.Loaded loadedOf(EnumResourceType typeIn)
	{
		return loadeds.get(typeIn);
	}

	public ResourcesManager loaded(Resource resourceIn, String[] pathArrayIn)
	{
		if (resourceIn == null || pathArrayIn == null)
		{
			throw new RuntimeException("[ERROR] Cannot put loaded from null resourceIn and pathArrayIn !");
		}

		loadeds.put(resourceIn.type(), new Loaded(resourceIn, pathArrayIn));

		return this;
	}

	public ResourcesManager add(Resource resourceIn)
	{
		resources.put(resourceIn.type(), resourceIn);

		return this;
	}

	public Resource of(EnumResourceType typeIn)
	{
		return resources.get(typeIn);
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

		public Resource.Builder make(ProjectModule.Builder moduleIn, EnumResourceType typeIn)
		{
			var builder = new Resource.Builder(moduleIn, this, typeIn);

			resources.add(builder);

			return builder;
		}

		public Resource.Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn)
		{
			var builder = new Resource.Builder(null, this, typeIn).basePath(basePathIn).function(functionIn);

			resources.add(builder);

			return builder;
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
	}
}
