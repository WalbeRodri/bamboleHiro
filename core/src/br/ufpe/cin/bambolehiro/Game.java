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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

import br.ufpe.cin.bambolehiro.Entities.Actor;
import br.ufpe.cin.bambolehiro.Entities.Level;
import br.ufpe.cin.bambolehiro.Entities.MovementObject;
import br.ufpe.cin.bambolehiro.Entities.Ring;

public class Game extends ApplicationAdapter {


	public interface IOpenActivity {
		void openScoreActivity(int score);
	}

	public interface IBluetooth {
		boolean readBLEData();
		String getLevel();
		boolean isConnected();
		String getBLEData();
	}


	private Texture backgroundTexture;
	private Ring ringNormal;
	private Ring ringPlus;
	private Array<Sprite> ringImages;
	private Texture hiroImage;
	private Sound dropSound;
	private Music stageMusic;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private Rectangle hiroRect;
	private Array<MovementObject> MOVEMENTS;
	private long lastDropTime;
	private int hiroSize;
	private final int viewWidth = Constants.GAME_WIDTH;
	private final int viewHeight = Constants.GAME_HEIGHT;
	private int ringVelocity;
	private int timeToSpawn;
	private BitmapFont whiteFont;
	private BitmapFont whiteFontLarge;
	private BitmapFont greenFont;
	private BitmapFont redFont;
	private int score;
	private float lastRingPosition;
	private String bamboleStatus;
	private Actor hiro;
	private Actor nextMid;
	private Actor nextLeft;
	private Actor nextRight;
	private Sprite RING;
	private Array<String> movementsArray;

	// animation
	private Texture hiroRight;
	private Texture hiroMid;
	private Texture hiroLeft;
	private Animation<TextureRegion> hiroAnimationRight;
	private Animation<TextureRegion> hiroAnimationMid;
	private Animation<TextureRegion> hiroAnimationLeft;
	private float elapsedTime;

	// dance movement ("0" = neutral, "1" = Center, "2" = Right, "3" = Left)
	private ObjectMap<String,Animation<TextureRegion>> hiroAnimations;
	private String danceMove;


	private float highScore;
	private long lastScoreUpdate;
	private int regressionTime;
	private long lastRegressionTime;

	private String ringPos;
	private boolean isExtraPoint;
	private boolean isBounce;

	private Level level;
	private String levelName;


	// Native interfaces
	private IBluetooth bambole;
	private IOpenActivity openActivity;

	// local storage
	private Preferences prefs;

	// feedback
	private String feedbackText;
	private String music;
	private int musicDuration;
	private boolean isFalling;

	private boolean isRunning;
	private Array<Integer> movements;

	public void setOpenActivity(IOpenActivity callback) {
		openActivity = callback;
	}
	public void setBluetoothInterface(IBluetooth interfaceBluetooth){ bambole = interfaceBluetooth; }

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		movementsArray = new Array<String>();
		movementsArray.add("center");
		movementsArray.add("right");
		movementsArray.add("left");


		// load the images
		backgroundTexture = new Texture(Gdx.files.internal("bg.png"));
		hiro = new Actor("hiro_0.png");
		hiroImage = hiro.image;
		hiroSize = hiroImage.getHeight();

		nextMid = new Actor("hiro_next_mid.png");
		nextLeft = new Actor("hiro_next_left.png");
		nextRight = new Actor("hiro_next_right.png");

		ringImages = this.createRingImages();
		ringNormal = new Ring("center", false);
		ringPlus = new Ring("center", true);

		RING = new Sprite(ringImages.get(MathUtils.random(0, 2)));

		// LEVEL
		level = new Level();
		ObjectMap<String, String> levelData = level.getLevelByDifficulty("1");

		ringVelocity = Integer.valueOf(levelData.get("velocity"));
		timeToSpawn = Integer.valueOf(levelData.get("dropRingDuration"));
		regressionTime = Integer.valueOf(levelData.get("regression"));
		music = levelData.get("music");
		musicDuration = Integer.valueOf(levelData.get("musicDuration"));
		movements = this.createMovements(levelData.get("movements").contains("0"));

//		Gdx.app.debug("mytag", "KEYS:" +levelData.size);

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop_sound.wav"));
		stageMusic = Gdx.audio.newMusic(Gdx.files.internal(music));

		// start the playback of the background music immediately
		stageMusic.setLooping(false);

