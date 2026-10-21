package net.agusdropout.bloodyhell.client.render.entity.generic;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

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
}
