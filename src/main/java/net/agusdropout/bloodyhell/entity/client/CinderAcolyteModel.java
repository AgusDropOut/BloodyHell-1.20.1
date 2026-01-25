package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.CinderAcolyteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CinderAcolyteModel extends GeoModel<CinderAcolyteEntity> {
    @Override
    public ResourceLocation getModelResource(CinderAcolyteEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/cinder_acolyte.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CinderAcolyteEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/cinder_acolyte.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CinderAcolyteEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "animations/cinder_acolyte.animation.json");
    }
}