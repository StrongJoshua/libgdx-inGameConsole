package com.strongjoshua.console;

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
		 * The default log level. Prints in white to the console and has no special indicator in the log file.
		 */
		DEFAULT(new Color(1, 1, 1, 1)),
		/**
		 * Use to print errors. Prints in red to the console and has the '<i>ERROR</i>' marking in the log file.
		 */
		ERROR(new Color(1, 0, 0, 1));

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
		logArea.appendText(msg + "\n");
	}

	/**
	 * Logs a new entry to the console using {@link LogLevel#DEFAULT}.
	 * @param msg The message to be logged.
	 * @see LogLevel
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
				log(input.getText());
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