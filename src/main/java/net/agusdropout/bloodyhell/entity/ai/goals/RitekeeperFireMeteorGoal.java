package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodFireMeteorEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RitekeeperFireMeteorGoal extends Goal {
    private final RitekeeperEntity mob;
    private int animationTicks;

    private final int CAST_POINT = 30; // 1.5 Seconds
    private final int ANIMATION_TOTAL = 50;

    public RitekeeperFireMeteorGoal(RitekeeperEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && !this.mob.isCasting() && this.mob.isMeteorReady()
                && this.mob.distanceToSqr(target) < 900;
    }

    @Override
    public void start() {
        this.animationTicks = 0;
        this.mob.setCasting(true);
        this.mob.setCurrentSpell(1); // Meteor ID
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setCasting(false);
        this.mob.setCurrentSpell(0);
        this.mob.setMeteorCooldown(RitekeeperEntity.CD_METEOR_MAX);
    }

    @Override
    public boolean canContinueToUse() {
        return this.animationTicks < ANIMATION_TOTAL;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) this.mob.getLookControl().setLookAt(target, 30f, 30f);

        this.animationTicks++;

        // --- LOGIC MOVED HERE ---
        if (this.animationTicks == CAST_POINT && target != null) {

            // 1. Calculate Spawn Position (High above target)
            Vec3 spawnPos = target.position().add(0, 10, 0);

            // 2. Create Projectile
            BloodFireMeteorEntity meteor = new BloodFireMeteorEntity( this.mob.level(), this.mob, 10.0f, 0.3f, 1);
            meteor.setPos(spawnPos);

            // 3. Aim DOWN at the target
            Vec3 dir = target.position().subtract(spawnPos).normalize();
            meteor.setDeltaMovement(dir.scale(1.5)); // Speed

            // 4. Spawn & Sound
            this.mob.level().addFreshEntity(meteor);
            this.mob.playSound(SoundEvents.GHAST_SHOOT, 1.0f, 0.5f);
        }
    }
}