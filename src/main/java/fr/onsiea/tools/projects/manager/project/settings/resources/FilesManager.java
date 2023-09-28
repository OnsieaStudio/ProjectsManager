package fr.onsiea.tools.projects.manager.project.settings.resources;

import fr.onsiea.tools.projects.manager.project.settings.resources.Resource.Loaded;
import fr.onsiea.tools.utils.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class FilesManager
{
	private ResourcesManager resourcesManager;

	public final String append(EnumResourceFilesCollisionAction collisionActionIn, String toIn, String contentIn)
	{
		if (canMake(collisionActionIn, true, toIn))
		{
			try
			{
				FileUtils.append(toIn, contentIn);
			}
			catch (IOException eIn)
			{
				throw new RuntimeException(eIn);
			}
		}

		return toIn;
	}

	public final String write(EnumResourceFilesCollisionAction collisionActionIn, String toIn, String contentIn)
	{
		if (canMake(collisionActionIn, true, toIn))
		{
			try
			{
				FileUtils.replace(toIn, contentIn);
			}
			catch (IOException eIn)
			{
				throw new RuntimeException(eIn);
			}
		}

		return toIn;
	}

	public final String folderJunction(EnumResourceFilesCollisionAction collisionActionIn, Loaded loadedIn, String toIn)
	{
		if (loadedIn == null || loadedIn.pathArray().length == 0)
		{
			return null;
		}

		return folderJunction(collisionActionIn, loadedIn.pathArray()[0], toIn);
	}

	public final String folderJunction(EnumResourceFilesCollisionAction collisionActionIn, String fromFilePath, String toFilePathIn)
	{
		if (fromFilePath == null || toFilePathIn == null)
		{
			return null;
		}

		try
		{
			File toFile = new File(toFilePathIn);

			if (!canReplace(collisionActionIn, toFile))
			{
				return toFilePathIn;
			}

			if (!toFile.getParentFile().exists())
			{
				toFile.getParentFile().mkdirs();
			}
			File fromFile = new File(fromFilePath);
			Files.deleteIfExists(Path.of(toFilePathIn));

			Process process = null;
			if (System.getProperty("os.name").toLowerCase().contains("windows"))
			{
				process = new ProcessBuilder().command("cmd", "/c", "mklink", "/j", toFile.getAbsolutePath(), fromFilePath).start();
			}
			else
			{
				// TODO TEST OTHER OS folder junction code
				if (toFile.canExecute())
				{
					process = new ProcessBuilder().command(toFile.getAbsolutePath(), "-s", fromFile.getAbsolutePath(), toFile.getAbsolutePath()).start();
				}
				System.out.println("[WARN] At the moment, junctions creation cote with an operating system other than Windows isn't tested !");
			}

			if (process == null)
			{
				throw new RuntimeException("[ERROR] Cannot make junction, process creation failed !");
			}

			int errorCode;
			try
			{
				errorCode = process.waitFor();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				throw new IOException("Link operation was interrupted", e);
			}

			if (errorCode != 0)
			{
				throw new RuntimeException("[ERROR] Cannot make junction, operation failed !");
			}

			return toFilePathIn;
		}
		catch (IOException eIn)
		{
			throw new RuntimeException(eIn);
		}
	}

	public final String link(EnumResourceFilesCollisionAction collisionActionIn, Loaded loadedIn, String toIn)
	{
		if (loadedIn == null || loadedIn.pathArray().length == 0)
		{
			return null;
		}

		return link(collisionActionIn, loadedIn.pathArray()[0], toIn);
	}

	public final String link(EnumResourceFilesCollisionAction collisionActionIn, String fromFilePathIn, String toFilePathIn)
	{
		if (fromFilePathIn == null || toFilePathIn == null)
		{
			return null;
		}

		try
		{
			File toFile = new File(toFilePathIn);

			if (!canReplace(collisionActionIn, toFile))
			{
				return toFilePathIn;
			}

			if (!toFile.getParentFile().exists())
			{
				toFile.getParentFile().mkdirs();
			}
			Files.deleteIfExists(Path.of(toFilePathIn));
			FileUtils.link(fromFilePathIn, toFilePathIn);
		}
		catch (IOException eIn)
		{
			throw new RuntimeException(eIn);
		}

		return toFilePathIn;
	}

	public final String symbolicLink(EnumResourceFilesCollisionAction collisionActionIn, Loaded loadedIn, String toIn)
	{
		if (loadedIn == null || loadedIn.pathArray().length == 0)
		{
			return null;
		}

		return symbolicLink(collisionActionIn, loadedIn.pathArray()[0], toIn);
	}


	public final String symbolicLink(EnumResourceFilesCollisionAction collisionActionIn, String fromFilePathIn, String toFilePathIn)
	{
		if (fromFilePathIn == null || toFilePathIn == null)
		{
			return null;
		}

		try
		{
			File toFile = new File(toFilePathIn);
			if (!toFile.getParentFile().exists())
			{
				toFile.getParentFile().mkdirs();
			}
			File fromFile = new File(fromFilePathIn);

			var fromPath = fromFile.toPath();
			var toPath   = toFile.toPath();
			if (toFile.exists())
			{
				if (Files.isSymbolicLink(toPath))
				{
					if (Files.readSymbolicLink(toPath).equals(fromPath))
					{
						return toFilePathIn;
					}
				}
			}

			if (!canReplace(collisionActionIn, toFile))
			{
				return toFilePathIn;
			}

			Files.deleteIfExists(toPath);
			FileUtils.symbolicLink(fromFile.getAbsolutePath(), toFilePathIn);
		}
		catch (IOException eIn)
		{
			throw new RuntimeException(eIn);
		}

		return toFilePathIn;
	}

	public final String mkdir(EnumResourceFilesCollisionAction collisionActionIn, String folderPathIn)
	{
		if (canMake(collisionActionIn, false, new File(folderPathIn)))
		{
			FileUtils.mkdirs(folderPathIn);
		}

		return folderPathIn;
	}

	public final void mkdirs(EnumResourceFilesCollisionAction collisionActionIn, String... foldersPathsIn)
	{
		for (var folderPath : foldersPathsIn)
		{
			if (canMake(collisionActionIn, false, new File(folderPath)))
			{
				FileUtils.mkdirs(folderPath);
			}
		}
	}

	public final String mkdirAllChild(EnumResourceFilesCollisionAction collisionActionIn, String basePathIn, String... childFoldersPathsIn)
	{
		for (var folderPath : childFoldersPathsIn)
		{
			var child = new File(basePathIn, folderPath);
			if (canMake(collisionActionIn, false, child))
			{
				FileUtils.mkdirs(child.getAbsolutePath());
			}
		}

		return basePathIn;
	}

	public final boolean canMake(EnumResourceFilesCollisionAction collisionActionIn, boolean isFileIn, String filePathIn)
	{
		return canMake(collisionActionIn, isFileIn, new File(filePathIn));
	}

	public final boolean canMake(EnumResourceFilesCollisionAction collisionActionIn, boolean isFileIn, File fileIn)
	{
		if (!fileIn.exists())
		{
			return true;
		}

		if (isFileIn)
		{
			if (!fileIn.isFile())
			{
				throw new RuntimeException("[ERROR] Cannot make file \"" + fileIn.getAbsolutePath() + "\" already exists and is folder !");
			}
		}
		else
		{
			if (fileIn.isFile())
			{
				throw new RuntimeException("[ERROR] Cannot make folder \"" + fileIn.getAbsolutePath() + "\" already exists and is file !");
			}
		}

		switch (collisionActionIn)
		{
			case REPLACE:
				try
				{
					FileUtils.delete(fileIn);
				}
				catch (IOException eIn)
				{
					throw new RuntimeException(eIn);
				}

				return true;
			case ASK:
				System.out.println("\"" + fileIn.getAbsolutePath() + "\" already exists. Must be replaced ? [yes: Y, YES, y, yes], [no: N, NO, n, no]");
				var scanner = new Scanner(System.in);

				while (true)
				{
					switch (scanner.nextLine().toLowerCase())
					{
						case "y", "yes":
							return true;
						case "n", "no":
							return false;
					}
				}
		}
		return false;
	}

	public final boolean canReplace(EnumResourceFilesCollisionAction collisionActionIn, String filePathIn)
	{
		return canReplace(collisionActionIn, new File(filePathIn));
	}

	public final boolean canReplace(EnumResourceFilesCollisionAction collisionActionIn, File fileIn)
	{
		if (!fileIn.exists())
		{
			return true;
		}

		switch (collisionActionIn)
		{
			case REPLACE:
				try
				{
					FileUtils.delete(fileIn);
				}
				catch (IOException eIn)
				{
					throw new RuntimeException(eIn);
				}

				return true;
			case ASK:
				System.out.println("\"" + fileIn.getAbsolutePath() + "\" already exists. Must be replaced ? [yes: Y, YES, y, yes], [no: N, NO, n, no]");
				var scanner = new Scanner(System.in);

				while (true)
				{
					switch (scanner.nextLine().toLowerCase())
					{
						case "y", "yes":
							return true;
						case "n", "no":
							return false;
					}
				}
		}
		return false;
	}
}