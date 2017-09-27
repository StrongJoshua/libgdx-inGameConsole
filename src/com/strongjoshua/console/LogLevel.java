
package com.strongjoshua.console;

import com.badlogic.gdx.graphics.Color;

/** Specifies the 'level' of a log entry. The level affects the color of the entry in the console and is also displayed next to the
 * entry when the log entries are printed to a file with {@link Console#printLogToFile(String)}.
 *
 * @author StrongJoshua */
public enum LogLevel {
	/** The default log level. Prints in white to the console and has no special indicator in the log file.<br>
	 * Intentional Use: debugging. */
	DEFAULT(new Color(1, 1, 1, 1), ""), /** Use to print errors. Prints in red to the console and has the '<i>ERROR</i>' marking in
	 * the log file.<br>
	 * Intentional Use: printing internal console errors; debugging. */
	ERROR(new Color(217f / 255f, 0, 0, 1), "Error: "), /** Prints in green. Use to print success notifications of events. Intentional
	 * Use: Print successful execution of console commands (if needed). */
	SUCCESS(new Color(0, 217f / 255f, 0, 1), "Success! "), /** Prints in white with {@literal "> "} prepended to the command. Has
	 * that prepended text as the indicator in the log file. Intentional Use: To be used by the console, alone. */
	COMMAND(new Color(1, 1, 1, 1), "> ");

	private Color color;
	private String identifier;

	LogLevel (Color c, String identity) {
		this.color = c;
		identifier = identity;
	}

	Color getColor () {
		return color;
	}

	String getIdentifier () {
		return identifier;
	}
}
