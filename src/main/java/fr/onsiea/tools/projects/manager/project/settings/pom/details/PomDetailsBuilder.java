package fr.onsiea.tools.projects.manager.project.settings.pom.details;

import fr.onsiea.tools.projects.manager.project.ProjectModule;
import fr.onsiea.tools.projects.manager.project.settings.pom.PomManager;
import fr.onsiea.tools.projects.manager.project.settings.pom.PomManager.IChild;
import fr.onsiea.tools.utils.function.IOIFunction;
import fr.onsiea.tools.utils.string.StringUtils;
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
	private @Setter String                           name;
	public final    Map<String, PomDetailsBuilder>   childMap;
	private @Setter boolean                          replaceExisting;
	private @Setter boolean                          canHadMultiples;
	private @Setter boolean                          addIfEmpty;
	private @Setter int                              index;
	private @Setter int                              level;
	private @Setter EnumTagState                     tagState;
	private         String                           startTagStart;
	private         String                           startTagEnd;
	private         String                           startTagContent;
	private         String                           endTagContent;
	private         String                           endTagStart;
	private         String                           endTagEnd;
	private final   Map<String, String>              shortcuts;
	private         String                           details;
	private @Setter String                           betweenTag;
	private @Setter String                           betweenChild;
	private         IOIFunction<String, IPomDetails> before;
	private         IOIFunction<String, IPomDetails> after;
	private         IPomDetails                      built;
	private         boolean                          isBlocTag;

	public PomDetailsBuilder(ProjectModule.Builder moduleIn, PomDetailsBuilder previousIn, PomManager.Builder pomManagerIn, String idIn)
	{
		module          = moduleIn;
		previous        = previousIn;
		pomManager      = pomManagerIn;
		id              = idIn;
		name            = id;
		childMap        = new LinkedHashMap<>();
		shortcuts       = new LinkedHashMap<>();
		index           = pomManagerIn.childCount();
		replaceExisting = false;
		canHadMultiples = false;
		addIfEmpty      = false;
		tagState        = EnumTagState.UNDEFINED;
	}

	public PomDetailsBuilder copy()
	{
		return copy(previous != null ? previous : pomManager());
	}

	public PomDetailsBuilder copy(IChild<?> intoIn)
	{
		int i   = 0;
		var _id = id + "-" + i;
		i++;
		while (containsChild(_id))
		{
			_id = id + "-" + i;

			i++;
		}

		var builder = intoIn.pomDetails(_id).name(this.name).replaceExisting(replaceExisting).canHadMultiples(canHadMultiples).addIfEmpty(addIfEmpty)
				.tagState(tagState).startTagStart(startTagStart).startTagEnd(startTagEnd)
				.startTagContent(startTagContent).endTagContent(endTagContent).endTagStart(endTagStart).endTagEnd(endTagEnd)
				.betweenTag(betweenTag).betweenChild(betweenChild);

		for (var child : childMap.values())
		{
			child.copy(builder);
		}

		return builder;
	}

	public boolean isEmpty()
	{
		if (details != null && !StringUtils.isBlank(details))
		{
			return false;
		}

		for (var child : childMap.values())
		{
			if (!child.isEmpty())
			{
				return false;
			}
		}

		return true;
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
	public void put(String idIn, PomDetailsBuilder builderIn)
	{
		childMap.put(idIn, builderIn);
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
		isBlocTag = false;

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
		isBlocTag = true;

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

			built = new PomTag(pomManager.build(), name, childArray, betweenChild, before, details, after, replaceExisting, addIfEmpty, index, level, startTagStart, startTagContent, startTagEnd, endTagStart, endTagContent, endTagEnd, betweenTag, isBlocTag);
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