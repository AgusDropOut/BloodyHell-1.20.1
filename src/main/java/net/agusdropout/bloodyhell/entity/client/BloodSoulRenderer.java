package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.entity.soul.BloodSoulEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BloodSoulRenderer extends EntityRenderer<BloodSoulEntity> {
    public BloodSoulRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodSoulEntity entity) {
        return null; // No texture needed
    }

    // Override render to do nothing (particles handled in entity tick)
}