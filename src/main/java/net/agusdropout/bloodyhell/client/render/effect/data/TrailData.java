package net.agusdropout.bloodyhell.client.render.effect.data;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public record TrailData(List<Vec3> history, Vec3 cameraPos, float width, float r, float g, float b, float alpha, float time) {
}
