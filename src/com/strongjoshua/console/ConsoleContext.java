package com.strongjoshua.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

class ConsoleContext extends Table {
	private Label label, copy;
	private InputListener stageListener;

	ConsoleContext (Skin skin) {
		copy = new Label("Copy", skin);
		copy.addListener(new ClickListener() {
			@Override public void clicked (InputEvent event, float x, float y) {
				if (event.getPointer() != 0)
					return;
				if (label == null)
					throw new RuntimeException("Trying to copy a null label (this should never happen).");
				Gdx.app.getClipboard().setContents(label.getText().toString());
				ConsoleContext.this.remove();
			}
		});
		this.setBackground(skin.getDrawable("default-rect"));
		this.add(copy);
		this.pad(5);
		this.setSize(this.getPrefWidth(), this.getPrefHeight());

		stageListener = new InputListener() {
			@Override public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (ConsoleContext.this.hit(x, y, false) == null)
					remove();
				return true;
			}
		};
	}

	void setLabel (Label l) {
		label = l;
	}

	@Override protected void setStage (Stage stage) {
		super.setStage(stage);
		if (stage != null)
			stage.addListener(stageListener);
	}

	@Override public boolean remove () {
		if (getStage() != null)
			getStage().removeListener(stageListener);
		return super.remove();
	}
}
