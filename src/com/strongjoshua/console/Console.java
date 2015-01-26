package com.strongjoshua.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class Console {
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
	}
	
	/**
	 * Creates the console using the default skin.
	 */
	public Console() {
		this(new Skin());
	}

	/**
	 * Draws the console.
	 * @param b The batch to draw to. Assumes {@link Batch#begin()} has been called and {@link Batch#end()} will be called sometime after
	 *            this method completes.
	 */
	public void draw(Batch b) {
		Matrix4 orig = b.getProjectionMatrix();
		Matrix4 consoleMatrix;
		OrthographicCamera tmp = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		tmp.position.set(tmp.viewportWidth / 2, tmp.viewportHeight / 2, 0);
		consoleMatrix = tmp.combined;

		b.setProjectionMatrix(consoleMatrix);
		
		display.draw(b, 1);
		
		b.setProjectionMatrix(orig);
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
			this.addActor(logArea);
			this.addActor(input);
			this.setHeight(this.getPrefHeight());
			this.setWidth(this.getPrefWidth());
		}
	}
}