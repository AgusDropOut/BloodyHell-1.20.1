package net.agusdropout.bloodyhell.item.custom.base;

public enum StaffCastType {
    THROW(40.0f),
    CIRCLE(20.0f),
    RISE(80.0f),
    HOLD(20.0f),
    SEQUENTIAL(20.0f);

    private final float baseTicks;

    StaffCastType(float baseTicks) {
        this.baseTicks = baseTicks;
    }

    public float getBaseTicks() {
        return this.baseTicks;
    }
}