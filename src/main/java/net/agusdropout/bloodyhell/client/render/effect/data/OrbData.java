package net.agusdropout.bloodyhell.client.render.effect.data;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public record OrbData(Matrix4f pose, Matrix4f projection, Matrix3f rotation) {
}
