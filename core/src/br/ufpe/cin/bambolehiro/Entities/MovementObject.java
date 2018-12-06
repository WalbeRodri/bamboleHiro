package br.ufpe.cin.bambolehiro.Entities;

import com.badlogic.gdx.math.Rectangle;

public class MovementObject {
    public Rectangle rect;
    public Ring ring;

    public MovementObject(Rectangle rect, Ring ring) {
        this.rect = rect;
        this.ring = ring;
    }


}
