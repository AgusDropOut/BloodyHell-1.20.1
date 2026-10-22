package net.agusdropout.bloodyhell.effect;

import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BloodFireEffect extends MobEffect {

    public BloodFireEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        if (entity instanceof RitekeeperEntity) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(1.0F);
            }
        }

        else {

            entity.hurt(entity.damageSources().magic(), 1.0F);
        }

        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {

        int i = 40 >> amplifier;
        if (i > 0) {
            return duration % i == 0;
        } else {
            return true;
        }
    }
}