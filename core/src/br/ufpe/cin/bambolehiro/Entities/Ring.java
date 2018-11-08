package br.ufpe.cin.bambolehiro.Entities;

import br.ufpe.cin.bambolehiro.Constants;

public class Ring {
    public static final int SIZE = Constants.RING_SIZE;
    public static int HEIGHT;
    public static int WIDTH;

    private int x;
    private int y;
    private int velocity = Constants.RING_VELOCITY;
    private int value;
    private boolean isPlus;

    public Ring(int x, int y, int value, boolean isPlus) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.isPlus = isPlus;
    }

    public void draw(){
        if (this.isPlus) {

        }

    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
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
