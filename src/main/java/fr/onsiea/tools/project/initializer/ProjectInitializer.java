package fr.onsiea.tools.project.initializer;

import fr.onsiea.tools.project.initializer.project.ProjectModule.Builder;
import fr.onsiea.tools.project.initializer.project.settings.resources.EnumResourceStructure;
import fr.onsiea.tools.project.initializer.project.settings.resources.EnumResourceType;
import fr.onsiea.tools.project.initializer.project.settings.resources.Resource.IPathGetter;
import fr.onsiea.tools.project.initializer.project.settings.resources.Resource.IResourceFunction;
import fr.onsiea.tools.project.initializer.project.settings.resources.Resource.Loaded;
import fr.onsiea.utils.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectInitializer
{
	private final static String  PROGRAMMING_DRIVE          = "P:\\";
	private final static String  LANGUAGE                   = "java";
	private final static String  PROGRAMMING_PATH           = PROGRAMMING_DRIVE + LANGUAGE + "\\";
	private final static String  SOURCES_PATH               = PROGRAMMING_PATH + "sources\\";
	private final static String  RESOURCES_PATH             = PROGRAMMING_PATH + "resources\\";
	private final static String  IDE                        = "intellij";
	private final static String  IDE_PATH                   = PROGRAMMING_PATH + "ide\\" + IDE;
	private final static String  GIT_PATH                   = PROGRAMMING_PATH + "git\\";
	private final static String  PROJECT_PATH               = PROGRAMMING_PATH + "projects\\";
	private final static String  GLOBAL_PROJECT_PATH        = PROGRAMMING_DRIVE + "projects\\";
	private final static String  SOURCES_FOLDER_NAME        = "sources";
	private final static String  TEST_SOURCES_FOLDER_NAME   = "testSources";
	private final static boolean TESTS_SOURCES              = true;
	private final static String  RESOURCES_FOLDER_NAME      = "resources";
	private final static String  TEST_RESOURCES_FOLDER_NAME = "testResources";
	private final static boolean TESTS_RESOURCES            = true;

	// TODO resources AND files path management
	// TODO file cache to avoid replace already created file with ProjectInitializer (according date of file)
	// TODO auto add / replacement / ask files collision
	// TODO graphical interface, arguments, string, [code] and file config system
	// TODO jar/zip/folder libraries / sources management and auto sort
	// TODO receive file / config from network (FTP, local disk ...)
	// TODO reset all runtimes at end
	// TODO [StringBuilder cache] performance comparison
	// TODO multithreading
	// TODO common compile system with state returning, to avoid recompilation
	// TODO onsiea logger implementation
	// TODO eclipse, intellij idea and netbeans runtimes

	public static void main(String[] args)
	{
		String LWJGL = """
				<properties>
					<lwjgl.version>3.3.3-SNAPSHOT</lwjgl.version>
					<joml.version>1.10.5</joml.version>
					<lwjgl3-awt.version>0.1.8</lwjgl3-awt.version>
					<steamworks4j.version>1.9.0</steamworks4j.version>
					<steamworks4j-server.version>1.9.0</steamworks4j-server.version>
					<lwjgl.natives>natives-windows</lwjgl.natives>
				</properties>
				      
					<repositories>
						<repository>
							<id>sonatype-snapshots</id>
							<url>https://oss.sonatype.org/content/repositories/snapshots</url>
							<releases><enabled>false</enabled></releases>
							<snapshots><enabled>true</enabled></snapshots>
						</repository>
					</repositories>
				      
				<dependencyManagement>
					<dependencies>
						<dependency>
							<groupId>org.lwjgl</groupId>
							<artifactId>lwjgl-bom</artifactId>
							<version>${lwjgl.version}</version>
							<scope>import</scope>
							<type>pom</type>
						</dependency>
					</dependencies>
				</dependencyManagement>
				      
				<dependencies>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-assimp</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-bgfx</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-cuda</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-glfw</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-meshoptimizer</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-nanovg</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-nuklear</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-openal</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-opengl</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-par</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-shaderc</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-spvc</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-stb</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-vma</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-vulkan</artifactId>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-assimp</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-bgfx</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-glfw</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-meshoptimizer</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-nanovg</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-nuklear</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-openal</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-opengl</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-par</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-shaderc</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-spvc</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-stb</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
					<dependency>
						<groupId>org.lwjgl</groupId>
						<artifactId>lwjgl-vma</artifactId>
						<classifier>${lwjgl.natives}</classifier>
					</dependency>
						<dependency>
						<groupId>org.joml</groupId>
						<artifactId>joml</artifactId>
						<version>${joml.version}</version>
					</dependency>
						<dependency>
						<groupId>org.lwjglx</groupId>
						<artifactId>lwjgl3-awt</artifactId>
						<version>${lwjgl3-awt.version}</version>
					</dependency>
						<dependency>
						<groupId>com.code-disaster.steamworks4j</groupId>
						<artifactId>steamworks4j</artifactId>
						<version>${steamworks4j.version}</version>
					</dependency>
						<dependency>
						<groupId>com.code-disaster.steamworks4j</groupId>
						<artifactId>steamworks4j-server</artifactId>
						<version>${steamworks4j-server.version}</version>
					</dependency>
				</dependencies>""";

		IResourceFunction projectFunction = ((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
		                                     {
			                                     var path = pathIn + "\\";
			                                     if (structureIn.equals(EnumResourceStructure.FLAT) && resourceIn.structure().equals(EnumResourceStructure.BOTH))
			                                     {
				                                     path += ".flat\\";
			                                     }
			                                     path += (moduleIn.parent() != null ? moduleIn.name() : "");
			                                     FileUtils.mkdirs(path);
			                                     path += (moduleIn.parent() != null ? moduleIn.name() + betweenIn : "");
			                                     if (betweenIn.contentEquals("\\"))
			                                     {
				                                     FileUtils.mkdirs(path);
			                                     }

			                                     symbolicLink(moduleIn.loadedOf(EnumResourceType.MAVEN), path + "pom.xml");
			                                     symbolicLink(moduleIn.loadedOf(EnumResourceType.SOURCES), path + "sources");
			                                     symbolicLink(moduleIn.loadedOf(EnumResourceType.TEST_SOURCES), path + "testSources");
			                                     symbolicLink(moduleIn.loadedOf(EnumResourceType.RESOURCES), path + "resources");
			                                     symbolicLink(moduleIn.loadedOf(EnumResourceType.TEST_RESOURCES), path + "testResources");

			                                     symbolicLink(moduleIn.loadedOf(EnumResourceType.GIT), path + (betweenIn.contentEquals("_") ? "" : ".") + "git");
			                                     symbolicLink(moduleIn.loadedOf(EnumResourceType.IDE), path + (betweenIn.contentEquals("_") ? "" : ".") + "ide");

			                                     return null;
		                                     });

		IPathGetter projectPathFunction = ((moduleIn, resourceIn) -> resourceIn.basePath() + "\\" + moduleIn.firstName() + "\\");

		var module = new Builder("ludart").prefix("fr").groupId("onsiea").path("onsiea\\game\\Ludart")
				  .makeResource(EnumResourceType.SOURCES).basePath(SOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					if (moduleIn.modules().length == 0)
					{
						var path = pathIn + "\\" + SOURCES_FOLDER_NAME;
						FileUtils.mkdirs(path);

						return path;
					}

					return null;
				}).end()
				.makeResource(EnumResourceType.TEST_SOURCES).basePath(SOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					if (moduleIn.modules().length == 0 && TESTS_SOURCES)
					{
						var path = pathIn + "\\" + TEST_SOURCES_FOLDER_NAME;
						FileUtils.mkdirs(path);

						return path;
					}

					return null;
				}).end()
				.makeResource(EnumResourceType.MAVEN).basePath(SOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					var pomPath = pathIn + "\\pom.xml";
					try
					{
						FileUtils.replace(pomPath, moduleIn.pomManager().details());
					}
					catch (IOException eIn)
					{
						throw new RuntimeException(eIn);
					}

					return pomPath;
				}).end()
				 .makeResource(EnumResourceType.RESOURCES).basePath(RESOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					if (moduleIn.modules().length == 0)
					{
						var path = pathIn + "\\" + RESOURCES_FOLDER_NAME;
						FileUtils.mkdirs(path);

						return path;
					}


					return null;
				}).end()
				  .makeResource(EnumResourceType.TEST_RESOURCES).basePath(RESOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					if (moduleIn.modules().length == 0 && TESTS_RESOURCES)
					{
						var path = pathIn + "\\" + TEST_RESOURCES_FOLDER_NAME;
						FileUtils.mkdirs(path);

						return path;
					}

					return null;
				}).end()
				   .makeResource(EnumResourceType.IDE).basePath(IDE_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					FileUtils.mkdirs(pathIn);

					symbolicLink(moduleIn.loadedOf(EnumResourceType.MAVEN), pathIn + "\\pom.xml");

					if (moduleIn.modules().length > 0)
					{
						return pathIn;
					}

					var commonPath = pathIn + "\\src\\";
					FileUtils.mkdirs(commonPath + "main", commonPath + "test");

					symbolicLink(moduleIn.loadedOf(EnumResourceType.SOURCES), commonPath + "main\\java");
					symbolicLink(moduleIn.loadedOf(EnumResourceType.TEST_SOURCES), commonPath + "test\\java");
					symbolicLink(moduleIn.loadedOf(EnumResourceType.RESOURCES), commonPath + "main\\resources");
					symbolicLink(moduleIn.loadedOf(EnumResourceType.TEST_RESOURCES), commonPath + "test\\resources");

					return null;
				}).end()
				 .makeResource(EnumResourceType.GIT).basePath(GIT_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					FileUtils.mkdirs(pathIn);

					link(moduleIn.loadedOf(EnumResourceType.MAVEN), pathIn + "\\pom.xml");

					if (moduleIn.modules().length > 0)
					{
						return pathIn;
					}

					var commonPath = pathIn + "\\src\\";
					FileUtils.mkdirs(commonPath + "main", commonPath + "test");

					symbolicLink(moduleIn.loadedOf(EnumResourceType.SOURCES), commonPath + "main\\java");
					symbolicLink(moduleIn.loadedOf(EnumResourceType.TEST_SOURCES), commonPath + "test\\java");
					symbolicLink(moduleIn.loadedOf(EnumResourceType.RESOURCES), commonPath + "main\\resources");
					symbolicLink(moduleIn.loadedOf(EnumResourceType.TEST_RESOURCES), commonPath + "test\\resources");

					return null;
				}).end()
				  .makeResource(EnumResourceType.PROJECT).basePath(PROJECT_PATH)
				.structure(EnumResourceStructure.BOTH)
				 .pathGetter(projectPathFunction)
				  .function(projectFunction).end()
				   .makeResource(EnumResourceType.GLOBAL_PROJECT).basePath(GLOBAL_PROJECT_PATH)
				.structure(EnumResourceStructure.BOTH)
				 .pathGetter(projectPathFunction)
				  .function(projectFunction).end()
				  .module("common").name("LudartCommon")
				  .dependants("manager", "game", "core", "prototype")
				  .dependant("client").pomCompile(LWJGL)
				// TODO common profile system (client and prototype for example)
				.build();

		module.show();
		module.runtime();
	}


	public final static void symbolicLink(Loaded loadedIn, String toIn)
	{
		if (loadedIn == null || loadedIn.pathArray().length == 0)
		{
			return;
		}

		try
		{
			File toFile = new File(toIn);
			if (!toFile.getParentFile().exists())
			{
				toFile.getParentFile().mkdirs();
			}
			File fromFile = new File(loadedIn.pathArray()[0]);

			var fromPath = fromFile.toPath();
			var toPath   = toFile.toPath();
			if (toFile.exists())
			{
				if (Files.isSymbolicLink(toPath))
				{
					if (Files.readSymbolicLink(toPath).equals(fromPath))
					{
						return;
					}
				}
				else if (toFile.isDirectory())
				{
					System.out.println("444 " + fromPath + " " + toPath);

					return;
				}
			}

			Files.deleteIfExists(toPath);
			FileUtils.symbolicLink(fromFile.getAbsolutePath(), toIn);
		}
		catch (IOException eIn)
		{
			throw new RuntimeException(eIn);
		}
	}

	public final static void link(Loaded loadedIn, String toIn)
	{
		if (loadedIn == null || loadedIn.pathArray().length == 0)
		{
			return;
		}

		try
		{
			File file = new File(toIn);
			if (file.exists() && file.isDirectory())
			{
				return;
			}
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			Files.deleteIfExists(Path.of(toIn));
			FileUtils.link(loadedIn.pathArray()[0], toIn);
		}
		catch (IOException eIn)
		{
			throw new RuntimeException(eIn);
		}
	}
}