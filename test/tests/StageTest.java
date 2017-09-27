
package tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.annotation.ConsoleDoc;

public class StageTest extends ApplicationAdapter {
	private Stage stage;
	private GUIConsole console;
	private Image image;
	private Label selectLabel;
	private Label deselectLabel;

	@Override
	public void create () {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		Skin skin = new Skin(Gdx.files.classpath("tests/test_skin/uiskin.json"));
		console = new GUIConsole(skin);
		console.setCommandExecutor(new MyCommandExecutor());
		console.setSizePercent(100, 50);

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Input.Keys.F) {
					blink();
					return true;
				} else if (keycode == Input.Keys.TAB) {
					console.select();
					return true;
				} else if (keycode == Input.Keys.D) {
					System.out.println("toggle disabled");
					console.setDisabled(!console.isDisabled());
					return true;
				}
				return false;
			}
		});

		image = new Image(new Texture(Gdx.files.classpath("tests/badlogic" + "" + ".jpg")));
		image.setScale(.5f);
		stage.addActor(image);

		selectLabel = new Label("Select", skin);
		deselectLabel = new Label("Deselect", skin);
		stage.addActor(selectLabel);
		stage.addActor(deselectLabel);
		int padding = 25;
		selectLabel.setPosition(Gdx.graphics.getWidth() - selectLabel.getWidth() - deselectLabel.getWidth() - 2 * padding,
			selectLabel.getHeight());
		deselectLabel.setPosition(Gdx.graphics.getWidth() - deselectLabel.getWidth() - padding, deselectLabel.getHeight());
	}

	@Override
	public void render () {
		if (Gdx.input.justTouched()) {
			Actor actor = stage.hit(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), true);
			if (actor == selectLabel) {
				console.select();
			} else if (actor == deselectLabel) {
				console.deselect();
			}
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
		console.draw();
	}

	private void blink () {
		image.addAction(Actions.sequence(Actions.fadeOut(1), Actions.fadeIn(1)));
	}

	@Override
	public void dispose () {
		console.dispose();
		stage.dispose();
		super.dispose();
	}

	private class MyCommandExecutor extends CommandExecutor {
		@ConsoleDoc(description = "Makes the badlogic image fade out and back " + "" + "" + "in.")
		public void blink () {
			StageTest.this.blink();
		}

		public void setExecuteHiddenCommands (boolean enabled) {
			console.setExecuteHiddenCommands(enabled);
			console.log("ExecuteHiddenCommands was set to " + enabled);
		}

		public void setDisplayHiddenCommands (boolean enabled) {
			console.setDisplayHiddenCommands(enabled);
			console.log("DisplayHiddenCommands was set to " + enabled);
		}
	}

	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new StageTest(), config);
	}
}
