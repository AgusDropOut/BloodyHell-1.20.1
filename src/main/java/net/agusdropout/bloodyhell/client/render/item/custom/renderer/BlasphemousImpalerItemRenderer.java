package net.agusdropout.bloodyhell.client.render.item.custom.renderer;


import net.agusdropout.bloodyhell.client.render.item.custom.model.BlasphemousImpalerItemModel;
import net.agusdropout.bloodyhell.item.custom.BlasphemousImpalerItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BlasphemousImpalerItemRenderer extends GeoItemRenderer<BlasphemousImpalerItem> {
    public BlasphemousImpalerItemRenderer() {
        super(new BlasphemousImpalerItemModel());
    }
}