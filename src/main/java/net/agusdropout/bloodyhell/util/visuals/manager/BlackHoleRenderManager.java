package net.agusdropout.bloodyhell.util.visuals.manager;

import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.particle.custom.BlackHoleParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class BlackHoleRenderManager {
    private static final List<BlackHoleRenderData> ACTIVE_BLACK_HOLES = new ArrayList<>();
    private static final DeferredMatrices MATRICES = new DeferredMatrices(VertexSorting.ORTHOGRAPHIC_Z);

    public static void addBlackHole(BlackHoleParticle particle, Camera camera, float partialTicks) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (ACTIVE_BLACK_HOLES.isEmpty()) {
            MATRICES.capture();
        }
        ACTIVE_BLACK_HOLES.add(new BlackHoleRenderData(particle, camera, partialTicks));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_BLACK_HOLES.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        int screenTexId = mc.getMainRenderTarget().getColorTextureId();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        MATRICES.apply();

        RenderSystem.enableDepthTest();

        for (BlackHoleRenderData data : ACTIVE_BLACK_HOLES) {
            data.particle.doDeferredRender(data.camera, data.partialTicks, screenTexId);
        }

        MATRICES.restore();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        ACTIVE_BLACK_HOLES.clear();
    }

    private static class BlackHoleRenderData {
        BlackHoleParticle particle;
        Camera camera;
        float partialTicks;

        BlackHoleRenderData(BlackHoleParticle particle, Camera camera, float partialTicks) {
            this.particle = particle;
            this.camera = camera;
            this.partialTicks = partialTicks;
        }
    }
}