package net.agusdropout.bloodyhell.item.custom.staffs;

public record KillStreak(int charge, long nextDecayTick) {
    public static final int GRACE_TICKS = 100;
    public static final int DECAY_TICKS = 60;

    public static KillStreak empty() {
        return new KillStreak(0, 0L);
    }

    public KillStreak afterKill(long now, int max) {
        return new KillStreak(Math.min(charge + 1, max), now + GRACE_TICKS);
    }

    public KillStreak at(long now) {
        if (charge <= 0 || now < nextDecayTick) return this;
        long steps = 1 + (now - nextDecayTick) / DECAY_TICKS;
        long left = charge - steps;
        if (left <= 0) return empty();
        return new KillStreak((int) left, nextDecayTick + steps * DECAY_TICKS);
    }
}
