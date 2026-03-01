package net.agusdropout.bloodyhell.util.capability;

import net.agusdropout.bloodyhell.capability.insight.PlayerInsight;
import net.agusdropout.bloodyhell.capability.insight.PlayerInsightProvider;
import net.agusdropout.bloodyhell.networking.ModMessages;

import net.agusdropout.bloodyhell.networking.packet.S2CDataSyncInsightPacket;
import net.minecraft.server.level.ServerPlayer;

public class InsightHelper {

    public static void addInsight(ServerPlayer player, int amount) {
        player.getCapability(PlayerInsightProvider.PLAYER_INSIGHT).ifPresent(insight -> {
            insight.addInsight(amount);
            sync(player, insight);
        });
    }

    public static void subInsight(ServerPlayer player, int amount) {
        player.getCapability(PlayerInsightProvider.PLAYER_INSIGHT).ifPresent(insight -> {
            insight.subInsight(amount);
            sync(player, insight);
        });
    }

    public static void setInsight(ServerPlayer player, int amount) {
        player.getCapability(PlayerInsightProvider.PLAYER_INSIGHT).ifPresent(insight -> {
            insight.setInsight(amount);
            sync(player, insight);
        });
    }

    public static void syncInsight(ServerPlayer player) {
        player.getCapability(PlayerInsightProvider.PLAYER_INSIGHT).ifPresent(insight -> {
            sync(player, insight);
        });
    }

    private static void sync(ServerPlayer player, PlayerInsight insight) {
        ModMessages.sendToPlayer(new S2CDataSyncInsightPacket(insight.getInsight()), player);
    }
}