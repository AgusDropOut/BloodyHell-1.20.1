package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.minecraft.util.StringRepresentable;

public enum ConnectionType implements StringRepresentable {
    NONE,
    SINGLE,

    LEFT_SMALL,
    RIGHT_SMALL,

    LEFT_BIG,
    RIGHT_BIG;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}