package br.ufpe.cin.bambolehiro;

import java.util.Iterator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import br.ufpe.cin.bambolehiro.Entities.Ring;

public class Game extends ApplicationAdapter {
	private Texture backgroundTexture;
	private Ring ringImage;
	private Ring ringPlusImage;
	private Texture hiroImage;
	private Texture lucyImage;
	private Sound dropSound;
	private Music backgroundMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle hiro;
	private Rectangle lucy;
	private Array<Rectangle> rings;
	private long lastDropTime;
	private int hiroSize;
	private int lucySize;
	private final int ringVelocity = Constants.RING_VELOCITY;
	private final int viewWidth = Constants.GAME_WIDTH;
	private final int viewHeight = Constants.GAME_HEIGHT;
	private final int timeToSpawn = Constants.DROP_RING_DURATION;
	private BitmapFont whiteFont;
	private BitmapFont greenFont;
	private BitmapFont redFont;
	private int totalPoints;
	private String lucyPos = "right";
	private float lastRingPosition;
	private String bamboleStatus;
	private IBluetooth bluetoothCom;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// load the images
		backgroundTexture = new Texture(Gdx.files.internal("bg.png"));
		hiroImage = new Texture(Gdx.files.internal("hiro_0.png"));
		lucyImage = new Texture(Gdx.files.internal("lucy_0.png"));

		lucySize = lucyImage.getHeight();
		hiroSize = hiroImage.getHeight();

		ringImage = new Ring(false);
		ringPlusImage = new Ring(true);

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop_sound.wav"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("bambole.mp3"));

		// start the playback of the background music immediately
		backgroundMusic.setLooping(true);
		backgroundMusic.play();

		// load Font
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Asap-Bold.ttf"));
		whiteFont = createFont(fontGenerator, 24, Color.WHITE);
		greenFont = createFont(fontGenerator, 24, Color.GREEN);
		redFont = createFont(fontGenerator, 24, Color.RED);
		fontGenerator.dispose(); // avoid memory leaks

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
	}

	private String getBamboleStatus() {
		bamboleStatus = "BAMBOLE ";
		if (true) {
			bamboleStatus += "CONECTADO";
		} else {
			bamboleStatus += "DESCONECTADO";
		}
		return bamboleStatus;
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
		ring.width = Ring.WIDTH;
		ring.height = Ring.HEIGHT;
		rings.add(ring);
		lastDropTime = TimeUtils.millis();
		lastRingPosition = ring.x;
	}

	private BitmapFont createFont(FreeTypeFontGenerator generator, int size, Color colorName) {
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = size;
		parameter.color = colorName;
		parameter.borderWidth = 2;
		return generator.generateFont(parameter);
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

		if (getBLEStatus()) {
            greenFont.draw(batch, getBamboleStatus(), 0, 20);
            try{
				greenFont.draw(batch, ""+bluetoothCom.isConnected(), 20, 20);
            } catch (Exception e){
				greenFont.draw(batch, "none", 0, 100);
			}
        } else {
            redFont.draw(batch, getBamboleStatus(), 0, 20);
        }

		whiteFont.draw(batch, "PONTOS: " + totalPoints, viewWidth/2 + viewWidth/4 , 20);
		batch.draw(lucyImage, lucy.x, lucy.y);
		batch.draw(hiroImage, hiro.x, hiro.y);
		for(Rectangle r: rings) {
			batch.draw(ringImage.getRect(), r.x, r.y);
		}
		batch.end();

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
			if(r.y + Ring.WIDTH < 0) iter.remove();
			if(r.overlaps(hiro)) {
				lastRingPosition = lucy.x;
				totalPoints+=5;
				dropSound.play();
				iter.remove();
			}
		}
	}

	private boolean getBLEStatus() {
        // method to check if BLE is connected
	    return true;
    }

	public void setBluetoothInterface(IBluetooth interfaceBluetooth)
	{
		bluetoothCom = interfaceBluetooth;
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		ringImage.dispose();
		ringPlusImage.dispose();
		hiroImage.dispose();
		dropSound.dispose();
		greenFont.dispose();
		whiteFont.dispose();
		redFont.dispose();
		backgroundMusic.dispose();
		batch.dispose();
	}
}

//Gdx.app.debug("mytag", "my debug message");