package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.minecraft.util.StringRepresentable;

public enum ConnectionType implements StringRepresentable {
    NONE,           // Internal (Hidden)
    SINGLE,         // 1x1 OR Middle (Uses 1x1 texture)

    // 2-Wide Wall
    LEFT_SMALL,     // Left side of a 2x2
    RIGHT_SMALL,    // Right side of a 2x2

    // 3-Wide Wall (or larger)
    LEFT_BIG,       // Left Corner of 3x3
    RIGHT_BIG;      // Right Corner of 3x3

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}