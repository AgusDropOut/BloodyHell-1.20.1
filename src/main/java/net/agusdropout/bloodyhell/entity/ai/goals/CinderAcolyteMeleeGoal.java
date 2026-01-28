package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.CinderAcolyteEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CinderAcolyteMeleeGoal extends Goal {
    private final CinderAcolyteEntity mob;
    private int attackTicks;
    private final int ATTACK_DAMAGE_POINT = 15; // 1.0 seconds (20 ticks)
    private final int ANIMATION_DURATION = 25; // Total animation length

    public CinderAcolyteMeleeGoal(CinderAcolyteEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive()
                && this.mob.distanceTo(target) < 2.0D // Very close range
                && this.mob.isMeleeReady()
                && !this.mob.isFlameAttacking();
    }

    @Override
    public void start() {
        this.attackTicks = 0;
        this.mob.setMeleeAttacking(true);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setMeleeAttacking(false);
        this.mob.setMeleeCooldown(CinderAcolyteEntity.MELEE_ATTACK_COOLDOWN_MAX);
    }

    @Override
    public boolean canContinueToUse() {
        return this.attackTicks < ANIMATION_DURATION;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        System.out.println("Cinder Acolyte Melee Attack Tick: " + this.attackTicks);

        this.attackTicks++;

        // Deal damage at exactly 1.0s
        if (this.attackTicks == ATTACK_DAMAGE_POINT && target != null) {
            double distSqr = this.mob.distanceToSqr(target);
            if (distSqr < 12.0D) { // Allow slight range forgiveness
                this.mob.performMeleeDamage(target);
                mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                        SoundEvents.CHAIN_BREAK, mob.getSoundSource(),
                        1.0F, 1.0F + (mob.getRandom().nextFloat() - mob.getRandom().nextFloat()) * 0.2F);

            }
        }
        if(this.attackTicks > ANIMATION_DURATION) {
            this.stop();
        }
    }
}