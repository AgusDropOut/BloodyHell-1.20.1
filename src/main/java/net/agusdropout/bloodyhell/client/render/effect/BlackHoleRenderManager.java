package net.agusdropout.bloodyhell.client.render.effect;

import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.render.effect.api.ScreenSampledDraw;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredMatrices;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.effect.data.BlackHoleRenderData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class BlackHoleRenderManager implements DeferredRenderManager {
    private final List<BlackHoleRenderData> ACTIVE_BLACK_HOLES = new ArrayList<>();
    private final DeferredMatrices matrices = new DeferredMatrices();

    public void addBlackHole(ScreenSampledDraw draw, Camera camera, float partialTicks) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (ACTIVE_BLACK_HOLES.isEmpty()) {
            matrices.capture();
        }
        ACTIVE_BLACK_HOLES.add(new BlackHoleRenderData(draw, camera, partialTicks));
    }

    @Override
    public String name() {
        return "BlackHole";
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (ACTIVE_BLACK_HOLES.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        int screenTexId = mc.getMainRenderTarget().getColorTextureId();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.apply();

        RenderSystem.enableDepthTest();

        for (BlackHoleRenderData data : ACTIVE_BLACK_HOLES) {
            data.draw().draw(data.camera(), data.partialTicks(), screenTexId);
        }

        matrices.restore();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        ACTIVE_BLACK_HOLES.clear();
    }

}