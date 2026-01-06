package net.agusdropout.bloodyhell.entity.client.armor;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.BlasphemiteArmorItem;
import net.agusdropout.bloodyhell.item.custom.BloodArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasphemiteArmorModel extends GeoModel<BlasphemiteArmorItem> {
    private final ResourceLocation model = new ResourceLocation(BloodyHell.MODID, "geo/blasphemite_armor.geo.json");
    private final ResourceLocation texture = new ResourceLocation(BloodyHell.MODID, "textures/armor/blasphemite_armor.png");

    @Override
    public ResourceLocation getModelResource(BlasphemiteArmorItem bloodArmorItem) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(BlasphemiteArmorItem bloodArmorItem) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(BlasphemiteArmorItem bloodArmorItem) {
        return null;
    }
}
