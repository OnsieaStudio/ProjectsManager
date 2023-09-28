package fr.onsiea.tools.projects.manager.project.settings.resources;

/**
 * Local: allows it to be used only in the current module.
 * <p> <p>
 * Child: allows it to be used by the direct child if it does not have the implementation of the resource.
 * <p>
 * Global: allows it to be used by all children recursively if they do not have the implementation of the resource.
 */
public enum EnumResourceScope
{
	CHILD, GLOBAL, LOCAL
}