		// load Font
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Asap-Bold.ttf"));
		whiteFont = Utils.createFont(fontGenerator, 24, Color.WHITE);
		whiteFontLarge = Utils.createFont(fontGenerator, 48, Color.WHITE);
		greenFont = Utils.createFont(fontGenerator, 24, Color.GREEN);
		redFont = Utils.createFont(fontGenerator, 24, Color.RED);
		fontGenerator.dispose(); // avoid memory leaks

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, viewWidth, viewHeight);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		// create a Rectangle to logically represent the bucket
		hiroRect = new Rectangle();
		hiroRect.x = viewWidth / 2; // center the bucket horizontally
		hiroRect.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		hiroRect.width = hiroSize;
		hiroRect.height = hiroSize;


		// create the raindrops array and spawn the first raindrop
		MOVEMENTS = new Array<MovementObject>();

		// load all spritesheets for animation
		hiroRight = new Texture(Gdx.files.internal("hiro_dir_spritesheet.png"));
		hiroLeft = new Texture(Gdx.files.internal("hiro_esq_spritesheet.png"));
		hiroMid = new Texture(Gdx.files.internal("hiro_mid_spritesheet.png"));

		hiroAnimationMid = getAnimationBySpriteSheet(hiroMid, 4, 2);
		hiroAnimationRight = getAnimationBySpriteSheet(hiroRight, 3, 2);
		hiroAnimationLeft = getAnimationBySpriteSheet(hiroLeft, 4, 2);
		hiroAnimations = new ObjectMap<String, Animation<TextureRegion>>();

		hiroAnimations.put("1", hiroAnimationMid);
		hiroAnimations.put("2", hiroAnimationRight);
		hiroAnimations.put("3", hiroAnimationLeft);

		elapsedTime = 0f;
		danceMove = "1";
		ringPos = "1";
		isFalling = false;

		// simple state for store data
		prefs = Gdx.app.getPreferences("bambolehiro");
