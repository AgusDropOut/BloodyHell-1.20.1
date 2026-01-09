package net.agusdropout.bloodyhell.item.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.BlasphemousImpalerItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousImpalerItemModel extends GeoModel<BlasphemousImpalerItem> {
    @Override
    public ResourceLocation getModelResource(BlasphemousImpalerItem object) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blasphemous_impaler.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(BlasphemousImpalerItem object) {
        return new ResourceLocation(BloodyHell.MODID, "textures/item/blasphemous_impaler.png");
    }
    @Override
    public ResourceLocation getAnimationResource(BlasphemousImpalerItem animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blasphemous_impaler.animation.json");
    }
}