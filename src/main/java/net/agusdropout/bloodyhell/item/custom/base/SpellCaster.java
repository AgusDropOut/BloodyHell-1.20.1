package net.agusdropout.bloodyhell.item.custom.base;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class SpellCaster {
    private SpellCaster() {}

    public static void tick(Level level, Player player, BaseSpellBookItem<?> book, ItemStack bookStack, InteractionHand hand, int ticksUsed, boolean castByStaff) {
        StaffCastType castType = book.getStaffCastType();
        int cycle = book.getCycleTicks();

        if (castType == StaffCastType.SEQUENTIAL && ticksUsed > 0 && ticksUsed % cycle == book.getMinChargeTime() % cycle) {
            book.executeCast(level, player, hand, bookStack);
        }

        if (level.isClientSide) {
            book.spawnProgressiveParticles(level, player, ticksUsed);
        }
        book.playChargeSound(level, player, ticksUsed);

        if (castByStaff && castType == StaffCastType.THROW && ticksUsed == book.getMinChargeTime()) {
            book.executeCast(level, player, hand, bookStack);
            player.stopUsingItem();
        }
    }

    public static void release(Level level, Player player, BaseSpellBookItem<?> book, ItemStack bookStack, InteractionHand hand, int ticksUsed, boolean castByStaff) {
        StaffCastType castType = book.getStaffCastType();
        if (castType == StaffCastType.SEQUENTIAL) return;
        if (castByStaff && castType == StaffCastType.THROW) return;
        if (ticksUsed >= book.getMinChargeTime()) {
            book.executeCast(level, player, hand, bookStack);
        }
    }
}
