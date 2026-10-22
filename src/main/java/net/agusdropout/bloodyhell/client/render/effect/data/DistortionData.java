package net.agusdropout.bloodyhell.client.render.effect.data;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record DistortionData(Matrix4f pose, float size, Vector3f color, float alpha, float time, Quaternionf camRot) {
    public DistortionData {
        camRot = (camRot != null) ? new Quaternionf(camRot) : null;
    }
}
