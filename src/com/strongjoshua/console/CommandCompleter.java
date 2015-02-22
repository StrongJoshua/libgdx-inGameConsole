package com.strongjoshua.console;

import java.lang.reflect.Method;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;

class CommandCompleter {
	private ObjectSet<String> possibleCommands;
	private ObjectSetIterator<String> iterator;
	private String setString;

	public CommandCompleter() {
		possibleCommands = new ObjectSet<String>();
		setString = "";
	}

	public void set(CommandExecutor ce, String s) {
		reset();
		setString = s.toLowerCase();
		Method[] methods = ce.getClass().getDeclaredMethods();
		for(Method m : methods) {
			String name = m.getName();
			if(name.toLowerCase().startsWith(setString))
				possibleCommands.add(name);
		}
		iterator = new ObjectSetIterator<String>(possibleCommands);
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
		if(!iterator.hasNext)
			iterator.reset();
		return iterator.next();
	}
}