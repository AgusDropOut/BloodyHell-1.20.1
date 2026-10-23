package net.agusdropout.bloodyhell.item.custom.staffs;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.item.custom.base.SpellAffinity;

public class StaffOfScarletCinderItem extends BaseStaffItem {
    public StaffOfScarletCinderItem(Properties properties) {
        super(properties, SpellAffinity.BLOOD_FIRE, ModEffects.BLOOD_FIRE_EFFECT);
    }

    @Override
    public String getId() {
        return "staff_of_scarlet_cinder";
    }

}