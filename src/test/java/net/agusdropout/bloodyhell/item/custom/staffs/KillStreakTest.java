package net.agusdropout.bloodyhell.item.custom.staffs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class KillStreakTest {
    private static final int FULL = 5;

    private static KillStreak streakOf(int kills, long at) {
        KillStreak streak = KillStreak.empty();
        for (int i = 0; i < kills; i++) streak = streak.afterKill(at, FULL);
        return streak;
    }

    @Test
    void everyKillAddsOneUpToFull() {
        assertEquals(1, streakOf(1, 0).charge());
        assertEquals(5, streakOf(5, 0).charge());
        assertEquals(5, streakOf(9, 0).charge());
    }

    @Test
    void nothingIsLostDuringTheGracePeriod() {
        KillStreak streak = streakOf(3, 0);
        assertSame(streak, streak.at(KillStreak.GRACE_TICKS - 1));
        assertEquals(3, streak.at(KillStreak.GRACE_TICKS - 1).charge());
    }

    @Test
    void oneChargeIsLostWhenTheGracePeriodEnds() {
        assertEquals(2, streakOf(3, 0).at(KillStreak.GRACE_TICKS).charge());
    }

    @Test
    void thenOneChargeIsLostEveryDecayPeriod() {
        KillStreak streak = streakOf(5, 0);
        long start = KillStreak.GRACE_TICKS;
        assertEquals(4, streak.at(start).charge());
        assertEquals(4, streak.at(start + KillStreak.DECAY_TICKS - 1).charge());
        assertEquals(3, streak.at(start + KillStreak.DECAY_TICKS).charge());
        assertEquals(1, streak.at(start + 3L * KillStreak.DECAY_TICKS).charge());
        assertEquals(0, streak.at(start + 4L * KillStreak.DECAY_TICKS).charge());
    }

    @Test
    void aLongAbsenceEmptiesTheStreak() {
        assertEquals(0, streakOf(5, 0).at(1_000_000L).charge());
    }

    @Test
    void evaluatingInSeveralStepsGivesTheSameResultAsOneStep() {
        KillStreak streak = streakOf(5, 0);
        long end = 200;
        KillStreak stepped = streak;
        for (long now = 0; now <= end; now += 7) stepped = stepped.at(now);
        assertEquals(streak.at(end).charge(), stepped.at(end).charge());
    }

    @Test
    void aKillRestartsTheGracePeriod() {
        KillStreak streak = streakOf(2, 0).at(KillStreak.GRACE_TICKS - 1).afterKill(KillStreak.GRACE_TICKS - 1, FULL);
        assertEquals(3, streak.charge());
        assertEquals(3, streak.at(2L * KillStreak.GRACE_TICKS - 2).charge());
    }

    @Test
    void anEmptyStreakStaysEmpty() {
        assertEquals(0, KillStreak.empty().at(5_000L).charge());
    }
}
