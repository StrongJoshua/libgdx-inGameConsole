package com.strongjoshua.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.log.Log;
import com.strongjoshua.console.log.LogEntry;
import com.strongjoshua.console.log.LogLevel;

class ConsoleDisplay extends Table {
	private Console console;
	private Log log;
	private Stage stage;
	private Table logEntries;
	private TextField input;
	private Skin skin;
	private Array<Label> labels;
	private String fontName;
	private ScrollPane scroll;
	private boolean selected = true;
	private Drawable mouseHoverDrawable;
	private Drawable selectedDrawable;
	private Array<LogEntry> selections = new Array<LogEntry>();

	protected ConsoleDisplay(Console console, Log log, Stage stage, Skin skin) {
		this.setFillParent(false);
		this.console = console;
		this.log = log;
		this.stage = stage;
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
		input.setTextFieldListener(new FieldListener(console));

		scroll = new ScrollPane(logEntries, skin);
		scroll.setFadeScrollBars(false);
		scroll.setScrollbarsOnTop(false);
		scroll.setOverscroll(false, false);

		this.add(scroll).expand().fill().pad(4).row();
		this.add(input).expandX().fillX().pad(4);
		this.addListener(new KeyListener(console, input));
	}
	
	public void setMouseHoverDrawble(Drawable mouseHoverDrawable) {
		this.mouseHoverDrawable = mouseHoverDrawable;
	}
	
	public void setSelectedDrawable (Drawable selectedDrawable) {
		this.selectedDrawable = selectedDrawable;
	}
	
	public Drawable getSelectDrawable () {
		return this.selectedDrawable;
	}
	
	protected void refresh() {
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
			l.setUserObject(le);
			
			LabelStyle lb = new LabelStyle();
			lb.font = skin.getFont(fontName);
			lb.fontColor = le.getColor();
			l.setStyle(lb);
			logEntries.add(l).expandX().fillX().top().left().padLeft(4).row();
		}
		scroll.validate();
		scroll.setScrollPercentY(1);
	}

	void setHidden(boolean hidden) {
		if (hidden) {
			stage.setKeyboardFocus(null);
			stage.setScrollFocus(null);
		} else {
			input.setText("");
			if (selected) {
				select();
			}
		}
	}

	public void select() {
		selected = true;
		if (console.isVisible()) {
			stage.setKeyboardFocus(input);
			stage.setScrollFocus(scroll);
		}
	}

	public void deselect() {
		selected = false;
		stage.setKeyboardFocus(null);
		stage.setScrollFocus(null);
	}
	
	public LogEntry getHoveredLogEntry() {
		Vector3 stageCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
		stage.getCamera().unproject(stageCoords);
		
		float x = stageCoords.x;
		float y = stageCoords.y;
		
		for(Label l : labels) {
			Vector2 localToStage = l.localToStageCoordinates(new Vector2());
			
			float x1 = localToStage.x;
			float x2 = x1 + l.getWidth();
			float y1 = localToStage.y;
			float y2 = y1 + l.getHeight();
			
			LabelStyle ls = l.getStyle();
			if(ls == null) ls = new LabelStyle();
			if(x >= x1 && x <= x2 && y >= y1 && y <= y2) {
				if(l.getUserObject() != null && l.getUserObject() instanceof LogEntry) {
					return (LogEntry)l.getUserObject();
				}
				return null;
			}
		}
		return null;
	}

	public void updateLabelBackground () {
		if(mouseHoverDrawable == null && selectedDrawable == null) return;
		LogEntry logEntry = getHoveredLogEntry();
		for(Label l : labels) {
			LabelStyle ls = l.getStyle();
			if(l.getUserObject() == null || !(l.getUserObject() instanceof LogEntry)) continue;
			LogEntry le = (LogEntry) l.getUserObject();
			if(selections.contains(le, true)) {
				if(ls.background != selectedDrawable) {
					ls.background = selectedDrawable;
				}
			}
			else if(logEntry == le) {
				if(ls.background != mouseHoverDrawable) {
					ls.background = mouseHoverDrawable;
				}
			}
			else {
				if(ls.background != null) {
					ls.background = null;
				}
			}
		}
	}

	public Console getConsole () {
		return console;
	}

	public Array<LogEntry> getSelections () {
		return selections;
	}

	public Array<LogEntry> getLogEntries () {
		return log.getLogEntries();
	}
}