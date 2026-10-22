package net.agusdropout.bloodyhell.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public final class ModSoundTypes {
    public static final SoundType SILENT = new SoundType(
            0.0F,
            0.0F,
            SoundEvents.WOOL_BREAK,
            SoundEvents.WOOL_STEP,
            SoundEvents.WOOL_PLACE,
            SoundEvents.WOOL_HIT,
            SoundEvents.WOOL_FALL
    );

    private ModSoundTypes() {}
}
