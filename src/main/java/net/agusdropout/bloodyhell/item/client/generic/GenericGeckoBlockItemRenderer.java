package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.item.client.BloodAltarItemModel;
import net.agusdropout.bloodyhell.item.custom.BloodAltarItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericGeckoBlockItemRenderer extends GeoItemRenderer<BaseGeckoBlockItem> {
    public GenericGeckoBlockItemRenderer() {
        super(new GenericGeckoBlockItemModel());
    }

}
