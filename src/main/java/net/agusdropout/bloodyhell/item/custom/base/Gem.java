package net.agusdropout.bloodyhell.item.custom.base;

import net.minecraft.network.chat.Component;

public class Gem {

    private final double value;
    private final GemType type;
    private final String stat;

    public Gem(GemType type, String stat, double value) {
        this.type = type;
        this.stat = stat;
        this.value = value;
    }

    public double getValue() {
        return value;
    }
    public GemType getType() {
        return type;
    }
    public String getStat() {
        return stat;
    }

}
