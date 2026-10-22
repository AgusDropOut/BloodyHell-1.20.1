package net.agusdropout.bloodyhell.client.render.effect.api;

import net.minecraft.client.Camera;

@FunctionalInterface
public interface ScreenSampledDraw {
    void draw(Camera camera, float partialTicks, int screenTextureId);
}
