package net.agusdropout.bloodyhell.capability.insight;

import net.minecraft.nbt.CompoundTag;

public class PlayerInsight {
    private int insight;
    public static final int MIN_INSIGHT = 0;
    public static final int MAX_INSIGHT = 100;

    public int getInsight() {
        return insight;
    }

    public void setInsight(int set) {
        this.insight = Math.max(MIN_INSIGHT, Math.min(set, MAX_INSIGHT));
    }

    public void addInsight(int add) {
        this.insight = Math.min(this.insight + add, MAX_INSIGHT);
    }

    public void subInsight(int sub) {
        this.insight = Math.max(this.insight - sub, MIN_INSIGHT);
    }

    public void copyFrom(PlayerInsight source) {
        this.insight = source.insight;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("insight", insight);
    }

    public void loadNBTData(CompoundTag nbt) {
        insight = nbt.getInt("insight");
    }
}