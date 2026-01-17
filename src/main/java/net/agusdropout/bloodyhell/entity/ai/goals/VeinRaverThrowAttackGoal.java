package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.VeinraverEntity;
import net.agusdropout.bloodyhell.entity.projectile.VisceralProjectile;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class VeinRaverThrowAttackGoal extends Goal {

    private final VeinraverEntity veinraverEntity;

    private int goalTime;
    private BlockPos targetPos;

    public VeinRaverThrowAttackGoal(VeinraverEntity veinraverEntity) {
        this.veinraverEntity = veinraverEntity;
    }

    @Override
    public void start() {
        veinraverEntity.setThrowing(true);
        veinraverEntity.getNavigation().stop();
        this.goalTime = 30;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.veinraverEntity.getTarget();
        if (target != null) {
            double distance = this.veinraverEntity.distanceTo(target);
            return distance > 9 && !veinraverEntity.isThrowing() && !veinraverEntity.isSlashAttack() && !veinraverEntity.isSlamAttack() && veinraverEntity.getThrowCooldown() == 0;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return goalTime > 0 && veinraverEntity.getTarget() != null;
    }

    @Override
    public void stop() {
        super.stop();
        this.veinraverEntity.setThrowing(false);
        this.veinraverEntity.setThrowCooldown(120);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        goalTime--;
        if (goalTime > 0) {
            if (veinraverEntity.getTarget() != null) {
                veinraverEntity.getNavigation().stop();
            }
            if (goalTime == 5) {
                performThrowAttack();
            }

        } else {
            stop();
        }
        spawnVisceralParticles();
    }

    public void performThrowAttack() {
        if (veinraverEntity.getTarget() != null) {
            this.targetPos = veinraverEntity.getTarget().blockPosition();
        }
        if (targetPos == null) return;

        veinraverEntity.lookAt(veinraverEntity.getTarget(), 30.0F, 30.0F);

        this.veinraverEntity.level().playSound(null, this.veinraverEntity.blockPosition(),
                ModSounds.GRAWL_DROWN.get(), SoundSource.HOSTILE, 1.0F, 1.0F);


        Vec3 startPos = this.veinraverEntity.position().add(0, 3, 0);
        Vec3 targetVec = Vec3.atCenterOf(targetPos).add(0,2,0);
        Vec3 direction = targetVec.subtract(startPos);

        direction = direction.normalize();


        double speedFactor = 0.5;
        Vec3 velocity = direction.scale(speedFactor);

        VisceralProjectile projectile = new VisceralProjectile(
                this.veinraverEntity.level(),
                startPos.x, startPos.y, startPos.z,
                (float) velocity.x, (float) velocity.y, (float) velocity.z, 0.004F,
                6.0f,
                this.veinraverEntity
        );

        this.veinraverEntity.level().addFreshEntity(projectile);
    }

    private void spawnVisceralParticles() {
        if(veinraverEntity.level() instanceof ServerLevel level) {


            Vec3 lookDirection = veinraverEntity.getViewVector(1.0F);
            Vec3 spawnPos = veinraverEntity.position().add(lookDirection.scale(1.5));

            for (int i = 0; i < 3; i++) {
                level.sendParticles(
                        ModParticles.VICERAL_PARTICLE.get(),
                        spawnPos.x, spawnPos.y+3.5, spawnPos.z,
                        0,
                        Math.random() * 0.1 - 0.05,
                        Math.random() * 0.1 - 0.05,
                        Math.random() * 0.1 - 0.05,
                        0.1
                );
            }
        }
    }
}
