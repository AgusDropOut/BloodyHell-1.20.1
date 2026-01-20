package net.agusdropout.bloodyhell.effect;

import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
// Import your other custom entities here if needed
// import net.agusdropout.bloodyhell.entity.custom.IgorEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BloodFireEffect extends MobEffect {

    public BloodFireEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 1. "Unholy Healing" Logic
        // If the entity is one of your blood constructs/bosses, HEAL them.
        if (entity instanceof RitekeeperEntity) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(1.0F); // Heals 0.5 hearts per tick cycle
            }
        }
        // 2. Damage Logic for everyone else
        else {
            // Deals magic damage so armor doesn't fully block it
            entity.hurt(entity.damageSources().magic(), 1.0F);
        }

        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Runs logic every 40 ticks (2 seconds) to avoid spamming damage too fast
        // Change "40" to "20" if you want damage every second (more lethal)
        int i = 40 >> amplifier;
        if (i > 0) {
            return duration % i == 0;
        } else {
            return true;
        }
    }
}