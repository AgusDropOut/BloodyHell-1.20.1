package net.agusdropout.bloodyhell.util.capability;

import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class CrimsonVeilHelper {

    public static boolean hasEnough(Player player, int amount) {
        AtomicBoolean hasEnough = new AtomicBoolean(false);
        player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(cap -> {
            hasEnough.set(cap.getCrimsonVeil() >= amount);
        });
        return hasEnough.get();
    }

    public static boolean consume(Player player, int amount) {
        if (player.level().isClientSide) return false;

        AtomicBoolean success = new AtomicBoolean(false);
        player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(cap -> {
            if (cap.getCrimsonVeil() >= amount) {
                cap.subCrimsomveil(amount);
                if (player instanceof ServerPlayer serverPlayer) {
                    sync(serverPlayer);
                }
                success.set(true);
            }
        });
        return success.get();
    }

    public static void restore(Player player, int amount) {
        if (player.level().isClientSide) return;

        player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(cap -> {
            cap.addCrimsomveil(amount);
            if (player instanceof ServerPlayer serverPlayer) {
                sync(serverPlayer);
            }
        });
    }

    public static void sync(ServerPlayer player) {
        player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(cap -> {
            ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(cap.getCrimsonVeil()), player);
        });
    }

    public static int getAmount(Player player) {
        var cap = player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).resolve();
        return cap.map(crimsonVeil -> crimsonVeil.getCrimsonVeil()).orElse(0);
    }
}