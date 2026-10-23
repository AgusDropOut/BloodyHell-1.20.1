package net.agusdropout.bloodyhell.item.custom.base;

public record SpellBookDefinition(String id, String icon, StaffCastType castType, SpellAffinity affinity,
                                  int crimsonCost, int chargeTicks, int cooldownTicks) {
    public SpellBookDefinition {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("a spell book needs an id");
        if (icon == null || icon.isEmpty()) throw new IllegalArgumentException("spell book " + id + " needs an icon");
        if (castType == null) throw new IllegalArgumentException("spell book " + id + " needs a cast type");
        if (affinity == null) throw new IllegalArgumentException("spell book " + id + " needs an affinity");
        if (crimsonCost < 0) throw new IllegalArgumentException("spell book " + id + " has a negative cost");
        if (chargeTicks < 1) throw new IllegalArgumentException("spell book " + id + " needs at least 1 charge tick");
        if (cooldownTicks < 0) throw new IllegalArgumentException("spell book " + id + " has a negative cooldown");
    }
}
