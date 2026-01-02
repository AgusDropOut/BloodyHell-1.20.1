package net.agusdropout.bloodyhell.item.custom;

import net.minecraft.world.item.ItemStack;

public interface IComboWeapon {
    // Lógica de daño extra
    float getComboDamageBonus(ItemStack stack);

    /**
     * Verifica si ha pasado el tiempo límite para continuar el combo.
     * @param stack El item.
     * @param currentTime El tiempo actual (System.currentTimeMillis()).
     * @return true si el combo debe reiniciarse/cancelarse la animación.
     */
    boolean isComboWindowExpired(ItemStack stack, long currentTime);
}