package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.BloodFireMeteorProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BloodFireMeteorRenderer extends EntityRenderer<BloodFireMeteorProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_fire_meteor.png");
    // Emissive Texture (The model with only the glowing parts painted)
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_fire_meteor_glowmask.png");

    private final BloodFireMeteorModel<BloodFireMeteorProjectile> model;

    public BloodFireMeteorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BloodFireMeteorModel<>(context.bakeLayer(BloodFireMeteorModel.LAYER_LOCATION));
    }

    @Override
    public void render(BloodFireMeteorProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0.0D, 1.5D, 0.0D);

        // Scale Logic
        float maxScale = entity.getScale();
        float scale = maxScale;

        if (entity.tickCount < 40) {
            float progress = (entity.tickCount + partialTick) / 40.0f;
            scale = Math.min(progress, 1.0f) * maxScale;
        }

        poseStack.scale(-scale, -scale, scale);

        // Apply Rotation
        this.model.setupAnim(entity, 0, 0, entity.tickCount + partialTick, 0, 0);

        // --- 1. RENDER BASE LAYER ---
        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        // --- 2. RENDER EMISSIVE GLOW LAYER ---
        // Pulse Logic: Oscillates between 0.6 and 1.0 alpha
        float time = entity.tickCount + partialTick;
        float pulse = (Mth.sin(time * 0.15f) * 0.4f) + 0.6f;

        // Use full bright light (15728880) for the glowmask
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(GLOW_TEXTURE));
        this.model.renderToBuffer(poseStack, glowConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, pulse);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodFireMeteorProjectile entity) {
        return TEXTURE;
    }
}