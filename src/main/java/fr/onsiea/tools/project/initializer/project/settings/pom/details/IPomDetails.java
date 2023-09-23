package fr.onsiea.tools.project.initializer.project.settings.pom.details;

public interface IPomDetails
{
	String name();

	String details();

	String beforeAccordingTo(IPomDetails beforeIn);

	String afterAccordingTo(IPomDetails afterIn);

	int level();

	int index();
}