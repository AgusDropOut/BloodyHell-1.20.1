package net.agusdropout.bloodyhell.util.visuals.manager;

public final class DeferredTaskQueues {
    public static final TaskQueueRenderManager ENTITY_GLITTER = new TaskQueueRenderManager();
    public static final TaskQueueRenderManager SHAPE_GLITTER = new TaskQueueRenderManager();

    private DeferredTaskQueues() {}
}
