package net.agusdropout.bloodyhell.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class BloodFireLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public BloodFireLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        // --- BRUTE FORCE CHECKS (Ghosting & Water) ---
        MobEffectInstance instance = entity.getEffect(ModEffects.BLOOD_FIRE_EFFECT.get());

        // Instant removal if null, almost finished, or in water
        if (instance == null || instance.getDuration() <= 1 || entity.isInWater()) {
            return;
        }

        poseStack.pushPose();

        // =============================================================
        // STEP 1: EXIT THE ENTITY'S COORDINATE SYSTEM (Name Tag Logic)
        // =============================================================

        // 1a. Move to the desired height (Chest/Head area)
        // We use 0.5 to center it on the hitbox.
        poseStack.translate(0.0D, entity.getBbHeight() * 0.5F, 0.0D);

        // 1b. The Entity Renderer scales everything by (-1, -1, 1).
        // We MUST undo this to get back to standard coordinates (Y is Up).
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        // 1c. Undo the Body Rotation.
        // POSITIVE rotation here because we flipped the scale in 1b.
        float bodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(bodyRot));

        // =============================================================
        // STEP 2: APPLY CAMERA ROTATION (The Vanilla Way)
        // =============================================================

        // 2a. Get the Camera Rotation directly
        Quaternionf cameraRot = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        poseStack.mulPose(cameraRot);

        // 2b. Move slightly "Forward" (Towards camera) to prevent clipping
        poseStack.translate(0.0F, 0.0F, 0.1F);


        // =============================================================
        // STEP 3: DYNAMIC SCALING & RENDER
        // =============================================================

        // Calculate Scale
        float xScale = entity.getBbWidth() * 2.0F;
        float yScale = Math.max(xScale, entity.getBbHeight() * 1.2F);

        // Apply Scale
        // Note: We use POSITIVE scale here because we already normalized the space in Step 1b.
        poseStack.scale(xScale, yScale, xScale);

        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer()
                .getBlockModel(ModBlocks.BLOOD_FIRE.get().defaultBlockState())
                .getParticleIcon();

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();

        float size = 0.5f;
        float u0 = sprite.getU0(); float u1 = sprite.getU1();
        float v0 = sprite.getV0(); float v1 = sprite.getV1();

        // Standard Quad (Top-Up mapping) because Y is UP now.
        consumer.vertex(pose, -size, size, 0.0f).color(255, 255, 255, 255).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, -size, -size, 0.0f).color(255, 255, 255, 255).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, size, -size, 0.0f).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, size, size, 0.0f).color(255, 255, 255, 255).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();

        poseStack.popPose();
    }
}