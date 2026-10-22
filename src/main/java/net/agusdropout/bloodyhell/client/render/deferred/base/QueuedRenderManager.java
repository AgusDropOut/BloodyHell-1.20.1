package net.agusdropout.bloodyhell.client.render.deferred.base;

import net.agusdropout.bloodyhell.client.render.deferred.DeferredMatrices;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.EffectState;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;

import java.util.ArrayList;
import java.util.List;

public abstract class QueuedRenderManager<T> implements DeferredRenderManager {
    private final String name;
    private final EffectState state;
    private final List<T> queue = new ArrayList<>();
    private final DeferredMatrices matrices = new DeferredMatrices();

    protected QueuedRenderManager(String name, EffectState state) {
        this.name = name;
        this.state = state;
    }

    @Override
    public final String name() {
        return name;
    }

    protected final void enqueue(T item) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (queue.isEmpty()) {
            matrices.capture();
        }
        queue.add(item);
    }

    protected void applyView(DeferredMatrices matrices, FrameContext frame) {
        matrices.apply();
    }

    protected abstract void draw(FrameContext frame, List<T> items);

    @Override
    public final void renderAndClear(FrameContext frame) {
        if (queue.isEmpty()) return;

        state.begin().run();
        applyView(matrices, frame);

        draw(frame, queue);

        matrices.restore();
        state.end().run();

        queue.clear();
    }
}
