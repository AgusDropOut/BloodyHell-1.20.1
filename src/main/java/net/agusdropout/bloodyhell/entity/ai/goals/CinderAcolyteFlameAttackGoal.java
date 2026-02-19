package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.CinderAcolyteEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CinderAcolyteFlameAttackGoal extends Goal {
    private final CinderAcolyteEntity mob;
    private int attackTicks;

    private final int DAMAGE_START_POINT = 15; // 0.75 seconds (Windup)
    private final int ANIMATION_DURATION = 40;  // 3.0 seconds (Total)
    private static final int minimunDistance = 16; // 4 Blocks sqr

    public CinderAcolyteFlameAttackGoal(CinderAcolyteEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        double dist = this.mob.distanceToSqr(target);

        return dist > minimunDistance && dist < 64.0D
                && this.mob.isFlameReady()
                && !this.mob.isMeleeAttacking();
    }

    @Override
    public void start() {
        this.attackTicks = 0;
        this.mob.setFlameAttacking(true);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setFlameAttacking(false);
        this.mob.setFlameCooldown(CinderAcolyteEntity.FLAME_ATTACK_COOLDOWN_MAX);
    }

    @Override
    public boolean canContinueToUse() {
        return this.attackTicks < ANIMATION_DURATION;
    }

    @Override
    public void tick() {
        this.attackTicks++;


        if (this.attackTicks < DAMAGE_START_POINT) {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
        }


        if (this.attackTicks >= DAMAGE_START_POINT) {
            if (this.attackTicks % 4 == 0) {
                this.mob.performFlameAreaDamage();
            }
        }

        if(this.attackTicks >= ANIMATION_DURATION) {
            stop();
        }
    }

    public static int getFlameAttackDistance() {
        return minimunDistance;
    }
}