package net.agusdropout.bloodyhell.client.render.item.generic;

import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericSpellBookRenderer extends GeoItemRenderer<BaseSpellBookItem<?>> {

    public GenericSpellBookRenderer() {
        super(new GenericSpellBookModel());
    }
}