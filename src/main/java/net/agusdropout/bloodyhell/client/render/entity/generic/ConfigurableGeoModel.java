package net.agusdropout.bloodyhell.client.render.entity.generic;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.function.BiConsumer;

public class ConfigurableGeoModel<T extends GeoAnimatable> extends GenericGeckoEntityModel<T> {
    public ConfigurableGeoModel(String resourceId) {
        super(resourceId);
    }

    public ConfigurableGeoModel(String resourceId, String textureId) {
        super(resourceId, textureId);
    }

    public ConfigurableGeoModel(String resourceId, String headBone, float pitchFactor, float yawFactor) {
        super(resourceId, headBone, pitchFactor, yawFactor);
    }

    public ConfigurableGeoModel(String resourceId, String textureId, String headBone,
                                float pitchFactor, float yawFactor) {
        super(resourceId, textureId, headBone, pitchFactor, yawFactor);
    }

    public ConfigurableGeoModel(String resourceId, String textureId, String headBone,
                                BiConsumer<T, CoreGeoBone> customHeadRotation) {
        super(resourceId, textureId, headBone, customHeadRotation);
    }

    public ConfigurableGeoModel(String resourceId, String textureId,
                                CustomAnimation<T> customAnimations) {
        super(resourceId, textureId, customAnimations);
    }

    public ConfigurableGeoModel(String resourceId, String textureId, String headBone,
                                float pitchFactor, float yawFactor,
                                CustomAnimation<T> customAnimations) {
        super(resourceId, textureId, headBone, pitchFactor, yawFactor, customAnimations);
    }

    public static <T extends Entity & GeoAnimatable> CustomAnimation<T> alignBoneToRotation(String boneName) {
        return new CustomAnimation<T>() {
            @Override
            public void apply(GenericGeckoEntityModel<T> model, T animatable, AnimationState<T> animationState) {
                CoreGeoBone bone = model.getAnimationProcessor().getBone(boneName);
                if (bone != null) {
                    bone.setRotY(animatable.getYRot() * Mth.DEG_TO_RAD);
                    bone.setRotX(-animatable.getXRot() * Mth.DEG_TO_RAD);
                    bone.setRotZ(-animatable.getXRot() * Mth.DEG_TO_RAD);
                }
            }
        };
    }
}
