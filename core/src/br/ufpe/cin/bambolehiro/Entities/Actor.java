package br.ufpe.cin.bambolehiro.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Actor {

    public Texture image;
    public int x;
    public int y;
    public boolean isRunning;

    public Actor(String assetImage) {
        this.isRunning = false;
        this.image = create(assetImage);
    }

    public Texture getImage() {
        return image;
    }

    public void move(int step, String direction){
        // move horizontally
        if (direction == "right") {
            this.x += step;
        } else {
            this.x -= step;
        }
    }

    public Texture create(String assetImage){
        return new Texture(Gdx.files.internal(assetImage));
    }

    public void dispose(){
        this.image.dispose();
    }
}
