package net.agusdropout.bloodyhell.item.custom.base;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class SpellCaster {
    private SpellCaster() {}

    public static SpellModifiers modifiersOf(ItemStack staff, BaseSpellBookItem book) {
        if (staff.getItem() instanceof BaseStaffItem staffItem) {
            return staffItem.modifiersFor(staff, book);
        }
        return SpellModifiers.NONE;
    }

    public static void tick(Level level, Player player, BaseSpellBookItem book, ItemStack bookStack, InteractionHand hand, int ticksUsed, ItemStack staff) {
        StaffCastType castType = book.getStaffCastType();
        SpellModifiers modifiers = modifiersOf(staff, book);
        int cycle = book.getCycleTicks(modifiers);

        if (castType == StaffCastType.SEQUENTIAL && ticksUsed > 0 && ticksUsed % cycle == book.getMinChargeTime() % cycle) {
            book.executeCast(level, player, hand, bookStack, modifiers);
        }

        if (level.isClientSide) {
            book.spawnProgressiveParticles(level, player, ticksUsed);
        }
        book.playChargeSound(level, player, ticksUsed);

        if (!staff.isEmpty() && castType == StaffCastType.THROW && ticksUsed == book.getMinChargeTime()) {
            book.executeCast(level, player, hand, bookStack, modifiers);
            player.stopUsingItem();
        }
    }

    public static void release(Level level, Player player, BaseSpellBookItem book, ItemStack bookStack, InteractionHand hand, int ticksUsed, ItemStack staff) {
        StaffCastType castType = book.getStaffCastType();
        if (castType == StaffCastType.SEQUENTIAL) return;
        if (!staff.isEmpty() && castType == StaffCastType.THROW) return;
        if (ticksUsed >= book.getMinChargeTime()) {
            book.executeCast(level, player, hand, bookStack, modifiersOf(staff, book));
        }
    }
}