//		prefs.clear();
		prefs.putString("username", "UsuarioTeste");
		if (prefs.getString("highScore").equals("")) {
			highScore = 0;
		} else {
			highScore = Float.valueOf(prefs.getString("highScore"));
		}
	}

	private String getBamboleStatus() {
		bamboleStatus = bambole.isConnected() ? "[LIGADO]" : "[DESLIGADO]";
		return bamboleStatus;
	}

	private void spawnRings() {
		Rectangle ring = new Rectangle();

		ring.x = ((viewWidth / 4) / 2) - 64; // ring appears on the center the screen

		int extra = MathUtils.random(1,10);
		isExtraPoint = extra % 5 == 0;

		int pos = MathUtils.random(0,2);
		String type = movementsArray.get(pos);

		ring.y = viewHeight;
		ring.width = Ring.WIDTH;
		ring.height = Ring.HEIGHT;

		MovementObject obj = new MovementObject(ring, new Ring(type, isExtraPoint));
		MOVEMENTS.add(obj);

		lastDropTime = TimeUtils.millis();
		lastScoreUpdate = TimeUtils.millis();
		lastRingPosition = ring.x;
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
//		Gdx.app.debug("mytag", ""+bluetoothCom.readBLEData());

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the assets
		batch.begin();
		batch.draw(backgroundTexture, -20 , 0, viewWidth+40, viewHeight);

		if (bambole.isConnected()) {
            greenFont.draw(batch, getBamboleStatus(), viewWidth/2 + viewWidth/4, viewHeight);
        } else {
            redFont.draw(batch, getBamboleStatus(), viewWidth/2 + viewWidth/4, viewHeight);
        }

		// DEBUG GIROU
		// greenFont.draw(batch, "GIROU: " + getBounce(), 0, 100);

		whiteFont.draw(batch, "PONTOS: " + score, viewWidth/2 + viewWidth/4 , 20);

//		if(TimeUtils.millis() - lastDropTime > timeToSpawn) spawnRings();
//		whiteFont.draw(batch, musicDuration+"", viewWidth/2 , 20);

		isRunning = bambole.isConnected() ? true : true;

		// Rings spawned
		for(MovementObject mov: MOVEMENTS) {
			// dance moves: "0" = neutral, "1" = Center, "2" = Right, "3" = Left
			Rectangle r = mov.rect;
			if (isExtraPoint) {
				if (ringPos.equals("1")) batch.draw(ringImages.get(3), r.x, r.y);
				else if (ringPos.equals("2")) batch.draw(ringImages.get(4), r.x, r.y);
				else if (ringPos.equals("3")) batch.draw(ringImages.get(5), r.x, r.y);
			} else {
				if (ringPos.equals("1")) batch.draw(ringImages.get(0), r.x, r.y);
				else if (ringPos.equals("2")) batch.draw(ringImages.get(1), r.x, r.y);
				else if (ringPos.equals("3")) batch.draw(ringImages.get(2), r.x, r.y);
			}

//			RING.draw(batch);
		}

		// ANIMATION
		// Hiro stay on the center of the screen
		hiroRect.x = (viewWidth/4 * 3) - hiroSize/2;

		if (regressionTime > 0) {
			batch.draw(hiroImage, hiroRect.x, hiroRect.y);
		} else if (danceMove != null) {
			TextureRegion currentFrame = hiroAnimations.get(danceMove).getKeyFrame(elapsedTime, isRunning);
			batch.draw(currentFrame, hiroRect.x, hiroRect.y);
		}

		// Countdown on screen before game starting
		if ((regressionTime > 0) && (isRunning)) {
			whiteFontLarge.draw(batch, regressionTime+"", viewWidth/2, viewHeight/2);
			if(!stageMusic.isPlaying()) {
				stageMusic.play();
			}
		}

		batch.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLUE);
		shapeRenderer.line(viewWidth/4, viewHeight, viewWidth/4, 0);
		shapeRenderer.end();


		if (regressionTime > 0) {
			if (TimeUtils.millis() - lastRegressionTime > 1000) {
				regressionTime--;
				lastRegressionTime = TimeUtils.millis();
			}
			if (! (stageMusic.isPlaying()) ){
				stageMusic.pause();
			}
			return;
		}

		// PAUSE GAME IF bambole is disconnected
		if (!(isRunning)){
			stageMusic.pause();
			return;
		}



		// check if we need to create a new ring (10s delay)
		if(TimeUtils.millis() - lastDropTime > timeToSpawn) spawnRings();

		// update score according to ring (1s delay)
		if(TimeUtils.millis() - lastScoreUpdate > 1000) {
			if (isExtraPoint && this.getBounce()) {
				score += ringPlus.getValue();
			} else if (!(isExtraPoint) && this.getBounce()){
				score += ringNormal.getValue();
			} else {
				score++;
			}
			setBounce(false);
			lastScoreUpdate = TimeUtils.millis();
		}

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the latter case we play back
		// a sound effect as well.
		for (Iterator<MovementObject> iter = MOVEMENTS.iterator(); iter.hasNext(); ) {
			MovementObject mov = iter.next();
			Rectangle r = mov.rect;
			r.y -= ringVelocity * Gdx.graphics.getDeltaTime();

//			RING.setBounds(r.x,r.y,r.width,r.height);
			RING.setBounds(viewWidth/2, viewHeight/2, r.width, r.height);


			ringPos = MathUtils.random(1,3)+"";
			isFalling = true;

			if(r.y + Ring.WIDTH < 0) iter.remove();

			if(r.y < 0) {
				isFalling = false;
				if (ringPos.equals("1")) {
					if (isExtraPoint) {
						RING.set(ringImages.get(3));
					} else {
						RING.set(ringImages.get(0));
					}
					danceMove = "1";
				} else if (ringPos.equals("2")) {
					if (isExtraPoint) {
						RING.set(ringImages.get(4));
					} else {
						RING.set(ringImages.get(1));
					}
					danceMove = "2";
				} else {
					if (isExtraPoint) {
						RING.set(ringImages.get(5));
					} else {
						RING.set(ringImages.get(2));
					}
					danceMove = "3";
				}
				lastRingPosition = viewWidth/2;
				dropSound.play();

				iter.remove();
			}
		}

		// each 1 second decrease the music duration
		if(TimeUtils.millis() - lastScoreUpdate > 1000) musicDuration--;

		// Stage ending
		if (! (stageMusic.isPlaying())) {
			this.prefs.putString("score", String.valueOf(score));
			if (score > highScore) {
				this.prefs.putString("highScore", String.valueOf(score));
			}
			prefs.flush();
			this.stageClear(score);
		}


		// continue the music
		// if (!(stageMusic.isPlaying())) {
		//		stageMusic.play();
		// }
	}

	private boolean getBounce() {
		isBounce = bambole.readBLEData() && bambole.isConnected();
		return isBounce;
	}

	private void setBounce(boolean value) {
		isBounce = value;
	}


	@Override
	public void dispose() {
		// dispose of all the native resources

		// ring objects
		ringNormal.dispose();

		// hiro objects
		hiroImage.dispose();
		hiro.dispose();
		hiroLeft.dispose();
		hiroRight.dispose();
		hiroMid.dispose();

		// music objects
		dropSound.dispose();
		stageMusic.dispose();

		// general objects
		greenFont.dispose();
		whiteFont.dispose();
		redFont.dispose();
		batch.dispose();
		shapeRenderer.dispose();
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

	private void stageClear(int score) {
		// stage is over, open the Score Activity
		if (openActivity != null) {
			openActivity.openScoreActivity(score);
		}
	}

	private Array<Sprite> createRingImages() {
		Array<Sprite> tmp = new Array<Sprite>();
		tmp.add(new Sprite(new Ring("center", false).image));
		tmp.add(new Sprite(new Ring("right", false).image));
		tmp.add(new Sprite(new Ring("left", false).image));

		tmp.add(new Sprite(new Ring("center", true).image));
		tmp.add(new Sprite(new Ring("right", true).image));
		tmp.add(new Sprite(new Ring("left", true).image));

		return tmp;
	}

	private Array<Integer> createMovements(boolean allPositions) {
		// this function generates 1K of random movements
		// movements: 1 - center, 2 - right, 3 - left
		Array<Integer> movements = new Array<Integer>();
		for (int i=0;i<1000;i++) {
			int movement = allPositions ? MathUtils.random(1, 3) : MathUtils.random(2, 3);
			movements.add(movement);
		}

		return movements;
	}

}

//Gdx.app.debug("mytag", "my debug message");