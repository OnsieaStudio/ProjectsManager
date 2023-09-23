package fr.onsiea.tools.project.initializer.project.settings.resources;

import fr.onsiea.tools.project.initializer.project.ProjectModule;
import fr.onsiea.utils.string.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public record Resource(EnumResourceType type, EnumResourceScope scope, EnumResourceState state, EnumResourceFilesCollisionAction action, EnumResourceStructure structure, String basePath, int flags,
                       fr.onsiea.tools.project.initializer.project.settings.resources.Resource.IPathGetter pathGetter, fr.onsiea.tools.project.initializer.project.settings.resources.Resource.IPathGetter betweenGetter,
                       fr.onsiea.tools.project.initializer.project.settings.resources.Resource.IResourceFunction function)
{
	/**
	 * 0 -> TREE structure (TREE or BOTH structure enabled)
	 * 1 -> FLAT structure (FLAT or BOTH structure enabled)
	 */
	public String[] make(ProjectModule moduleIn)
	{
		if (state.equals(EnumResourceState.DISABLED))
		{
			return null;
		}

		List<String> paths = new ArrayList<>();

		if (structure.equals(EnumResourceStructure.TREE) || structure.equals(EnumResourceStructure.BOTH))
		{
			var basePath = StringUtils.nonNull(pathGetter.path(moduleIn, this, EnumResourceStructure.TREE));
			var between  = StringUtils.nonNull(betweenGetter.path(moduleIn, this, EnumResourceStructure.TREE));

			var path = function.make(moduleIn, this, basePath, between, EnumResourceStructure.TREE);

			if (path != null)
			{
				paths.add(path);
			}
		}

		if (structure.equals(EnumResourceStructure.FLAT) || structure.equals(EnumResourceStructure.BOTH))
		{
			var basePath = StringUtils.nonNull(pathGetter.path(moduleIn, this, EnumResourceStructure.FLAT));
			var between  = StringUtils.nonNull(betweenGetter.path(moduleIn, this, EnumResourceStructure.FLAT));

			var path = function.make(moduleIn, this, basePath, between, EnumResourceStructure.FLAT);

			if (path != null)
			{
				paths.add(path);
			}
		}

		var pathsArray = new String[paths.size()];

		int i = 0;
		for (var path : paths)
		{
			pathsArray[i] = path;

			i++;
		}

		return pathsArray;
	}

	public boolean isScope(EnumResourceScope... scopesIn)
	{
		if (scopesIn == null)
		{
			return false;
		}

		for (var scope : scopesIn)
		{
			if (scope == null)
			{
				continue;
			}

			if (scope.equals(this.scope))
			{
				return true;
			}
		}

		return false;
	}

	public interface IPathGetter
	{
		default String path(ProjectModule moduleIn, Resource resourceIn, EnumResourceStructure structureIn)
		{
			switch (structureIn)
			{
				case FLAT ->
				{
					return flat(moduleIn, resourceIn);
				}
				case TREE ->
				{
					return tree(moduleIn, resourceIn);
				}
			}

			return null;
		}

		String flat(ProjectModule moduleIn, Resource resourceIn);

		default String tree(ProjectModule moduleIn, Resource resourceIn)
		{
			return flat(moduleIn, resourceIn);
		}
	}

	public interface IResourceFunction
	{
		String make(ProjectModule moduleIn, Resource resourceIn, String pathIn, String betweenIn, EnumResourceStructure structureIn);
	}

	@Getter
	@Setter
	public final static class Builder
	{
		private final ProjectModule.Builder            module;
		private final ResourcesManager.Builder         resourcesManager;
		private final EnumResourceType                 type;
		private       EnumResourceScope                scope;
		private       EnumResourceState                state;
		private       EnumResourceFilesCollisionAction action;
		private       EnumResourceStructure            structure;
		private       String                           basePath;
		private       int                              flags;
		private       IPathGetter                      pathGetter;
		private       IPathGetter                      betweenGetter;
		private       IResourceFunction                function;

		public Builder(ProjectModule.Builder moduleIn, ResourcesManager.Builder resourcesManagerIn, EnumResourceType typeIn)
		{
			module           = moduleIn;
			resourcesManager = resourcesManagerIn;
			type             = typeIn;
			scope            = EnumResourceScope.GLOBAL;
			state            = EnumResourceState.ENABLED;
			action           = EnumResourceFilesCollisionAction.ASK;
			structure        = EnumResourceStructure.TREE;
		}

		public Builder disable()
		{
			state = EnumResourceState.DISABLED;

			return this;
		}

		public Builder enable()
		{
			state = EnumResourceState.ENABLED;

			return this;
		}

		public ProjectModule.Builder end()
		{
			return module;
		}

		public ProjectModule.Builder build()
		{
			resourcesManager.add(new Resource(type, scope, state, action, structure, basePath, flags, pathGetter != null ? pathGetter : (moduleIn, resourceIn) -> (resourceIn.basePath != null ? basePath + "\\" : "") + moduleIn.filePath().replace("\\\\",
					resourceIn.betweenGetter.path(moduleIn,
							resourceIn, EnumResourceStructure.TREE)) + "\\", betweenGetter != null ? betweenGetter : new IPathGetter()
			{
				@Override
				public String flat(ProjectModule moduleIn, Resource resourceIn)
				{
					return "_";
				}

				@Override
				public String tree(ProjectModule moduleIn, Resource resourceIn)
				{
					return "\\";
				}
			}, function));

			return module;
		}
	}

	@Getter
	@AllArgsConstructor
	public final static class Loaded
	{
		private final @Delegate Resource resource;
		private final           String[] pathArray;

		public int pathCount()
		{
			return pathArray.length;
		}
	}
}