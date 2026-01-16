package net.agusdropout.bloodyhell.entity.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.BloodNovaEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodNovaEntityRenderer extends GeoEntityRenderer<BloodNovaEntity> {
    public BloodNovaEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BloodNovaEntityModel());
        this.shadowRadius = 0.01f;

    }

    @Override
    public ResourceLocation getTextureLocation(BloodNovaEntity instance) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_nova_entity.png");
    }

    @Override
    public RenderType getRenderType(BloodNovaEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(BloodNovaEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
