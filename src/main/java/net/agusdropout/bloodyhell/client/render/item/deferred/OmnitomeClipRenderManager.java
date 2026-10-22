package net.agusdropout.bloodyhell.client.render.item.deferred;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.agusdropout.bloodyhell.client.render.item.custom.renderer.OmnitomeRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.client.render.ModRenderTypes;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
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

import java.util.ArrayList;
import java.util.List;

public class OmnitomeClipRenderManager implements DeferredRenderManager {
    public static final OmnitomeClipRenderManager INSTANCE = new OmnitomeClipRenderManager();

    private static class ClipTask {
        ItemStack stack;
        ItemDisplayContext transform;
        Matrix4f poseMatrix;
        Matrix3f normalMatrix;
        Matrix4f projectionMatrix;
        Matrix4f modelViewMatrix;
        int packedLight;
        int packedOverlay;
        float morphProgress;
    }

    private final List<ClipTask> TASKS = new ArrayList<>();

    public void queueClipBook(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, int packedLight, int packedOverlay, float morphProgress) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BaseSpellBookItem<?>)) return;

        if (RenderPassGuard.shouldSkipDeferred(transform)) return;
        ClipTask task = new ClipTask();
        task.stack = stack.copy();
        task.stack.getOrCreateTag().putBoolean("spellbook_is_open", true);
        task.transform = transform;
        task.poseMatrix = new Matrix4f(poseStack.last().pose());
        task.normalMatrix = new Matrix3f(poseStack.last().normal());

        task.projectionMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
        task.modelViewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());

        task.packedLight = packedLight;
        task.packedOverlay = packedOverlay;
        task.morphProgress = morphProgress;

        TASKS.add(task);
    }

    @Override
    public String name() {
        return "OmnitomeClip";
    }

    @Override
    public void renderAndClear(FrameContext frame) {
        if (TASKS.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorting currentSorting = RenderSystem.getVertexSorting();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        for (ClipTask task : TASKS) {
            OmnitomeRenderer.isChildMorphing = true;

            if (ModShaders.EMERGE_CLIP_SHADER != null) {
                ModShaders.EMERGE_CLIP_SHADER.safeGetUniform("TimeAnim").set(ShaderTime.seconds());
                ModShaders.EMERGE_CLIP_SHADER.safeGetUniform("MorphProgress").set(task.morphProgress);
            }

            VertexSorting sorting = (task.transform == ItemDisplayContext.GUI) ?
                    VertexSorting.ORTHOGRAPHIC_Z : VertexSorting.DISTANCE_TO_ORIGIN;
            RenderSystem.setProjectionMatrix(task.projectionMatrix, sorting);

            PoseStack rsStack = RenderSystem.getModelViewStack();
            rsStack.pushPose();
            rsStack.setIdentity();
            rsStack.mulPoseMatrix(task.modelViewMatrix);
            RenderSystem.applyModelViewMatrix();

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

            rsStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        RenderSystem.setProjectionMatrix(currentProj, currentSorting);

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        TASKS.clear();
    }
}