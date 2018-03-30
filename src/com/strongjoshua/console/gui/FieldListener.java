package com.strongjoshua.console.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.strongjoshua.console.Console;

public class FieldListener implements TextFieldListener {
	private Console console;
	private String lastText;
	private int lastCursorPosition = 0;
	
	public FieldListener(Console console) {
		this.console = console;
	}
	
	@Override
	public void keyTyped(TextField textField, char c) {
		if(!console.hasFocus()) {
			textField.setText(lastText);
			textField.setCursorPosition(lastCursorPosition);
		}
		else if (("" + c).equalsIgnoreCase(Keys.toString(console.getDisplayKeyID()))) {
			String s = textField.getText();
			textField.setText(s.substring(0, s.length() - 1));
		}
		else {
			lastCursorPosition = textField.getCursorPosition();
			lastText = textField.getText();
		}
	}
}