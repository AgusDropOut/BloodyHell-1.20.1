package net.agusdropout.bloodyhell.item.custom.staffs;

import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.item.custom.base.SpellAffinity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StaffOfTheGoldenSorrowItem extends BaseStaffItem {

    public StaffOfTheGoldenSorrowItem(Properties properties) {
        super(properties, SpellAffinity.RHNULL, null);
    }

    @Override
    public String getId() {
        return "staff_of_the_golden_sorrow";
    }

}