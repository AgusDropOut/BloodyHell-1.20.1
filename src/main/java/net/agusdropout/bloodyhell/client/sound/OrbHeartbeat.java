package net.agusdropout.bloodyhell.client.sound;

public final class OrbHeartbeat {
    public static final float PULSES_PER_CLOCK_UNIT = 1.5f;
    public static final float BIG_PHASE = 0.5f;
    public static final float SMALL_PHASE = 0.35f;

    private OrbHeartbeat() {}

    public static int bigBeats(float previousClock, float clock) {
        return crossings(previousClock, clock, BIG_PHASE);
    }

    public static int smallBeats(float previousClock, float clock) {
        return crossings(previousClock, clock, SMALL_PHASE);
    }

    private static int crossings(float previousClock, float clock, float phase) {
        double before = Math.floor(previousClock * PULSES_PER_CLOCK_UNIT - phase);
        double after = Math.floor(clock * PULSES_PER_CLOCK_UNIT - phase);
        return (int) Math.max(0.0, after - before);
    }
}
