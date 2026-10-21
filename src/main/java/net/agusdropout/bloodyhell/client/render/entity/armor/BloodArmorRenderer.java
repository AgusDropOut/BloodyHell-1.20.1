package net.agusdropout.bloodyhell.client.render.entity.armor;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.BloodArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class BloodArmorRenderer extends GeoArmorRenderer<BloodArmorItem> {
    public BloodArmorRenderer() {
            super(new BloodArmorModel());
    }

}
