package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.OmenGazerEntity;
import net.agusdropout.bloodyhell.entity.projectile.SmallCrimsonDagger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class OmenGazerThrowGoal extends Goal {
    private final OmenGazerEntity omenGazer;
    private static final double THROW_SPEED = 1.2;
    private int throwCooldown = 20;

    public OmenGazerThrowGoal(OmenGazerEntity omenGazer) {
        this.omenGazer = omenGazer;
    }

    @Override
    public void start() {
        LivingEntity target = this.omenGazer.getTarget();
        if (target != null && !this.omenGazer.level().isClientSide) {
            omenGazer.setThrowingCooldown(120);
            omenGazer.setThrowing(true);
            omenGazer.swing(InteractionHand.MAIN_HAND);

            // Calculate spawn position
            double spawnX = omenGazer.getX();
            double spawnY = omenGazer.getY() + 1.5;
            double spawnZ = omenGazer.getZ();

            // Calculate aim vector: (Target Center) - (Spawn Position)
            Vec3 direction = new Vec3(
                    target.getX() - spawnX,
                    target.getBoundingBox().getCenter().y - spawnY, // Aim at the body center
                    target.getZ() - spawnZ
            ).normalize().scale(THROW_SPEED);

            // Use the new Constructor (Level, x, y, z, Owner)
            SmallCrimsonDagger dagger = new SmallCrimsonDagger(
                    this.omenGazer.level(),
                    spawnX, spawnY, spawnZ,
                    omenGazer
            );

            // Set Velocity manually after instantiation
            dagger.setDeltaMovement(direction);

            // Add entity to world
            this.omenGazer.level().addFreshEntity(dagger);
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.omenGazer.getTarget();

        if (target != null) {
            double dist = omenGazer.distanceTo(target);
            return !omenGazer.isCharging()
                    && omenGazer.getThrowingCooldown() == 0
                    && dist > 6
                    && dist < 12
                    && !omenGazer.isAboutToExplode();
        }
        return false;
    }

    @Override
    public void tick() {
        this.throwCooldown--;
        if (this.omenGazer.getTarget() != null) {
            this.omenGazer.getLookControl().setLookAt(omenGazer.getTarget(), 30.0F, 30.0F);
        }
        if (this.throwCooldown <= 0) {
            stop();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        this.omenGazer.setThrowing(false);
    }
}