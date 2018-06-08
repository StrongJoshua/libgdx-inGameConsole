package com.strongjoshua.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConsoleContext {
	private Table root;
	private Label label, copy;
	private InputListener stageListener;

	ConsoleContext (Class<? extends Table> tableClass, Skin skin) {
		try {
			root = tableClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Table class does not support empty constructor.");
		}
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
		root.setBackground(skin.getDrawable("default-rect"));
		root.add(copy);
		root.pad(5);
		root.setSize(root.getPrefWidth(), root.getPrefHeight());

		stageListener = new InputListener() {
			@Override public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (ConsoleContext.this.root.hit(x, y, false) == null)
					remove();
				return true;
			}
		};
	}

	void setLabel (Label l) {
		label = l;
	}

	protected void setStage (Stage stage) {
		if (stage != null) {
			stage.addListener(stageListener);
			stage.addActor(root);
		}
	}

	protected boolean remove () {
		if (root.getStage() != null)
			root.getStage().removeListener(stageListener);
		return root.remove();
	}

	protected void setPosition (float x, float y) {
		root.setPosition(x, y);
	}
}
