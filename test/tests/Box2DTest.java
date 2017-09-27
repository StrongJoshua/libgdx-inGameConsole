
package tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;
import com.strongjoshua.console.annotation.ConsoleDoc;
import com.strongjoshua.console.annotation.HiddenCommand;

/** Extension of the <a href=
 * 'https://github.com/StrongJoshua/libgdx-utils/blob/master/src/com/strongjoshua/libgdx_utils/tests/Box2DTest.java'>Simple Box2D
 * test</a> to incorporate the console.
 *
 * @author StrongJoshua */
public class Box2DTest extends ApplicationAdapter {
	SpriteBatch batch;
	Sprite[] sprites;
	World world;
	OrthographicCamera c;
	Box2DDebugRenderer debugRenderer;
	Body[] bodies;
	final int WIDTH = 100, HEIGHT = 100;
	float mX, mY, ratio;
	Console console;
	MyCommandExecutor cExec;

	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		w *= 2;
		float h = Gdx.graphics.getHeight();
		h *= 2;
		ratio = h / w;
		Gdx.app.getGraphics().setWindowedMode((int)w, (int)h);

		mX = (float)WIDTH / w;
		mY = (float)HEIGHT / h;

		Box2D.init();
		world = new World(new Vector2(0, -9.81f), true);
		batch = new SpriteBatch();

		sprites = new Sprite[250];
		bodies = new Body[sprites.length];
		float k, j;
		for (int i = 0; i < sprites.length; i++) {
			if (i < 50) {
				k = 0;
				j = 1;
			} else if (i < 100) {
				k = 50 * sprites[i - 1].getWidth() + sprites[i - 1].getWidth() / 2;
				j = 2;
			} else if (i < 150) {
				k = 100 * sprites[i - 1].getWidth() + sprites[i - 1].getWidth() / 2;
				j = 3;
			} else if (i < 200) {
				k = 150 * sprites[i - 1].getWidth() + sprites[i - 1].getWidth() / 2;
				j = 4;
			} else {
				k = 200 * sprites[i - 1].getWidth() + sprites[i - 1].getWidth() / 2;
				j = 5;
			}

			sprites[i] = new Sprite(new Texture(Gdx.files.classpath("tests/badlogic.jpg")));
			sprites[i].setSize(2, 2);
			sprites[i].setOriginCenter();

			BodyDef bdef = new BodyDef();
			bdef.type = BodyDef.BodyType.DynamicBody;
			bdef.position.set(i * sprites[i].getWidth() + sprites[i].getWidth() / 2 - k, 15 * j);
			bodies[i] = world.createBody(bdef);
			PolygonShape poly = new PolygonShape();
			poly.setAsBox(sprites[i].getWidth() / 2, sprites[i].getHeight() / 2);
			FixtureDef fdef = new FixtureDef();
			fdef.shape = poly;
			fdef.restitution = .2f;
			fdef.density = 1f;
			bodies[i].createFixture(fdef);

			poly.dispose();
		}

		BodyDef bdef = new BodyDef();
		bdef.type = BodyDef.BodyType.StaticBody;
		bdef.position.set(WIDTH / 2, -5);
		Body b = world.createBody(bdef);
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(50, 5);
		b.createFixture(poly, 0);

		bdef = new BodyDef();
		bdef.type = BodyDef.BodyType.StaticBody;
		bdef.position.set(-5, HEIGHT / 2);
		b = world.createBody(bdef);
		poly = new PolygonShape();
		poly.setAsBox(5, 50);
		b.createFixture(poly, 0);

		bdef = new BodyDef();
		bdef.type = BodyDef.BodyType.StaticBody;
		bdef.position.set(WIDTH + 5, (HEIGHT * ratio) / 2);
		b = world.createBody(bdef);
		poly = new PolygonShape();
		poly.setAsBox(5, 50);
		b.createFixture(poly, 0);

		bdef = new BodyDef();
		bdef.type = BodyDef.BodyType.StaticBody;
		bdef.position.set(WIDTH / 2, HEIGHT * ratio + 5);
		b = world.createBody(bdef);
		poly = new PolygonShape();
		poly.setAsBox(50, 5);
		b.createFixture(poly, 0);

		poly.dispose();

		c = new OrthographicCamera(WIDTH, HEIGHT * ratio);
		c.position.set(c.viewportWidth / 2, c.viewportHeight / 2, 0);
		c.update();
		batch.setProjectionMatrix(c.combined);

		debugRenderer = new Box2DDebugRenderer();

