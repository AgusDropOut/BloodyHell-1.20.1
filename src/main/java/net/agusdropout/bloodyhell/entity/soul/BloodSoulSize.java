package net.agusdropout.bloodyhell.entity.soul;

public enum BloodSoulSize {
    SMALL(0.5f, 100),   // 100 mB
    MEDIUM(1.0f, 250),  // 250 mB
    LARGE(2.0f, 1000);  // 1 Bucket (1000 mB)

    public final float scale;
    public final int fluidAmount;

    BloodSoulSize(float scale, int fluidAmount) {
        this.scale = scale;
        this.fluidAmount = fluidAmount;
    }
}