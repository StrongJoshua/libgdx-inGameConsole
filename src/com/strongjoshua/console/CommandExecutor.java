/**
 * Copyright 2015 StrongJoshua (strongjoshua@hotmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.strongjoshua.console;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import com.badlogic.gdx.Gdx;

/**
 * Extend this class (child must be <code>public</code>) and fill it with methods (also <code>public</code>) that you wish to have work with
 * the {@link Console}. Then call {@link Console#setCommandExecutor(CommandExecutor)}.<br>
 * <br>
 * <b>Notes</b><br>
 * <ul>
 * <li>Arguments <i><b>must</b></i> be primitive types (the only exception being {@link String}).</li>
 * <li>No two methods, of the same name, can have the same number of parameters. Make multiple methods with more specific names if they
 * must have the same number of parameters.</li>
 * <li>Methods are case-<b>insensitive</b> when invoked from the console.</li>
 * </ul>
 * 
 * @author StrongJoshua
 */
public abstract class CommandExecutor {
	private Console console;

	protected void setConsole(Console c) {
		console = c;
	}

	/**
	 * Prints the log to a local file.
	 * @param path The relative path of the local file to print to.
	 */
	public final void printLog(String path) {
		console.printLogToFile(path);
	}

	/**
	 * Closes the application completely.
	 */
	public final void exitApp() {
		Gdx.app.exit();
	}

	/**
	 * Shows all available methods, and their parameter types, in the console.
	 */
	public final void help() {
		Method[] methods = this.getClass().getDeclaredMethods();
		String s = "";
		for(int j = 0; j < methods.length; j++) {
			Method m = methods[j];
			if(Modifier.isPublic(m.getModifiers())) {
				s += m.getName();
				s += " : ";

				Parameter[] params = m.getParameters();
				for(int i = 0; i < params.length; i++) {
					s += params[i].getType().getSimpleName();
					if(i < params.length - 1)
						s += ", ";
				}
			}
			if(j < methods.length - 1)
				s += "\n";
		}

		console.log(s);
	}
}