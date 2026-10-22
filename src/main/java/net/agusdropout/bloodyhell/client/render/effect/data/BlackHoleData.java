package net.agusdropout.bloodyhell.client.render.effect.data;

import org.joml.Matrix4f;

public record BlackHoleData(Matrix4f pose, float radius, float time, float alpha, int color) {
}
