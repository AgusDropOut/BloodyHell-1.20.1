package net.agusdropout.bloodyhell.client.render.item.custom.renderer;


import net.agusdropout.bloodyhell.client.render.item.custom.model.StaffOfGoldenSorrowModel;
import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.client.render.item.layer.StaffOrbLayer;
import net.agusdropout.bloodyhell.util.visuals.manager.GoldenSorrowOrbRenderManager;


public class StaffOfTheGoldenSorrowRenderer extends GenericStaffRenderer {
    public StaffOfTheGoldenSorrowRenderer() {
        super(new StaffOfGoldenSorrowModel());
        this.addRenderLayer(new StaffOrbLayer(this, GoldenSorrowOrbRenderManager::addOrbData));
    }
}