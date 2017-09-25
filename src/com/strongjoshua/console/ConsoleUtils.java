/**
 *
 */

package com.strongjoshua.console;

import com.badlogic.gdx.utils.reflect.Method;
import com.strongjoshua.console.annotation.HiddenCommand;

/** @author Eric */
public final class ConsoleUtils {
	public static boolean canExecuteCommand (Console console, Method method) {
		return console.isExecuteHiddenCommandsEnabled() || !method.isAnnotationPresent(HiddenCommand.class);
	}

	public static boolean canDisplayCommand (Console console, Method method) {
		return console.isDisplayHiddenCommandsEnabled() || !method.isAnnotationPresent(HiddenCommand.class);
	}

	public static String exceptionToString (final Throwable throwable) {
		StringBuilder result = new StringBuilder();
		Throwable cause = throwable;

		while (cause != null) {
			if (result.length() == 0) {
				result.append("\nException in thread \"").append(Thread.currentThread().getName()).append("\" ");
			} else {
				result.append("\nCaused by: ");
			}
			result.append(cause.getClass().getCanonicalName()).append(": ").append(cause.getMessage());

			for (final StackTraceElement traceElement : cause.getStackTrace()) {
				result.append("\n\tat ").append(traceElement.toString());
			}
			cause = cause.getCause();
		}
		return result.toString();
	}
}
