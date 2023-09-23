package fr.onsiea.tools.project.initializer.project.settings.pom.details;

import fr.onsiea.tools.project.initializer.project.ProjectModule;
import fr.onsiea.tools.project.initializer.project.settings.pom.PomManager;
import fr.onsiea.tools.project.initializer.project.settings.pom.PomManager.IChild;
import fr.onsiea.utils.function.IOIFunction;
import fr.onsiea.utils.string.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public final class PomDetailsBuilder implements IChild<PomDetailsBuilder>
{
	private final   ProjectModule.Builder            module;
	private final   PomDetailsBuilder                previous;
	private final   PomManager.Builder               pomManager;
	private final   String                           id;
	private final   Map<String, PomDetailsBuilder>   childMap;
	private @Setter String                           name;
	private @Setter EnumTagState                     tagState;
	private @Setter String                           startTagStart;
	private @Setter String                           startTagEnd;
	private @Setter String                           startTagContent;
	private @Setter String                           endTagContent;
	private @Setter String                           endTagStart;
	private @Setter String                           endTagEnd;
	private         Map<String, String>              shortcuts;
	private @Setter String                           details;
	private @Setter String                           betweenTag;
	private @Setter String                           betweenChild;
	private         IOIFunction<String, IPomDetails> before;
	private         IOIFunction<String, IPomDetails> after;
	private @Setter boolean                          replaceExisting;
	private @Setter boolean                          canHadMultiples;
	private @Setter boolean                          addIfEmpty;
	private @Setter int                              index;
	private @Setter int                              level;
	private         IPomDetails                      built;

	public PomDetailsBuilder(ProjectModule.Builder moduleIn, PomManager.Builder pomManagerIn, String idIn)
	{
		module          = moduleIn;
		previous        = null;
		pomManager      = pomManagerIn;
		id              = idIn;
		name            = id;
		tagState        = EnumTagState.UNDEFINED;
		childMap        = new LinkedHashMap<>();
		index           = pomManagerIn.childCount();
		replaceExisting = false;
		canHadMultiples = false;
		addIfEmpty      = false;
	}

	public PomDetailsBuilder(ProjectModule.Builder moduleIn, PomDetailsBuilder previousIn, PomManager.Builder pomManagerIn, String idIn)
	{
		module          = moduleIn;
		previous        = previousIn;
		pomManager      = pomManagerIn;
		id              = idIn;
		name            = id;
		tagState        = EnumTagState.UNDEFINED;
		childMap        = new LinkedHashMap<>();
		shortcuts       = new LinkedHashMap<>();
		index           = pomManagerIn.childCount();
		replaceExisting = false;
		canHadMultiples = false;
		addIfEmpty      = false;
	}

	public PomDetailsBuilder afterFunction(IOIFunction<String, IPomDetails> functionIn)
	{
		after = functionIn;

		return this;
	}

	public PomDetailsBuilder after(String afterIn)
	{
		after = (detailsAfterIn) -> afterIn;

		return this;
	}

	public PomDetailsBuilder beforeFunction(IOIFunction<String, IPomDetails> functionIn)
	{
		before = functionIn;

		return this;
	}


	public PomDetailsBuilder before(String beforeIn)
	{
		before = (detailsBeforeIn) -> beforeIn;

		return this;
	}

	@Override
	public PomDetailsBuilder parent()
	{
		return this;
	}

	@Override
	public String shortcutOf(String idIn)
	{
		return shortcuts.get(idIn);
	}

	public PomDetailsBuilder shortcut(String idIn, String pathIn)
	{
		shortcuts.put(idIn, pathIn);

		return this;
	}

	@Override
	public PomDetailsBuilder currentChild()
	{
		return this;
	}

	public PomDetailsBuilder strictGet(String nameIn)
	{
		return childMap.get(nameIn);
	}

	@Override
	public PomDetailsBuilder put(String idIn, PomDetailsBuilder builderIn)
	{
		childMap.put(idIn, builderIn);

		return this;
	}

	@Override
	public int childCount()
	{
		return childMap.size();
	}

	public PomDetailsBuilder enableLineTag()
	{
		tagState   = EnumTagState.ENABLED;
		betweenTag = "";
		if (betweenChild == null)
		{
			betweenChild = " ";
		}

		return this;
	}

	public PomDetailsBuilder enableBlocTag()
	{
		tagState   = EnumTagState.ENABLED;
		betweenTag = "\n";
		if (betweenChild == null)
		{
			betweenChild = "\n";
		}

		return this;
	}

	public PomDetailsBuilder disableTag()
	{
		tagState = EnumTagState.DISABLED;

		startTagStart   = null;
		startTagEnd     = null;
		startTagContent = null;
		endTagContent   = null;
		endTagStart     = null;
		endTagEnd       = null;

		childMap.clear();

		return this;
	}

	public PomDetailsBuilder startTag(String contentIn)
	{
		startTagStart("");
		startTagContent(contentIn);
		startTagEnd("");

		return this;
	}

	public PomDetailsBuilder endTag(String contentIn)
	{
		endTagStart("");
		endTagContent(contentIn);
		endTagEnd("");

		return this;
	}

	public PomDetailsBuilder startTagStart(String startTagStartIn)
	{
		startTagStart = startTagStartIn;
		tagState      = EnumTagState.ENABLED;

		return this;
	}

	public PomDetailsBuilder startTagEnd(String startTagEndIn)
	{
		startTagEnd = startTagEndIn;
		tagState    = EnumTagState.ENABLED;

		return this;
	}

	public PomDetailsBuilder startTagContent(String startTagContentIn)
	{
		startTagContent = startTagContentIn;
		tagState        = EnumTagState.ENABLED;

		return this;
	}

	public PomDetailsBuilder endTagStart(String endTagStartIn)
	{
		endTagStart = endTagStartIn;
		tagState    = EnumTagState.ENABLED;

		return this;
	}

	public PomDetailsBuilder endTagContent(String endTagContentIn)
	{
		endTagContent = endTagContentIn;
		tagState      = EnumTagState.ENABLED;

		return this;
	}

	public PomDetailsBuilder endTagEnd(String endTagEndIn)
	{
		endTagEnd = endTagEndIn;
		tagState  = EnumTagState.ENABLED;

		return this;
	}

	public PomDetailsBuilder details(String... detailsIn)
	{
		if (replaceExisting)
		{
			replace(detailsIn);
		}
		else
		{
			append(detailsIn);
		}

		return this;
	}

	public PomDetailsBuilder detail(String detailIn)
	{
		if (replaceExisting)
		{
			replace(detailIn);
		}
		else
		{
			append(detailIn);
		}

		return this;
	}

	public PomDetailsBuilder replace(String... detailsIn)
	{
		StringBuilder finalDetails = new StringBuilder();

		int i = 0;
		for (var details : detailsIn)
		{
			if (StringUtils.isBlank(details))
			{
				continue;
			}

			finalDetails.append(details);

			i++;
			if (i < detailsIn.length)
			{
				finalDetails.append((betweenChild != null ? betweenChild : "\n"));
			}
		}

		details = finalDetails.toString();

		return this;
	}

	public PomDetailsBuilder append(String... detailsIn)
	{
		StringBuilder finalDetails = new StringBuilder();

		int i = 0;
		for (var details : detailsIn)
		{
			finalDetails.append(details);

			i++;
			if (i < detailsIn.length)
			{
				finalDetails.append((betweenChild != null ? betweenChild : "\n"));
			}
		}

		if (details == null)
		{
			details = finalDetails.toString();
		}
		else
		{
			details += finalDetails.toString();
		}

		return this;
	}

	public PomDetailsBuilder previousChild()
	{
		if (previous == null)
		{
			throw new RuntimeException("[ERROR] PomManager/DetailsBuilder Cannot return previous, because is null !");
		}

		return previous;
	}

	public IPomDetails build()
	{
		if (built != null)
		{
			return built;
		}

		if (betweenChild == null)
		{
			betweenChild = "\n";
		}

		var childArray = new IPomDetails[childMap.size()];
		if (tagState.equals(EnumTagState.ENABLED) || (tagState.equals(EnumTagState.UNDEFINED) && (startTagStart != null || startTagContent != null || startTagEnd != null || endTagStart != null || endTagContent != null || endTagEnd != null)))
		{
			if (startTagStart == null)
			{
				startTagStart = "<";
			}
			if (startTagContent == null)
			{
				startTagContent = name;
			}
			if (startTagEnd == null)
			{
				startTagEnd = ">";
			}

			if (endTagStart == null)
			{
				endTagStart = "</";
			}
			if (endTagContent == null)
			{
				endTagContent = name;
			}
			if (endTagEnd == null)
			{
				endTagEnd = ">";
			}

			if (betweenTag == null)
			{
				betweenTag = "\n";
			}

			built = new PomTag(pomManager.build(), name, childArray, betweenChild, before, details, after, replaceExisting, addIfEmpty, index, level, startTagStart, startTagContent, startTagEnd, endTagStart, endTagContent, endTagEnd, betweenTag);
		}
		else
		{
			built = new PomDetails(pomManager.build(), name, childArray, betweenChild, before, details, after, replaceExisting, addIfEmpty, index, level);
		}

		int i = 0;
		for (var child : childMap.values())
		{
			childArray[i] = child.build();

			i++;
		}

		return built;
	}

	public ProjectModule.Builder end()
	{
		return module;
	}
}