package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import org.joml.Matrix4f;

public final class DeferredMatrices {
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f modelView = new Matrix4f();
    private final Matrix4f previousProjection = new Matrix4f();
    private VertexSorting previousSorting = VertexSorting.DISTANCE_TO_ORIGIN;

    public void capture() {
        projection.set(RenderSystem.getProjectionMatrix());
        modelView.set(RenderSystem.getModelViewMatrix());
    }

    public void applyProjection() {
        previousProjection.set(RenderSystem.getProjectionMatrix());
        previousSorting = RenderSystem.getVertexSorting();
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.setProjectionMatrix(projection, VertexSorting.DISTANCE_TO_ORIGIN);
    }

    public void apply() {
        applyProjection();
        PoseStack stack = RenderSystem.getModelViewStack();
        stack.setIdentity();
        stack.mulPoseMatrix(modelView);
        RenderSystem.applyModelViewMatrix();
    }

    public void restore() {
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(previousProjection, previousSorting);
    }
}
