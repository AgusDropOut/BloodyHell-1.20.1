package net.agusdropout.bloodyhell.entity.unknown.client.model;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.unknown.custom.EchoOfTheNamelessEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EchoOfTheNamelessModel extends GeoModel<EchoOfTheNamelessEntity> {

    @Override
    public ResourceLocation getModelResource(EchoOfTheNamelessEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/echo_of_the_nameless.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EchoOfTheNamelessEntity object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/entity/echo_of_the_nameless.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EchoOfTheNamelessEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/echo_of_the_nameless.animation.json");
    }
}