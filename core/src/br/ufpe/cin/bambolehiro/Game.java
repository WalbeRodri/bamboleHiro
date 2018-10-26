package br.ufpe.cin.bambolehiro;

import java.util.Iterator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Game extends ApplicationAdapter {
	private Texture backgroundTexture;
	private Texture ringImage;
	private Pixmap ringImageOrigin;
	private Texture hiroImage;
	private Pixmap hiroImageOrigin;
	private Texture lucyImage;
	private Pixmap lucyImageOrigin;
	private Sound dropSound;
	private Music backgroundMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle hiro;
	private Rectangle lucy;
	private Array<Rectangle> rings;
	private long lastDropTime;
	private final int ringSize = 32;
	private final int hiroSize = 128;
	private final int lucySize = 128;
	private final int ringVelocity = 50;
	private final int viewWidth = 800;
	private final int viewHeight = 480;
	private final int timeToSpawn = 10000; // in milliseconds
	private BitmapFont font;
	private int totalPoints;
	private String tipMessage = "Aguarde até Hiro tocar no Bambolê";
	private String lucyPos = "right";
	private float lastRingPosition;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// load the images for the droplet and the bucket
		backgroundTexture = new Texture(Gdx.files.internal("bg.png"));
		ringImageOrigin = new Pixmap(Gdx.files.internal("ring.png"));
		hiroImageOrigin = new Pixmap(Gdx.files.internal("Hiro_Neutro.png"));
		lucyImageOrigin = new Pixmap(Gdx.files.internal("Lucy_Neutro.png"));

		// resize textures
		ringImage = resizeToPixels(ringImageOrigin, ringSize);
		hiroImage = resizeToPixels(hiroImageOrigin, hiroSize);
		lucyImage = resizeToPixels(lucyImageOrigin, lucySize);

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop_sound.wav"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("bambole.mp3"));

		// start the playback of the background music immediately
		backgroundMusic.setLooping(true);
		backgroundMusic.play();

		// load Font
		font = new BitmapFont(); // Arial default
		font.getData().setScale(2, 2);

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, viewWidth, viewHeight);
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the bucket
		hiro = new Rectangle();
		hiro.x = viewWidth / 2 - hiroSize / 2; // center the bucket horizontally
		hiro.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		hiro.width = hiroSize;
		hiro.height = hiroSize;

		lucy = new Rectangle();
		lucy.x = viewWidth / 2 - lucySize / 2; // center the bucket horizontally
		lucy.y = viewHeight - lucySize; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		lucy.width = lucySize;
		lucy.height = lucySize;

		// create the raindrops array and spawn the first raindrop
		rings = new Array<Rectangle>();
		spawnRings();
	}

	private void spawnRings() {
		Rectangle ring = new Rectangle();
		int pos = MathUtils.random(-1,1);
		if (pos == 0) pos = 1;
		if (pos == 1) pos = 2;

		ring.x = lucy.x + (pos * 50); // ring appears on the left/right of Lucy
		if(ring.x < 0) ring.x = 0;
		if(ring.x > viewWidth + hiroSize) ring.x = viewWidth - hiroSize;

		ring.y = viewHeight - lucySize;
		ring.width = ringSize;
		ring.height = ringSize;
		rings.add(ring);
		lastDropTime = TimeUtils.millis();
		lastRingPosition = ring.x;
	}

	private Texture resizeToPixels(Pixmap image, int pixels) {
		Pixmap pixmap = new Pixmap(pixels, pixels, image.getFormat());
		pixmap.drawPixmap(image,
				0, 0, image.getWidth(), image.getHeight(),
				0, 0, pixmap.getWidth(), pixmap.getHeight()
		);
		Texture texture = new Texture(pixmap);
		pixmap.dispose();

		return texture;
	}

	@Override
	public void render() {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and
		// all drops
		batch.begin();
		batch.draw(backgroundTexture, -20 , 0, viewWidth+40, viewHeight);
		font.draw(batch, "PONTOS: " + totalPoints, viewWidth/2 + 200 , 40);
		batch.draw(lucyImage, lucy.x, lucy.y);
		batch.draw(hiroImage, hiro.x, hiro.y);
		for(Rectangle r: rings) {
			batch.draw(ringImage, r.x, r.y);
		}
		batch.end();

		// process user input
		// if(Gdx.input.isTouched()) {
		//	Vector3 touchPos = new Vector3();
		//	touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		//	camera.unproject(touchPos);
		//  hiro.x = touchPos.x - hiroSize / 2;
		// }

		// if(Gdx.input.isKeyPressed(Keys.LEFT)) hiro.x -= 200 * Gdx.graphics.getDeltaTime();
		// if(Gdx.input.isKeyPressed(Keys.RIGHT)) hiro.x += 200 * Gdx.graphics.getDeltaTime();

		// make sure Hiro stays within the screen bounds
		if(hiro.x < 0) hiro.x = 0;
		if(hiro.x > viewWidth - hiroSize) hiro.x = viewWidth - hiroSize;

		// make sure Lucy stays within the screen bounds
		if(lucy.x < 0) {
			lucy.x = 0;
			lucyPos = "right";
		}
		if(lucy.x > viewWidth - lucySize) {
			lucy.x = viewWidth - lucySize;
			lucyPos = "left";
		}

		// Lucy animation
		if (lucyPos == "right") lucy.x += 1;
		if (lucyPos == "left") lucy.x -= 1;

		// Hiro follows the rings position
		if (hiro.x > lastRingPosition) hiro.x -= 1;
		else if (hiro.x < 0) hiro.x += 1;
		else hiro.x += 1;

		// check if we need to create a new raindrop
		if(TimeUtils.millis() - lastDropTime > timeToSpawn) spawnRings();

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the latter case we play back
		// a sound effect as well.
		for (Iterator<Rectangle> iter = rings.iterator(); iter.hasNext(); ) {
			Rectangle r = iter.next();
			r.y -= ringVelocity * Gdx.graphics.getDeltaTime();
			if(r.y + ringSize < 0) iter.remove();
			if(r.overlaps(hiro)) {
				lastRingPosition = lucy.x;
				totalPoints+=5;
				dropSound.play();
				iter.remove();
			}
		}
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		ringImage.dispose();
		hiroImage.dispose();
		dropSound.dispose();
		font.dispose();
		backgroundMusic.dispose();
		batch.dispose();
	}
}

//Gdx.app.debug("mytag", "my debug message");