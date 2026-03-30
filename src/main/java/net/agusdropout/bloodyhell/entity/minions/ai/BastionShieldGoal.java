package net.agusdropout.bloodyhell.entity.minions.ai;

import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BastionShieldGoal extends Goal {
    private final BastionOfTheUnknownEntity entity;
    private int shieldTime;
    private float maxShieldDamage;

    public BastionShieldGoal(BastionOfTheUnknownEntity entity, float maxShieldDamage) {
        this.entity = entity;
        this.maxShieldDamage = maxShieldDamage;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && this.entity.getShieldCooldown() <= 0
                && !this.entity.isLunging()
                && this.entity.distanceToSqr(target) < 256.0D
                && this.entity.getRandom().nextInt(20) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.shieldTime > 0 && this.entity.getShieldedDamage() < this.entity.getMaxShieldCapacity();
    }

    @Override
    public void start() {
        this.shieldTime = 60;
        this.entity.setShieldedDamage(0.0F);
        this.entity.setBlocking(true);
        this.entity.getNavigation().stop();
        this.entity.triggerAnim("action_controller", "block");
    }

    @Override
    public void stop() {
        this.entity.setShieldCooldown(100);
        this.entity.setBlocking(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
        if ( shieldTime < 0 || this.entity.getShieldedDamage() >= this.maxShieldDamage) {
            stop();
        }
        entity.getNavigation().stop();

        this.shieldTime--;
    }
}