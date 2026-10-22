package net.agusdropout.bloodyhell.client.render.effect.data;

import net.agusdropout.bloodyhell.client.render.effect.api.ScreenSampledDraw;
import net.minecraft.client.Camera;

public record BlackHoleRenderData(ScreenSampledDraw draw, Camera camera, float partialTicks) {
}
