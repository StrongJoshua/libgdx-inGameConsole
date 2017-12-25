package com.strongjoshua.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.strongjoshua.console.Console;

class DisplayListener extends InputListener {
	private final Console console;

	/**
	 * @param console
	 */
	DisplayListener(Console console) {
		this.console = console;
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		if (console.isDisabled()) return false;
		if (!console.hasFocus()) return false;
		if (keycode == console.getDisplayKeyID()) {
			console.setVisible(!console.isVisible());
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if(button == 0) {
			if(!console.isDisabled() && console.isVisible()) {
				if(console.hitsConsole(Gdx.input.getX(), Gdx.input.getY())) {
					if(!console.hasFocus()) {
						console.setFocus(true);
					}
					return true;
				}
				else {
					console.setFocus(false);
				}
			}
		}
		return false;
	}
	
	
}