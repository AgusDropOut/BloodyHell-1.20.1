package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.entity.projectile.spell.BloodPortalEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BloodPortalRenderer extends EntityRenderer<BloodPortalEntity> {

    public BloodPortalRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(BloodPortalEntity entity, Frustum camera, double camX, double camY, double camZ) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(BloodPortalEntity entity) {
        return null;
    }
}