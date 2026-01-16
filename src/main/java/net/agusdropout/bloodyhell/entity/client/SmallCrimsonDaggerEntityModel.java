package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;

import net.agusdropout.bloodyhell.entity.projectile.SmallCrimsonDagger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class SmallCrimsonDaggerEntityModel extends GeoModel<SmallCrimsonDagger> {
    @Override
    public ResourceLocation getModelResource(SmallCrimsonDagger object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/small_crimson_dagger.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SmallCrimsonDagger object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/small_crimson_dagger.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SmallCrimsonDagger animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/small_crimson_dagger.animation.json") ;
    }

    @Override
    public void setCustomAnimations(SmallCrimsonDagger animatable, long instanceId, AnimationState<SmallCrimsonDagger> animationState) {
        Optional<GeoBone> bone = this.getBone("small_crimson_dagger");

        if (bone != null && bone.isPresent()) {
            if (instanceId == animatable.getId()) {
                float yRot = animatable.getYRot();
                float xRot = animatable.getXRot();
                bone.get().setRotX(xRot * Mth.DEG_TO_RAD);
                bone.get().setRotY(yRot * Mth.DEG_TO_RAD);
            }
        }
    }
}
