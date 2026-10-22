package net.agusdropout.bloodyhell.client.render.effect;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredMatrices;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SphericalShieldRenderManager implements DeferredRenderManager {

    private final Queue<Runnable> RENDER_QUEUE = new ConcurrentLinkedQueue<>();
    private final DeferredMatrices matrices = new DeferredMatrices();

    public void queueRender(Runnable renderTask) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (RENDER_QUEUE.isEmpty()) {
            matrices.capture();
        }
        RENDER_QUEUE.add(renderTask);
    }

    @Override
    public String name() {
        return "SphericalShield";
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (RENDER_QUEUE.isEmpty()) {
            return;
        }

        matrices.apply();

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();

        Runnable task;
        while ((task = RENDER_QUEUE.poll()) != null) {
            task.run();
        }

        matrices.restore();

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }
}
