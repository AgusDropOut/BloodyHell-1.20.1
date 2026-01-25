package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.FailedRemnantEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FailedRemnantModel extends GeoModel<FailedRemnantEntity> {
    @Override
    public ResourceLocation getModelResource(FailedRemnantEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/failed_remnant.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FailedRemnantEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/failed_remnant.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FailedRemnantEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "animations/failed_remnant.animation.json");
    }
}