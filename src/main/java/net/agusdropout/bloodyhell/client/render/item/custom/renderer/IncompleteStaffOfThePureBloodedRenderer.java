package net.agusdropout.bloodyhell.client.render.item.custom.renderer;

import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.client.render.item.layer.StaffOrbLayer;
import net.agusdropout.bloodyhell.client.render.effect.Effects;


public class IncompleteStaffOfThePureBloodedRenderer extends GenericStaffRenderer {
    public IncompleteStaffOfThePureBloodedRenderer() {
        super();

        this.addRenderLayer(new StaffOrbLayer(this, Effects.BLOOD_ORB::submit));
    }
}