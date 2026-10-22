package net.agusdropout.bloodyhell.item.custom.base;

public enum SpellType {
    BLOODFIRE_COLUMN("bloodfire_column"),
    RHNULL_IMPALER("rhnull_impaler"),
    BLOODFIRE_METEOR("bloodfire_meteor"),
    BLOODFIRE_SOUL("bloodfire_soul"),
    BLOOD_SCRATCH("blood_scratch"),
    BLOOD_SPHERE("blood_sphere"),
    BLOOD_NOVA("blood_nova"),
    BLOOD_DAGGERSRAIN("blood_daggersrain"),
    RHNULL_HEAVY_SWORD("rhnull_heavy_sword"),
    RHNULL_PAIN_THRONE("rhnull_golden_throne"),
    RHNULL_ORB_EMITTER("rhnull_orb_emitter");

    private final String id;

    SpellType(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}