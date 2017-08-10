/**
 *
 */

package com.strongjoshua.console;

import com.badlogic.gdx.utils.reflect.Method;
import com.strongjoshua.console.annotation.HiddenCommand;

/** @author Eric */
public final class ConsoleUtils {
	public static boolean canExecuteCommand (Console console, Method method) {
		return console.isExecuteHiddenCommandsEnabled() || !method
				.isAnnotationPresent(HiddenCommand.class);
	}

	public static boolean canDisplayCommand (Console console, Method method) {
		return console.isDisplayHiddenCommandsEnabled() || !method
				.isAnnotationPresent(HiddenCommand.class);
	}

}
