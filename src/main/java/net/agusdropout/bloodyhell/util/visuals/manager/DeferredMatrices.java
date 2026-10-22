package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import org.joml.Matrix4f;

public final class DeferredMatrices {
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f modelView = new Matrix4f();
    private final Matrix4f previousProjection = new Matrix4f();
    private final VertexSorting restoreSorting;

    public DeferredMatrices(VertexSorting restoreSorting) {
        this.restoreSorting = restoreSorting;
    }

    public void capture() {
        projection.set(RenderSystem.getProjectionMatrix());
        modelView.set(RenderSystem.getModelViewMatrix());
    }

    public void apply() {
        previousProjection.set(RenderSystem.getProjectionMatrix());
        PoseStack stack = RenderSystem.getModelViewStack();
        stack.pushPose();

        RenderSystem.setProjectionMatrix(projection, VertexSorting.DISTANCE_TO_ORIGIN);
        stack.setIdentity();
        stack.mulPoseMatrix(modelView);
        RenderSystem.applyModelViewMatrix();
    }

    public void restore() {
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(previousProjection, restoreSorting);
    }
}
