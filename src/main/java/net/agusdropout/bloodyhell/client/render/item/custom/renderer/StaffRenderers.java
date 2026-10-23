package net.agusdropout.bloodyhell.client.render.item.custom.renderer;

import net.agusdropout.bloodyhell.client.render.effect.Effects;
import net.agusdropout.bloodyhell.client.render.item.custom.model.StaffOfGoldenSorrowModel;
import net.agusdropout.bloodyhell.client.render.item.custom.model.StaffOfScarletCinderModel;
import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffModel;
import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.client.render.item.layer.StaffOrbLayer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;
import java.util.function.Supplier;

public final class StaffRenderers {
    private record Look(Supplier<GeoModel<BaseStaffItem>> model, StaffOrbLayer.OrbSink orb) {}

    private static final Map<String, Look> LOOKS = Map.of(
            "incomplete_staff_of_the_pure_blooded", new Look(GenericStaffModel::new, Effects.BLOOD_ORB::submit),
            "staff_of_scarlet_cinder", new Look(StaffOfScarletCinderModel::new, Effects.SCARLET_CINDER_ORB::submit),
            "staff_of_the_golden_sorrow", new Look(StaffOfGoldenSorrowModel::new, Effects.GOLDEN_SORROW_ORB::submit));

    private StaffRenderers() {}

    public static GenericStaffRenderer create(String staffId) {
        Look look = LOOKS.get(staffId);
        if (look == null) return new GenericStaffRenderer();
        GenericStaffRenderer renderer = new GenericStaffRenderer(look.model().get());
        renderer.addRenderLayer(new StaffOrbLayer(renderer, look.orb()));
        return renderer;
    }
}
