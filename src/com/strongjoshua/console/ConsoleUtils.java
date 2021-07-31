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

import com.badlogic.gdx.utils.reflect.Method;
import com.strongjoshua.console.annotation.HiddenCommand;

/**
 * @author Eric
 */
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
			result.append("\nCaused by: ")
					.append(cause.getClass().getCanonicalName())
					.append(": ")
					.append(cause.getMessage());

			for (final StackTraceElement traceElement : cause.getStackTrace()) {
				result.append("\n\tat ").append(traceElement.toString());
			}
			cause = cause.getCause();
		}
		return result.toString();
	}
}
