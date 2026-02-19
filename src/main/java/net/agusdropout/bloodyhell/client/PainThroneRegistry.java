package net.agusdropout.bloodyhell.client;

import net.minecraft.client.Minecraft;

import java.util.*;

public class PainThroneRegistry {

    private static final Map<UUID, Long> ACTIVE_VICTIMS = new HashMap<>();

    public static void addVictim(UUID uuid, int duration) {
        long endTime = Minecraft.getInstance().level.getGameTime() + duration;
        ACTIVE_VICTIMS.put(uuid, endTime);
    }

    public static float getIntensity(UUID uuid) {
        Long endTime = ACTIVE_VICTIMS.get(uuid);

        if (endTime == null) return 0f;

        long currentTime = Minecraft.getInstance().level.getGameTime();
        long remaining = endTime - currentTime;

        if (remaining <= 0) {
            ACTIVE_VICTIMS.remove(uuid);
            return 0f;
        }

        return Math.min(1.0f, remaining / 5.0f);
    }

    public static void printActiveVictims() {
        System.out.println("Active Pain Throne Victims:");
        for (Map.Entry<UUID, Long> entry : ACTIVE_VICTIMS.entrySet()) {
            System.out.println("Entity ID: " + entry.getKey() + ", End Time: " + entry.getValue());
        }
    }
}