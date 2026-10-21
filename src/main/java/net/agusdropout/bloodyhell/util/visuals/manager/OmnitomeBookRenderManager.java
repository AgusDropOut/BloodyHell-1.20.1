package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.agusdropout.bloodyhell.client.render.item.custom.renderer.OmnitomeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class OmnitomeBookRenderManager {
    public static final OmnitomeBookRenderManager INSTANCE = new OmnitomeBookRenderManager();

    private static class BookTask {
        OmnitomeRenderer renderer;
        ItemStack stack;
        ItemDisplayContext transform;
        Matrix4f poseMatrix;
        Matrix3f normalMatrix;
        Matrix4f projectionMatrix;
        Matrix4f modelViewMatrix;
        int packedLight;
        int packedOverlay;
    }

    private final List<BookTask> TASKS = new ArrayList<>();

    public void queueBook(OmnitomeRenderer renderer, ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, int packedLight, int packedOverlay) {
        if (RenderPassGuard.shouldSkipDeferred(transform)) return;
        if (transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            BookTask task = new BookTask();
            task.renderer = renderer;
            task.stack = stack.copy();
            task.transform = transform;
            task.poseMatrix = new Matrix4f(poseStack.last().pose());
            task.normalMatrix = new Matrix3f(poseStack.last().normal());
            task.projectionMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
            task.modelViewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
            task.packedLight = packedLight;
            task.packedOverlay = packedOverlay;

            TASKS.add(task);
        }
    }

    public void renderAllAndClear() {
        if (TASKS.isEmpty()) return;

        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorting currentSorting = RenderSystem.getVertexSorting();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        OmnitomeRenderer.isManagerRendering = true;

        for (BookTask task : TASKS) {
            VertexSorting sorting = (task.transform == ItemDisplayContext.GUI) ?
                    VertexSorting.ORTHOGRAPHIC_Z : VertexSorting.DISTANCE_TO_ORIGIN;
            RenderSystem.setProjectionMatrix(task.projectionMatrix, sorting);

            PoseStack rsStack = RenderSystem.getModelViewStack();
            rsStack.pushPose();
            rsStack.setIdentity();
            rsStack.mulPoseMatrix(task.modelViewMatrix);
            RenderSystem.applyModelViewMatrix();

            PoseStack reconstructedPose = new PoseStack();
            reconstructedPose.last().pose().set(task.poseMatrix);
            reconstructedPose.last().normal().set(task.normalMatrix);

            task.renderer.renderByItem(task.stack, task.transform, reconstructedPose, immediateBuffer, task.packedLight, task.packedOverlay);

            immediateBuffer.endBatch();

            rsStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        OmnitomeRenderer.isManagerRendering = false;

        RenderSystem.setProjectionMatrix(currentProj, currentSorting);
        RenderSystem.disableBlend();

        TASKS.clear();
    }
}