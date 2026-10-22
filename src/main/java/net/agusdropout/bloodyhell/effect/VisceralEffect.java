package net.agusdropout.bloodyhell.effect;

import net.agusdropout.bloodyhell.item.registry.GemItems;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VisceralEffect extends MobEffect {

    public VisceralEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        if (entity.getType().is(ModTags.Entities.INMUNE_TO_VISCERAL_EFFECT)) {
            return;
        }
        if (entity instanceof Player player) {
            if (player.getInventory().contains(GemItems.BLASPHEMOUS_RING.get().getDefaultInstance())) {
                return;
            }
        }

        if (!entity.hasEffect(MobEffects.CONFUSION) || entity.getEffect(MobEffects.CONFUSION).getDuration() < 20) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0, false, false, false));
        }

        if (entity.tickCount % 60 == 0) {
            performHeave(entity, amplifier);
        }

        if (entity.tickCount % 40 == 0) {
            float damage = 1.0F + amplifier;

            entity.hurt(entity.damageSources().magic(), damage);
        }

        super.applyEffectTick(entity, amplifier);
    }

    private void performHeave(LivingEntity entity, int amplifier) {
        RandomSource random = entity.getRandom();

        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 1 + amplifier, false, false, false));

        Vec3 lookVec = entity.getLookAngle();
        double speed = 0.2;

        double startY = entity.getY() + entity.getEyeHeight() * 0.8;

        for (int i = 0; i < 6; i++) {
            double spreadX = (random.nextDouble() - 0.5) * 0.2;
            double spreadY = (random.nextDouble() - 0.5) * 0.2;
            double spreadZ = (random.nextDouble() - 0.5) * 0.2;

            entity.level().addParticle(
                    ModParticles.VICERAL_PARTICLE.get(),
                    entity.getX(), startY, entity.getZ(),
                    lookVec.x * speed + spreadX,
                    lookVec.y * speed + spreadY - 0.05,
                    lookVec.z * speed + spreadZ
            );
        }

        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.HONEY_BLOCK_BREAK, entity.getSoundSource(), 1.0F, 0.8F);

        if (entity instanceof Player player) {
            player.setXRot(Mth.clamp(player.getXRot() + 3.0F, -90F, 90F));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}