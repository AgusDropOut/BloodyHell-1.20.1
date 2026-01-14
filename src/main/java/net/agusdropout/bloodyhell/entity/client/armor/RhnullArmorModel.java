package net.agusdropout.bloodyhell.entity.client.armor;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.BlasphemiteArmorItem;
import net.agusdropout.bloodyhell.item.custom.RhnullArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RhnullArmorModel extends GeoModel<RhnullArmorItem> {
    private final ResourceLocation model = new ResourceLocation(BloodyHell.MODID, "geo/rhnull_armor.geo.json");
    private final ResourceLocation texture = new ResourceLocation(BloodyHell.MODID, "textures/armor/rhnull_armor.png");
    private final ResourceLocation animation = new ResourceLocation(BloodyHell.MODID, "animations/rhnull_armor.animation.json");

    @Override
    public ResourceLocation getModelResource(RhnullArmorItem bloodArmorItem) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(RhnullArmorItem bloodArmorItem) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(RhnullArmorItem bloodArmorItem) {
        return animation;
    }
}
