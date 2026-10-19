package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.item.client.books.OmnitomeRenderer;
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
        int packedLight;
        int packedOverlay;
    }

    private final List<BookTask> TASKS = new ArrayList<>();
    private final Matrix4f savedProjection = new Matrix4f();

    public void queueBook(OmnitomeRenderer renderer, ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, int packedLight, int packedOverlay) {
        if (transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            savedProjection.set(RenderSystem.getProjectionMatrix());

            BookTask task = new BookTask();
            task.renderer = renderer;
            task.stack = stack.copy();
            task.transform = transform;
            task.poseMatrix = new Matrix4f(poseStack.last().pose());
            task.normalMatrix = new Matrix3f(poseStack.last().normal());
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
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, com.mojang.blaze3d.vertex.VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        RenderSystem.applyModelViewMatrix();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();


        OmnitomeRenderer.isManagerRendering = true;

        for (BookTask task : TASKS) {
            PoseStack reconstructedPose = new PoseStack();
            reconstructedPose.last().pose().set(task.poseMatrix);
            reconstructedPose.last().normal().set(task.normalMatrix);


            task.renderer.renderByItem(task.stack, task.transform, reconstructedPose, immediateBuffer, task.packedLight, task.packedOverlay);
        }

        immediateBuffer.endBatch();
        OmnitomeRenderer.isManagerRendering = false;

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, com.mojang.blaze3d.vertex.VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.disableBlend();

        TASKS.clear();
    }
}