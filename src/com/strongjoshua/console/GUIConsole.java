/**
 * Copyright 2015 StrongJoshua (strongjoshua@hotmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.strongjoshua.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.Array;

/** A simple console that allows live logging, and live execution of methods, from within an application. Please see the <a
 * href="https://github.com/StrongJoshua/libgdx-inGameConsole">GitHub Repository</a> for more information.
 *
 * @author StrongJoshua */
public class GUIConsole extends AbstractConsole {

	private int keyID;

	private ConsoleDisplay display;
	private boolean hidden = true;
	private boolean usesMultiplexer = false;
	private InputProcessor appInput;
	private InputMultiplexer multiplexer;
	private Stage stage;
	private CommandHistory commandHistory;
	private CommandCompleter commandCompleter;
	private Window consoleWindow;

	/** Creates the console using the default skin.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this
	 * console uses a multiplexer to circumvent it).
	 *
	 * @see Console#dispose() */
	public GUIConsole () {
		this(new Skin(Gdx.files.classpath("default_skin/uiskin.json")));
	}

	/** Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this
	 * console uses a multiplexer to circumvent it).
	 *
	 * @param skin Uses skins for Label, TextField, and Table. Skin <b>must</b> contain a font called 'default-font'.
	 * @see Console#dispose() */
	public GUIConsole (Skin skin) {
		this(skin, true);
	}

	/** Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this
	 * console uses a multiplexer to circumvent it).
	 *
	 * @param useMultiplexer If internal multiplexer should be used
	 * @see Console#dispose() */
	public GUIConsole (boolean useMultiplexer) {
		this(new Skin(Gdx.files.classpath("default_skin/uiskin.json")), useMultiplexer);
	}

	/** Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this
	 * console uses a multiplexer to circumvent it).
	 *
	 * @param skin Uses skins for Label, TextField, and Table. Skin <b>must</b> contain a font called 'default-font'.
	 * @param useMultiplexer If internal multiplexer should be used
	 * @see Console#dispose() */
	public GUIConsole (Skin skin, boolean useMultiplexer) {
		this(skin, useMultiplexer, Keys.APOSTROPHE);
	}

