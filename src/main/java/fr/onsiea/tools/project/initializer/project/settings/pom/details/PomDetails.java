package fr.onsiea.tools.project.initializer.project.settings.pom.details;

import fr.onsiea.tools.project.initializer.project.settings.pom.PomManager;
import fr.onsiea.utils.function.IOIFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PomDetails implements IPomDetails
{
	protected final PomManager                       pomManager;
	protected final String                           name;
	protected final IPomDetails[]                    childArray;
	protected final String                           betweenChild;
	protected final IOIFunction<String, IPomDetails> before;
	protected final String                           details;
	protected final IOIFunction<String, IPomDetails> after;
	protected final boolean                          replaceExisting;
	protected final boolean                          addIfEmpty;
	protected final int                              index;
	protected final int                              level;

	public String afterAccordingTo(IPomDetails afterIn)
	{
		return after != null ? after.execute(afterIn) : null;
	}

	public String beforeAccordingTo(IPomDetails beforeIn)
	{
		return before != null ? before.execute(beforeIn) : null;
	}
}