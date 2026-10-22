package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RitekeeperEvasionGoal extends Goal {
    private final RitekeeperEntity mob;
    private int cooldown = 0;
    private int dashDuration = 0;

    private final double EVADE_DISTANCE = 6.0D;
    private final float DASH_STRENGTH = 1.5f;
    private final int MAX_COOLDOWN = 100;
    private final int DASH_TIME = 20;

    public RitekeeperEvasionGoal(RitekeeperEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();

        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        return target != null
                && !this.mob.isCasting()
                && !this.mob.isEvading()
                && this.mob.distanceToSqr(target) < (EVADE_DISTANCE * EVADE_DISTANCE);
    }

    @Override
    public void start() {
        this.dashDuration = 0;
        LivingEntity target = this.mob.getTarget();
        if (target != null) {

            this.mob.setEvading(true);

            dashAway(target);

            this.mob.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 0.5f);
            spawnParticleCloud();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.dashDuration < DASH_TIME;
    }

    @Override
    public void tick() {
        this.dashDuration++;

        if (this.dashDuration % 2 == 0) {

            ParticleHelper.spawn(this.mob.level(), ParticleTypes.LARGE_SMOKE,
                    this.mob.getRandomX(0.5),
                    this.mob.getRandomY(),
                    this.mob.getRandomZ(0.5),
                    0, 0, 0);
        }
    }

    @Override
    public void stop() {

        this.mob.setEvading(false);
        this.cooldown = MAX_COOLDOWN;

        this.mob.setDeltaMovement(Vec3.ZERO);

        this.mob.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0f, 0.8f);
        spawnParticleCloud();
    }

    private void dashAway(LivingEntity target) {
        Vec3 mobPos = this.mob.position();
        Vec3 targetPos = target.position();

        Vec3 toPlayer = targetPos.subtract(mobPos).normalize();

        Vec3[] directions = {
                new Vec3(1, 0, 0),
                new Vec3(-1, 0, 0),
                new Vec3(0, 0, 1),
                new Vec3(0, 0, -1),
                new Vec3(0.707, 0, 0.707),
                new Vec3(0.707, 0, -0.707),
                new Vec3(-0.707, 0, 0.707),
                new Vec3(-0.707, 0, -0.707)
        };

        Vec3 bestDir = Vec3.ZERO;
        double maxDistance = 0.0;

        for (Vec3 dir : directions) {

            if (dir.dot(toPlayer) > 0.5) continue;

            Vec3 start = mobPos.add(0, this.mob.getEyeHeight(), 0);
            Vec3 end = start.add(dir.scale(EVADE_DISTANCE));

            BlockHitResult result = this.mob.level().clip(new ClipContext(
                    start,
                    end,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this.mob
            ));

            double dist = result.getLocation().distanceTo(start);

            if (dist > maxDistance) {
                maxDistance = dist;
                bestDir = dir;
            }
        }

        if (maxDistance > 1.0) {

            Vec3 finalVelocity = new Vec3(bestDir.x, 0.35, bestDir.z).normalize().scale(DASH_STRENGTH);

            this.mob.lookAt(target, 180.0f, 30.0f);
            this.mob.setDeltaMovement(finalVelocity);
        } else {

            this.mob.setDeltaMovement(0, 0.7, 0);
        }
    }

    private void spawnParticleCloud() {
        RandomSource random = this.mob.getRandom();

        for(int i = 0; i < 30; i++) {

            if (random.nextBoolean()) {

                ParticleHelper.spawn(this.mob.level(), ModParticles.BLOOD_SIGIL_PARTICLE.get(),
                        this.mob.getRandomX(1.5),
                        this.mob.getRandomY(),
                        this.mob.getRandomZ(1.5),
                        (random.nextDouble()-0.5)*0.2,
                        0.05,
                        (random.nextDouble()-0.5)*0.2);
            } else {

                ParticleHelper.spawn(this.mob.level(), ParticleTypes.LARGE_SMOKE,
                        this.mob.getRandomX(1.0),
                        this.mob.getRandomY(),
                        this.mob.getRandomZ(1.0),
                        0, 0.1, 0);
            }
        }
    }
}