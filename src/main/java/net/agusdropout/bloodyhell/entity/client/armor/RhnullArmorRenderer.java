package net.agusdropout.bloodyhell.entity.client.armor;

import net.agusdropout.bloodyhell.item.custom.BloodArmorItem;
import net.agusdropout.bloodyhell.item.custom.RhnullArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class RhnullArmorRenderer extends GeoArmorRenderer<RhnullArmorItem> {
    public RhnullArmorRenderer() {
            super(new RhnullArmorModel());
            addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

}
