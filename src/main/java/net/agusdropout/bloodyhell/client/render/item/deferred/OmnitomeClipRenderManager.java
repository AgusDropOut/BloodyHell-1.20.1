package net.agusdropout.bloodyhell.client.render.item.deferred;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.agusdropout.bloodyhell.client.render.ModRenderTypes;
import net.agusdropout.bloodyhell.client.render.deferred.MatrixSnapshot;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.item.deferred.data.ClipTask;
import net.agusdropout.bloodyhell.client.shader.ModShaders;
import net.agusdropout.bloodyhell.client.shader.RenderPassGuard;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.ArrayList;
import java.util.List;

public class OmnitomeClipRenderManager implements DeferredRenderManager {
    private final List<ClipTask> tasks = new ArrayList<>();

    public void queueClipBook(GeoItemRenderer<?> childRenderer, ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, int packedLight, int packedOverlay, float morphProgress) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BaseSpellBookItem)) return;

        if (RenderPassGuard.shouldSkipDeferred(transform)) return;
        ItemStack openStack = stack.copy();
        openStack.getOrCreateTag().putBoolean("spellbook_is_open", true);
        tasks.add(new ClipTask(
                childRenderer,
                openStack,
                transform,
                new Matrix4f(poseStack.last().pose()),
                new Matrix3f(poseStack.last().normal()),
                MatrixSnapshot.capture(),
                packedLight,
                packedOverlay,
                morphProgress));
    }

    @Override
    public String name() {
        return "OmnitomeClip";
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void renderAndClear(FrameContext frame) {
        if (tasks.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        VertexSorting currentSorting = RenderSystem.getVertexSorting();

        MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        for (ClipTask task : tasks) {
            ItemPassState.childPass(() -> {
                if (ModShaders.EMERGE_CLIP_SHADER != null) {
                    ModShaders.EMERGE_CLIP_SHADER.safeGetUniform("TimeAnim").set(ShaderTime.seconds());
                    ModShaders.EMERGE_CLIP_SHADER.safeGetUniform("MorphProgress").set(task.morphProgress());
                }

                task.matrices().apply(ItemPassState.sortingFor(task.transform()));

                GeoItemRenderer childRenderer = task.childRenderer();
                if (childRenderer != null) {
                    software.bernie.geckolib.core.animatable.GeoAnimatable animatableItem = (software.bernie.geckolib.core.animatable.GeoAnimatable) task.stack().getItem();
                    ResourceLocation childTexture = childRenderer.getGeoModel().getTextureResource(animatableItem);

                    RenderType clipRenderType = ModRenderTypes.getEmergeClip(childTexture);
                    VertexConsumer clipBuffer = immediateBuffer.getBuffer(clipRenderType);

                    PoseStack reconstructedPose = new PoseStack();
                    reconstructedPose.last().pose().set(task.pose());
                    reconstructedPose.last().normal().set(task.normal());

                    childRenderer.renderByItem(task.stack(), task.transform(), reconstructedPose, type -> clipBuffer, task.packedLight(), task.packedOverlay());

                    immediateBuffer.endBatch(clipRenderType);
                }
            });

            MatrixSnapshot.release();
        }

        RenderSystem.setProjectionMatrix(currentProj, currentSorting);

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

        tasks.clear();
    }
}
