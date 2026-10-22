package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;

import java.util.ArrayList;
import java.util.List;

public class TaskQueueRenderManager {
    private final List<Runnable> tasks = new ArrayList<>();
    private final DeferredMatrices matrices = new DeferredMatrices(VertexSorting.ORTHOGRAPHIC_Z);

    public void queue(Runnable task) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (tasks.isEmpty()) {
            matrices.capture();
        }
        tasks.add(task);
    }

    public void renderAndClear() {
        if (tasks.isEmpty()) return;

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        matrices.apply();

        for (Runnable task : tasks) {
            task.run();
        }

        matrices.restore();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();

        tasks.clear();
    }
}
