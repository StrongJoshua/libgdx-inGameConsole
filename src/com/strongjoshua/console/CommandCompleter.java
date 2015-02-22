package com.strongjoshua.console;

import java.lang.reflect.Method;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;

class CommandCompleter {
	private ObjectSet<String> possibleCommands;
	private ObjectSetIterator<String> iterator;
	private String setString;

	public CommandCompleter() {
		possibleCommands = new ObjectSet<>();
		setString = "";
	}

	public void set(CommandExecutor ce, String s) {
		reset();
		setString = s.toLowerCase();
		Array<Method> methods = getAllMethodsOn(ce);
		for(Method m : methods) {
			String name = m.getName();
			if(name.toLowerCase().startsWith(setString))
				possibleCommands.add(name);
		}
		iterator = new ObjectSetIterator<>(possibleCommands);
	}

	public void reset() {
		possibleCommands.clear();
		setString = "";
		iterator = null;
	}

	public boolean isNew() {
		return possibleCommands.size == 0;
	}

	public boolean wasSetWith(String s) {
		return setString.equalsIgnoreCase(s);
	}

	public String next() {
		if(!iterator.hasNext) {
			iterator.reset();
			return setString;
		}
		return iterator.next();
	}

	private Array<Method> getAllMethodsOn(CommandExecutor ce) {
		Array<Method> methods = new Array<>();
		for (Method method : ce.getClass().getDeclaredMethods()) {
			methods.add(method);
		}

		for (Method method : ce.getClass().getSuperclass().getDeclaredMethods()) {
			methods.add(method);
		}

		return methods;
	}
}