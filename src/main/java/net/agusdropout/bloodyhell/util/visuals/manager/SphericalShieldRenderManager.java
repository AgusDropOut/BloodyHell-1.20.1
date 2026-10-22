package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SphericalShieldRenderManager {

    private static final Queue<Runnable> RENDER_QUEUE = new ConcurrentLinkedQueue<>();
    private static final DeferredMatrices MATRICES = new DeferredMatrices();

    public static void queueRender(Runnable renderTask) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (RENDER_QUEUE.isEmpty()) {
            MATRICES.capture();
        }
        RENDER_QUEUE.add(renderTask);
    }

    public static void renderAllAndClear() {
        if (RENDER_QUEUE.isEmpty()) {
            return;
        }

        MATRICES.apply();

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();

        Runnable task;
        while ((task = RENDER_QUEUE.poll()) != null) {
            task.run();
        }

        MATRICES.restore();

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }
}
