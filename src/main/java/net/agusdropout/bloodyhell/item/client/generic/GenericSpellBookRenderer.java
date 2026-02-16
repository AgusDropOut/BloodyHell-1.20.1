package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

// No more <T extends ...>
public class GenericSpellBookRenderer extends GeoItemRenderer<BaseSpellBookItem<?>> {

    public GenericSpellBookRenderer() {
        // We can instantiate the model directly here since it's no longer generic
        super(new GenericSpellBookModel());
    }
}