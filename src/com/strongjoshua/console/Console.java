package com.strongjoshua.console;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Console {
	public enum LogLevel {
		ERROR (new Color(1, 0, 0, 1));
		
		private Color color;
		LogLevel(Color c) {
			this.color = c;
		}
		
		Color getColor() {
			return color;
		}
	}
	
	private int keyID;
	private boolean disabled;
	private Log log;
	
	public Console() {
		log = new Log();
	}
	
	/**
	 * Draws the console.
	 * @param b The batch to draw to. Assumes {@link Batch#begin()} has been called and {@link Batch#end()} will be called sometime after this method completes.
	 */
	public void draw(Batch b) {
		
	}
	
	public void log(String msg, LogLevel level) {
		log.addEntry(msg, level);
	}
	
	/**
	 * Prints all log entries to the given file. Log entries include logs in the code and commands from within in the console while the program is running.
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
	 * Gets the console's display key. If the console is enabled, the console will be shown upon this key being pressed.
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
}