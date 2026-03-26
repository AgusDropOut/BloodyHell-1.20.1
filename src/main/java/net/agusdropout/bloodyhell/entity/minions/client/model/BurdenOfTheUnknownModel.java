package net.agusdropout.bloodyhell.entity.minions.client.model;

import net.agusdropout.bloodyhell.entity.minions.client.base.GenericMinionModel;
import net.agusdropout.bloodyhell.entity.minions.custom.BurdenOfTheUnknownEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class BurdenOfTheUnknownModel extends GenericMinionModel<BurdenOfTheUnknownEntity> {
    @Override
    public void setCustomAnimations(BurdenOfTheUnknownEntity animatable, long instanceId, AnimationState<BurdenOfTheUnknownEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone cannonBone = getAnimationProcessor().getBone("cannon");
        if (cannonBone != null) {
            float pitchRads = animatable.getCannonPitch() * ((float) Math.PI / 180F);
            cannonBone.setRotX(-pitchRads);
        }
    }
}
