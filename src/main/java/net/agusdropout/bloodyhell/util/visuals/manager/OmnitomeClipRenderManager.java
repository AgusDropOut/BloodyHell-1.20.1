package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.item.client.books.OmnitomeRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.util.visuals.ModRenderTypes;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.agusdropout.bloodyhell.util.visuals.ShaderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.joml.Matrix4f;
import org.joml.Matrix3f;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.ArrayList;
import java.util.List;

public class OmnitomeClipRenderManager {
    public static final OmnitomeClipRenderManager INSTANCE = new OmnitomeClipRenderManager();

    private static class ClipTask {
        ItemStack stack;
        ItemDisplayContext transform;
        Matrix4f poseMatrix;
        Matrix3f normalMatrix;
        int packedLight;
        int packedOverlay;
        float morphProgress;
    }

    private final List<ClipTask> TASKS = new ArrayList<>();
    private final Matrix4f savedProjection = new Matrix4f();

    public void queueClipBook(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, int packedLight, int packedOverlay, float morphProgress) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BaseSpellBookItem<?>)) return;


        if (transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
        } else if (TASKS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
        }

        ClipTask task = new ClipTask();
        task.stack = stack.copy();
        task.stack.getOrCreateTag().putBoolean("spellbook_is_open", true);
        task.transform = transform;

        task.poseMatrix = new Matrix4f(poseStack.last().pose());
        task.normalMatrix = new Matrix3f(poseStack.last().normal());

        task.packedLight = packedLight;
        task.packedOverlay = packedOverlay;
        task.morphProgress = morphProgress;

        TASKS.add(task);
    }

    public void renderAllAndClear() {
        if (TASKS.isEmpty()) return;


        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);


        rsStack.setIdentity();
        RenderSystem.applyModelViewMatrix();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        for (ClipTask task : TASKS) {
            OmnitomeRenderer.isChildMorphing = true;

            if (ModShaders.EMERGE_CLIP_SHADER != null) {
                ModShaders.EMERGE_CLIP_SHADER.safeGetUniform("TimeAnim").set((float) (net.minecraft.Util.getMillis() % 100000L) / 1000.0f);
                ModShaders.EMERGE_CLIP_SHADER.safeGetUniform("MorphProgress").set(task.morphProgress);
            }

            Object customRenderer = IClientItemExtensions.of(task.stack).getCustomRenderer();
            if (customRenderer instanceof GeoItemRenderer) {
                @SuppressWarnings({"rawtypes"})
                GeoItemRenderer childRenderer = (GeoItemRenderer) customRenderer;

                software.bernie.geckolib.core.animatable.GeoAnimatable animatableItem = (software.bernie.geckolib.core.animatable.GeoAnimatable) task.stack.getItem();
                ResourceLocation childTexture = childRenderer.getGeoModel().getTextureResource(animatableItem);

                RenderType clipRenderType = ModRenderTypes.getEmergeClip(childTexture);
                VertexConsumer clipBuffer = immediateBuffer.getBuffer(clipRenderType);

                PoseStack reconstructedPose = new PoseStack();
                reconstructedPose.last().pose().set(task.poseMatrix);
                reconstructedPose.last().normal().set(task.normalMatrix);

                childRenderer.renderByItem(task.stack, task.transform, reconstructedPose, type -> clipBuffer, task.packedLight, task.packedOverlay);
                immediateBuffer.endBatch(clipRenderType);
            }
            OmnitomeRenderer.isChildMorphing = false;
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        TASKS.clear();
    }
}