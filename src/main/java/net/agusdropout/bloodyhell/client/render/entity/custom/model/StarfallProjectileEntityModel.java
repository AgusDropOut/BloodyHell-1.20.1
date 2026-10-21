package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.StarfallProjectile;
import net.agusdropout.bloodyhell.entity.projectile.VirulentAnchorProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class StarfallProjectileEntityModel extends GeoModel<StarfallProjectile> {
    @Override
    public ResourceLocation getModelResource(StarfallProjectile object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/starfall_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StarfallProjectile object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/starfall_projectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StarfallProjectile animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/starfall_projectile.animation.json") ;
    }

    @Override
    public void setCustomAnimations(StarfallProjectile animatable, long instanceId, AnimationState<StarfallProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("starfall_projectile");

        if (bone != null) {
                float yRot = animatable.getYRot();
                float xRot = animatable.getXRot();
                bone.setRotY(yRot * Mth.DEG_TO_RAD);
                bone.setRotX(-xRot * Mth.DEG_TO_RAD);
                bone.setRotZ(-xRot * Mth.DEG_TO_RAD);
        }
    }
}
