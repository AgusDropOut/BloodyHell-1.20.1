package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.BloodNovaEntity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BloodNovaEntityModel extends GeoModel<BloodNovaEntity> {
    @Override
    public ResourceLocation getModelResource(BloodNovaEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blood_nova_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BloodNovaEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/blood_nova_entity.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BloodNovaEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blood_nova_entity.animation.json") ;
    }

    @Override
    public void setCustomAnimations(BloodNovaEntity animatable, long instanceId, AnimationState<BloodNovaEntity> animationState) {

    }
}
