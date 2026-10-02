package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSmallWhirlwindEntity;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousWhirlwindEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousSmallWhirlwindEntityModel extends GeoModel<BlasphemousSmallWhirlwindEntity> {
    @Override
    public ResourceLocation getModelResource(BlasphemousSmallWhirlwindEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blasphemous_small_whirlwind_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemousSmallWhirlwindEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/blasphemous_small_whirlwind_entity.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemousSmallWhirlwindEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blasphemous_small_whirlwind_entity.animation.json");
    }

    @Override
    public void setCustomAnimations(BlasphemousSmallWhirlwindEntity animatable, long instanceId, AnimationState<BlasphemousSmallWhirlwindEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        float pt = animationState.getPartialTick();
        float time = animatable.tickCount + pt;


        CoreGeoBone mainRing = this.getAnimationProcessor().getBone("mainring");
        if (mainRing != null) {
            float angle = time * 0.15f;
            mainRing.setRotY(angle);
        }

    }
}
