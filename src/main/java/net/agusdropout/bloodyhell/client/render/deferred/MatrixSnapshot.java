package net.agusdropout.bloodyhell.client.render.deferred;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import org.joml.Matrix4f;

public record MatrixSnapshot(Matrix4f projection, Matrix4f modelView) {

    public static MatrixSnapshot capture() {
        return new MatrixSnapshot(new Matrix4f(RenderSystem.getProjectionMatrix()), new Matrix4f(RenderSystem.getModelViewMatrix()));
    }

    public void apply(VertexSorting sorting) {
        RenderSystem.setProjectionMatrix(projection, sorting);

        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(modelView);
        RenderSystem.applyModelViewMatrix();
    }

    public static void release() {
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
