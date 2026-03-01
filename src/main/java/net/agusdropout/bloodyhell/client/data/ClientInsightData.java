package net.agusdropout.bloodyhell.client.data;

import net.agusdropout.bloodyhell.capability.insight.PlayerInsight;

public class ClientInsightData {
    private static int playerInsight = 100;

    public static void set(int insight) {
        if(insight > PlayerInsight.MAX_INSIGHT) {
            insight = PlayerInsight.MAX_INSIGHT;
        }
        ClientInsightData.playerInsight = insight;
    }
    public static int getPlayerInsight() {
        return playerInsight;
    }
}
