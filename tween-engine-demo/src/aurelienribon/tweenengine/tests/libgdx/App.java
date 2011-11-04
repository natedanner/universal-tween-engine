package aurelienribon.tweenengine.tests.libgdx;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenGroup;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.Tweenable;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class App implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch sb;
	private TweenManager tweenManager;

	private BitmapFont font;
	private String text;
	
	private Sprite sprite1;
	private Sprite sprite2;
	private Sprite sprite3;
	private Sprite sprite4;
	private TweenSprite tweenSprite1;
	private TweenSprite tweenSprite2;
	private TweenSprite tweenSprite3;
	private TweenSprite tweenSprite4;

	private boolean canBeRestarted = false;

	@Override
	public void create() {
		// Input manager
		Gdx.input.setInputProcessor(inputProcessor);

		// Camera + Spritebatch + font
		float ratio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		this.camera = new OrthographicCamera(6, 6/ratio);
		this.sb = new SpriteBatch();
		this.font = new BitmapFont();
		font.setColor(Color.BLACK);

		// Creation of the sprites, classic way
		Texture tex1 = new Texture(Gdx.files.internal("data/logo1.png"));
		Texture tex2 = new Texture(Gdx.files.internal("data/logo2.png"));
		Texture tex3 = new Texture(Gdx.files.internal("data/logo3.png"));
		Texture tex4 = new Texture(Gdx.files.internal("data/logo4.png"));
		tex1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		tex2.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		tex3.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		tex4.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		this.sprite1 = new Sprite(tex1);
		this.sprite2 = new Sprite(tex2);
		this.sprite3 = new Sprite(tex3);
		this.sprite4 = new Sprite(tex4);
		sprite1.setSize(1, 1);
		sprite2.setSize(1, 1);
		sprite3.setSize(1, 1);
		sprite4.setSize(1, 1);
		sprite1.setOrigin(0.5f, 0.5f);
		sprite2.setOrigin(0.5f, 0.5f);
		sprite3.setOrigin(0.5f, 0.5f);
		sprite4.setOrigin(0.5f, 0.5f);
		sprite1.setPosition((6f/5f)*1 - 3 - 0.5f, -0.5f);
		sprite2.setPosition((6f/5f)*2 - 3 - 0.5f, -0.5f);
		sprite3.setPosition((6f/5f)*3 - 3 - 0.5f, -0.5f);
		sprite4.setPosition((6f/5f)*4 - 3 - 0.5f, -0.5f);

		// Creation of a TweenManager
		Tween.setPoolEnabled(true);
		tweenManager = new TweenManager();

		// Creation of the Tweenables associated with the previous sprites.
		// I use a composition pattern instead of deriving from Sprite. This
		// allows an easier management of Tweenables and I can still use every
		// constructor of the Sprite class to define the sprites. Moreover,
		// some methods in libgdx (as TextureAtlas) are sprite factories, so
		// they create the sprites for you. Thus, you can only tween them with
		// such composition pattern.
		tweenSprite1 = new TweenSprite(sprite1);
		tweenSprite2 = new TweenSprite(sprite2);
		tweenSprite3 = new TweenSprite(sprite3);
		tweenSprite4 = new TweenSprite(sprite4);

		// Demo of the Tween.call possibility. It's just a timer :)
		text = "Idle (auto-start in 2 second)";
		Tween.call(new TweenCallback() {
			@Override public void tweenEventOccured(Types eventType, Tween tween) {
				start();
			}
		}).delay(2000).addToManager(tweenManager);
	}

	@Override
	public void render() {
		// Remember to update the tween manager periodically!
		tweenManager.update();

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(gl.GL_COLOR_BUFFER_BIT);

		sb.setProjectionMatrix(camera.combined);
		sb.begin();
		sprite1.draw(sb);
		sprite2.draw(sb);
		sprite3.draw(sb);
		sprite4.draw(sb);
		sb.end();

		sb.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sb.begin();
		font.setColor(Color.BLACK);
		font.draw(sb, text, 5, 45);
		font.setColor(0.5f, 0.5f, 0.5f, 1);
		font.draw(sb, "Running tweens: " + tweenManager.getTweenCount(), 5, 25);
		font.draw(sb, "Tweens in pool: " + Tween.getPoolSize(), 150, 25);
		sb.end();
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}

	// -------------------------------------------------------------------------
	// ANIMATION
	// -------------------------------------------------------------------------

	private TweenGroup buildAnimation(Tweenable target, int delay) {
		return TweenGroup.sequence(
			TweenGroup.parallel(
				Tween.set(target, TweenSprite.POSITION_XY).target(0, 0),
				Tween.set(target, TweenSprite.SCALE_XY).target(10, 10),
				Tween.set(target, TweenSprite.ROTATION).target(0),
				Tween.set(target, TweenSprite.OPACITY).target(0)
			),
			TweenGroup.tempo(delay),
			TweenGroup.parallel(
				Tween.to(target, TweenSprite.OPACITY, 1000, Quart.INOUT).target(1),
				Tween.to(target, TweenSprite.SCALE_XY, 1000, Quart.INOUT).target(1, 1)
			),
			TweenGroup.tempo(-500),
			Tween.to(target, TweenSprite.POSITION_XY, 1000, Back.OUT).targetCurrent(),
			Tween.to(target, TweenSprite.ROTATION, 800, Cubic.INOUT).target(360),
			TweenGroup.tempo(200),
			TweenGroup.parallel(
				Tween.to(target, TweenSprite.SCALE_XY, 300, Quad.IN).target(3, 3),
				Tween.to(target, TweenSprite.OPACITY, 300, Quad.IN).target(0)
			)
		);
	}

	private void start() {
		TweenGroup.sequence(
			// First, we show a message
			Tween.call(new TweenCallback() {
				private int cnt = 0;
				@Override public void tweenEventOccured(Types eventType, Tween tween) {
					text = "Current iteration: " + (++cnt)+ " / 5";
				}
			}),

			// Then, we animate the sprites
			TweenGroup.parallel(
				buildAnimation(tweenSprite1, 0),
				buildAnimation(tweenSprite2, 200),
				buildAnimation(tweenSprite3, 400),
				buildAnimation(tweenSprite4, 600)
			),

			// Finally, we invite the user to restart
			Tween.call(new TweenCallback() {
				private int cnt = 0;
				@Override public void tweenEventOccured(Types eventType, Tween tween) {
					if (++cnt == 5) {
						text = "Done! Click to restart";
						canBeRestarted = true;
					}
				}
			})
		).repeat(4, 0).addToManager(tweenManager);
	}

	// -------------------------------------------------------------------------
	// INPUT
	// -------------------------------------------------------------------------

	private InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if (canBeRestarted) {
				canBeRestarted = false;
				// If the user touches the screen, we kill every running tween
				// and restart the animation.
				tweenManager.kill(tweenSprite1);
				tweenManager.kill(tweenSprite2);
				tweenManager.kill(tweenSprite3);
				tweenManager.kill(tweenSprite4);
				start();
			}
			return true;
		}
	};
}