package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.RitekeeperEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.ParticleHelper; // Import your helper
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RitekeeperEvasionGoal extends Goal {
    private final RitekeeperEntity mob;
    private int cooldown = 0;
    private int dashDuration = 0;

    // Config
    private final double EVADE_DISTANCE = 6.0D;
    private final float DASH_STRENGTH = 1.5f;
    private final int MAX_COOLDOWN = 100; // 5 Seconds cooldown
    private final int DASH_TIME = 20;     // 1 Second duration of invisibility

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
            // 1. Set Invisible/Invulnerable State
            this.mob.setEvading(true);

            // 2. Apply Velocity
            dashAway(target);

            // 3. Initial FX (Disappear Cloud)
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

        // TRAIL EFFECT (Updated to use Helper)
        if (this.dashDuration % 2 == 0) {
            // Helper handles sending this to all clients
            ParticleHelper.spawn(this.mob.level(), ParticleTypes.LARGE_SMOKE,
                    this.mob.getRandomX(0.5),
                    this.mob.getRandomY(),
                    this.mob.getRandomZ(0.5),
                    0, 0, 0);
        }
    }

    @Override
    public void stop() {
        // 1. Restore State
        this.mob.setEvading(false);
        this.cooldown = MAX_COOLDOWN;

        // 2. Stop movement
        this.mob.setDeltaMovement(Vec3.ZERO);

        // 3. Reappear FX
        this.mob.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0f, 0.8f);
        spawnParticleCloud();
    }

    private void dashAway(LivingEntity target) {
        Vec3 bossPos = this.mob.position();
        Vec3 targetPos = target.position();
        Vec3 dir = bossPos.subtract(targetPos).normalize();
        dir = new Vec3(dir.x, 0.2, dir.z).normalize();

        this.mob.lookAt(target, 180.0f, 30.0f);
        this.mob.setDeltaMovement(dir.scale(DASH_STRENGTH));
    }

    private void spawnParticleCloud() {
        RandomSource random = this.mob.getRandom();

        // Spawns a dense cluster of particles
        for(int i = 0; i < 30; i++) {

            // Mix of Blood Sigil and Large Smoke
            if (random.nextBoolean()) {
                // FIXED: Using ParticleHelper
                ParticleHelper.spawn(this.mob.level(), ModParticles.BLOOD_SIGIL_PARTICLE.get(),
                        this.mob.getRandomX(1.5),
                        this.mob.getRandomY(),
                        this.mob.getRandomZ(1.5),
                        (random.nextDouble()-0.5)*0.2,
                        0.05,
                        (random.nextDouble()-0.5)*0.2);
            } else {
                // FIXED: Using ParticleHelper
                ParticleHelper.spawn(this.mob.level(), ParticleTypes.LARGE_SMOKE,
                        this.mob.getRandomX(1.0),
                        this.mob.getRandomY(),
                        this.mob.getRandomZ(1.0),
                        0, 0.1, 0);
            }
        }
    }
}