package net.agusdropout.bloodyhell.entity.ai.goals;


import net.agusdropout.bloodyhell.entity.custom.VeilStalkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;




/**
 * Aerial combat goal for Veil Stalker.
 * - Smooth hovering movement
 * - Random short dashes with animation
 * - Occasional dive attacks using parabolic motion
 */
public class VeilStalkerDiveAttackGoal extends Goal {
    private final VeilStalkerEntity mob;
    private final double baseSpeed;
    private final Random random = new Random();

    private double orbitRadius;
    private double orbitAngle;
    private double targetAltitude;

    private int ticksUntilDive;
    private int dashCooldown;
    private int dashTicks;
    private int hoverTicks;
    private boolean diving = false;
    private double diveProgress = 0.0;

    private Vec3 diveStart;
    private Vec3 diveEnd;

    public VeilStalkerDiveAttackGoal(VeilStalkerEntity mob, double baseSpeed) {
        this.mob = mob;
        this.baseSpeed = baseSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null && mob.isAlive();
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        this.orbitRadius = 10.0 + random.nextDouble() * 8.0;
        this.orbitAngle = random.nextDouble() * Math.PI * 2;
        this.targetAltitude = target.getY() + 8.0 + random.nextDouble() * 4.0;
        this.ticksUntilDive = 80 + random.nextInt(60);
        this.dashCooldown = 40 + random.nextInt(60);
        this.hoverTicks = 0;
        this.dashTicks = 0;
        this.diving = false;
        this.diveProgress = 0.0;
        this.mob.setStalking(true);
        this.mob.setDashing(false);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            mob.setStalking(false);
            mob.setDashing(false);
            mob.setAttacking(false);
            return;
        }

        Level level = mob.level();

        BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mob.blockPosition());
        double groundY = groundPos.getY();
        if (targetAltitude < groundY + 6.0)
            targetAltitude = groundY + 6.0;

        // === DASH STATE ===
        if (mob.isDashing()) {
            dashTicks--;
            if (dashTicks <= 0) {
                mob.setDashing(false);
                mob.setStalking(true);
            }

            // Maintain dash motion and look direction
            Vec3 motion = mob.getDeltaMovement();
            float yaw = (float) (Math.toDegrees(Math.atan2(-motion.x, motion.z)));
            float pitch = (float) (Math.toDegrees(-Math.atan2(motion.y, motion.horizontalDistance())));
            mob.setYRot(yaw);
            mob.setXRot(pitch);
            return; // Skip rest of logic while dashing
        }

        // === DIVE BEHAVIOR ===
        if (diving) {
            handleDiveTick();

            if (diveProgress == 0.0) {
                diveStart = mob.position();
                diveEnd = target.position();
            }

            double t = diveProgress;


            Vec3 midXZ = new Vec3(
                    Mth.lerp(t, diveStart.x, diveEnd.x),
                    0,
                    Mth.lerp(t, diveStart.z, diveEnd.z)
            );


            double heightOffset = 4 * Math.pow(t - 0.5, 2) - 1;
            double arcHeight = 4.0;


            Vec3 diveTarget = new Vec3(
                    midXZ.x,
                    Mth.lerp(t, diveStart.y, diveEnd.y) + (arcHeight * heightOffset),
                    midXZ.z
            );

            Vec3 motion = diveTarget.subtract(mob.position()).normalize().scale(baseSpeed * 0.7);
            mob.setDeltaMovement(motion);
            mob.setStalking(false);
            mob.setAttacking(true);
            diveProgress += 0.045;
            if (diveProgress >= 2) {
                diving = false;
                mob.setAttacking(false);
                mob.setStalking(true);
                mob.setFlyingState(true);
                ticksUntilDive = 100 + random.nextInt(60);
                orbitAngle += Math.PI;

                return;
            }




            List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(1.5));
            for (LivingEntity e : nearby) {
                if (e != mob && e.isAlive()) {
                    mob.doHurtTarget(e);
                }
            }

            return;
        }

        double destY = targetAltitude;

        // === RANDOM DASH TRIGGER ===
        if (--dashCooldown <= 0) {
            orbitAngle += (random.nextBoolean() ? 1 : -1) * Math.toRadians(45);
            dashCooldown = 60 + random.nextInt(60);

            Vec3 dashDir = new Vec3(Math.cos(orbitAngle), 0, Math.sin(orbitAngle)).normalize();
            Vec3 dashMotion = dashDir.scale(baseSpeed * (0.8 + random.nextDouble() * 0.4));
            mob.setDeltaMovement(dashMotion.x, mob.getDeltaMovement().y, dashMotion.z);

            // Enable dash state for animation
            mob.setDashing(true);
            mob.setStalking(false);
            dashTicks = 10; // short dash (about 1 second)

            mob.playSound(SoundEvents.PHANTOM_FLAP, 0.6f, 1.0f);
            return;
        }

        // === NORMAL HOVERING MOVEMENT ===
        Vec3 targetPos = new Vec3(
                target.getX() + orbitRadius * Math.cos(orbitAngle),
                destY,
                target.getZ() + orbitRadius * Math.sin(orbitAngle)
        );

        Vec3 desiredMotion = targetPos.subtract(mob.position()).normalize().scale(baseSpeed * 0.45);
        Vec3 smoothedMotion = mob.getDeltaMovement().scale(0.8).add(desiredMotion.scale(0.2));
        mob.setDeltaMovement(smoothedMotion);

        Vec3 lookVec = target.position().subtract(mob.position());
        float yaw = (float) (Math.toDegrees(Math.atan2(-lookVec.x, lookVec.z)));
        float pitch = (float) (Math.toDegrees(-Math.atan2(lookVec.y, lookVec.horizontalDistance())));
        mob.setYRot(yaw);
        mob.setXRot(pitch);

        // === TRIGGER DIVE ===
        if (--ticksUntilDive <= 0 && mob.hasLineOfSight(target)) {
            diving = true;
            diveProgress = 0.0;
            diveStart = mob.position();
            Vec3 diveDir = target.position().subtract(mob.position()).normalize();
            diveEnd = target.position().add(diveDir.scale(6.0));

            mob.playSound(SoundEvents.PHANTOM_SWOOP, 1.0f, 0.8f + random.nextFloat() * 0.4f);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() != null && mob.getTarget().isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }



    private void handleDiveTick() {

        if (this.mob.level() instanceof ServerLevel server ) {
            server.sendParticles(ParticleTypes.END_ROD, mob.getX(), mob.getY(), mob.getZ(),
                    6, 0, 0, 0, 0.02);
        }
    }

}