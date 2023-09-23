package fr.onsiea.tools.project.initializer;

import fr.onsiea.tools.project.initializer.project.ProjectModule;
import fr.onsiea.tools.project.initializer.project.settings.resources.EnumResourceType;
import fr.onsiea.utils.file.FileUtils;

import java.io.IOException;

public class ProjectInitializer
{
	private final static String  PROGRAMMATION_PATH        = "P:\\";
	private final static String  LANGUAGE                  = "java";
	private final static String  SOURCES_PATH              = PROGRAMMATION_PATH + LANGUAGE + "\\sources";
	private final static String  SOURCES_FOLDER_NAME       = "sources";
	private final static String  TESTS_SOURCES_FOLDER_NAME = "testsSources";
	private final static boolean TESTS_SOURCES             = true;

	private final static String  RESOURCES_PATH              = PROGRAMMATION_PATH + LANGUAGE + "\\sources";
	private final static String  RESOURCES_FOLDER_NAME       = "sources";
	private final static String  TESTS_RESOURCES_FOLDER_NAME = "testsSources";
	private final static boolean TESTS_RESOURCES             = true;

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

		var module = new ProjectModule.Builder("ludart").prefix("fr").groupId("onsiea").path("onsiea\\game\\")
				  .makeResource(EnumResourceType.SOURCES).basePath(SOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					if (moduleIn.modules().length == 0)
					{
						FileUtils.mkdirs(pathIn + "\\" + SOURCES_FOLDER_NAME);

						return pathIn;
					}

					return null;
				}).end()
				.makeResource(EnumResourceType.TEST_SOURCES).basePath(SOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					if (moduleIn.modules().length == 0 && TESTS_SOURCES)
					{
						FileUtils.mkdirs(pathIn + "\\" + TESTS_SOURCES_FOLDER_NAME);

						return pathIn;
					}

					return null;
				}).end()
				.makeResource(EnumResourceType.MAVEN).basePath(SOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					try
					{
						FileUtils.replace(pathIn + "\\pom.xml", moduleIn.pomManager().details());
					}
					catch (IOException eIn)
					{
						throw new RuntimeException(eIn);
					}

					return pathIn + "\\pom.xml";
				}).end()
				 .makeResource(EnumResourceType.RESOURCES).basePath(RESOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					if (moduleIn.modules().length == 0)
					{
						FileUtils.mkdirs(pathIn + "\\" + RESOURCES_FOLDER_NAME);

						return pathIn;
					}


					return null;
				}).end()
				  .makeResource(EnumResourceType.RESOURCES).basePath(RESOURCES_PATH).function((moduleIn, resourceIn, pathIn, betweenIn, structureIn) ->
				{
					if (moduleIn.modules().length == 0 && TESTS_RESOURCES)
					{
						FileUtils.mkdirs(pathIn + "\\" + TESTS_RESOURCES_FOLDER_NAME);

						return pathIn;
					}

					return null;
				}).end()
				  .module("common").name("LudartCommon")
				  .dependants("manager", "game", "core", "prototype")
				  .dependant("client").pomCompile(LWJGL)
				// TODO common profile system (client and prototype for example)
				.build();

		module.show();
		module.runtime();
	}
}