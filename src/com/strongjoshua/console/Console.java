/**
 * Copyright 2015 StrongJoshua (strongjoshua@hotmail.com)
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * A simple console that allows live logging, and live execution of methods, from within an application. Please see the <a
 * href="https://github.com/StrongJoshua/libgdx-inGameConsole">GitHub Repository</a> for more information.
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
		 * Prints in white with {@literal "> "} prepended to the command. Has that prepended text as the indicator in the log file.
		 * Intentional Use: To be used by the console, alone.
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

	/**
	 * Use to set the amount of entries to be stored to unlimited.
	 */
	public static final int UNLIMITED_ENTRIES = -1;

	private int keyID = Input.Keys.GRAVE;
	private boolean disabled;
	private Log log;
	private ConsoleDisplay display;
	private boolean hidden = true;
	private boolean usesMultiplexer = false;
	private InputProcessor appInput;
	private InputMultiplexer multiplexer;
	private Stage stage;
	private CommandExecutor exec;
	private CommandHistory commandHistory;
	private CommandCompleter commandCompleter;
	private Window consoleWindow;
	private Boolean logToSystem;

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
	 * Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this console
	 * uses a multiplexer to circumvent it).
	 * @param skin Uses skins for Label, TextField, and Table. Skin <b>must</b> contain a font called 'default-font'.
	 * @see Console#dispose()
	 */
	public Console(Skin skin) {
		this(skin, true);
	}

	/**
	 * Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this console
	 * uses a multiplexer to circumvent it).
	 * @param useMultiplexer If internal multiplexer should be used
	 * @see Console#dispose()
	 */
	public Console(boolean useMultiplexer) {
		this(new Skin(Gdx.files.classpath("default_skin/uiskin.json")), useMultiplexer);
	}

	/**
	 * Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this console
	 * uses a multiplexer to circumvent it).
	 * @param skin Uses skins for Label, TextField, and Table. Skin <b>must</b> contain a font called 'default-font'.
	 * @param useMultiplexer If internal multiplexer should be used
	 * @see Console#dispose()
	 */
	public Console(Skin skin, boolean useMultiplexer) {
		stage = new Stage();
		log = new Log();
		display = new ConsoleDisplay(skin);
		commandHistory = new CommandHistory();
		commandCompleter = new CommandCompleter();
		logToSystem = false;
	
		usesMultiplexer = useMultiplexer;
		if(useMultiplexer) {
			resetInputProcessing();
		}
		
	
		display.pad(4);
		display.padTop(22);
		display.setFillParent(true);

		consoleWindow = new Window("Console", skin);
		consoleWindow.setMovable(true);
		consoleWindow.setResizable(true);
		consoleWindow.setKeepWithinStage(true);
		consoleWindow.addActor(display);
	
		stage.addActor(consoleWindow);
		stage.setKeyboardFocus(display);
		
		setSizePercent(50, 50);
		setPositionPercent(50, 50);
	}

	/**
	 * @param numEntries maximum number of entries the console will hold.
	 */
	public void setMaxEntries(int numEntries) {
		if(numEntries > 0 || numEntries == UNLIMITED_ENTRIES)
			log.setMaxEntries(numEntries);
		else
			throw new IllegalArgumentException("Maximum entries must be greater than 0 or use Console.UNLIMITED_ENTRIES.");
	}

	/**
	 * Clears all log entries.
	 */
	public void clear() {
		log.getLogEntries().clear();
		display.refresh();
	}

	/**
	 * Set size of the console in pixels
	 * @param width width of the console in pixels
	 * @param height height of the console in pixels
	 */
	public void setSize(int width, int height) {
		if(width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Pixel size must be greater than 0.");
		}
		consoleWindow.setSize(width, height);
	}
	
	/**
	 * Sets if the console should also log to System.out
	 * @param log to the system
	 */
	public void setLoggingToSystem(Boolean log)
	{
		this.logToSystem = log;
	}

	/**
	 * Set size of the console as a percent of screen size
	 * @param widthPct width of the console as a percent of screen width
	 * @param heightPct height of the console as a percent of screen height
	 */
	public void setSizePercent(int widthPct, int heightPct) {
		if(widthPct <= 0 || heightPct <= 0) {
			throw new IllegalArgumentException("Size percentage must be greater than 0.");
		}
		if(widthPct > 100 || heightPct > 100) {
			throw new IllegalArgumentException("Size percentage cannot be greater than 100.");
		}
		int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		consoleWindow.setSize((int) (w * widthPct / 100.0f), (int) (h * heightPct / 100.0f));
	}

	/**
	 * Set position of the lower left corner of the console
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		consoleWindow.setPosition(x, y);
	}

	/**
	 * Set position of the lower left corner of the console as a percent of screen size
	 * 
	 */
	public void setPositionPercent(int xPct, int yPct) {
		if(xPct > 100 || yPct > 100) {
			throw new IllegalArgumentException("Error: The console would be drawn outside of the screen.");
		}
		int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		consoleWindow.setPosition((int) (w * xPct / 100.0f), (int) (h * yPct / 100.0f));
	}

	/**
	 * Call this method if you changed the input processor while this console was active.
	 */
	public void resetInputProcessing() {
		usesMultiplexer = true;
		appInput = Gdx.input.getInputProcessor();
		if(appInput != null) {
			if(hasStage(appInput)) {
				log("Console already added to input processor!", LogLevel.ERROR);
				Gdx.app.log("Console", "Already added to input processor!");
				return;
			}
			multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(stage);
			multiplexer.addProcessor(appInput);
			Gdx.input.setInputProcessor(multiplexer);
		}
		else
			Gdx.input.setInputProcessor(stage);
	}

	/**
	 * Compares the given processor to the console's stage. If given a multiplexer, it is iterated through recursively to check all of the
	 * multiplexer's processors for comparison.
	 * @param processor
	 * @return processor == this.stage
	 */
	private boolean hasStage(InputProcessor processor) {
		if(!(processor instanceof InputMultiplexer)) {
			return processor == stage;
		}
		InputMultiplexer im = (InputMultiplexer) processor;
		Array<InputProcessor> ips = im.getProcessors();
		for(InputProcessor ip : ips) {
			if(hasStage(ip)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return {@link InputProcessor} for this {@link Console}
	 */
	public InputProcessor getInputProcessor() {
		return stage;
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
	 * Calls {@link Console#refresh(boolean)} with true.
	 */
	public void refresh() {
		this.refresh(true);
	}
	
	/**
	 * Refreshes the console's stage. Use if the app's window size was changed.
	 * @param retain True if you want position and size percentages to be kept.
	 */
	public void refresh(boolean retain) {
		int oldWPct = 0, oldHPct = 0, oldXPosPct = 0, oldYPosPct = 0;
		if(retain) {
			oldWPct = (int) (consoleWindow.getWidth() / stage.getWidth() * 100);
			oldHPct = (int) (consoleWindow.getHeight() / stage.getHeight() * 100);
			oldXPosPct = (int) (consoleWindow.getX() / stage.getWidth() * 100);
			oldYPosPct = (int) (consoleWindow.getY() / stage.getHeight() * 100);
		}
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		stage.getViewport().setWorldSize(width, height);
		stage.getViewport().update(width, height, true);
		if(retain) {
			this.setSizePercent(oldWPct, oldHPct);
			this.setPositionPercent(oldXPosPct, oldYPosPct);
		}
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
		
		if(logToSystem)
		{
			System.out.println(msg);
		}
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
		if(disabled && !hidden)
			((KeyListener) display.getListeners().get(0)).keyDown(null, keyID);
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
		Method[] methods = ClassReflection.getMethods(clazz);
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
			Class<?>[] params = m.getParameterTypes();
			if(numArgs != params.length)
				continue;
			else {
				try {
					m.invoke(exec, args);
					return;
				} catch(ReflectionException e) {
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

	private Vector3 stageCoords = new Vector3();

	/**
	 * Returns if the given screen coordinates hit the console.
	 * @param screenX
	 * @param screenY
	 * @return True, if the console was hit.
	 */
	public boolean hitsConsole(float screenX, float screenY) {
		if(disabled || hidden)
			return false;
		stage.getCamera().unproject(stageCoords.set(screenX, screenY, 0));
		return stage.hit(stageCoords.x, stageCoords.y, true) != null;
	}

	private class ConsoleDisplay extends Table {
		private Table logEntries;
		private TextField input;
		private Skin skin;
		private Array<Label> labels;

		protected ConsoleDisplay(Skin skin) {
			super(skin);

			this.setFillParent(false);
			this.skin = skin;

			labels = new Array<Label>();

			logEntries = new Table(skin);
			

			input = new TextField("", skin);
			input.setTextFieldListener(new FieldListener());
			

			scroll = new ScrollPane(logEntries, skin);
			scroll.setFadeScrollBars(false);
			scroll.setScrollbarsOnTop(false);
			scroll.setOverscroll(false, false);

			this.add(scroll).expand().fill().pad(4).row();;
			this.add(input).expandX().fillX().pad(4);
			this.addListener(new KeyListener(input));
		}

		protected void refresh() {
			Array<LogEntry> entries = log.getLogEntries();
			logEntries.clear();

			// expand first so labels start at the bottom
			logEntries.add().expand().fill().row();
			int size = entries.size;
			for(int i = 0; i < size; i++) {
				LogEntry le = entries.get(i);
				Label l;
				// recycle the labels so we don't create new ones every refresh
				if(labels.size > i) {
					l = labels.get(i);
				}
				else {
					l = new Label("", skin, "default-font", LogLevel.DEFAULT.getColor());
					l.setWrap(true);
					labels.add(l);
				}
				l.setText(le.toConsoleString());
				l.setColor(le.getColor());
				logEntries.add(l).expandX().fillX().top().left().padLeft(4).row();
			}
			scroll.validate();
			scroll.setScrollPercentY(1);
		}
	}

	private ScrollPane scroll;

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
			if(disabled)
				return false;
			
			// reset command completer because input string may have changed
			if(keycode != Keys.TAB) {
				commandCompleter.reset();
			}

			if(keycode == Keys.ENTER) {
				String s = input.getText();
				if(s.length() == 0 || s.equals("") || s.split(" ").length == 0)
					return false;
				if(exec != null) {
					commandHistory.store(s);
					execCommand(s);
				}
				else
					log("No command executor has been set. Please call setCommandExecutor for this console in your code and restart.",
							LogLevel.ERROR);
				input.setText("");
				return true;
			}
			else if(keycode == Keys.UP) {
				input.setText(commandHistory.getPreviousCommand());
				input.setCursorPosition(input.getText().length());
				return true;
			}
			else if(keycode == Keys.DOWN) {
				input.setText(commandHistory.getNextCommand());
				input.setCursorPosition(input.getText().length());
				return true;
			}
			else if(keycode == Keys.TAB) {
				String s = input.getText();
				if(s.length() == 0)
					return false;
				if(commandCompleter.isNew()) {
					commandCompleter.set(exec, s);
				}
				input.setText(commandCompleter.next());
				input.setCursorPosition(input.getText().length());
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
		if(usesMultiplexer && appInput != null) {
			Gdx.input.setInputProcessor(appInput);
		}
		stage.dispose();
	}

	/**
	 * @return If console is hidden
	 */
	public boolean isHidden() {
		return hidden;
	}


}