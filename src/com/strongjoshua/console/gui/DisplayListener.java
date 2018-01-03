package com.strongjoshua.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.log.LogEntry;

public class DisplayListener extends InputListener {
	private final Console console;
	private final ConsoleDisplay display;
	private LogEntry lastSelected;

	public DisplayListener(ConsoleDisplay display) {
		this.console = display.getConsole();
		this.display = display;
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		if (console.isDisabled()) return false;
		if (keycode == console.getDisplayKeyID()) {
			console.setVisible(!console.isVisible());
			return true;
		}
		
		if (!console.hasFocus()) return false;

		else if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.A)) {
			// [L-CTRL] + [A] -> select all
			Array<LogEntry> selections = display.getSelections();
			selections.clear();
			selections.addAll(display.getLogEntries());
			display.updateLabelBackground();
			return true;
		}
		else if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.C)) {
			// [L-CTRL] + [A] -> to clipboard
			String content = "";
			Array<LogEntry> selections = display.getSelections();
			for(LogEntry le : selections) {
				content += le.toConsoleString() + "\n";
			}
			Gdx.app.getClipboard().setContents(content);
		}
		return false;
	}

	private void handleFocus(InputEvent event) {
		if(event.getButton() == 0) {
			if(!console.isDisabled() && console.isVisible()) {
				if(console.hitsConsole(Gdx.input.getX(), Gdx.input.getY())) {
					if(!console.hasFocus()) {
						console.setFocus(true);
					}
				}
				else if(console.hasFocus()) {
					console.setFocus(false);
				}
			}
		}
	}
	
	private void handleSelections(InputEvent event) {
		if(event.getButton() != 0) return;
		if(display.getSelectDrawable() == null) return;
		
		Array<LogEntry> selections = display.getSelections();
		LogEntry le = display.getHoveredLogEntry();
		if(le != null) {
			if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && !Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
				// [L-CTRL] (add/remove single selection)
				if(selections.contains(le, true)) {
					selections.removeValue(le, true);
				}
				else {
					selections.add(le);
				}
			}
			else if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
				// [L-SHIFT] + optional [L-CTRL] (add/set multi selection)
				if(selections.size == 0) return;
				
				// if [L-CTRL] is not pressed reset selections
				if(!Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
					selections.clear();
				}
				
				// build from-/to-index
				int selectedIndex = display.getLogEntries().indexOf(le, true);
				int lastIndex = display.getLogEntries().indexOf(lastSelected, true);
				
				int fromIndex = selectedIndex < lastIndex ? selectedIndex : lastIndex;
				int toIndex = selectedIndex < lastIndex ? lastIndex : selectedIndex;
				for(int index = fromIndex; index <= toIndex; index++) {
					selections.add(display.getLogEntries().get(index));
				}
			}
			else {
				// single selection (reset current selections)
				selections.clear();
				if(selections.contains(le, true)) {
					selections.removeValue(le, true);
				}
				else {
					lastSelected = le;
					selections.add(le);
				}
			}
		}
		else {
			selections.clear();
		}
		display.updateLabelBackground();
	}
	
	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		handleFocus(event);
		handleSelections(event);
		return false;
	}
	
	
}