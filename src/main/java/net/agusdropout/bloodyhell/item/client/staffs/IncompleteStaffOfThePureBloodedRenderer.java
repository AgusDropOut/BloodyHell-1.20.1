package net.agusdropout.bloodyhell.item.client.staffs;

import net.agusdropout.bloodyhell.item.client.base.BaseStaffRenderer;
import net.agusdropout.bloodyhell.item.client.layer.BloodOrbLayer;


public class IncompleteStaffOfThePureBloodedRenderer extends BaseStaffRenderer {
    public IncompleteStaffOfThePureBloodedRenderer() {
        super();

        this.addRenderLayer(new BloodOrbLayer(this));
    }
}