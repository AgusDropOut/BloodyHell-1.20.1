package net.agusdropout.bloodyhell.client.render.item.custom.renderer;


import net.agusdropout.bloodyhell.client.render.item.custom.model.StaffOfScarletCinderModel;
import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.client.render.item.layer.StaffOrbLayer;
import net.agusdropout.bloodyhell.client.render.effect.OrbEffects;

public class StaffOfScarletCinderRenderer extends GenericStaffRenderer {
    public StaffOfScarletCinderRenderer() {
        super(new StaffOfScarletCinderModel());

        this.addRenderLayer(new StaffOrbLayer(this, OrbEffects.SCARLET_CINDER::submit));
    }
}