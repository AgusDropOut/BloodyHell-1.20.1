package net.agusdropout.bloodyhell.util.visuals.types;

import net.minecraft.world.phys.Vec3;

public class MiniBlob {
    private Vec3 position;
    private float size;
    private float r;
    private float g;
    private float b;

    public MiniBlob(Vec3 position, float size, float r, float g, float b) {
        this.position = position;
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public float getSize() {
        return size;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }
}