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

public class CommandHistory {
	private final Array<String> commands = new Array<String>(true, 20);
	private int index;

	public void store (String command) {
		if (commands.size > 0 && isLastCommand(command)) {
			return;
		}
		commands.insert(0, command);
		indexAtBeginning();
	}

	public String getPreviousCommand () {
		index++;

		if (commands.size == 0) {
			indexAtBeginning();
			return "";
		} else if (index >= commands.size) {
			index = 0;
		}

		return commands.get(index);
	}

	public String getNextCommand () {
		index--;
		if (commands.size <= 1 || index < 0) {
			indexAtBeginning();
			return "";
		}
		return commands.get(index);
	}

	private boolean isLastCommand (String command) {
		return command.equals(commands.first());
	}

	private void indexAtBeginning () {
		index = -1;
	}
}
