package fr.onsiea.tools.project.initializer.project.settings.pom;

import fr.onsiea.tools.project.initializer.project.settings.pom.PomManager.IChild;
import fr.onsiea.utils.string.StringUtils;

import java.util.regex.Pattern;

public class PomCompiler
{
	public final static Pattern BALISE_PATTERN       = Pattern.compile(Pattern.quote("<") + "[.[^<>]]*" + Pattern.quote(">"), Pattern.DOTALL);
	public final static Pattern START_BALISE_PATTERN = Pattern.compile(Pattern.quote("<") + "[.[^<>/]][.[^<>]]*" + Pattern.quote(">"), Pattern.DOTALL);
	public final static Pattern END_BALISE_PATTERN   = Pattern.compile(Pattern.quote("</") + "[.[^<>]]*" + Pattern.quote(">"), Pattern.DOTALL);

	public final static void compile(String contentIn, PomManager.Builder builderIn)
	{
		var lines = contentIn.split("[\r\n]+");

		IChild current = builderIn;

		for (var line : lines)
		{
			var _line = StringUtils.removeUnusedBlanks(line);

			if (_line.isEmpty() || line.isBlank() || _line.matches("($|[ \t\r\n ]|^)+"))
			{
				continue;
			}
			var matcher = BALISE_PATTERN.matcher(line);

			int    last     = 0;
			String lastName = null;
			while (matcher.find())
			{
				var start = matcher.start();
				var group = matcher.group();
				var name  = group.replace("</", "").replace("<", "").replace(">", "");

				boolean oneLine = group.matches("^<" + Pattern.quote("?") + ".*" + Pattern.quote("?") + ">$");
				if (!oneLine)
				{
					if (current == null)
					{
						throw new RuntimeException("[ERROR] PomManager.Builder : compilation error !");
					}

					String details;
					if (last != matcher.start())
					{
						details = line.substring(last, start);
						if (!(details.isBlank() || details.isEmpty() || details.matches("($|[ \t\r\n ]|^)+")) && current.currentChild() != null)
						{
							current.currentChild().details(details);
						}
					}

					if (group.startsWith("</") && ((current == null || current.currentChild() == null) || name.contentEquals(current.currentChild().name())))
					{
						if (current != null && current.currentChild() != null && lastName != null && lastName.contentEquals(current.currentChild().name()))
						{
							current.currentChild().enableLineTag();
						}
						else
						{
							current.currentChild().enableBlocTag();
						}

						if (current.currentChild() != null)
						{
							current = current.currentChild().previous();
						}

						if (current == null)
						{
							current = builderIn;
						}
					}
					else
					{
						lastName = name;
						current  = current.pomDetails(name);
					}
				}
				last = matcher.end();
			}
		}
	}
}
