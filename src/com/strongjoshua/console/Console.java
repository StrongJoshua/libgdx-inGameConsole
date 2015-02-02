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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * A simple console that allows live logging, and live execution of methods, from within an application. Please see the
 * <a href="https://github.com/StrongJoshua/libgdx-inGameConsole">GitHub Repository</a> for more information.
 * 
 * @author StrongJoshua
 */
public class Console implements Disposable {
	/**
	 * Specifies the 'level' of a log entry. The level affects the color of the entry in the console and is also displayed next to the
	 * entry when the log entries are printed to a file with {@link Console#printLogToFile(String)}.
	 * 
	 * @author StrongJoshua
	 */
	public enum LogLevel {
		/**
		 * The default log level. Prints in white to the console and has no special indicator in the log file.<br>
		 * Intentional Use: debugging.
		 */
		DEFAULT(new Color(1, 1, 1, 1), ""),
		/**
		 * Use to print errors. Prints in red to the console and has the '<i>ERROR</i>' marking in the log file.<br>
		 * Intentional Use: printing internal console errors; debugging.
		 */
		ERROR(new Color(217f / 255f, 0, 0, 1), "Error: "),
		/**
		 * Prints in green. Use to print success notifications of events. Intentional Use: Print successful execution of console commands
		 * (if needed).
		 */
		SUCCESS(new Color(0, 217f / 255f, 0, 1), "Success! "),
		/**
		 * Prints in white with {@literal "> "} prepended to the command. Has that prepended text as the indicator in the log file. Intentional Use:
		 * To be used by the console, alone.
		 */
		COMMAND(new Color(1, 1, 1, 1), "> ");

		private Color color;
		private String identifier;

		LogLevel(Color c, String identity) {
			this.color = c;
			identifier = identity;
		}

		Color getColor() {
			return color;
		}

		String getIdentifier() {
			return identifier;
		}
	}

	private int keyID = Input.Keys.GRAVE;
	private boolean disabled;
	private Log log;
	private ConsoleDisplay display;
	private boolean hidden = true;
	private InputProcessor appInput;
	private InputMultiplexer multiplexer;
	private Stage stage;
	private CommandExecutor exec;

