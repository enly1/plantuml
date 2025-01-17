/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * 
 *
 */
package net.sourceforge.plantuml.creole.command;

import net.sourceforge.plantuml.ThemeStyle;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.creole.legacy.StripeSimple;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.Splitter;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorRuntimeException;

public class CommandCreoleColorChange implements Command {

	private static final Pattern2 pattern = MyPattern.cmpile("^(" + Splitter.fontColorPattern2 + "(.*?)\\</color\\>)");

	private static final Pattern2 patternEol = MyPattern.cmpile("^(" + Splitter.fontColorPattern2 + "(.*)$)");

	private final Pattern2 mypattern;
	private final ThemeStyle themeStyle;

	public static Command create(ThemeStyle themeStyle) {
		return new CommandCreoleColorChange(themeStyle, pattern);
	}

	public static Command createEol(ThemeStyle themeStyle) {
		return new CommandCreoleColorChange(themeStyle, patternEol);
	}

	private CommandCreoleColorChange(ThemeStyle themeStyle, Pattern2 pattern) {
		this.mypattern = pattern;
		this.themeStyle = themeStyle;

	}

	public int matchingSize(String line) {
		final Matcher2 m = mypattern.matcher(line);
		if (m.find() == false) {
			return 0;
		}
		return m.group(2).length();
	}

	public String executeAndGetRemaining(String line, StripeSimple stripe) throws NoSuchColorRuntimeException {
		final Matcher2 m = mypattern.matcher(line);
		if (m.find() == false) {
			throw new IllegalStateException();
		}
		final FontConfiguration fc1 = stripe.getActualFontConfiguration();
		final String s = m.group(2);
		try {
			final HColor color = HColorSet.instance().getColor(themeStyle, s);
			final FontConfiguration fc2 = fc1.changeColor(color);
			stripe.setActualFontConfiguration(fc2);
		} catch (NoSuchColorException e) {
			// Too late for parsing error
			// So we just ignore
		}
		stripe.analyzeAndAdd(m.group(3));
		stripe.setActualFontConfiguration(fc1);
		return line.substring(m.group(1).length());
	}

}
