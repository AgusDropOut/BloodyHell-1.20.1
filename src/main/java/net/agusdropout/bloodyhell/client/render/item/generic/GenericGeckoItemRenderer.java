package net.agusdropout.bloodyhell.client.render.item.generic;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.agusdropout.bloodyhell.item.custom.base.GeckoAsset;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GenericGeckoItemRenderer<T extends Item & GeoItem & GeckoAsset> extends GeoItemRenderer<T> {
    public GenericGeckoItemRenderer(boolean hasGlowMask) {
        super(new GenericGeckoItemModel<>());
        if (hasGlowMask) {
            this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        }
    }
}
