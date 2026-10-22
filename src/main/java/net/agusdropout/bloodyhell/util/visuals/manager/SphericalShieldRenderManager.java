package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import org.joml.Matrix4f;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SphericalShieldRenderManager {

    private static final Queue<Runnable> RENDER_QUEUE = new ConcurrentLinkedQueue<>();
    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void queueRender(Runnable renderTask) {
        if (RenderPassGuard.shouldSkipDeferred()) return;
        if (RENDER_QUEUE.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(RenderSystem.getModelViewMatrix());
        }
        RENDER_QUEUE.add(renderTask);
    }

    public static void renderAllAndClear() {
        if (RENDER_QUEUE.isEmpty()) {
            return;
        }

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();

        Runnable task;
        while ((task = RENDER_QUEUE.poll()) != null) {
            task.run();
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }
}