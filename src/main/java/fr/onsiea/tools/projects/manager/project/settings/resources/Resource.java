package fr.onsiea.tools.projects.manager.project.settings.resources;

import fr.onsiea.tools.projects.manager.project.ProjectModule;
import fr.onsiea.tools.utils.function.IOIFunction;
import fr.onsiea.tools.utils.string.StringUtils;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public record Resource(EnumResourceType type, EnumResourceScope scope, EnumResourceState state, EnumResourceFilesCollisionAction action, EnumResourceStructure structure, String basePath, int flags,
                       IOIFunction<Boolean, ProjectModule> conditionFunction,
                       Resource.IPathGetter pathGetter, Resource.IPathGetter betweenGetter,
                       Resource.IResourceFunction function, FilesManager filesManager)
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

		if (!conditionFunction.execute(moduleIn))
		{
			return null;
		}

		List<String> paths = new ArrayList<>();

		if (structure.equals(EnumResourceStructure.TREE) || structure.equals(EnumResourceStructure.BOTH))
		{
			var basePath = StringUtils.nonNull(pathGetter.path(moduleIn, this, EnumResourceStructure.TREE));
			var between  = StringUtils.nonNull(betweenGetter.path(moduleIn, this, EnumResourceStructure.TREE));

			var path = function.make(moduleIn, this, basePath, between, EnumResourceStructure.TREE, filesManager);

			if (path != null)
			{
				paths.add(path);
			}
		}

		if (structure.equals(EnumResourceStructure.FLAT) || structure.equals(EnumResourceStructure.BOTH))
		{
			var basePath = StringUtils.nonNull(pathGetter.path(moduleIn, this, EnumResourceStructure.FLAT));
			var between  = StringUtils.nonNull(betweenGetter.path(moduleIn, this, EnumResourceStructure.FLAT));

			var path = function.make(moduleIn, this, basePath, between, EnumResourceStructure.FLAT, filesManager);

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

	// Delegated

	public String append(String toIn, String contentIn)
	{
		return filesManager.append(action, toIn, contentIn);
	}

	public String write(String toIn, String contentIn)
	{
		return filesManager.write(action, toIn, contentIn);
	}

	public String folderJunction(Loaded loadedIn, String toIn)
	{
		return filesManager.folderJunction(action, loadedIn, toIn);
	}

	public String folderJunction(String fromPathFileIn, String toFilePathIn)
	{
		return filesManager.folderJunction(action, fromPathFileIn, toFilePathIn);
	}

	public String link(Loaded loadedIn, String toIn)
	{
		return filesManager.link(action, loadedIn, toIn);
	}

	public String link(String fromPathFileIn, String toFilePathIn)
	{
		return filesManager.link(action, fromPathFileIn, toFilePathIn);
	}

	public String symbolicLink(Loaded loadedIn, String toIn)
	{
		return filesManager.symbolicLink(action, loadedIn, toIn);
	}

	public String symbolicLink(String fromPathFileIn, String toFilePathIn)
	{
		return filesManager.symbolicLink(action, fromPathFileIn, toFilePathIn);
	}

	public String mkdir(String folderPathIn)
	{
		return filesManager.mkdir(action, folderPathIn);
	}

	public void mkdirs(String... foldersPathsIn)
	{
		filesManager.mkdirs(action, foldersPathsIn);
	}

	public String mkdirAllChild(String basePathIn, String... childFoldersPathsIn)
	{
		return filesManager.mkdirAllChild(action, basePathIn, childFoldersPathsIn);
	}

	public boolean canMake(boolean isFileIn, String filePathIn)
	{
		return filesManager.canMake(action, isFileIn, filePathIn);
	}

	public boolean canMake(boolean isFileIn, File fileIn)
	{
		return filesManager.canMake(action, isFileIn, fileIn);
	}

	public boolean canReplace(String filePathIn)
	{
		return filesManager.canReplace(action, new File(filePathIn));
	}

	public boolean canReplace(File fileIn)
	{
		return filesManager.canReplace(action, fileIn);
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
		String make(ProjectModule moduleIn, Resource resourceIn, String pathIn, String betweenIn, EnumResourceStructure structureIn, FilesManager filesManagerIn);
	}

	@Getter
	@Setter
	public final static class Builder
	{
		private final ProjectModule.Builder               module;
		private final ResourcesManager.Builder            resourcesManager;
		private final EnumResourceType                    type;
		private       EnumResourceScope                   scope;
		private       EnumResourceState                   state;
		private       EnumResourceFilesCollisionAction    action;
		private       EnumResourceStructure               structure;
		private       String                              basePath;
		private       int                                 flags;
		private       IOIFunction<Boolean, ProjectModule> conditionFunction;
		private       IPathGetter                         pathGetter;
		private       IPathGetter                         betweenGetter;
		private       IResourceFunction                   function;

		public Builder(ProjectModule.Builder moduleIn, ResourcesManager.Builder resourcesManagerIn, EnumResourceType typeIn)
		{
			module           = moduleIn;
			resourcesManager = resourcesManagerIn;
			type             = typeIn;
			scope            = EnumResourceScope.GLOBAL;
			state            = EnumResourceState.ENABLED;
			action           = EnumResourceFilesCollisionAction.KEEP;
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

		public Builder hasntModulesCondition()
		{
			conditionFunction = (moduleIn) -> !moduleIn.hasModules();

			return this;
		}

		public ProjectModule.Builder end()
		{
			return module;
		}

		public ProjectModule.Builder build()
		{
			resourcesManager.add(new Resource(type, scope, state, action, structure, basePath, flags, conditionFunction != null ? conditionFunction : (moduleIn) -> true, pathGetter != null ? pathGetter :
					(moduleIn, resourceIn) -> (resourceIn.basePath != null ? basePath + (basePath.endsWith("\\") ? "" : "\\") : "") + moduleIn.filePath().replace("\\\\",
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
			}, function, resourcesManager.filesManager()));

			return module;
		}

		// Delegated

		public int resourceCount()
		{
			return resourcesManager.resourceCount();
		}

		public Builder add(EnumResourceType typeIn)
		{
			return resourcesManager.add(module, typeIn);
		}

		public Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn)
		{
			resourcesManager.add(module, typeIn, basePathIn, functionIn);

			return this;
		}

		public Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, boolean hasModulesConditionIn)
		{
			resourcesManager.add(module, typeIn, basePathIn, functionIn, hasModulesConditionIn);

			return this;
		}

		public Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, EnumResourceFilesCollisionAction actionIn)
		{
			resourcesManager.add(module, typeIn, basePathIn, functionIn, actionIn);

			return this;
		}

		public Builder add(EnumResourceType typeIn, String basePathIn, IResourceFunction functionIn, EnumResourceFilesCollisionAction actionIn, boolean moduleConditionIn)
		{
			resourcesManager.add(module, typeIn, basePathIn, functionIn, actionIn, moduleConditionIn);

			return this;
		}

		public Builder add(Resource resourceIn)
		{
			resourcesManager.add(resourceIn);

			return this;
		}

		public Resource of(EnumResourceType typeIn)
		{
			return resourcesManager.of(typeIn);
		}

		public FilesManager filesManager()
		{
			return resourcesManager.filesManager();
		}
	}

	public record Loaded(@Delegate Resource resource, String[] pathArray)
	{
		public int pathCount()
		{
			return pathArray.length;
		}
	}
}