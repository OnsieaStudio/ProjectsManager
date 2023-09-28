package fr.onsiea.tools.projects.manager.project.settings.pom.details;

public interface IPomDetails
{
	String name();

	String details();

	String beforeAccordingTo(IPomDetails beforeIn);

	String afterAccordingTo(IPomDetails afterIn);

	int level();

	int index();
}