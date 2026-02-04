package net.agusdropout.bloodyhell.item.custom.base;

import net.minecraft.world.item.ItemStack;

public interface IComboWeapon {
    float getComboDamageBonus(ItemStack stack);

    boolean isComboWindowExpired(ItemStack stack, long currentTime);

    // --- NUEVO PARA ARMAS PESADAS ---

    // Si devuelve true, cancelamos el daño vanilla inmediato
    default boolean shouldCancelStandardAttack() {
        return false;
    }

    // Cuántos ticks esperar desde el click hasta que ocurre el impacto (Damage Delay)
    // 20 ticks = 1 segundo
    default int getDamageDelay(int comboStep) {
        return 0;
    }
}