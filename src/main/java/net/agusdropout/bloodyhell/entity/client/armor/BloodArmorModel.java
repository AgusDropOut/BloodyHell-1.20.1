package net.agusdropout.bloodyhell.entity.client.armor;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.BloodArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class BloodArmorModel extends GeoModel<BloodArmorItem> {
    private final ResourceLocation model = new ResourceLocation(BloodyHell.MODID, "geo/sanguinite_armor.geo.json");
    private final ResourceLocation texture = new ResourceLocation(BloodyHell.MODID, "textures/armor/sanguinite_armor.png");
    private final ResourceLocation animation = new ResourceLocation(BloodyHell.MODID, "animations/sanguinite_armor.animation.json");
    @Override
    public ResourceLocation getModelResource(BloodArmorItem bloodArmorItem) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(BloodArmorItem bloodArmorItem) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(BloodArmorItem bloodArmorItem) {
        return animation;
    }
}
