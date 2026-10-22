package net.agusdropout.bloodyhell.client.render.deferred.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.client.render.deferred.api.EffectState;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;

import java.util.List;

public class TaskQueueRenderManager extends QueuedRenderManager<Runnable> {
    public static final EffectState DEFAULT_STATE = new EffectState(
            () -> {
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableCull();
            },
            () -> {
                RenderSystem.depthMask(true);
                RenderSystem.disableBlend();
                RenderSystem.enableCull();
            });

    public TaskQueueRenderManager(String name) {
        this(name, DEFAULT_STATE);
    }

    public TaskQueueRenderManager(String name, EffectState state) {
        super(name, state);
    }

    public void queue(Runnable task) {
        enqueue(task);
    }

    @Override
    protected void draw(FrameContext frame, List<Runnable> tasks) {
        for (Runnable task : tasks) {
            task.run();
        }
    }
}
