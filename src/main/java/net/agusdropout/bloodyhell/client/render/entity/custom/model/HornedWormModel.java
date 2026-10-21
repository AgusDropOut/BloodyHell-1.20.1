package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.HornedWormEntity;
import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HornedWormModel extends GeoModel<HornedWormEntity> {
    @Override
    public ResourceLocation getModelResource(HornedWormEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/horned_worm.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HornedWormEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/horned_worm.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HornedWormEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/horned_worm.animation.json");
    }

    @Override
    public void setCustomAnimations(HornedWormEntity animatable, long instanceId, AnimationState<HornedWormEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        CoreGeoBone fullBody = getAnimationProcessor().getBone("horned_worm");

        if (fullBody != null) {

            fullBody.setHidden(false);


            if (animatable != null && animatable.isBurrowed()) {
                fullBody.setHidden(true);
            }
        }

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(-entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(-entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
