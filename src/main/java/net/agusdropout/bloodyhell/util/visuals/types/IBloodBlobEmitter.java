package net.agusdropout.bloodyhell.util.visuals.types;

import org.joml.Vector3f;

public interface IBloodBlobEmitter {
    Vector3f getBlobCenter();
    Vector3f getBloodBaseColor();
    Vector3f getBloodGlowColor();
    float getChargeLevel();
    float getStabilizationLevel();
    boolean isActive();
}