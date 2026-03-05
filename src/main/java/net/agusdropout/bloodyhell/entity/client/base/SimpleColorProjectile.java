package net.agusdropout.bloodyhell.entity.client.base;

import org.joml.Vector3f;

public interface SimpleColorProjectile {
    Vector3f getBaseColor();
    Vector3f getHighlightColor();
    int getLifeTicks();
}
