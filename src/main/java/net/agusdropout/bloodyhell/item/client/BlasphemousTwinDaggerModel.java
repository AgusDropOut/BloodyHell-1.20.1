package net.agusdropout.bloodyhell.item.client;


import net.agusdropout.bloodyhell.item.custom.BlasphemousTwinDaggerItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousTwinDaggerModel extends GeoModel<BlasphemousTwinDaggerItem> {
    @Override
    public ResourceLocation getModelResource(BlasphemousTwinDaggerItem object) {
        return new ResourceLocation("bloodyhell", "geo/blasphemous_twin_daggers.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemousTwinDaggerItem object) {
        return new ResourceLocation("bloodyhell", "textures/item/blasphemous_twin_daggers.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemousTwinDaggerItem animatable) {
        return new ResourceLocation("bloodyhell", "animations/blasphemous_twin_daggers.animation.json");
    }

}