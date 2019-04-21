package tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;

public class VisUITest extends ApplicationAdapter {
	public static void main(String[] args) {
		new LwjglApplication(new VisUITest(), new LwjglApplicationConfiguration());
	}

	private Console console;

	@Override public void create () {
		VisUI.load();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		console = new GUIConsole(VisUI.getSkin(), false, 0, VisWindow.class, VisTable.class, "default-pane", TextField.class,
			VisTextButton.class, VisLabel.class, VisScrollPane.class);
		console.setCommandExecutor(new MyCommandExecutor());
		console.setSizePercent(100, 100);
		console.setPosition(0, 0);
		console.setVisible(true);
		console.enableSubmitButton(true);
		console.resetInputProcessing();
	}

	@Override public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		console.draw();
	}

	private class MyCommandExecutor extends CommandExecutor {
		public void success() {
			console.log("Success", LogLevel.SUCCESS);
		}

		public void error() {
			console.log("Error", LogLevel.ERROR);
		}
	}
}