		console = new GUIConsole(new Skin(Gdx.files.classpath("tests/test_skin/uiskin.json")), false);
		cExec = new MyCommandExecutor();
		console.setCommandExecutor(cExec);
		// set to 'Z' to demonstrate that it works with binds other than the
		// default
		console.setDisplayKeyID(Input.Keys.Z);
		console.setVisible(true);
		console.setConsoleStackTrace(true);

		// test multiple resets with nested multiplexers
		InputMultiplexer im1 = new InputMultiplexer();
		im1.addProcessor(new Stage());
		im1.addProcessor(new Stage());

		InputMultiplexer im2 = new InputMultiplexer();
		im2.addProcessor(new Stage());
		im2.addProcessor(new Stage());
		im1.addProcessor(im2);
		Gdx.input.setInputProcessor(im1);
		console.setMaxEntries(16);
		console.resetInputProcessing();
		// console already present, logged to consoles
		console.resetInputProcessing();

		console.setSizePercent(100, 33);
		console.setPositionPercent(0, 67);
	}

	@Override
	public void render () {
		if (Gdx.input.isTouched()) {
			float x = Gdx.input.getX();
			float y = Gdx.input.getY();

			if (!console.hitsConsole(x, y)) {
				Vector3 worldVector = c.unproject(new Vector3(x, y, 0));

				createExplosion(worldVector.x, worldVector.y, 2000);

				console.log(String.format("Created touch explosion at %.2f, %.2f!", worldVector.x, worldVector.y), LogLevel.SUCCESS);
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
		world.step(1 / 60f, 6, 2);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for (int i = 0; i < bodies.length; i++) {
			Vector2 pos = bodies[i].getPosition();
			sprites[i].setPosition(pos.x - sprites[i].getWidth() / 2, pos.y - sprites[i].getHeight() / 2);
			sprites[i].setRotation(MathUtils.radiansToDegrees * bodies[i].getAngle());
			sprites[i].draw(batch);
		}

		batch.end();

		debugRenderer.render(world, c.combined);

		console.draw();
	}

	/** Creates an explosion that applies forces to the bodies relative to their position and the given x and y values.
	 *
	 * @param maxForce The maximum force to be applied to the bodies (diminishes as distance from touch increases). */
	private void createExplosion (float x, float y, float maxForce) {
		float force;
		Vector2 touch = new Vector2(x, y);
		for (int i = 0; i < bodies.length; i++) {
			Body b = bodies[i];
			Vector2 v = b.getPosition();
			float dist = v.dst2(touch);

			if (dist == 0)
				force = maxForce;
			else
				force = MathUtils.clamp(maxForce / dist, 0, maxForce);

			float angle = v.cpy().sub(touch).angle();
			float xForce = force * MathUtils.cosDeg(angle);
			float yForce = force * MathUtils.sinDeg(angle);

			Vector3 touch3, v3, boundMin, boundMax, intersection;
			touch3 = new Vector3(touch.x, touch.y, 0);
			v3 = new Vector3(v.x, v.y, 0);
			boundMin = new Vector3(v.x - 1, v.y - 1, 0);
			boundMax = new Vector3(v.x + 1, v.y + 1, 0);
			intersection = Vector3.Zero;

			Intersector.intersectRayBounds(new Ray(touch3, v3), new BoundingBox(boundMin, boundMax), intersection);

			b.applyForce(new Vector2(xForce, yForce), new Vector2(intersection.x, intersection.y), true);
		}
	}

	@Override
	public void dispose () {
		console.dispose();
		super.dispose();
	}

	public class MyCommandExecutor extends CommandExecutor {
		@HiddenCommand
		public void superExplode () {
			explode(0, 0, 1000000);
		}

		public void setExecuteHiddenCommands (boolean enabled) {
			console.setExecuteHiddenCommands(enabled);
			console.log("ExecuteHiddenCommands was set to " + enabled);
		}

		public void setDisplayHiddenCommands (boolean enabled) {
			console.setDisplayHiddenCommands(enabled);
			console.log("DisplayHiddenCommands was set to " + enabled);
		}

		@ConsoleDoc(description = "Creates an explosion.", paramDescriptions = {"The x coordinate", "The y coordinate",
			"The amount of force"})
		public void explode (float x, float y, float maxForce) {
			createExplosion(x, y, maxForce);
			console.log("Created console explosion!", LogLevel.SUCCESS);
		}

		public void clear () {
			console.clear();
		}

		public void failFunction () {
			throw new RuntimeException("This function was designed to fail.");
		}
	}

	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Box2DTest(), config);
	}
}
