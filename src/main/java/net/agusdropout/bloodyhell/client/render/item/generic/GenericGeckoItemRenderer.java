package net.agusdropout.bloodyhell.client.render.item.generic;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GenericGeckoItemRenderer extends GeoItemRenderer<BaseGeckoItem> {
    public GenericGeckoItemRenderer(boolean hasGlowMask) {
        super(new GenericGeckoItemModel());
        if (hasGlowMask) {
            this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        }
    }
}
