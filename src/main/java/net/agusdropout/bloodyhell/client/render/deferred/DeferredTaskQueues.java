package net.agusdropout.bloodyhell.client.render.deferred;

import net.agusdropout.bloodyhell.client.render.deferred.base.TaskQueueRenderManager;

public final class DeferredTaskQueues {
    public static final TaskQueueRenderManager ENTITY_GLITTER = new TaskQueueRenderManager();
    public static final TaskQueueRenderManager SHAPE_GLITTER = new TaskQueueRenderManager();
    public static final TaskQueueRenderManager INSIGHT = new TaskQueueRenderManager();

    private DeferredTaskQueues() {}
}
