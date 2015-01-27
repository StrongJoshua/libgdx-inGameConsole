package com.strongjoshua.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Disposable;

public class Console implements InputProcessor, Disposable {
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
	private Matrix4 consoleMatrix;
	private SpriteBatch batch;

	/**
	 * Creates the console.
	 * @param skin Uses skins for TextField, TextArea, and Table.
	 */
	public Console(Skin skin) {
		log = new Log();
		input = new TextField("", skin);
		logArea = new TextArea("", skin);
		logArea.setPrefRows(20);
		display = new ConsoleDisplay(skin);
		display.pack();
		appInput = Gdx.input.getInputProcessor();
		if(appInput != null) {
			multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(this);
			multiplexer.addProcessor(appInput);
			Gdx.input.setInputProcessor(multiplexer);
		}
		else
			Gdx.input.setInputProcessor(this);
		
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		OrthographicCamera tmp = new OrthographicCamera(width, height);
		tmp.position.set(tmp.viewportWidth / 2, tmp.viewportHeight / 2, 0);
		tmp.update();
		consoleMatrix = tmp.combined;
		batch = new SpriteBatch();
		batch.setProjectionMatrix(consoleMatrix);
		display.setPosition(width - display.getWidth(), height - display.getHeight());
	}
	
	/**
	 * Creates the console using the default skin.
	 */
	public Console() {
		this(new Skin(Gdx.files.classpath("default_skin/uiskin.json")));
	}

	/**
	 * Draws the console.
	 */
	public void draw() {
		if(disabled || hidden) return;
		
		batch.begin();
		display.draw(batch, 1);
		batch.end();
	}

	/**
	 * Logs a new entry to the console.
	 * @param msg The message to be logged.
	 * @param level The {@link LogLevel} of the log entry.
	 * @see LogLevel
	 */
	public void log(String msg, LogLevel level) {
		log.addEntry(msg, level);
	}
	
	/**
	 * Logs a new entry to the console using {@link LogLevel#DEFAULT}.
	 * @param msg The message to be logged.
	 * @see LogLevel
	 */
	public void log(String msg) {
		log.addEntry(msg, LogLevel.DEFAULT);
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
		public ConsoleDisplay(Skin skin) {
			super(skin);
			this.clear();
			this.pad(0);
			this.add(logArea).expand().fill().row();
			this.add(input).expand().fill();
			this.setSize(this.getPrefWidth(), this.getPrefHeight());
		}
	}

	public boolean keyDown(int keycode) {
		return false;
	}

	public boolean keyUp(int keycode) {
		if(keycode == keyID) {
			hidden = !hidden;
			return true;
		}
		return false;
	}

	public boolean keyTyped(char character) {
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	public boolean scrolled(int amount) {
		return false;
	}

	public void dispose() {
		Gdx.input.setInputProcessor(appInput);
		batch.dispose();
	}
}