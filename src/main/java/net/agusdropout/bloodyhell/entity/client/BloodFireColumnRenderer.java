package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodFireColumnEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class BloodFireColumnRenderer extends EntityRenderer<BloodFireColumnEntity> {

    private final RandomSource random = RandomSource.create();

    public BloodFireColumnRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodFireColumnEntity entity) {
        return null;
    }

    @Override
    public void render(BloodFireColumnEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }


}