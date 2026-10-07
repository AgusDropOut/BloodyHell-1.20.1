package net.agusdropout.bloodyhell.item.client.staffs;

import net.agusdropout.bloodyhell.item.client.base.BaseStaffRenderer;
import net.agusdropout.bloodyhell.item.client.layer.GoldenSorrowOrbLayer;


public class StaffOfTheGoldenSorrowRenderer extends BaseStaffRenderer {
    public StaffOfTheGoldenSorrowRenderer() {
        super(new StaffOfGoldenSorrowModel());
        this.addRenderLayer(new GoldenSorrowOrbLayer(this));
    }
}