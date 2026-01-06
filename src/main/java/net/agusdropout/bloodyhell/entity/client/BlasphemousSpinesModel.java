package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;

import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSpinesEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousSpinesModel extends GeoModel<BlasphemousSpinesEntity> {
    @Override
    public ResourceLocation getModelResource(BlasphemousSpinesEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blasphemous_spines.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemousSpinesEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/blasphemous_spines.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemousSpinesEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blasphemous_spines.animation.json");
    }
}