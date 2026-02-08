package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.VisceralProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class VisceralProjectileEntityModel extends GeoModel<VisceralProjectile> {
    @Override
    public ResourceLocation getModelResource(VisceralProjectile object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/visceral_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VisceralProjectile object) {
        if(!object.isAlternative()){
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/visceral_projectile_alternative.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/visceral_projectile.png");
        }

    }

    @Override
    public ResourceLocation getAnimationResource(VisceralProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(VisceralProjectile animatable, long instanceId, AnimationState<VisceralProjectile> animationState) {
        Optional<GeoBone> bone = this.getBone("visceral_projectile");

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
