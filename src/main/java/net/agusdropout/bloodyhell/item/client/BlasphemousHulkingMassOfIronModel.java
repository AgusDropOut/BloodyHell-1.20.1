package net.agusdropout.bloodyhell.item.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.BlasphemousHulkingMassOfIronItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemousHulkingMassOfIronModel extends GeoModel<BlasphemousHulkingMassOfIronItem> {
    @Override
    public ResourceLocation getModelResource(BlasphemousHulkingMassOfIronItem animatable) {
        return new ResourceLocation(BloodyHell.MODID, "geo/blasphemous_hulking_mass_of_iron.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemousHulkingMassOfIronItem animatable) {
        return new ResourceLocation(BloodyHell.MODID, "textures/item/blasphemous_hulking_mass_of_iron.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemousHulkingMassOfIronItem animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/blasphemous_hulking_mass_of_iron.animation.json");
    }
}
