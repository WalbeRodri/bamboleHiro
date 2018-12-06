package br.ufpe.cin.bambolehiro.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import br.ufpe.cin.bambolehiro.Constants;

public class Ring {
    public static final int SIZE = Constants.RING_SIZE;
    public static int HEIGHT = Constants.RING_SIZE;
    public static int WIDTH = Constants.RING_SIZE;

    public int x;
    public int y;
    private int velocity = Constants.RING_BASIC_VELOCITY;
    private int value;

    private Pixmap pixmap;
    public Texture image;
    private String type;

    public Ring(String type, boolean isPlus) {
        this.type = type;
        this.image = this.create(type, isPlus);
    }

    private Texture resizeToPixels(Pixmap image, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, image.getFormat());
        pixmap.drawPixmap(image,
                0, 0, image.getWidth(), image.getHeight(),
                0, 0, pixmap.getWidth(), pixmap.getHeight()
        );
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public Texture create(String type, boolean isPlus) {
        // resize textures
        if (isPlus) {
            this.value = 50;
            if (type.equals("left")) {
                pixmap = new Pixmap(Gdx.files.internal("ring_plus_esq.png"));
            } else if (type.equals("right")) {
                pixmap = new Pixmap(Gdx.files.internal("ring_plus_dir.png"));
            } else {
                // center
                pixmap = new Pixmap(Gdx.files.internal("ring_plus_cin.png"));
            }
        } else {
            this.value = 10;
            if (type.equals("left")) {
                pixmap = new Pixmap(Gdx.files.internal("hiro_next_left.png"));
            } else if (type.equals("right")) {
                pixmap = new Pixmap(Gdx.files.internal("hiro_next_right.png"));
            } else {
                // center
                pixmap = new Pixmap(Gdx.files.internal("hiro_next_mid.png"));
            }
        }

        return resizeToPixels(pixmap, WIDTH, HEIGHT);
    }

    public void dispose(){
        image.dispose();
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public void move(int step) {
        this.y -= step;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}
