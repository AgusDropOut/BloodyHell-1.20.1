package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GenericGeckoBlockItemRenderer extends GeoItemRenderer<BaseGeckoBlockItem> {
    public GenericGeckoBlockItemRenderer(boolean hasGlowingLayer) {
        super(new GenericGeckoBlockItemModel());
        if(hasGlowingLayer) {
            addRenderLayer(new AutoGlowingGeoLayer<>(this));
        }
    }

}
