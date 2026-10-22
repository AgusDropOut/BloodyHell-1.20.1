package net.agusdropout.bloodyhell.client.render.deferred.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredMatrices;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;

import java.util.ArrayList;
import java.util.List;

public class TaskQueueRenderManager implements DeferredRenderManager {
    private final String name;
    private final List<Runnable> tasks = new ArrayList<>();
    private final DeferredMatrices matrices = new DeferredMatrices();

    public TaskQueueRenderManager(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    public void queue(Runnable task) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (tasks.isEmpty()) {
            matrices.capture();
        }
        tasks.add(task);
    }

    @Override
    public void renderAndClear(FrameContext frame) {
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
