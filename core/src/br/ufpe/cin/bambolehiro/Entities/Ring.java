package br.ufpe.cin.bambolehiro.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import br.ufpe.cin.bambolehiro.Constants;

public class Ring {
    public static final int SIZE = Constants.RING_SIZE;
    public static int HEIGHT = Constants.RING_SIZE;
    public static int WIDTH = Constants.RING_SIZE;

    private int x;
    private int y;
    private int velocity = Constants.RING_VELOCITY;
    private int value;
    private boolean isPlus;

    private Pixmap image;
    private Texture rect;

    public Ring(boolean isPlus) {
        this.isPlus = isPlus;
        this.rect = this.create();
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

    public Texture create() {
        // resize textures
        if (this.isPlus) {
            image = new Pixmap(Gdx.files.internal("ring_plus.png"));
        } else {
            image = new Pixmap(Gdx.files.internal("ring.png"));
        }

        return resizeToPixels(image, WIDTH, HEIGHT);
    }

    public void draw(){

    }

    public void dispose(){
        this.rect.dispose();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Texture getRect() {
        return this.rect;
    }

    public int getVelocity() {
        return this.velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public void move(int step) {
        this.y -= 1;
    }
}
