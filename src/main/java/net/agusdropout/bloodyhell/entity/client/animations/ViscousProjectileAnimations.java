package net.agusdropout.bloodyhell.entity.client.animations;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class ViscousProjectileAnimations {
    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(0.0F).looping()
            .addAnimation("c1", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.8F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("c2", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.1414F, 1.1414F, 1.1414F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("c3", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 0.8F, 1.2F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("c4", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.15F, 0.85F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("c5", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.1768F, 0.8232F, 0.8232F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("c6", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.8939F, 0.8939F, 0.8939F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("c7", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.1F, 1.1F), AnimationChannel.Interpolations.LINEAR)))
            .build();
}