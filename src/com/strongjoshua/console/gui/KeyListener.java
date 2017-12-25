package com.strongjoshua.console.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.strongjoshua.console.CommandHistory;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.LogLevel;

public class KeyListener extends InputListener {
	private Console console;
	private TextField input;
	private	CommandHistory commandHistory;
	private CommandCompleter commandCompleter;

	public KeyListener(Console console, TextField tf) {
		this.console = console;
		this.input = tf;
		
		commandHistory = new CommandHistory();
		commandCompleter = new CommandCompleter();
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		if (console.isDisabled()) return false;
		if (!console.hasFocus()) return false;
		
		// reset command completer because input string may have changed
		if (keycode != Keys.TAB) {
			commandCompleter.reset();
		}

		if (keycode == Keys.ENTER && !console.isVisible()) {
			String s = input.getText();
			if (s.length() == 0 || s.split(" ").length == 0) {
				return false;
			}
			if (console.getCommandExecutor() != null) {
				commandHistory.store(s);
				console.execCommand(s);
			} else {
				console.log("No command executor has been set. Please call setCommandExecutor for this console in your code and restart.",
						LogLevel.ERROR);
			}
			input.setText("");
			return true;
		} else if (keycode == Keys.UP && console.isVisible()) {
			input.setText(commandHistory.getPreviousCommand());
			input.setCursorPosition(input.getText().length());
			return true;
		} else if (keycode == Keys.DOWN && console.isVisible()) {
			input.setText(commandHistory.getNextCommand());
			input.setCursorPosition(input.getText().length());
			return true;
		} else if (keycode == Keys.TAB && console.isVisible()) {
			String s = input.getText();
			if (s.length() == 0) {
				return false;
			}
			if (commandCompleter != null) {
				if (console.getCommandExecutor() != null && commandCompleter.isNew()) {
					commandCompleter.set(console.getCommandExecutor(), s);
				}
				String nextStr = commandCompleter.next();
				if (nextStr != null) {
					input.setText(nextStr);
					input.setCursorPosition(input.getText().length());
				}
			}
			return true;
		}
		return false;
	}
}