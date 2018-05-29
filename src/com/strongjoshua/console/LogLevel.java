/**
 * Copyright 2018 StrongJoshua (strongjoshua@hotmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.strongjoshua.console;

import com.badlogic.gdx.graphics.Color;

/**
 * Specifies the 'level' of a log entry. The level affects the color of the entry in the console and is also displayed next to the
 * entry when the log entries are printed to a file with {@link Console#printLogToFile(String)}.
 *
 * @author StrongJoshua
 */
public enum LogLevel {
	/**
	 * The default log level. Prints in white to the console and has no special indicator in the log file.<br>
	 * <b>Intentional Use:</b> debugging.
	 */
	DEFAULT(new Color(1, 1, 1, 1), ""), /**
	 * Use to print errors. Prints in red to the console and has the '<i>ERROR</i>' marking in
	 * the log file.<br>
	 * <b>Intentional Use:</b> printing internal console errors; debugging.
	 */
	ERROR(new Color(217f / 255f, 0, 0, 1), "Error: "), /**
	 * Prints in green. Use to print success notifications of events.<br>
	 * <b>Intentional Use:</b> Print successful execution of console commands (if needed).
	 */
	SUCCESS(new Color(0, 217f / 255f, 0, 1), "Success! "), /**
	 * Prints in white with {@literal "> "} prepended to the command. Also has
	 * that prepended text as the indicator in the log file.<br>
	 * <b>Intentional Use:</b> To be used by the console, alone.
	 */
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
