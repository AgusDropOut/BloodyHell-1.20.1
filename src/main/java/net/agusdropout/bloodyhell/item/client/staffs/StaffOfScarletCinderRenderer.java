package net.agusdropout.bloodyhell.item.client.staffs;

import net.agusdropout.bloodyhell.item.client.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.item.client.layer.ScarletCinderOrbLayer;

public class StaffOfScarletCinderRenderer extends GenericStaffRenderer {
    public StaffOfScarletCinderRenderer() {
        super(new StaffOfScarletCinderModel());

        this.addRenderLayer(new ScarletCinderOrbLayer(this));
    }
}