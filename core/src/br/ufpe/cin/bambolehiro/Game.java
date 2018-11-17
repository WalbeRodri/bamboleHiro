package br.ufpe.cin.bambolehiro;

import java.util.Iterator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

import br.ufpe.cin.bambolehiro.Entities.Actor;
import br.ufpe.cin.bambolehiro.Entities.Ring;

public class Game extends ApplicationAdapter {


	public interface IOpenActivity {
		void openScoreActivity(double score);
	}


	private Texture backgroundTexture;
	private Ring ringImage;
	private Ring ringPlusImage;
	private Texture hiroImage;
	private Texture lucyImage;
	private Sound dropSound;
	private Music stageMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle hiroRect;
	private Rectangle lucyRect;
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
	private double score;
	private String lucyPos = "right";
	private float lastRingPosition;
	private String bamboleStatus;
	private IBluetooth bluetoothCom;
	private Actor hiro;
	private Actor lucy;
	private Sprite RING;

	// animation
	private Texture hiroRight;
	private Texture hiroMid;
	private Texture hiroLeft;
	private Texture lucyRight;
	private Texture lucyLeft;
	private Animation<TextureRegion> hiroAnimationRight;
	private Animation<TextureRegion> hiroAnimationMid;
	private Animation<TextureRegion> hiroAnimationLeft;
	private Animation<TextureRegion> lucyAnimationRight;
	private Animation<TextureRegion> lucyAnimationLeft;
	private float elapsedTime;

	// dance movement ("0" = neutral, "1" = Center, "2" = Right, "3" = Left)
	private ObjectMap<String,Animation<TextureRegion>> hiroAnimations;
	private ObjectMap<String,Animation<TextureRegion>> lucyAnimations;
	private String danceMove;

	private IOpenActivity openActivity;
	private Preferences prefs;

	private float highScore;

	public void setOpenActivity(IOpenActivity callback) {
		openActivity = callback;
	}

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// load the images
		backgroundTexture = new Texture(Gdx.files.internal("bg.png"));
		hiro = new Actor("hiro_0.png");
		hiroImage = hiro.image;
		lucy = new Actor("lucy_0.png");
		lucyImage = lucy.image;

		lucySize = lucyImage.getHeight();
		hiroSize = hiroImage.getHeight();

		ringImage = new Ring(false);
		RING = new Sprite(ringImage.image);
		ringPlusImage = new Ring(true);

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop_sound.wav"));
		stageMusic = Gdx.audio.newMusic(Gdx.files.internal("bambole.mp3"));

		// start the playback of the background music immediately
		stageMusic.setLooping(false);
		stageMusic.play();

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
		hiroRect = new Rectangle();
		hiroRect.x = viewWidth / 2 - hiroSize / 2; // center the bucket horizontally
		hiroRect.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		hiroRect.width = hiroSize;
		hiroRect.height = hiroSize;

		lucyRect = new Rectangle();
		lucyRect.x = viewWidth / 2 - lucySize / 2; // center the bucket horizontally
		lucyRect.y = viewHeight - lucySize; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		lucyRect.width = lucySize;
		lucyRect.height = lucySize;

		// create the raindrops array and spawn the first raindrop
		rings = new Array<Rectangle>();

		// load all spritesheets for animation
		hiroRight = new Texture(Gdx.files.internal("hiro_dir_spritesheet.png"));
		hiroLeft = new Texture(Gdx.files.internal("hiro_esq_spritesheet.png"));
		hiroMid = new Texture(Gdx.files.internal("hiro_mid_spritesheet.png"));

		hiroAnimationMid = getAnimationBySpriteSheet(hiroMid, 3, 1);
		hiroAnimationRight = getAnimationBySpriteSheet(hiroRight, 3, 1);
		hiroAnimationLeft = getAnimationBySpriteSheet(hiroLeft, 3, 1);
		hiroAnimations = new ObjectMap<String, Animation<TextureRegion>>();

		hiroAnimations.put("1", hiroAnimationMid);
		hiroAnimations.put("2", hiroAnimationRight);
		hiroAnimations.put("3", hiroAnimationLeft);

		lucyRight = new Texture(Gdx.files.internal("lucy_dir_spritesheet.png"));
		lucyLeft = new Texture(Gdx.files.internal("lucy_esq_spritesheet.png"));

		lucyAnimationRight = getAnimationBySpriteSheet(lucyRight, 2, 1);
		lucyAnimationLeft = getAnimationBySpriteSheet(lucyLeft, 2, 1);
		lucyAnimations = new ObjectMap<String, Animation<TextureRegion>>();

		lucyAnimations.put("left", lucyAnimationLeft);
		lucyAnimations.put("right", lucyAnimationRight);

		elapsedTime = 0f;
		danceMove = "0";

