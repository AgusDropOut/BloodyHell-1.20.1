package net.agusdropout.bloodyhell.entity.minions.ai;

import net.agusdropout.bloodyhell.entity.minions.custom.WeepingOcularEntity;
import net.agusdropout.bloodyhell.entity.projectile.WeepingTearEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class OcularShootGoal extends Goal {
    private final WeepingOcularEntity ocular;
    private LivingEntity target;
    private int attackCooldown = -1;
    private final double speedModifier;
    private final int attackInterval;
    private final float attackRadiusSqr;

    public OcularShootGoal(WeepingOcularEntity ocular, double speedModifier, int attackInterval, float attackRadius) {
        this.ocular = ocular;
        this.speedModifier = speedModifier;
        this.attackInterval = attackInterval;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.ocular.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            this.target = livingEntity;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || !this.ocular.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.target = null;
        this.attackCooldown = -1;
        this.ocular.getNavigation().stop();
    }

    @Override
    public void tick() {
        double distanceSq = this.ocular.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean hasLineOfSight = this.ocular.getSensing().hasLineOfSight(this.target);

        if (distanceSq <= this.attackRadiusSqr && hasLineOfSight) {
            this.ocular.getNavigation().stop();
        } else {
            this.ocular.getNavigation().moveTo(this.target, this.speedModifier);
        }

        this.ocular.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (--this.attackCooldown == 0) {
            if (!hasLineOfSight) {
                return;
            }


            this.ocular.triggerShootAnimation();

            this.performAttack(this.target);
            this.attackCooldown = this.attackInterval;

        } else if (this.attackCooldown < 0) {
            this.attackCooldown = this.attackInterval;
        }
    }

    private void performAttack(LivingEntity target) {

        Vec3 shootVector = new Vec3(
                target.getX() - this.ocular.getX(),
                target.getY(0.5D) - this.ocular.getY(0.5D),
                target.getZ() - this.ocular.getZ()
        );

        WeepingTearEntity projectile = new WeepingTearEntity(this.ocular.level(), this.ocular.getX(), this.ocular.getY(0.5D), this.ocular.getZ(), this.ocular);
        projectile.setOwners(this.ocular);
        projectile.shoot(shootVector.x, shootVector.y, shootVector.z, 1.2F, 1.0F); // Speed and inaccuracy
        this.ocular.level().addFreshEntity(projectile);
    }
}