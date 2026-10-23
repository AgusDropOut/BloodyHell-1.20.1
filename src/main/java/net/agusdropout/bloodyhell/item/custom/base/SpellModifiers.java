package net.agusdropout.bloodyhell.item.custom.base;

public record SpellModifiers(float damage, float cost, float cooldown) {
    public static final SpellModifiers NONE = new SpellModifiers(1.0f, 1.0f, 1.0f);

    public int scaleCost(int baseCost) {
        return Math.max(0, Math.round(baseCost * cost));
    }

    public int scaleCooldown(int baseCooldown) {
        return Math.max(0, Math.round(baseCooldown * cooldown));
    }
}
