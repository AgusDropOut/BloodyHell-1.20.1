package net.agusdropout.bloodyhell.entity.minions.client.base;


import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericMinionModel<T extends AbstractMinionEntity> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation("bloodyhell", "geo/" + animatable.getMinionId() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation("bloodyhell", "textures/entity/" + animatable.getMinionId() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation("bloodyhell", "animations/" + animatable.getMinionId() + ".animation.json");
    }
}