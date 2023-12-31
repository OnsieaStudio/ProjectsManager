package fr.onsiea.tools.projects.manager.project.settings.pom.details;

import fr.onsiea.tools.projects.manager.project.settings.pom.PomManager;
import fr.onsiea.tools.utils.function.IOIFunction;
import lombok.Getter;

@Getter
public class PomTag extends PomDetails implements IPomDetails
{
	private final String  startTagStart;
	private final String  startTagContent;
	private final String  startTagEnd;
	private final String  endTagStart;
	private final String  endTagContent;
	private final String  endTagEnd;
	private final String  betweenTag;
	private final boolean isBlocTag;

	public PomTag(PomManager pomManagerIn, String nameIn, IPomDetails[] childArray, String betweenChildIn, IOIFunction<String, IPomDetails> beforeIn, String detailsIn, IOIFunction<String, IPomDetails> afterIn, boolean replaceExistingIn, boolean addIfEmptyIn, int indexIn,
	              int levelIn, String startTagStartIn, String startTagContentIn, String startTagEndIn, String endTagStartIn, String endTagContentIn, String endTagEndIn, String betweenTagIn, boolean isBlocTagIn)
	{
		super(pomManagerIn, nameIn, childArray, betweenChildIn, beforeIn, detailsIn, afterIn, replaceExistingIn, addIfEmptyIn, indexIn, levelIn);

		startTagStart   = startTagStartIn;
		startTagContent = startTagContentIn;
		startTagEnd     = startTagEndIn;
		endTagStart     = endTagStartIn;
		endTagContent   = endTagContentIn;
		endTagEnd       = endTagEndIn;
		betweenTag      = betweenTagIn;
		isBlocTag       = isBlocTagIn;
	}
}