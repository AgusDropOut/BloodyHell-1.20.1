package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.staffs.StaffOfTheGoldenSorrowItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID)
public class StaffKillStreakHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() instanceof Player) return;
        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof StaffOfTheGoldenSorrowItem)) held = player.getOffhandItem();
        if (held.getItem() instanceof StaffOfTheGoldenSorrowItem staff) {
            staff.registerKill(held, player.level().getGameTime());
        }
    }
}
