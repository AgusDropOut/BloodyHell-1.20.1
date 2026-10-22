package net.agusdropout.bloodyhell.client.render.effect.data;

import org.joml.Matrix4f;

public record BillboardData(Matrix4f pose, float size, float r, float g, float b, float alpha, float time) {
}
