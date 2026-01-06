package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;

import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSpearEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousSpearModel extends GeoModel<BlasphemousSpearEntity> {
    @Override
    public ResourceLocation getModelResource(BlasphemousSpearEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blasphemous_spear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemousSpearEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/blasphemous_spear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemousSpearEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blasphemous_spear.animation.json");
    }


}