	/**
	 * Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this console
	 * uses a multiplexer to circumvent it).
	 * @param skin Uses skins for Label, TextField, and Table. Skin <b>must</b> contain a font called 'default-font'.
	 * @see Console#dispose()
	 */
	public Console(Skin skin) {
		stage = new Stage();
		log = new Log();
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		display = new ConsoleDisplay(skin, width / 2, height / 2);
		appInput = Gdx.input.getInputProcessor();
		if(appInput != null) {
			multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(stage);
			multiplexer.addProcessor(appInput);
			Gdx.input.setInputProcessor(multiplexer);
		}
		else
			Gdx.input.setInputProcessor(stage);

		display.setPosition(width / 2, height / 2);

		stage.addActor(display);
		stage.setKeyboardFocus(display);
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
		display.refresh();
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
	 * Prints all log entries to the given file. Log entries include logs in the code and commands made from within in the console while
	 * the program is running.<br>
	 * 
	 * <b>WARNING</b><br>
	 * The file that is sent to this function will be overwritten!
	 * 
	 * @param file The relative path to the file to print to. This method uses {@link Files#local(String)}.
	 */
	public void printLogToFile(String file) {
		this.printLogToFile(Gdx.files.local(file));
	}

	/**
	 * Prints all log entries to the given file. Log entries include logs in the code and commands made from within in the console while
	 * the program is running.<br>
	 * 
	 * <b>WARNING</b><br>
	 * The file that is sent to this function will be overwritten!
	 * 
	 * @param fh The {@link FileHandle} that links to the file to be written to. Note that <code>classpath</code> and <code>internal</code>
	 *            FileHandles cannot be written to.
	 */
	public void printLogToFile(FileHandle fh) {
		if(log.printToFile(fh))
			log("Successfully wrote logs to file.", LogLevel.SUCCESS);
		else
			log("Unable to write logs to file.", LogLevel.ERROR);
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
	 * @param code The new key's ID. Cannot be {@link Keys#ENTER}.
	 * @see Console#getKeyID()
	 */
	public void setKeyID(int code) {
		if(code == Keys.ENTER)
			return;
		keyID = code;
	}

	/**
	 * Sets this console's {@link CommandExecutor}. Its methods are the methods that are referenced within the console. Can be set to null,
	 * but this will result in no commands being fired.
	 * @param commandExec
	 */
	public void setCommandExecutor(CommandExecutor commandExec) {
		exec = commandExec;
		exec.setConsole(this);
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

		// attempt to convert arguments to numbers. If the conversion does not work, keep the argument as a string.
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
			if(methods[i].getName().equalsIgnoreCase(methodName))
				possible.add(i);
		}
		if(possible.size <= 0) {
			log("No such method found.", LogLevel.ERROR);
			return;
		}
		int size = possible.size;
		int numArgs;
		numArgs = args == null ? 0 : args.length;
		for(int i = 0; i < size; i++) {
			Method m = methods[possible.get(i)];
			Parameter[] params = m.getParameters();
			if(numArgs != params.length)
				continue;
			else {
				try {
					m.invoke(exec, args);
					return;
				} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					String msg = e.getMessage();
					if(msg == null || msg.length() <= 0 || msg.equals("")) {
						msg = "Unknown Error";
						e.printStackTrace();
					}
					log(msg, LogLevel.ERROR);
					return;
				}
			}
		}
		log("Bad parameters. Check your code.", LogLevel.ERROR);
	}

	public boolean hitsConsole(float screenX, float screenY) {
		if(disabled || hidden)
			return false;
		Vector3 stageCoords = stage.getCamera().unproject(new Vector3(screenX, screenY, 0));
		return stage.hit(stageCoords.x, stageCoords.y, true) != null;
	}

	private class ConsoleDisplay extends Table {

		private Table logEntries;
		private TextField input;
		private ScrollPane scroll;
		private Skin skin;

		protected ConsoleDisplay(Skin skin, float width, float height) {
			super(skin);

			this.setFillParent(false);
			this.skin = skin;
			this.setSize(width, height);

			logEntries = new Table(skin);

			input = new TextField("", skin);
			input.setTextFieldListener(new FieldListener());

			scroll = new ScrollPane(logEntries, skin);
			scroll.setFadeScrollBars(false);
			scroll.setScrollbarsOnTop(false);
			scroll.setOverscroll(false, false);

			this.add(scroll).expand().fill().row();
			this.add(input).expandX().fillX();
			this.addListener(new KeyListener(input));
		}

		protected void refresh() {
			Array<LogEntry> entries = log.getLogEntries();
			logEntries.clear();

			int size = entries.size;
			for(int i = 0; i < size; i++) {
				LogEntry le = entries.get(i);
				Label l = new Label(le.toConsoleString(), skin, "default-font", le.getColor());
				l.setWrap(true);
				Cell<Label> c = logEntries.add(l).expandX().fillX().top().left();
				if(i == size - 1)
					c.expand();
				c.row();
			}
			scroll.validate();
			scroll.setScrollPercentY(1);
		}
	}

	private class FieldListener implements TextFieldListener {
		@Override
		public void keyTyped(TextField textField, char c) {
			if(("" + c).equalsIgnoreCase(Keys.toString(keyID))) {
				String s = textField.getText();
				textField.setText(s.substring(0, s.length() - 1));
			}
		}
	}

	private class KeyListener extends InputListener {
		private TextField input;

		protected KeyListener(TextField tf) {
			input = tf;
		}

		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			if(keycode == Keys.ENTER) {
				String s = input.getText();
				if(s.length() == 0 || s.equals(""))
					return false;
				if(exec != null) {
					execCommand(input.getText());
				}
				else
					log("No command executor has been set. Please call setCommandExecutor for this console in your code and restart.",
							LogLevel.ERROR);
				input.setText("");
				return true;
			}
			else if(keycode == keyID) {
				hidden = !hidden;
				if(hidden) {
					input.setText("");
					stage.setKeyboardFocus(display);
					display.setTouchable(Touchable.disabled);
				}
				else {
					stage.setKeyboardFocus(input);
					display.setTouchable(Touchable.childrenOnly);
				}
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