package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RitekeeperModel extends GeoModel<RitekeeperEntity> {

    @Override
    public ResourceLocation getModelResource(RitekeeperEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/ritekeeper.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RitekeeperEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/ritekeeper.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RitekeeperEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "animations/ritekeeper.animation.json");
    }

    @Override
    public void setCustomAnimations(RitekeeperEntity animatable, long instanceId, AnimationState<RitekeeperEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // 1. Find the head bone (Ensure your bone in Blockbench is named "head")
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            // 2. Get the head rotation data from the entity
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            // 3. Apply the rotation (Convert degrees to radians)
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}