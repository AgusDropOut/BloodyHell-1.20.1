package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import net.agusdropout.bloodyhell.client.render.entity.generic.GenericMinionModel;
import net.agusdropout.bloodyhell.entity.minions.custom.BurdenOfTheUnknownEntity;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class BurdenOfTheUnknownModel extends GenericMinionModel<BurdenOfTheUnknownEntity> {



    @Override
    public void setCustomAnimations(BurdenOfTheUnknownEntity animatable, long instanceId, AnimationState<BurdenOfTheUnknownEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone cannonBone = getAnimationProcessor().getBone("cannon");

        if (cannonBone != null) {
            float targetPitch;

            if (!animatable.isAttacking()) {
                targetPitch = 0;
            } else {
                targetPitch = -animatable.getCannonPitch() * ((float) Math.PI / 180F);
            }

            float currentRotX = targetPitch;


            cannonBone.setRotX(currentRotX);
        }
    }
}