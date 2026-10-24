package net.agusdropout.bloodyhell.client.sound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrbHeartbeatTest {
    private static final float STEP = 0.01f;

    private static int countBig(float seconds) {
        int total = 0;
        float clock = 0.0f;
        while (clock < seconds) {
            float next = clock + STEP;
            total += OrbHeartbeat.bigBeats(clock, next);
            clock = next;
        }
        return total;
    }

    private static int countSmall(float seconds) {
        int total = 0;
        float clock = 0.0f;
        while (clock < seconds) {
            float next = clock + STEP;
            total += OrbHeartbeat.smallBeats(clock, next);
            clock = next;
        }
        return total;
    }

    @Test
    void thereIsOneBigAndOneSmallBeatPerPulseCycle() {
        assertEquals(15, countBig(10.0f), 1);
        assertEquals(15, countSmall(10.0f), 1);
    }

    @Test
    void theSmallBeatComesBeforeTheBigOneInEveryCycle() {
        float firstSmall = -1.0f;
        float firstBig = -1.0f;
        float clock = 0.0f;
        while (firstSmall < 0.0f || firstBig < 0.0f) {
            float next = clock + 0.001f;
            if (firstSmall < 0.0f && OrbHeartbeat.smallBeats(clock, next) > 0) firstSmall = next;
            if (firstBig < 0.0f && OrbHeartbeat.bigBeats(clock, next) > 0) firstBig = next;
            clock = next;
        }
        assertEquals(0.1f, firstBig - firstSmall, 0.005f);
    }

    @Test
    void aStandingClockProducesNoBeat() {
        assertEquals(0, OrbHeartbeat.bigBeats(3.3f, 3.3f));
        assertEquals(0, OrbHeartbeat.smallBeats(3.3f, 3.3f));
    }

    @Test
    void aBackwardsClockProducesNoBeat() {
        assertEquals(0, OrbHeartbeat.bigBeats(5.0f, 1.0f));
    }

    @Test
    void aLongFrameCountsEveryBeatInIt() {
        assertEquals(3, OrbHeartbeat.bigBeats(0.0f, 2.0f), 1);
    }
}
