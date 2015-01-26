package com.strongjoshua.console;

import com.badlogic.gdx.utils.Array;
import com.strongjoshua.console.Console.LogLevel;

class Log {
	private Array<LogEntry> logEntries;
	
	Log() {
		logEntries = new Array<LogEntry>();
	}
	
	void addEntry(String msg, LogLevel level) {
		logEntries.add(new LogEntry(msg, level));
	}
}