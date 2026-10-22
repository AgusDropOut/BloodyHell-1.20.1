package net.agusdropout.bloodyhell.effect;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class IlluminatedEffect extends MobEffect {
    protected IlluminatedEffect(MobEffectCategory mobEffectCategory, int amplifier) {
        super(mobEffectCategory, amplifier);
    }
    public void applyEffectTick(LivingEntity entity, int amplifier) {


            entity.removeEffect(ModEffects.BLOOD_LUST.get());

        super.applyEffectTick(entity, amplifier);
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {

        return true;
    }

}
