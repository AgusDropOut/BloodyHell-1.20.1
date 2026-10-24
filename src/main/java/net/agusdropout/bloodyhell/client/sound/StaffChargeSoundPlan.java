package net.agusdropout.bloodyhell.client.sound;

import java.util.Optional;

public final class StaffChargeSoundPlan {
    public enum Cue { NOTE, FULL, FADE }

    public record Step(Cue cue, float pitch) {}

    private static final int[] NOTE_SEMITONES = {0, 2, 4, 7};
    private static final float FADE_FROM_FULL_PITCH = 1.0f;
    private static final float FADE_TO_ZERO_PITCH = 0.7f;

    private StaffChargeSoundPlan() {}

    public static Optional<Step> between(int previous, int current, int full) {
        if (current > previous) {
            if (current >= full) return Optional.of(new Step(Cue.FULL, 1.0f));
            int index = Math.min(current, NOTE_SEMITONES.length) - 1;
            return Optional.of(new Step(Cue.NOTE, (float) Math.pow(2.0, NOTE_SEMITONES[index] / 12.0)));
        }
        if (current < previous) {
            if (current == 0) return Optional.of(new Step(Cue.FADE, FADE_TO_ZERO_PITCH));
            if (previous >= full) return Optional.of(new Step(Cue.FADE, FADE_FROM_FULL_PITCH));
        }
        return Optional.empty();
    }
}
