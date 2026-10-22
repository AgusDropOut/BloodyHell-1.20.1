package net.agusdropout.bloodyhell.client.render.deferred;

import net.agusdropout.bloodyhell.client.render.deferred.base.TaskQueueRenderManager;

public final class DeferredTaskQueues {
    public static final TaskQueueRenderManager ENTITY_GLITTER = new TaskQueueRenderManager("EntityGlitter");
    public static final TaskQueueRenderManager SHAPE_GLITTER = new TaskQueueRenderManager("ShapeGlitter");
    public static final TaskQueueRenderManager INSIGHT = new TaskQueueRenderManager("Insight");

    private DeferredTaskQueues() {}
}
