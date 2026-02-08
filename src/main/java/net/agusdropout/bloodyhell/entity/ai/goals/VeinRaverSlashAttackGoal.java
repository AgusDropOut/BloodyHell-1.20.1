package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.VeinraverEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodSlashEntity;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class VeinRaverSlashAttackGoal extends Goal {

    private final VeinraverEntity veinraverEntity;
    private int goalTime;

    public VeinRaverSlashAttackGoal(VeinraverEntity veinraverEntity) {
        this.veinraverEntity = veinraverEntity;
    }

    @Override
    public void start() {
        this.veinraverEntity.setSlashAttack(true);
        this.veinraverEntity.getNavigation().stop();
        this.goalTime = 50; // Total duration of the attack animation/windup
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.veinraverEntity.getTarget();
        if (target != null) {
            double distance = this.veinraverEntity.distanceTo(target);
            // Checks if target is within range and the entity is not busy
            return distance > 4.0 && distance < 12.0 &&
                    !this.veinraverEntity.isThrowing() &&
                    !this.veinraverEntity.isSlashAttack() &&
                    !this.veinraverEntity.isSlamAttack() &&
                    this.veinraverEntity.getSlashAttackCooldown() == 0;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.goalTime > 0 && this.veinraverEntity.getTarget() != null;
    }

    @Override
    public void stop() {
        super.stop();
        this.veinraverEntity.setSlashAttack(false);
        this.veinraverEntity.setSlashAttackCooldown(120); // 6 seconds cooldown
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.goalTime--;
        LivingEntity target = this.veinraverEntity.getTarget();

        if (this.goalTime > 0 && target != null) {
            this.veinraverEntity.getNavigation().stop();

            // Keep facing the target while winding up
            this.veinraverEntity.getLookControl().setLookAt(target, 30.0F, 30.0F);

            // Trigger point (Adjust based on animation keyframe)
            if (this.goalTime == 20) {
                performSlashAttack();
            }

        } else {
            stop();
        }
    }

    private void performSlashAttack() {
        LivingEntity target = this.veinraverEntity.getTarget();
        if (target == null) return;

        // Play Sound
        this.veinraverEntity.level().playSound(null, this.veinraverEntity.blockPosition(),
                ModSounds.VEINRAVER_SLASH.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

        // 1. Calculate Spawn Position (Eye height)
        Vec3 startPos = this.veinraverEntity.position().add(0, this.veinraverEntity.getEyeHeight() * 0.8, 0);

        // 2. Calculate Direction Vector
        // Aim at the center of the target (target.getY + half height)
        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 direction = targetPos.subtract(startPos).normalize();

        // 3. Convert Vector to Rotation (Yaw/Pitch)
        // atan2 returns radians, convert to degrees.
        // Subtract 90 from Yaw because Minecraft's rotation system is offset.
        float yaw = (float) (Mth.atan2(direction.z, direction.x) * (180.0D / Math.PI)) - 90.0F;

        // Calculate horizontal distance to properly calculate pitch
        double horizontalDist = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        // Pitch is negative for looking up, positive for looking down
        float pitch = (float) (-(Mth.atan2(direction.y, horizontalDist) * (180.0D / Math.PI)));

        // 4. Spawn the new Special Slash Projectile
        BloodSlashEntity specialSlash = new BloodSlashEntity(
                this.veinraverEntity.level(),
                startPos.x, startPos.y, startPos.z,
                12.0f, // Damage
                this.veinraverEntity,
                yaw,
                pitch
        );

        this.veinraverEntity.level().addFreshEntity(specialSlash);
    }
}