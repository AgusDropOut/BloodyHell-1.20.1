package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.entity.projectile.BloodFireColumnProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

public class RitekeeperFlamePillarGoal extends Goal {
    private final RitekeeperEntity mob;
    private int animationTicks;

    private final int CAST_POINT = 22; // 1.10 Seconds
    private final int ANIMATION_TOTAL = 45;

    public RitekeeperFlamePillarGoal(RitekeeperEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && !this.mob.isCasting() && this.mob.isPillarReady()
                && this.mob.distanceToSqr(target) < 256;
    }

    @Override
    public void start() {
        this.animationTicks = 0;
        this.mob.setCasting(true);
        this.mob.setCurrentSpell(3); // Pillar ID
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setCasting(false);
        this.mob.setCurrentSpell(0);
        this.mob.setPillarCooldown(RitekeeperEntity.CD_PILLAR_MAX);
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

            // 1. Create Pillar directly at target's feet
            BloodFireColumnProjectile pillar = new BloodFireColumnProjectile(ModEntityTypes.BLOOD_FIRE_COLUMN_PROJECTILE.get(),this.mob.level(),this.mob ,target.getX(), target.getY(), target.getZ());

            // 2. Spawn & Sound
            this.mob.level().addFreshEntity(pillar);
            this.mob.playSound(SoundEvents.FIRECHARGE_USE, 1.0f, 0.5f);
        }
    }
}