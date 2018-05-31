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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;

public class CommandCompleter {
	private ObjectSet<String> possibleCommands;
	private ObjectSetIterator<String> iterator;
	private String setString;

	public CommandCompleter () {
		possibleCommands = new ObjectSet<>();
		setString = "";
	}

	public void set (CommandExecutor ce, String s) {
		reset();
		setString = s.toLowerCase();
		Array<Method> methods = getAllMethods(ce);
		for (Method m : methods) {
			String name = m.getName();
			if (name.toLowerCase().startsWith(setString) && ConsoleUtils.canDisplayCommand(ce.console, m)) {
				possibleCommands.add(name);
			}
		}
		iterator = new ObjectSetIterator<>(possibleCommands);
	}

	public void reset () {
		possibleCommands.clear();
		setString = "";
		iterator = null;
	}

	public boolean isNew () {
		return possibleCommands.size == 0;
	}

	public boolean wasSetWith (String s) {
		return setString.equalsIgnoreCase(s);
	}

	public String next () {
		if (!iterator.hasNext) {
			iterator.reset();
			return setString;
		}
		return iterator.next();
	}

	private Array<Method> getAllMethods (CommandExecutor ce) {
		Array<Method> methods = new Array<>();
		Method[] ms = ClassReflection.getDeclaredMethods(ce.getClass());
		for (Method m : ms) {
			if (m.isPublic()) {
				methods.add(m);
			}
		}
		ms = ClassReflection.getDeclaredMethods(ce.getClass().getSuperclass());
		for (Method m : ms) {
			if (m.isPublic()) {
				methods.add(m);
			}
		}

		return methods;
	}
}
