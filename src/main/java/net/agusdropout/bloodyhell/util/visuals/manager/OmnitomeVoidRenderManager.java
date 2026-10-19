package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.item.client.books.OmnitomeRenderer;
import net.agusdropout.bloodyhell.util.visuals.ModRenderTypes;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;
import org.joml.Matrix3f;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.ArrayList;
import java.util.List;

public class OmnitomeVoidRenderManager {
    public static final OmnitomeVoidRenderManager INSTANCE = new OmnitomeVoidRenderManager();

    private static class VoidTask {
        OmnitomeRenderer renderer;
        ItemDisplayContext transform;
        Matrix4f poseMatrix;
        Matrix3f normalMatrix;
        Matrix4f projectionMatrix;
        Matrix4f modelViewMatrix;
        GeoBone bone;
        ResourceLocation texture;
        int packedLight;
        int packedOverlay;
        float red, green, blue, alpha;
    }

    private final List<VoidTask> TASKS = new ArrayList<>();

    public void queueVoidBone(OmnitomeRenderer renderer, Matrix4f poseMatrix, Matrix3f normalMatrix, GeoBone bone, ResourceLocation texture, ItemDisplayContext transform, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (net.agusdropout.bloodyhell.util.visuals.RenderPassGuard.shouldSkipDeferred(transform)) return;
        VoidTask task = new VoidTask();
        task.renderer = renderer;
        task.transform = transform;
        task.poseMatrix = new Matrix4f(poseMatrix);
        task.normalMatrix = new Matrix3f(normalMatrix);

        task.projectionMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
        task.modelViewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());

        task.bone = bone;
        task.texture = texture;
        task.packedLight = packedLight;
        task.packedOverlay = packedOverlay;
        task.red = red;
        task.green = green;
        task.blue = blue;
        task.alpha = alpha;

        TASKS.add(task);
    }

    public void renderAllAndClear() {
        if (TASKS.isEmpty()) return;

        RenderSystem.enableDepthTest();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorting currentSorting = RenderSystem.getVertexSorting();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        for (VoidTask task : TASKS) {
            if (ModShaders.OMNITOME_VOID_SHADER != null) {
                ModShaders.OMNITOME_VOID_SHADER.safeGetUniform("TimeAnim").set((float) (net.minecraft.Util.getMillis() % 100000L) / 1000.0f);
            }

            VertexSorting sorting = (task.transform == ItemDisplayContext.GUI) ?
                    VertexSorting.ORTHOGRAPHIC_Z : VertexSorting.DISTANCE_TO_ORIGIN;
            RenderSystem.setProjectionMatrix(task.projectionMatrix, sorting);

            PoseStack rsStack = RenderSystem.getModelViewStack();
            rsStack.pushPose();
            rsStack.setIdentity();
            rsStack.mulPoseMatrix(task.modelViewMatrix);
            RenderSystem.applyModelViewMatrix();

            RenderType voidType = ModRenderTypes.getOmnitomeVoid(task.texture);
            VertexConsumer voidBuffer = immediateBuffer.getBuffer(voidType);

            task.renderer.renderVoidCubesDirect(task.poseMatrix, task.normalMatrix, task.bone, voidBuffer, task.packedLight, task.packedOverlay, task.red, task.green, task.blue, task.alpha);

            immediateBuffer.endBatch(voidType);

            rsStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        RenderSystem.setProjectionMatrix(currentProj, currentSorting);
        TASKS.clear();
    }
}