package net.agusdropout.bloodyhell.entity.soul;

public enum BloodSoulSize {
    SMALL(0.5f, 100),
    MEDIUM(1.0f, 250),
    LARGE(2.0f, 1000);

    public final float scale;
    public final int fluidAmount;

    BloodSoulSize(float scale, int fluidAmount) {
        this.scale = scale;
        this.fluidAmount = fluidAmount;
    }
}