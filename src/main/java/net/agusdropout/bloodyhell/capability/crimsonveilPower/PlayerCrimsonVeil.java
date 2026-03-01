package net.agusdropout.bloodyhell.capability.crimsonveilPower;

import net.minecraft.nbt.CompoundTag;




public class PlayerCrimsonVeil {
    private int crimsonVeil;
    private final int MIN_CRIMSONVEIL = 0;
    private final int MAX_CRIMSOMVEIL = 100;

    public int getCrimsonVeil() {
        return crimsonVeil;
    }

    public void addCrimsomveil(int add) {
        this.crimsonVeil = Math.min(crimsonVeil + add, MAX_CRIMSOMVEIL);
    }

    public void subCrimsomveil(int sub) {
        this.crimsonVeil = Math.max(crimsonVeil - sub, MIN_CRIMSONVEIL);
    }

    public int getMAX_CRIMSOMVEIL() {
        return MAX_CRIMSOMVEIL;
    }

    public void copyFrom(PlayerCrimsonVeil source) {
        this.crimsonVeil = source.crimsonVeil;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("crimsonveil", crimsonVeil);
    }

    public void loadNBTData(CompoundTag nbt) {
        crimsonVeil = nbt.getInt("crimsonveil");
    }
}
