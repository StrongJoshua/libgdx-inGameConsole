package com.strongjoshua.console;

import com.strongjoshua.console.Console.LogLevel;

class LogEntry {
	private String text;
	private LogLevel level;
	
	public LogEntry(String msg, LogLevel level) {
		this.text = msg;
		this.level = level;
	}
}