	/** Creates the console.<br>
	 * <b>***IMPORTANT***</b> Call {@link Console#dispose()} to make your {@link InputProcessor} the default processor again (this
	 * console uses a multiplexer to circumvent it).
	 *
	 * @param skin Uses skins for Label, TextField, and Table. Skin <b>must</b> contain a font called 'default-font'.
	 * @param useMultiplexer If internal multiplexer should be used
	 * @param keyID Sets the key used to open/close the console
	 * @see Console#dispose() */
	public GUIConsole (Skin skin, boolean useMultiplexer, int keyID) {
		this.keyID = keyID;
		stage = new Stage();
		display = new ConsoleDisplay(skin);
		commandHistory = new CommandHistory();
		commandCompleter = new CommandCompleter();
		logToSystem = false;

		usesMultiplexer = useMultiplexer;
		if (useMultiplexer) {
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
		consoleWindow.setTouchable(Touchable.disabled);

		stage.addListener(new DisplayListener());
		stage.addActor(consoleWindow);
		stage.setKeyboardFocus(display);

		setSizePercent(50, 50);
		setPositionPercent(50, 50);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#setMaxEntries(int)
	 */
	@Override
	public void setMaxEntries (int numEntries) {
		if (numEntries > 0 || numEntries == UNLIMITED_ENTRIES) {
			log.setMaxEntries(numEntries);
		} else {
			throw new IllegalArgumentException("Maximum entries must be greater than 0 or use Console.UNLIMITED_ENTRIES.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#clear()
	 */
	@Override
	public void clear () {
		log.getLogEntries().clear();
		display.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#setSize(int, int)
	 */
	@Override
	public void setSize (int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Pixel size must be greater than 0.");
		}
		consoleWindow.setSize(width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#setSizePercent(float, float)
	 */
	@Override
	public void setSizePercent (float wPct, float hPct) {
		if (wPct <= 0 || hPct <= 0) {
			throw new IllegalArgumentException("Size percentage must be greater than 0.");
		}
		if (wPct > 100 || hPct > 100) {
			throw new IllegalArgumentException("Size percentage cannot be greater than 100.");
		}
		float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		consoleWindow.setSize(w * wPct / 100.0f, h * hPct / 100.0f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#setPosition(int, int)
	 */
	@Override
	public void setPosition (int x, int y) {
		consoleWindow.setPosition(x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#setPositionPercent(float, float)
	 */
	@Override
	public void setPositionPercent (float xPosPct, float yPosPct) {
		if (xPosPct > 100 || yPosPct > 100) {
			throw new IllegalArgumentException("Error: The console would be drawn outside of the screen.");
		}
		float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		consoleWindow.setPosition(w * xPosPct / 100.0f, h * yPosPct / 100.0f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#resetInputProcessing()
	 */
	@Override
	public void resetInputProcessing () {
		usesMultiplexer = true;
		appInput = Gdx.input.getInputProcessor();
		if (appInput != null) {
			if (hasStage(appInput)) {
				log("Console already added to input processor!", LogLevel.ERROR);
				Gdx.app.log("Console", "Already added to input processor!");
				return;
			}
			multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(stage);
			multiplexer.addProcessor(appInput);
			Gdx.input.setInputProcessor(multiplexer);
		} else {
			Gdx.input.setInputProcessor(stage);
		}
	}

	/** Compares the given processor to the console's stage. If given a multiplexer, it is iterated through recursively to check all
	 * of the multiplexer's processors for comparison.
	 *
	 * @param processor
	 * @return processor == this.stage */
	private boolean hasStage (InputProcessor processor) {
		if (!(processor instanceof InputMultiplexer)) {
			return processor == stage;
		}
		InputMultiplexer im = (InputMultiplexer)processor;
		Array<InputProcessor> ips = im.getProcessors();
		for (InputProcessor ip : ips) {
			if (hasStage(ip)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#getInputProcessor()
	 */
	@Override
	public InputProcessor getInputProcessor () {
		return stage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#draw()
	 */
	@Override
	public void draw () {
		if (disabled) {
			return;
		}
		stage.act();

		if (hidden) {
			return;
		}
		stage.draw();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#refresh()
	 */
	@Override
	public void refresh () {
		this.refresh(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#refresh(boolean)
	 */
	@Override
	public void refresh (boolean retain) {
		float oldWPct = 0, oldHPct = 0, oldXPosPct = 0, oldYPosPct = 0;
		if (retain) {
			oldWPct = consoleWindow.getWidth() / stage.getWidth() * 100;
			oldHPct = consoleWindow.getHeight() / stage.getHeight() * 100;
			oldXPosPct = consoleWindow.getX() / stage.getWidth() * 100;
			oldYPosPct = consoleWindow.getY() / stage.getHeight() * 100;
		}
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		stage.getViewport().setWorldSize(width, height);
		stage.getViewport().update(width, height, true);
		if (retain) {
			this.setSizePercent(oldWPct, oldHPct);
			this.setPositionPercent(oldXPosPct, oldYPosPct);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#log(java.lang.String, com.strongjoshua.console.GUIConsole.LogLevel)
	 */
	@Override
	public void log (String msg, LogLevel level) {
		super.log(msg, level);

		display.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#setDisabled(boolean)
	 */
	@Override
	public void setDisabled (boolean disabled) {
		if (disabled) {
			display.setHidden(true);
		}
		this.disabled = disabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#getKeyID()
	 */
	@Override
	public int getDisplayKeyID () {
		return keyID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#setKeyID(int)
	 */
	@Override
	public void setDisplayKeyID (int code) {
		if (code == Keys.ENTER) {
			return;
		}
		keyID = code;
	}

	private Vector3 stageCoords = new Vector3();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#hitsConsole(float, float)
	 */
	@Override
	public boolean hitsConsole (float screenX, float screenY) {
		if (disabled || hidden) {
			return false;
		}
		stage.getCamera().unproject(stageCoords.set(screenX, screenY, 0));
		return stage.hit(stageCoords.x, stageCoords.y, true) != null;
	}

	private class ConsoleDisplay extends Table {
		private Table logEntries;
		private TextField input;
		private Skin skin;
		private Array<Label> labels;
		private String fontName;
		private boolean selected = true;

		protected ConsoleDisplay (Skin skin) {
			this.setFillParent(false);
			this.skin = skin;

			if (skin.has("console-font", BitmapFont.class))
				fontName = "console-font";
			else
				fontName = "default-font";

			TextFieldStyle tfs = skin.get(TextFieldStyle.class);
			tfs.font = skin.getFont(fontName);

			labels = new Array<Label>();

			logEntries = new Table(skin);

			input = new TextField("", tfs);
			input.setTextFieldListener(new FieldListener());

			scroll = new ScrollPane(logEntries, skin);
			scroll.setFadeScrollBars(false);
			scroll.setScrollbarsOnTop(false);
			scroll.setOverscroll(false, false);

			this.add(scroll).expand().fill().pad(4).row();
			this.add(input).expandX().fillX().pad(4);
			this.addListener(new KeyListener(input));
		}

		protected void refresh () {
			Array<LogEntry> entries = log.getLogEntries();
			logEntries.clear();

			// expand first so labels start at the bottom
			logEntries.add().expand().fill().row();
			int size = entries.size;
			for (int i = 0; i < size; i++) {
				LogEntry le = entries.get(i);
				Label l;
				// recycle the labels so we don't create new ones every refresh
				if (labels.size > i) {
					l = labels.get(i);
				} else {
					l = new Label("", skin, fontName, LogLevel.DEFAULT.getColor());
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

		private void setHidden (boolean h) {
			hidden = h;
			if (hidden) {
				consoleWindow.setTouchable(Touchable.disabled);
				stage.setKeyboardFocus(null);
				stage.setScrollFocus(null);
			} else {
				input.setText("");
				consoleWindow.setTouchable(Touchable.childrenOnly);
				if (selected) {
					select();
				}
			}
		}

		public void select () {
			selected = true;
			if (!hidden) {
				stage.setKeyboardFocus(input);
				stage.setScrollFocus(scroll);
			}
		}

		public void deselect () {
			selected = false;
			stage.setKeyboardFocus(null);
			stage.setScrollFocus(null);
		}
	}

	private ScrollPane scroll;

	private class FieldListener implements TextFieldListener {
		@Override
		public void keyTyped (TextField textField, char c) {
			if (("" + c).equalsIgnoreCase(Keys.toString(keyID))) {
				String s = textField.getText();
				textField.setText(s.substring(0, s.length() - 1));
			}
		}
	}

	private class KeyListener extends InputListener {
		private TextField input;

		protected KeyListener (TextField tf) {
			input = tf;
		}

		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (disabled) return false;

			// reset command completer because input string may have changed
			if (keycode != Keys.TAB) {
				commandCompleter.reset();
			}

			if (keycode == Keys.ENTER && !hidden) {
				String s = input.getText();
				if (s.length() == 0 || s.split(" ").length == 0) {
					return false;
				}
				if (exec != null) {
					commandHistory.store(s);
					execCommand(s);
				} else {
					log("No command executor has been set. Please call setCommandExecutor for this console in your code and restart.",
						LogLevel.ERROR);
				}
				input.setText("");
				return true;
			} else if (keycode == Keys.UP && !hidden) {
				input.setText(commandHistory.getPreviousCommand());
				input.setCursorPosition(input.getText().length());
				return true;
			} else if (keycode == Keys.DOWN && !hidden) {
				input.setText(commandHistory.getNextCommand());
				input.setCursorPosition(input.getText().length());
				return true;
			} else if (keycode == Keys.TAB && !hidden) {
				String s = input.getText();
				if (s.length() == 0) {
					return false;
				}
				if (commandCompleter.isNew()) {
					commandCompleter.set(exec, s);
				}
				input.setText(commandCompleter.next());
				input.setCursorPosition(input.getText().length());
				return true;
			}
			return false;
		}
	}

	private class DisplayListener extends InputListener {
		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (disabled) return false;
			if (keycode == keyID) {
				display.setHidden(!hidden);
				return true;
			}
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#dispose()
	 */
	@Override
	public void dispose () {
		if (usesMultiplexer && appInput != null) {
			Gdx.input.setInputProcessor(appInput);
		}
		stage.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#isHidden()
	 */
	@Override
	public boolean isVisible () {
		return !hidden;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strongjoshua.console.Console#setHidden()
	 */
	public void setVisible (boolean visible) {
		display.setHidden(!visible);
	}

	@Override
	public void select () {
		display.select();
	}

	@Override
	public void deselect () {
		display.deselect();
	}
}
