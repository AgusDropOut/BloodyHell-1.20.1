package net.agusdropout.bloodyhell.client.render.entity.armor;

import net.agusdropout.bloodyhell.item.custom.BlasphemiteArmorItem;
import net.agusdropout.bloodyhell.item.custom.BloodArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class BlasphemiteArmorRenderer extends GeoArmorRenderer<BlasphemiteArmorItem> {
    public BlasphemiteArmorRenderer() {
            super(new BlasphemiteArmorModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

}
