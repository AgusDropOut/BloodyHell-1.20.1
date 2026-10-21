package net.agusdropout.bloodyhell.client.render.item.custom.renderer;


import net.agusdropout.bloodyhell.client.render.item.custom.model.BlasphemousHulkingMassOfIronModel;
import net.agusdropout.bloodyhell.item.custom.BlasphemousHulkingMassOfIronItem;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BlasphemousHulkingMassOfIronRenderer extends GeoItemRenderer<BlasphemousHulkingMassOfIronItem> {
    public BlasphemousHulkingMassOfIronRenderer() {

        super(new BlasphemousHulkingMassOfIronModel());

    }

}
