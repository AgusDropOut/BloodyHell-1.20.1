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
        Matrix4f poseMatrix;
        Matrix3f normalMatrix;
        GeoBone bone;
        ResourceLocation texture;
        int packedLight;
        int packedOverlay;
        float red, green, blue, alpha;
    }

    private final List<VoidTask> TASKS = new ArrayList<>();
    private final Matrix4f savedProjection = new Matrix4f();

    public void queueVoidBone(OmnitomeRenderer renderer, Matrix4f poseMatrix, Matrix3f normalMatrix, GeoBone bone, ResourceLocation texture, ItemDisplayContext transform, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
        } else if (TASKS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
        }

        VoidTask task = new VoidTask();
        task.renderer = renderer;
        task.poseMatrix = new Matrix4f(poseMatrix);
        task.normalMatrix = new Matrix3f(normalMatrix);
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
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);

        rsStack.setIdentity();
        RenderSystem.applyModelViewMatrix();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        for (VoidTask task : TASKS) {
            if (ModShaders.OMNITOME_VOID_SHADER != null) {
                ModShaders.OMNITOME_VOID_SHADER.safeGetUniform("TimeAnim").set((float) (net.minecraft.Util.getMillis() % 100000L) / 1000.0f);
            }

            RenderType voidType = ModRenderTypes.getOmnitomeVoid(task.texture);
            VertexConsumer voidBuffer = immediateBuffer.getBuffer(voidType);

            task.renderer.renderVoidCubesDirect(task.poseMatrix, task.normalMatrix, task.bone, voidBuffer, task.packedLight, task.packedOverlay, task.red, task.green, task.blue, task.alpha);

            immediateBuffer.endBatch(voidType);
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);




        TASKS.clear();
    }
}