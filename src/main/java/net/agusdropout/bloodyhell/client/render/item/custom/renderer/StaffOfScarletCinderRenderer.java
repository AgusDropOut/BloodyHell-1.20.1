package net.agusdropout.bloodyhell.client.render.item.custom.renderer;


import net.agusdropout.bloodyhell.client.render.item.custom.model.StaffOfScarletCinderModel;
import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.client.render.item.layer.StaffOrbLayer;
import net.agusdropout.bloodyhell.util.visuals.manager.ScarletCinderOrbRenderManager;

public class StaffOfScarletCinderRenderer extends GenericStaffRenderer {
    public StaffOfScarletCinderRenderer() {
        super(new StaffOfScarletCinderModel());

        this.addRenderLayer(new StaffOrbLayer(this, ScarletCinderOrbRenderManager::addOrbData));
    }
}