package net.agusdropout.bloodyhell.item.custom.staffs;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.item.custom.base.SpellAffinity;

public class IncompleteStaffOfThePureBloodedItem extends BaseStaffItem {
    public IncompleteStaffOfThePureBloodedItem(Properties properties) {
        super(properties, SpellAffinity.BLOOD, ModEffects.BLEEDING);
    }

    @Override
    public String getId() {
        return "incomplete_staff_of_the_pure_blooded";
    }

}