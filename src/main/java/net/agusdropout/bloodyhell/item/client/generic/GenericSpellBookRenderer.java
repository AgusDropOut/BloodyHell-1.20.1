package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;


public class GenericSpellBookRenderer extends GeoItemRenderer<BaseSpellBookItem<?>> {

    public GenericSpellBookRenderer() {

        super(new GenericSpellBookModel());
    }
}