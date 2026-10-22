package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class TaskQueueRenderManager {
    private final List<Runnable> tasks = new ArrayList<>();
    private final Matrix4f savedProjection = new Matrix4f();
    private final Matrix4f savedModelView = new Matrix4f();

    public void queue(Runnable task) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (tasks.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
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

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();

        for (Runnable task : tasks) {
            task.run();
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();

        tasks.clear();
    }
}
