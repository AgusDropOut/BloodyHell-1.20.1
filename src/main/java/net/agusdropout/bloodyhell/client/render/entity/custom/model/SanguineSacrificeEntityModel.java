package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.SanguineSacrificeEntity;
import net.agusdropout.bloodyhell.entity.custom.UnknownEntityArms;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SanguineSacrificeEntityModel extends GeoModel<SanguineSacrificeEntity> {
    @Override
    public ResourceLocation getModelResource(SanguineSacrificeEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/sanguine_sacrifice_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SanguineSacrificeEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/sanguine_sacrifice_entity.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SanguineSacrificeEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/sanguine_sacrifice_entity.animation.json");
    }
    @Override
    public void setCustomAnimations(SanguineSacrificeEntity animatable, long instanceId, AnimationState<SanguineSacrificeEntity> animationState) {

    }
}
