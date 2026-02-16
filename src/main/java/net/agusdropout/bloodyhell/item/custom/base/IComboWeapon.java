package net.agusdropout.bloodyhell.item.custom.base;

import net.minecraft.world.item.ItemStack;

public interface IComboWeapon {
    float getComboDamageBonus(ItemStack stack);

    boolean isComboWindowExpired(ItemStack stack, long currentTime);


    default boolean shouldCancelStandardAttack() {
        return false;
    }

    default int getDamageDelay(int comboStep) {
        return 0;
    }
}