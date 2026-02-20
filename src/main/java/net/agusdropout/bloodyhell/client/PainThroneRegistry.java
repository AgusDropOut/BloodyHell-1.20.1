package net.agusdropout.bloodyhell.client;

import net.minecraft.client.Minecraft;

import java.util.*;

public class PainThroneRegistry {

    private static final Map<UUID, Long> ACTIVE_JITTER_VICTIMS = new HashMap<>();

    public static void addVictim(UUID uuid, int duration) {
        long endTime = Minecraft.getInstance().level.getGameTime() + duration;
        ACTIVE_JITTER_VICTIMS.put(uuid, endTime);
    }

    public static float getIntensity(UUID uuid) {
        Long endTime = ACTIVE_JITTER_VICTIMS.get(uuid);

        if (endTime == null) return 0f;

        long currentTime = Minecraft.getInstance().level.getGameTime();
        long remaining = endTime - currentTime;

        if (remaining <= 0) {
            ACTIVE_JITTER_VICTIMS.remove(uuid);
            return 0f;
        }

        return Math.min(1.0f, remaining / 5.0f);
    }

    private static final Map<UUID, List<BrokenBoneInfo>> BROKEN_BONES = new HashMap<>();

    public static void breakBone(UUID uuid, int duration) {
        long endTime = Minecraft.getInstance().level.getGameTime() + duration;
        long uniqueSeed = new Random().nextLong();

        BROKEN_BONES.computeIfAbsent(uuid, k -> new ArrayList<>())
                .add(new BrokenBoneInfo(endTime, 0f, uniqueSeed));
    }

    public static class BrokenBoneInfo {
        public final long endTime;
        public float progress;
        public final long seed;

        public BrokenBoneInfo(long endTime, float progress, long seed) {
            this.endTime = endTime;
            this.progress = progress;
            this.seed = seed;
        }
    }

    // Returns the list of active breaks and updates their progress
    public static List<BrokenBoneInfo> getActiveBreaks(UUID uuid) {
        List<BrokenBoneInfo> breaks = BROKEN_BONES.get(uuid);
        if (breaks == null) return Collections.emptyList();

        long currentTime = Minecraft.getInstance().level.getGameTime();

        breaks.removeIf(info -> currentTime >= info.endTime);

        if (breaks.isEmpty()) {
            BROKEN_BONES.remove(uuid);
            return Collections.emptyList();
        }

        for (BrokenBoneInfo info : breaks) {
            if (info.progress < 1.0f) {
                info.progress = Math.min(1.0f, info.progress + 0.05f);
            }
        }

        return breaks;
    }



    public static boolean hasBrokenBone(UUID uuid) {
        return BROKEN_BONES.containsKey(uuid) && !BROKEN_BONES.get(uuid).isEmpty();
    }

    public static boolean hasJitterInfo(UUID uuid) {
        return ACTIVE_JITTER_VICTIMS.containsKey(uuid);
    }

    public static void printActiveVictims() {
        System.out.println("Active Pain Throne Victims:");
        for (Map.Entry<UUID, Long> entry : ACTIVE_JITTER_VICTIMS.entrySet()) {
            System.out.println("Entity ID: " + entry.getKey() + ", End Time: " + entry.getValue());
        }
    }
}