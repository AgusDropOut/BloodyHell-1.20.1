package net.agusdropout.bloodyhell.client.render.effect.data;

import net.minecraft.core.Direction;

public record DecalData(int id, double x, double y, double z, Direction face, float rot, float size, float r, float g, float b, float a) {
}
