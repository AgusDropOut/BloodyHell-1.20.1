package net.agusdropout.bloodyhell.entity.minions.client.model;

import net.agusdropout.bloodyhell.entity.custom.OmenGazerEntity;
import net.agusdropout.bloodyhell.entity.minions.client.base.GenericMinionModel;
import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.minions.custom.BurdenOfTheUnknownEntity;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class BastionOfTheUnknownModel extends GenericMinionModel<BastionOfTheUnknownEntity> {





    @Override
    public void setCustomAnimations(BastionOfTheUnknownEntity animatable, long instanceId, AnimationState<BastionOfTheUnknownEntity> animationState) {

        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(-entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(-entityData.netHeadYaw() * Mth.DEG_TO_RAD);

        }
    }
}