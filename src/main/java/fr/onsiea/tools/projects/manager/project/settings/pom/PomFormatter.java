package fr.onsiea.tools.projects.manager.project.settings.pom;

import fr.onsiea.tools.projects.manager.project.settings.pom.details.IPomDetails;
import fr.onsiea.tools.projects.manager.project.settings.pom.details.PomDetails;
import fr.onsiea.tools.projects.manager.project.settings.pom.details.PomTag;
import fr.onsiea.tools.utils.string.StringUtils;
import fr.onsiea.tools.utils.stringbuilder.CachedStringBuilder;
import fr.onsiea.tools.utils.stringbuilder.StringBuilderCache;

import java.util.LinkedHashMap;
import java.util.Map;

public class PomFormatter
{
	private       CachedStringBuilder       currentStringBuilder;
	private       int                       tabs;
	private final Map<Integer, IPomDetails> previousLevels;

	public PomFormatter()
	{
		previousLevels = new LinkedHashMap<>();
	}

	public final void reset()
	{
		currentStringBuilder.free();
		tabs                 = 0;
		currentStringBuilder = null;
		previousLevels.clear();
	}

	public final String formatAll(IPomDetails[] childArray)
	{
		currentStringBuilder = StringBuilderCache.use();
		boolean hasChanged = false;
		for (var child : childArray)
		{
			if (format(child))
			{
				hasChanged = true;
			}
		}

		if (!hasChanged)
		{
			return null;
		}

		autoTab();
		var output = currentStringBuilder.toString();
		reset();

		return StringUtils.isBlank(output) ? null : StringUtils.removeUnusedBlanks(output).replaceAll("(\r\n|\n)", System.lineSeparator());
	}

	public final void autoTab()
	{
		var toString = currentStringBuilder.toString();
		currentStringBuilder.setLength(0);
		var matcher = PomCompiler.BALISE_PATTERN.matcher(toString);

		var last = 0;
		while (matcher.find())
		{
			if (matcher.group().startsWith("</") && tabs - 1 >= 0)
			{
				tabs--;
			}

			var content = toString.substring(last, matcher.end());
			var lines   = content.split("(\n|\r\n)");
			if (lines != null)
			{
				if (lines.length <= 1)
				{
					if (!StringUtils.isBlank(content))
					{
						currentStringBuilder.append(content);
					}
				}
				else
				{
					int i = 0;
					for (var line : lines)
					{
						if (i > 0)
						{
							currentStringBuilder.append(System.lineSeparator());
						}

						if (!StringUtils.isBlank(line))
						{
							currentStringBuilder.append("\t".repeat(tabs)).append(line);
						}

						i++;
					}
				}
			}

			if (!matcher.group().startsWith("<?") && !matcher.group().startsWith("</") && !matcher.group().startsWith("<!--"))
			{
				tabs++;
			}

			last = matcher.end();
		}
	}

	public final boolean format(IPomDetails detailsIn)
	{
		var previousLength = currentStringBuilder.length();

		var hasChanged = false;

		var previous = previousLevels.get(detailsIn.level());
		if (previous != null)
		{
			StringBuilderCache.appendNonNull(currentStringBuilder, previous.afterAccordingTo(detailsIn));
			StringBuilderCache.appendNonNull(currentStringBuilder, detailsIn.beforeAccordingTo(previous));
		}

		if (detailsIn instanceof PomTag)
		{
			hasChanged = formatTag((PomTag) detailsIn);
		}
		else if (detailsIn instanceof PomDetails)
		{
			hasChanged = formatDetails((PomDetails) detailsIn);
		}

		if (!hasChanged)
		{
			currentStringBuilder.setLength(previousLength);
		}
		else
		{
			var diff = previousLevels.size() - detailsIn.level();

			if (diff > 0)
			{
				var lastLevel = previousLevels.size();
				for (int i = 0; i < diff; i++)
				{
					previousLevels.remove(lastLevel - i);
				}
			}

			previousLevels.put(detailsIn.level(), detailsIn);
		}

		return hasChanged;
	}

	public final boolean formatTag(PomTag pomDetailsIn)
	{
		StringBuilderCache.appendAllNonNull(currentStringBuilder, pomDetailsIn.startTagStart(), pomDetailsIn.startTagContent(), pomDetailsIn.startTagEnd());

		StringBuilderCache.appendAllNonNull(currentStringBuilder, pomDetailsIn.betweenTag());
		var hasChanged = formatDetails(pomDetailsIn);

		StringBuilderCache.appendAllNonNull(currentStringBuilder, pomDetailsIn.endTagStart(), pomDetailsIn.endTagContent(), pomDetailsIn.endTagEnd());


		return hasChanged;
	}

	public final boolean formatDetails(PomDetails pomDetailsIn)
	{
		int changes = 0;

		if (StringBuilderCache.appendNonBlank(currentStringBuilder, pomDetailsIn.details()))
		{
			changes++;
		}

		var lastLength = -1;
		for (var child : pomDetailsIn.childArray())
		{
			lastLength = currentStringBuilder.length();

			if (format(child))
			{
				if (lastLength != currentStringBuilder.length())
				{
					changes++;
				}

				if (pomDetailsIn.betweenChild() != null)
				{
					currentStringBuilder.append(pomDetailsIn.betweenChild());
				}
			}
		}

		return changes > 0 || pomDetailsIn.addIfEmpty();
	}
}
