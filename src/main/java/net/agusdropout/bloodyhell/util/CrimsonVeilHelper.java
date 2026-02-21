package net.agusdropout.bloodyhell.util;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class CrimsonVeilHelper {

    /**
     * Checks if the player has enough Crimson Veil.
     */
    public static boolean hasEnough(Player player, int amount) {
        AtomicBoolean hasEnough = new AtomicBoolean(false);
        player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(cap -> {
            hasEnough.set(cap.getCrimsonVeil() >= amount);
        });
        return hasEnough.get();
    }

    /**
     * Consumes Crimson Veil and Syncs to Client.
     * Returns true if successful, false if not enough resources.
     * Should only be called on Server.
     */
    public static boolean consume(Player player, int amount) {
        if (player.level().isClientSide) return false;

        AtomicBoolean success = new AtomicBoolean(false);
        player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(cap -> {
            if (cap.getCrimsonVeil() >= amount) {
                cap.subCrimsomveil(amount);

                // Handle Syncing
                if (player instanceof ServerPlayer serverPlayer) {
                    ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(cap.getCrimsonVeil()), serverPlayer);
                }
                success.set(true);
            }
        });
        return success.get();
    }


    /**
     * Restores Crimson Veil and Syncs to Client.
     * Should only be called on Server.
     */
    public static void restore(Player player, int amount) {
        if (player.level().isClientSide) return;

        player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(cap -> {
            cap.addCrimsomveil(amount);

            // Handle Syncing
            if (player instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(cap.getCrimsonVeil()), serverPlayer);
            }
        });
    }

    public static int getAmount(Player player) {
        var cap = player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).resolve();
        return cap.map(crimsonVeil -> crimsonVeil.getCrimsonVeil()).orElse(0);
    }
}