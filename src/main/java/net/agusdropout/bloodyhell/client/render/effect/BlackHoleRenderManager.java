package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.deferred.base.QueuedRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.api.ScreenSampledDraw;
import net.agusdropout.bloodyhell.client.render.effect.data.BlackHoleRenderData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

import java.util.List;

public class BlackHoleRenderManager extends QueuedRenderManager<BlackHoleRenderData> {

    public BlackHoleRenderManager() {
        super("BlackHole", EffectStates.BLACK_HOLE_SCREEN);
    }

    public void addBlackHole(ScreenSampledDraw draw, Camera camera, float partialTicks) {
        enqueue(new BlackHoleRenderData(draw, camera, partialTicks));
    }

    @Override
    protected void draw(FrameContext frame, List<BlackHoleRenderData> items) {
        int screenTexId = Minecraft.getInstance().getMainRenderTarget().getColorTextureId();

        RenderSystem.enableDepthTest();

        for (BlackHoleRenderData data : items) {
            data.draw().draw(data.camera(), data.partialTicks(), screenTexId);
        }
    }
}
