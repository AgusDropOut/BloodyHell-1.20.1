package net.agusdropout.bloodyhell.item.custom.staffs;

import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.item.custom.base.SpellAffinity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class StaffOfTheGoldenSorrowItem extends BaseStaffItem {
    private static final String NBT_NEXT_DECAY = "NextDecay";

    public StaffOfTheGoldenSorrowItem(Properties properties) {
        super(properties, SpellAffinity.RHNULL, null);
    }

    @Override
    public String getId() {
        return "staff_of_the_golden_sorrow";
    }

    public void registerKill(ItemStack staff, long gameTime) {
        write(staff, read(staff).at(gameTime).afterKill(gameTime, MARKED_FOR_FULL_BONUS));
    }

    @Override
    protected void tickCharge(ItemStack staff, Player player, boolean selected) {
        if (player.tickCount % BONUS_REFRESH_TICKS == 0) {
            refreshCharge(staff, player);
        }
    }

    @Override
    protected void refreshCharge(ItemStack staff, Player player) {
        write(staff, read(staff).at(player.level().getGameTime()));
    }

    @Override
    protected boolean hasCharge() {
        return true;
    }

    @Override
    protected void appendChargeSource(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.bloodyhell.staff.kill_streak", MARKED_FOR_FULL_BONUS, KillStreak.GRACE_TICKS / 20, KillStreak.DECAY_TICKS / 20).withStyle(ChatFormatting.GRAY));
    }

    private KillStreak read(ItemStack staff) {
        long nextDecay = staff.hasTag() ? staff.getTag().getLong(NBT_NEXT_DECAY) : 0L;
        return new KillStreak(getCharge(staff), nextDecay);
    }

    private void write(ItemStack staff, KillStreak streak) {
        storeCharge(staff, streak.charge());
        if (streak.charge() > 0) {
            staff.getOrCreateTag().putLong(NBT_NEXT_DECAY, streak.nextDecayTick());
        } else if (staff.hasTag()) {
            staff.getTag().remove(NBT_NEXT_DECAY);
        }
    }
}
