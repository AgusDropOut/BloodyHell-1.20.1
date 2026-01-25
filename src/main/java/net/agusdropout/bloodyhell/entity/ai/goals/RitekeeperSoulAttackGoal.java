package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.entity.projectile.BloodFireSoulProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RitekeeperSoulAttackGoal extends Goal {
    private final RitekeeperEntity mob;
    private int animationTicks;

    private final int CAST_POINT = 35; // 1.75 Seconds
    private final int ANIMATION_TOTAL = 55;

    public RitekeeperSoulAttackGoal(RitekeeperEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && !this.mob.isCasting() && this.mob.isSoulReady()
                && this.mob.distanceToSqr(target) < 400;
    }

    @Override
    public void start() {
        this.animationTicks = 0;
        this.mob.setCasting(true);
        this.mob.setCurrentSpell(2); // Soul ID
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setCasting(false);
        this.mob.setCurrentSpell(0);
        this.mob.setSoulCooldown(RitekeeperEntity.CD_SOUL_MAX);
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

            // 1. Spawn from Boss Chest Height
            Vec3 spawnPos = this.mob.position().add(0, 1.5, 0);

            // 2. Create Projectile
            BloodFireSoulProjectile soul = new BloodFireSoulProjectile(ModEntityTypes.BLOOD_FIRE_SOUL.get(), this.mob.level(),this.mob);
            soul.setPos(spawnPos);

            // 3. Aim at target
            Vec3 dir = target.getBoundingBox().getCenter().subtract(spawnPos).normalize();
            soul.setDeltaMovement(dir.scale(0.8)); // Slower, homing?

            // 4. Spawn & Sound
            this.mob.level().addFreshEntity(soul);
            this.mob.playSound(SoundEvents.BLAZE_SHOOT, 1.0f, 1.2f);
        }
    }
}