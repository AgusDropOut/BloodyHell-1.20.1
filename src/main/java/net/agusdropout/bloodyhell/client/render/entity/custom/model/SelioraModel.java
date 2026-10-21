package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.custom.SelioraEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SelioraModel extends GeoModel<SelioraEntity> {
    @Override
    public ResourceLocation getModelResource(SelioraEntity object) {
        if (!object.isSecondPhase()) {
            return new ResourceLocation(BloodyHell.MODID, "geo/seliora.geo.json");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "geo/seliora_second_phase.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(SelioraEntity object) {
        if (!object.isSecondPhase()) {
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/seliora.png");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "textures/entity/seliora_second_phase.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(SelioraEntity animatable) {
        if (!animatable.isSecondPhase()) {
            return new ResourceLocation(BloodyHell.MODID, "animations/seliora.animation.json");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "animations/seliora_second_phase.animation.json");
        }
    }

@Override
public void setCustomAnimations(SelioraEntity animatable, long instanceId, AnimationState<SelioraEntity> animationState) {

        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(-entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(-entityData.netHeadYaw() * Mth.DEG_TO_RAD);

        }

}
}
