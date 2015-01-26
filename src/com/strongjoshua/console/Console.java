package com.strongjoshua.console;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Console {
	private int keyID;
	private boolean disabled;
	
	public Console() {
		
	}
	
	public void draw(Batch b) {
		
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