package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.BlasphemousArmEntity;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousWhirlwindEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousWhirlwindEntityModel extends GeoModel<BlasphemousWhirlwindEntity> {
    @Override
    public ResourceLocation getModelResource(BlasphemousWhirlwindEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blasphemous_whirlwind_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemousWhirlwindEntity object) {
        if (!object.isMinorRingActive()){
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/blasphemous_whirlwind_entity.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/blasphemous_whirlwind_entity_no_small_ring.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemousWhirlwindEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blasphemous_whirlwind_entity.animation.json");
    }

    @Override
    public void setCustomAnimations(BlasphemousWhirlwindEntity animatable, long instanceId, AnimationState<BlasphemousWhirlwindEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone minorRing = this.getAnimationProcessor().getBone("minorring");

        float pt = animationState.getPartialTick();
        float time = animatable.tickCount + pt;

        CoreGeoBone mainRing = this.getAnimationProcessor().getBone("mainring");
        if (mainRing != null) {
            float angle = time * 0.15f;
            mainRing.setRotY(angle);
        }

        if (minorRing != null) {
            float orbit = time * 0.1f;
            minorRing.setRotY(orbit);

            float radius = 1.5f;
            float x = (float) Math.cos(orbit) * radius;
            float z = (float) Math.sin(orbit) * radius;
            minorRing.setPosX(x);
            minorRing.setPosZ(z);

            if (animatable.level().isClientSide) {
                float particleRadius = 4F;
                float px = (float) Math.cos(-orbit *2.5) * particleRadius;
                float pz = (float) Math.sin(-orbit *2.5) * particleRadius;

                    animatable.level().addParticle(
                            ModParticles.MAGIC_LINE_PARTICLE.get(),
                            animatable.getX() + px,
                            animatable.getY() + 1.0,
                            animatable.getZ() + pz,
                            0, 0, 0
                    );
            }
        }


        CoreGeoBone minorRingBody = this.getAnimationProcessor().getBone("minorringbody");
        if (minorRingBody != null) {
            float spin = time * 0.25f;
            minorRingBody.setRotY(spin);
        }

    }
}
