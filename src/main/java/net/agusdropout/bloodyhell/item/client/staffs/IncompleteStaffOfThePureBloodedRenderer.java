package net.agusdropout.bloodyhell.item.client.staffs;

import net.agusdropout.bloodyhell.item.client.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.item.client.layer.BloodOrbLayer;


public class IncompleteStaffOfThePureBloodedRenderer extends GenericStaffRenderer {
    public IncompleteStaffOfThePureBloodedRenderer() {
        super();

        this.addRenderLayer(new BloodOrbLayer(this));
    }
}