package net.agusdropout.bloodyhell.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.function.BooleanSupplier;

public class StaffEmberLoopSound extends AbstractTickableSoundInstance {
    private static final float VOLUME = 0.3f;

    private final BooleanSupplier wanted;

    public StaffEmberLoopSound(SoundEvent event, BooleanSupplier wanted) {
        super(event, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.delay = 0;
        this.volume = VOLUME;
        this.relative = true;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.wanted = wanted;
    }

    @Override
    public void tick() {
        if (!wanted.getAsBoolean()) stop();
    }
}
