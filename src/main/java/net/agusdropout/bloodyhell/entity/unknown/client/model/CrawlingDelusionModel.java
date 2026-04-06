package net.agusdropout.bloodyhell.entity.unknown.client.model;


import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.unknown.custom.CrawlingDelusionEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CrawlingDelusionModel extends GeoModel<CrawlingDelusionEntity> {

    @Override
    public ResourceLocation getModelResource(CrawlingDelusionEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/crawling_delusion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CrawlingDelusionEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/crawling_delusion.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CrawlingDelusionEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/crawling_delusion.animation.json");
    }

    @Override
    public void setCustomAnimations(CrawlingDelusionEntity animatable, long instanceId, AnimationState<CrawlingDelusionEntity> animationState) {

        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);

        }
    }
}