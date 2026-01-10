package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.GraveWalkerEntity;
import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GraveWalkerEntityModel extends GeoModel<GraveWalkerEntity> {
    @Override
    public ResourceLocation getModelResource(GraveWalkerEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/grave_walker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GraveWalkerEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/grave_walker.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GraveWalkerEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/grave_walker.animation.json");
    }

@Override
public void setCustomAnimations(GraveWalkerEntity animatable, long instanceId, AnimationState<GraveWalkerEntity> animationState) {

        CoreGeoBone head = getAnimationProcessor().getBone("head");


        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(-entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(-entityData.netHeadYaw() * Mth.DEG_TO_RAD);

        }

}
}
