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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.io.Writer;

public class Log {
	private Array<LogEntry> logEntries;
	private int numEntries = Console.UNLIMITED_ENTRIES;

	protected Log () {
		logEntries = new Array<LogEntry>();
	}

	public void setMaxEntries (int numEntries) {
		this.numEntries = numEntries;
	}

	protected void addEntry (String msg, LogLevel level) {
		logEntries.add(new LogEntry(msg, level));
		if (logEntries.size > numEntries && numEntries != Console.UNLIMITED_ENTRIES) {
			logEntries.removeIndex(0);
		}
	}

	protected Array<LogEntry> getLogEntries () {
		return logEntries;
	}

	public boolean printToFile (FileHandle fh) {
		if (fh.isDirectory()) {
			throw new IllegalArgumentException("File cannot be a directory!");
		}

		Writer out = null;
		try {
			out = fh.writer(false);
		} catch (Exception e) {
			return false;
		}

		String toWrite = "";
		for (LogEntry l : logEntries) {
			toWrite += l.toString() + "\n";
		}

		try {
			out.write(toWrite);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
