package net.agusdropout.bloodyhell.entity.minions.ai;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.minions.custom.BurdenOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.projectile.ViscousProjectileEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BurdenOfTheUnknownAttackGoal extends Goal {
    private final BurdenOfTheUnknownEntity entity;
    private final double speedModifier;
    private final int attackIntervalMin;
    private final float attackRadius;
    private int attackTime = -1;

    public BurdenOfTheUnknownAttackGoal(BurdenOfTheUnknownEntity entity, double speedModifier, int attackIntervalMin, float attackRadius) {
        this.entity = entity;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadius = attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return;

        double distance = this.entity.distanceToSqr(target);
        boolean canSee = this.entity.getSensing().hasLineOfSight(target);

        if (distance < (this.attackRadius * this.attackRadius) && canSee) {
            this.entity.getNavigation().stop();
        } else {
            this.entity.getNavigation().moveTo(target, this.speedModifier);
        }

        this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.updateCannonPitch(target);

        if (--this.attackTime <= 0 && canSee) {
            this.entity.triggerAnim("controller", "shoot");
            spawnProjectile(target);
            this.attackTime = this.attackIntervalMin;
        }
    }

    private void spawnProjectile( LivingEntity target) {
        ViscousProjectileEntity projectile = new ViscousProjectileEntity(ModEntityTypes.VISCOUS_PROJECTILE.get(), this.entity.level());

        double startX = this.entity.getX();
        double startY = this.entity.getY() + 1.5D;
        double startZ = this.entity.getZ();
        projectile.setPos(startX, startY, startZ);

        int minionColor = entity.getStripeColor();
        projectile.setProjectileColors(minionColor, minionColor);

        projectile.setDamage(8.0F);
        projectile.setDeltaMovement(calculateMortarVelocity(
                new Vec3(startX, startY, startZ),
                new Vec3(target.getX(), target.getY(), target.getZ()),
                1.5F,
                0.05F
        ));

        entity.level().addFreshEntity(projectile);
    }

    public static Vec3 calculateMortarVelocity(Vec3 startPos, Vec3 targetPos, float velocity, float gravity) {
        double dX = targetPos.x - startPos.x;
        double dZ = targetPos.z - startPos.z;
        double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);
        double dY = targetPos.y - startPos.y;

        double v2 = velocity * velocity;
        double v4 = v2 * v2;
        double g = gravity;
        double x = horizontalDistance;
        double y = dY;

        double innerRoot = v4 - g * (g * x * x + 2 * y * v2);

        if (innerRoot < 0) {
            return Vec3.ZERO;
        }

        double root = Math.sqrt(innerRoot);

        double angleCalc = Math.atan2(v2 + root, g * x);

        double yVel = velocity * Math.sin(angleCalc);
        double horizontalVelocity = velocity * Math.cos(angleCalc);

        double xVel = horizontalVelocity * (dX / horizontalDistance);
        double zVel = horizontalVelocity * (dZ / horizontalDistance);

        return new Vec3(xVel, yVel, zVel);
    }

    private void updateCannonPitch(LivingEntity target) {
        Vec3 startPos = new Vec3(this.entity.getX(), this.entity.getY() + 1.5D, this.entity.getZ());
        Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());

        float velocity = 1.5F;
        float gravity = 0.05F;

        Vec3 velocityVec = calculateMortarVelocity(startPos, targetPos, velocity, gravity);

        if (!velocityVec.equals(Vec3.ZERO)) {
            double horizontalVelocity = Math.sqrt(velocityVec.x * velocityVec.x + velocityVec.z * velocityVec.z);

            float pitch = (float) -(Math.atan2(velocityVec.y, horizontalVelocity) * (180F / Math.PI));
            pitch = Mth.clamp(pitch, -90.0F, 15.0F);

            this.entity.setCannonPitch(pitch);
        }
    }
}