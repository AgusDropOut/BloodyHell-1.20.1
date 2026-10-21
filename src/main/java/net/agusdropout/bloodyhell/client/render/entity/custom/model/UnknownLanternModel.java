package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class UnknownLanternModel extends GeoModel<UnknownLanternEntity> {

    @Override
    public ResourceLocation getModelResource(UnknownLanternEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/unknown_lantern.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(UnknownLanternEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/unknown_lantern.png");
    }

    @Override
    public ResourceLocation getAnimationResource(UnknownLanternEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "animations/unknown_lantern.animation.json");
    }

    @Override
    public void setCustomAnimations(UnknownLanternEntity animatable, long instanceId, AnimationState<UnknownLanternEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);


        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {

            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
