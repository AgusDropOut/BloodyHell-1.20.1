package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.util.visuals.manager.BloodStainDecalRenderManager;
import net.agusdropout.bloodyhell.entity.effects.BloodStainEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BloodStainRenderer extends EntityRenderer<BloodStainEntity> {

    public BloodStainRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BloodStainEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float rot = (entity.getId() * 1337) % 360;
        int tentacles = 5 + (entity.getId() % 3);

        float r = tentacles / 10.0f;
        float g = (entity.getId() % 100) / 100.0f;
        float b = 1.0f - ((entity.tickCount + partialTicks) / (float) entity.getMaxAge());
        float a = 1.0f;

        BloodStainDecalRenderManager.addDecal(
                entity.getId(),
                entity.getX(), entity.getY(), entity.getZ(),
                entity.getAttachFace(), rot, entity.getSize(),
                r, g, b, a
        );

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodStainEntity entity) {
        return null;
    }
}
