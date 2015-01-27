/**
 * Copyright 2015 StrongJoshua (swampert_555@yahoo.com)
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.NumberFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Console implements Disposable {
	/**
	 * Specifies the 'level' of a log entry. The level affects the color of the entry in the console and is also displayed next to the entry
	 * when the log entries are printed to a file with {@link Console#printLogToFile(String)}.
	 * 
	 * @author StrongJoshua
	 */
	public enum LogLevel {
		/**
		 * The default log level. Prints in white to the console and has no special indicator in the log file.<br>
		 * Intentional Use: debugging.
		 */
		DEFAULT(new Color(1, 1, 1, 1)),
		/**
		 * Use to print errors. Prints in red to the console and has the '<i>ERROR</i>' marking in the log file.<br>
		 * Intentional Use: printing internal console errors; debugging.
		 */
		ERROR(new Color(1, 0, 0, 1)),
		/**
		 * Prints in white with "> " prepended to the command. Has that prepended text as the indicator in the log file.
		 * Intentional Use: To be used by the console, alone.
		 */
		COMMAND(new Color(1, 1, 1, 1));

		private Color color;

		LogLevel(Color c) {
			this.color = c;
		}

		Color getColor() {
			return color;
		}
	}

	private int keyID = Input.Keys.GRAVE;
	private boolean disabled;
	private Log log;
	private TextField input;
	private TextArea logArea;
	private ConsoleDisplay display;
	private boolean hidden = true;
	private InputProcessor appInput;
	private InputMultiplexer multiplexer;
	private OrthographicCamera consoleCamera;
	private Stage stage;
	private CommandExecutor exec;

	/**
	 * Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this console
	 * uses a multiplexer to circumvent it).
	 * @param skin Uses skins for TextField, TextArea, and Table.
	 * @see Console#dispose()
	 */
	public Console(Skin skin) {
		log = new Log();
		input = new TextField("", skin);
		input.setTextFieldListener(new FieldListener());
		input.addListener(new EnterListener());
		logArea = new TextArea("", skin);
		logArea.setPrefRows(20);
		logArea.setDisabled(true);
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		display = new ConsoleDisplay(skin, width / 2);
		display.pack();
		display.addListener(new KeyIDListener());
		stage = new Stage();
		appInput = Gdx.input.getInputProcessor();
		if(appInput != null) {
			multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(stage);
			multiplexer.addProcessor(appInput);
			Gdx.input.setInputProcessor(multiplexer);
		}
		else
			Gdx.input.setInputProcessor(stage);

		consoleCamera = new OrthographicCamera(width, height);
		consoleCamera.position.set(consoleCamera.viewportWidth / 2, consoleCamera.viewportHeight / 2, 0);
		consoleCamera.update();
		stage.setViewport(new ScreenViewport(consoleCamera));
		display.setPosition(width - display.getWidth(), height - display.getHeight());
		stage.addActor(display);
		stage.setKeyboardFocus(display);
		this.log("Console has been set up");
	}

	/**
	 * Creates the console using the default skin.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this console
	 * uses a multiplexer to circumvent it).
	 * @see Console#dispose()
	 */
	public Console() {
		this(new Skin(Gdx.files.classpath("default_skin/uiskin.json")));
	}

	/**
	 * Draws the console.
	 */
	public void draw() {
		if(disabled)
			return;
		stage.act();
		
		if(hidden)
			return;
		stage.draw();
	}

	/**
	 * Logs a new entry to the console.
	 * @param msg The message to be logged.
	 * @param level The {@link LogLevel} of the log entry.
	 * @see LogLevel
	 */
	public void log(String msg, LogLevel level) {
		log.addEntry(msg, level);
		if(level.equals(LogLevel.COMMAND))
			msg = "> " + msg;
		logArea.appendText(msg + "\n");
	}

	/**
	 * Logs a new entry to the console using {@link LogLevel#DEFAULT}.
	 * @param msg The message to be logged.
	 * @see LogLevel
	 * @see Console#log(String, LogLevel)
	 */
	public void log(String msg) {
		this.log(msg, LogLevel.DEFAULT);
	}

	/**
	 * Prints all log entries to the given file. Log entries include logs in the code and commands from within in the console while the
	 * program is running.
	 * @param file The relative path to the file to print to.
	 */
	public void printLogToFile(String file) {

	}

	/**
	 * @return If the console is disabled.
	 * @see Console#setDisabled(boolean)
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * 
	 * @param disabled True if the console should be disabled (unable to be shown or used). False otherwise.
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Gets the console's display key. If the console is enabled, the console will be shown upon this key being pressed.<br>
	 * Default key is <b>`</b> a.k.a. '<b>backtick</b>'.
	 * @return the keyID
	 */
	public int getKeyID() {
		return keyID;
	}

	/**
	 * @param keyID The new key's ID.
	 * @see Console#getKeyID()
	 */
	public void setKeyID(int keyID) {
		this.keyID = keyID;
	}
	
	/**
	 * Sets this consoles {@link CommandExecutor}. Its methods are the methods that are referenced within the console.
	 * Can be set to null, but this will result in no commands being fired.
	 * @param commandExec
	 */
	public void setCommandExecutor(CommandExecutor commandExec) {
		exec = commandExec;
	}
	
	private void execCommand(String command) {
		log(command, LogLevel.COMMAND);
		
		String[] parts = command.split(" ");
		String methodName = parts[0];
		String[] sArgs = null;
		if(parts.length > 1) {
			sArgs = new String[parts.length - 1];
			for(int i = 1; i < parts.length; i++) {
				sArgs[i - 1] = parts[i];
			}
		}
		
		//attempt to convert arguments to numbers. If the conversion does not work, keep the argument as a string
		Object[] args = null;
		if(sArgs != null) {
			args = new Object[sArgs.length];
			for(int i = 0; i < sArgs.length; i++) {
				String s = sArgs[i];
				try {
					int j = Integer.parseInt(s);
					args[i] = j;
				} catch(NumberFormatException e) {
					try {
						float f = Float.parseFloat(s);
						args[i] = f;
					} catch(NumberFormatException e2) {
						args[i] = s;
					}
				}
			}
		}
		
		Class<? extends CommandExecutor> clazz = exec.getClass();
		Method[] methods = clazz.getMethods();
		Array<Integer> possible = new Array<Integer>();
		for(int i = 0; i < methods.length; i++) {
			if(methods[i].getName().equals(methodName)) possible.add(i);
		}
		if(possible.size <= 0) {
			log("No such method found.", LogLevel.ERROR);
			return;
		}
		int size = possible.size;
		for(int i = 0; i < size; i++) {
			Method m = methods[possible.get(i)];
			Parameter[] params = m.getParameters();
			if(args == null && params.length == 0) {
				//try to invoke
				try {
					m.invoke(exec, null);
					return;
				} catch(Exception e) {
					String msg = e.getMessage();
					if(msg == null || msg.length() <= 0 || msg.equals(""))
						msg = "Unknown Error";
					log(msg, LogLevel.ERROR);
					return;
				}
			}
			else if((args == null && params.length > 0) || (args.length != params.length))
				continue;
			else {
				//loop through parameters until match
				boolean match = true;
				for(int j = 0; j < params.length; j++) {
					Parameter p = params[j];
					Object arg = args[i];
					if(!p.getClass().equals(arg.getClass())) {
						match = false;
						break;
					}
				}
				if(match) {
					try {
						m.invoke(exec, args);
						return;
					} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						String msg = e.getMessage();
						if(msg == null || msg.length() <= 0 || msg.equals(""))
							msg = "Unknown Error";
						log(msg, LogLevel.ERROR);
						return;
					}
				}
			}
		}
		log("Bad parameters. Check your code.", LogLevel.ERROR);
	}

	private class ConsoleDisplay extends Table {
		public ConsoleDisplay(Skin skin, float w) {
			super(skin);
			this.setTouchable(Touchable.childrenOnly);
			this.clear();
			this.pad(0);
			this.add(logArea).expand().fill().width(w).row();
			this.add(input).expand().fill().width(w);
			this.setSize(this.getPrefWidth(), this.getPrefHeight());
		}
	}
	
	private class FieldListener implements TextFieldListener {
		@Override
		public void keyTyped(TextField textField, char c) {
			if(c == '`') {
				String s = textField.getText();
				textField.setText(s.substring(0, s.length() - 1));
			}
		}
	}
	
	private class EnterListener extends InputListener {
		@Override
		public boolean keyUp (InputEvent event, int keycode) {
			if(keycode == Input.Keys.ENTER) {
				String s = input.getText();
				if(s.length() == 0 || s.equals(""))
					return false;
				if(exec != null) {
					execCommand(input.getText());
				}
				else
					log("No command executor has been set. Please call setCommandExecutor for this console in your code and restart.", LogLevel.ERROR);
				input.setText("");
				return true;
			}
			return false;
		}
	}

	private class KeyIDListener extends InputListener {
		@Override
		public boolean keyUp (InputEvent event, int keycode) {
			if(keycode == keyID) {
				hidden = !hidden;
				if(hidden) {
					input.setText("");
					stage.setKeyboardFocus(display);
				}
				else
					stage.setKeyboardFocus(input);
				return true;
			}
			return false;
		}
	}

	/**
	 * Resets the {@link InputProcessor} to the one that was the default before this console object was created.
	 */
	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(appInput);
		stage.dispose();
	}
}