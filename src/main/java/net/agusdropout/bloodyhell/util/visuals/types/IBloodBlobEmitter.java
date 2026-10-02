package net.agusdropout.bloodyhell.util.visuals.types;

import org.joml.Vector3f;

public interface IBloodBlobEmitter {
    boolean isActive();
    Vector3f getBlobCenter();
    Vector3f getBloodBaseColor();
    Vector3f getBloodGlowColor();
    float getChargeLevel();
    float getStabilizationLevel();

    default float getExplosionProgress() { return 0.0f; }
    default float getSpasmIntensity() { return 0.0f; }
    default float getHeartbeatPulse() { return 0.0f; }
}