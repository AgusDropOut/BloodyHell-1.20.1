package net.agusdropout.bloodyhell.entity.client.animations;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class RhnullHeavySwordAnimations {

    public static final AnimationDefinition fall = AnimationDefinition.Builder.withLength(4.0F)
            .addAnimation("rhnull_heavy_sword_entity", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -45.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 45.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rhnull_heavy_sword_entity", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    // WAS: 14.0F (Moved "Down" in Blockbench, interpreted as "Up" here?)
                    // Let's invert it:
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, -14.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    // WAS: -39.0F (Moved "Down" in Blockbench, interpreted as "Up" here)
                    // Change to Positive to move DOWN in Minecraft:
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 39.0F, -22.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();
}
