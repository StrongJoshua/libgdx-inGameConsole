package tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.GUIConsole;

public class PauseTest extends ApplicationAdapter{
    private Stage stage;
    private GUIConsole console;
    private Image image;
    private boolean pause;

    public static void main (String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new PauseTest(), config);
    }

    @Override
    public void create(){
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.classpath("tests/test_skin/uiskin.json"));
        console = new GUIConsole(skin);
        console.setCommandExecutor(new MyCommandExecutor());
        console.setSizePercent(40, 50);
        console.setPositionPercent(100, 100);
        console.setDisplayKeyID(Input.Keys.Z);

        image = new Image(new Texture(Gdx.files.classpath("tests/badlogic" + "" + ".jpg")));
        image.setScale(1.5f);
        stage.addActor(image);
    }

    @Override public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(image.getActions().size == 0)
            image.addAction(Actions.sequence(Actions.fadeOut(1), Actions.fadeIn(1)));

        if(!pause)
            stage.act();

        stage.draw();
        console.draw();
    }

    private class MyCommandExecutor extends CommandExecutor{
        @Override
        protected void onShow(){
            pause = true;
        }

        @Override
        protected void onHide(){
            pause = false;
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
}
