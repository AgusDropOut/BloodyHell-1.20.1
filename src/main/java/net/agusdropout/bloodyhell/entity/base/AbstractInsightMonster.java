package net.agusdropout.bloodyhell.entity.base;

import net.agusdropout.bloodyhell.util.capability.InsightHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public abstract class AbstractInsightMonster extends Monster implements InsightEntity {

    protected AbstractInsightMonster(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }


    protected float getEntityInsightPlane(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            return InsightHelper.getInsight(player);
        } else if (entity instanceof TamableAnimal pet && pet.getOwner() instanceof ServerPlayer owner) {
            return InsightHelper.getInsight(owner);
        } else if (entity instanceof InsightEntity insightEntity) {
            return insightEntity.getMinimumInsight();
        }
        return -1.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            Entity attacker = source.getEntity();

            if (attacker != null) {
                float attackerInsight = this.getEntityInsightPlane(attacker);
                if (attackerInsight < this.getMinimumInsight()) {
                    return false;
                }
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
        float targetInsight = this.getEntityInsightPlane(target);
        if (targetInsight < this.getMinimumInsight()) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    protected float getSoundVolume() {
        return this.hasSufficientClientInsight() ? 1.0F : 0.0F;
    }

    @Override
    public void playAmbientSound() {
        if (this.hasSufficientClientInsight()) {
            super.playAmbientSound();
        }
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (this.hasSufficientClientInsight()) {
            super.playSound(sound, volume, pitch);
        }
    }
}