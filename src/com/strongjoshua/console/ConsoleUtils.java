/**
 * 
 */
package com.strongjoshua.console;

import com.badlogic.gdx.utils.reflect.Method;

/**
 * @author Eric
 *
 */
public final class ConsoleUtils {
	
	public static boolean canExecuteCommand(Console console, Method method) {
		if(!console.isExecuteHiddenCommandsEnabled() && method.isAnnotationPresent(HiddenCommand.class)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean canDisplayCommand(Console console, Method method) {
		if(!console.isDisplayHiddenCommandsEnabled() && method.isAnnotationPresent(HiddenCommand.class)) {
			return false;
		}
		
		return true;
	}

}
