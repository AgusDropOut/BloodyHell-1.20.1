package net.agusdropout.bloodyhell.client.sound;

import net.agusdropout.bloodyhell.client.sound.StaffChargeSoundPlan.Cue;
import net.agusdropout.bloodyhell.client.sound.StaffChargeSoundPlan.Step;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaffChargeSoundPlanTest {
    private static final int FULL = 5;

    private static Step step(int previous, int current) {
        Optional<Step> step = StaffChargeSoundPlan.between(previous, current, FULL);
        assertTrue(step.isPresent(), "expected a sound from " + previous + " to " + current);
        return step.get();
    }

    @Test
    void everyNewTargetBelowFullPlaysANoteThatRises() {
        float previousPitch = 0.0f;
        for (int count = 1; count < FULL; count++) {
            Step step = step(count - 1, count);
            assertEquals(Cue.NOTE, step.cue());
            assertTrue(step.pitch() > previousPitch, "the note for target " + count + " is not higher than the one before");
            previousPitch = step.pitch();
        }
    }

    @Test
    void theFirstNoteKeepsTheBasePitch() {
        assertEquals(1.0f, step(0, 1).pitch());
    }

    @Test
    void reachingFullPlaysTheFullSound() {
        assertEquals(Cue.FULL, step(4, 5).cue());
    }

    @Test
    void jumpingSeveralTargetsPlaysOnlyOneSound() {
        assertEquals(Cue.FULL, step(0, 5).cue());
        assertEquals(Cue.NOTE, step(0, 3).cue());
    }

    @Test
    void losingFullPlaysTheFadeAtTheBasePitch() {
        Step step = step(5, 4);
        assertEquals(Cue.FADE, step.cue());
        assertEquals(1.0f, step.pitch());
    }

    @Test
    void intermediateDropsAreSilent() {
        assertTrue(StaffChargeSoundPlan.between(4, 3, FULL).isEmpty());
        assertTrue(StaffChargeSoundPlan.between(3, 2, FULL).isEmpty());
        assertTrue(StaffChargeSoundPlan.between(2, 1, FULL).isEmpty());
    }

    @Test
    void reachingZeroPlaysALowerFade() {
        Step fromMiddle = step(3, 0);
        Step fromFull = step(5, 0);
        assertEquals(Cue.FADE, fromMiddle.cue());
        assertEquals(Cue.FADE, fromFull.cue());
        assertTrue(fromMiddle.pitch() < 1.0f);
        assertEquals(fromMiddle.pitch(), fromFull.pitch());
    }

    @Test
    void noChangeIsSilent() {
        for (int count = 0; count <= FULL; count++) {
            assertTrue(StaffChargeSoundPlan.between(count, count, FULL).isEmpty());
        }
    }

    @Test
    void notesStayInsideThePitchRangeOfTheGame() {
        for (int count = 1; count < FULL; count++) {
            float pitch = step(count - 1, count).pitch();
            assertTrue(pitch >= 0.5f && pitch <= 2.0f, "pitch out of range: " + pitch);
        }
    }
}
