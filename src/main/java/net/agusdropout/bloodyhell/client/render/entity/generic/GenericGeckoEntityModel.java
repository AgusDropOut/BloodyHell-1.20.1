package net.agusdropout.bloodyhell.client.render.entity.generic;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.function.BiConsumer;

public class GenericGeckoEntityModel<T extends GeoAnimatable> extends GeoModel<T> {
    @FunctionalInterface
    public interface CustomAnimation<T extends GeoAnimatable> {
        void apply(GenericGeckoEntityModel<T> model, T animatable, AnimationState<T> animationState);
    }

    private final String resourceId;
    private final String textureId;
    private final String headBone;
    private final float pitchFactor;
    private final float yawFactor;
    private final BiConsumer<T, CoreGeoBone> customHeadRotation;
    private final CustomAnimation<T> customAnimations;

    public GenericGeckoEntityModel(String resourceId) {
        this(resourceId, resourceId, null, 0.0f, 0.0f, null, null);
    }

    public GenericGeckoEntityModel(String resourceId, String textureId) {
        this(resourceId, textureId, null, 0.0f, 0.0f, null, null);
    }

    public GenericGeckoEntityModel(String resourceId, String headBone, float pitchFactor, float yawFactor) {
        this(resourceId, resourceId, headBone, pitchFactor, yawFactor, null, null);
    }

    public GenericGeckoEntityModel(String resourceId, String textureId, String headBone,
                                   float pitchFactor, float yawFactor) {
        this(resourceId, textureId, headBone, pitchFactor, yawFactor, null, null);
    }

    public GenericGeckoEntityModel(String resourceId, String textureId, String headBone,
                                   BiConsumer<T, CoreGeoBone> customHeadRotation) {
        this(resourceId, textureId, headBone, 0.0f, 0.0f, customHeadRotation, null);
    }

    public GenericGeckoEntityModel(String resourceId, String textureId,
                                   CustomAnimation<T> customAnimations) {
        this(resourceId, textureId, null, 0.0f, 0.0f, null, customAnimations);
    }

    private GenericGeckoEntityModel(String resourceId, String textureId, String headBone,
                                    float pitchFactor, float yawFactor,
                                    BiConsumer<T, CoreGeoBone> customHeadRotation,
                                    CustomAnimation<T> customAnimations) {
        this.resourceId = resourceId;
        this.textureId = textureId;
        this.headBone = headBone;
        this.pitchFactor = pitchFactor;
        this.yawFactor = yawFactor;
        this.customHeadRotation = customHeadRotation;
        this.customAnimations = customAnimations;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "geo/" + resourceId + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/" + textureId + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/" + resourceId + ".animation.json");
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        if (headBone != null) {
            CoreGeoBone head = getAnimationProcessor().getBone(headBone);
            if (head != null) {
                if (customHeadRotation != null) {
                    customHeadRotation.accept(animatable, head);
                } else {
                    EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
                    head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD * pitchFactor);
                    head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD * yawFactor);
                }
            }
        }
        if (customAnimations != null) {
            customAnimations.apply(this, animatable, animationState);
        }
    }
}
