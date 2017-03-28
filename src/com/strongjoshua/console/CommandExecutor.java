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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;

/** Extend this class (child must be <code>public</code>) and fill it with methods (also <code>public</code>) that you wish to
 * have work with the {@link Console}. Then call {@link Console#setCommandExecutor(CommandExecutor)}.<br>
 * <br>
 * <b>Notes</b><br>
 * <ul>
 * <li>Arguments <i><b>must</b></i> be primitive types (the only exception being {@link String}).</li>
 * <li>No two methods, of the same name, can have the same number of parameters. Make multiple methods with more specific names if
 * they must have the same number of parameters.</li>
 * <li>Methods are case-<b>insensitive</b> when invoked from the console.</li>
 * </ul>
 *
 * @author StrongJoshua */
public abstract class CommandExecutor {
	protected Console console;

	protected void setConsole (Console c) {
		console = c;
	}

	/** Prints the log to a local file.
	 * @param path The relative path of the local file to print to. */
	public final void printLog (String path) {
		console.printLogToFile(path);
	}

	/** Closes the application completely. */
	public final void exitApp () {
		Gdx.app.exit();
	}

	/** Shows all available methods, and their parameter types, in the console. */
	public final void help () {
		console.printCommands();
	}
}
