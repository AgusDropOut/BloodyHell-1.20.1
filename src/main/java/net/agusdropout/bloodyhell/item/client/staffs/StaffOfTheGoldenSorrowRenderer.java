package net.agusdropout.bloodyhell.item.client.staffs;

import net.agusdropout.bloodyhell.item.client.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.item.client.layer.GoldenSorrowOrbLayer;


public class StaffOfTheGoldenSorrowRenderer extends GenericStaffRenderer {
    public StaffOfTheGoldenSorrowRenderer() {
        super(new StaffOfGoldenSorrowModel());
        this.addRenderLayer(new GoldenSorrowOrbLayer(this));
    }
}