package net.agusdropout.bloodyhell.client.render.entity.armor;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class GenericGeckoArmorRenderer extends GeoArmorRenderer<BaseGeckoArmorItem> {
    public GenericGeckoArmorRenderer(BaseGeckoArmorItem.Look look) {
        super(new GenericGeckoArmorModel());
        if (look.glowing()) {
            addRenderLayer(new AutoGlowingGeoLayer<>(this));
        }
    }
}
