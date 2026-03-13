package net.agusdropout.bloodyhell.item.potions;

import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonVeil;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class BloodPotionItem extends PotionItem {

    private final int veilPowerGranted;

    public BloodPotionItem(Properties properties, int veilPowerGranted) {
        super(properties);
        this.veilPowerGranted = veilPowerGranted;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        if (!level.isClientSide && player != null) {
            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(crimsonVeil -> {
                if (crimsonVeil.getCrimsonVeil() < PlayerCrimsonVeil.MAX_CRIMSOMVEIL) {
                    crimsonVeil.addCrimsomveil(this.veilPowerGranted);
                    ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(crimsonVeil.getCrimsonVeil()), (ServerPlayer) player);
                }
            });
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        if (player == null || !player.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(ModItems.BLOOD_FLASK.get());
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(ModItems.BLOOD_FLASK.get()));
            }
        }

        entity.gameEvent(GameEvent.DRINK);
        return stack;
    }
}