		// simple state for store data
		prefs = Gdx.app.getPreferences("bambolehiro");
		prefs.putString("username", "UsuarioTeste");
		if (prefs.getString("highScore") == "") {
			highScore = 0;
		} else {
			highScore = Float.valueOf(prefs.getString("highScore"));
		}
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

		ring.x = lucyRect.x + (pos * 50); // ring appears on the left/right of Lucy
		Gdx.app.debug("mytag", ""+ ring.x);
		if (ring.x > lucyRect.x) {
			lucyPos = "right";
		} else {
			lucyPos = "left";
		}

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
		elapsedTime += Gdx.graphics.getDeltaTime();
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
				greenFont.draw(batch, "stream bambole: none", 0, 100);
			}
        } else {
            redFont.draw(batch, getBamboleStatus(), 0, 20);
        }

		whiteFont.draw(batch, "PONTOS: " + score, viewWidth/2 + viewWidth/4 , 20);

		TextureRegion lucyCurrentFrame = lucyAnimations.get(lucyPos).getKeyFrame(elapsedTime, true);
		batch.draw(lucyCurrentFrame, lucyRect.x, lucyRect.y);

		// ANIMATION
		if (danceMove.equals("0")) {
			batch.draw(hiroImage, hiroRect.x, hiroRect.y);
		} else {
			TextureRegion currentFrame = hiroAnimations.get(danceMove).getKeyFrame(elapsedTime, true);
			batch.draw(currentFrame, hiroRect.x, hiroRect.y);
		}

		for(Rectangle r: rings) {
			RING.draw(batch);
		}
		batch.end();

		// make sure Hiro stays within the screen bounds
		if(hiroRect.x < 0) hiroRect.x = 0;
		if(hiroRect.x > viewWidth - hiroSize) hiroRect.x = viewWidth - hiroSize;

		// make sure Lucy stays within the screen bounds
		if(lucyRect.x < 0) {
			lucyRect.x = 0;
			lucyPos = "right";
		}
		if(lucyRect.x > viewWidth - lucySize) {
			lucyRect.x = viewWidth - lucySize;
			lucyPos = "left";
		}

		// Lucy animation
		if (lucyPos == "right") lucyRect.x += 1;
		if (lucyPos == "left") lucyRect.x -= 1;

		// Hiro follows the rings position
		if (hiroRect.x > lastRingPosition) hiroRect.x -= 1;
		else if (hiroRect.x < 0) hiroRect.x += 1;
		else hiroRect.x += 1;

		// check if we need to create a new raindrop
		if(TimeUtils.millis() - lastDropTime > timeToSpawn) spawnRings();

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the latter case we play back
		// a sound effect as well.
		for (Iterator<Rectangle> iter = rings.iterator(); iter.hasNext(); ) {
			Rectangle r = iter.next();
			r.y -= ringVelocity * Gdx.graphics.getDeltaTime();
			RING.setBounds(r.x,r.y,r.width,r.height);
			RING.setOriginCenter();
			RING.rotate(10f);
			if(r.y + Ring.WIDTH < 0) iter.remove();
			if(r.overlaps(hiroRect)) {
				danceMove = MathUtils.random(1,3) + "";
				lastRingPosition = lucyRect.x;
				score+=5;
				dropSound.play();
				iter.remove();
			}
		}

		// Stage ending
		if (! (stageMusic.isPlaying())) {
			this.prefs.putString("score", String.valueOf(score));
			if (score > highScore) {
				this.prefs.putString("highScore", String.valueOf(score));
			}
			prefs.flush();
			this.stageClear(score);
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

		// ring objects
		ringImage.dispose();
		ringPlusImage.dispose();

		// hiro objects
		hiroImage.dispose();
		hiro.dispose();
		hiroLeft.dispose();
		hiroRight.dispose();
		hiroMid.dispose();

		// lucy objects
		lucyImage.dispose();
		lucy.dispose();
		lucyRight.dispose();
		lucyLeft.dispose();

		// music objects
		dropSound.dispose();
		stageMusic.dispose();

		// general objects
		greenFont.dispose();
		whiteFont.dispose();
		redFont.dispose();
		batch.dispose();
	}

	private Animation<TextureRegion> getAnimationBySpriteSheet(Texture spriteSheet, int FRAME_COLS, int FRAME_ROWS){
		TextureRegion[][] tmpFrames =
				TextureRegion.split(
						spriteSheet,
						spriteSheet.getWidth()/FRAME_COLS,
						spriteSheet.getHeight()/FRAME_ROWS);

		TextureRegion[] animationFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];

		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				animationFrames[index++] = tmpFrames[i][j];
			}
		}

		return new Animation<TextureRegion>(0.4f, animationFrames);
	}

	private void stageClear(double score) {
		// stage is over, open the Score Activity
		if (openActivity != null) {
			openActivity.openScoreActivity(score);
		}
	}

}

//Gdx.app.debug("mytag", "my debug message");