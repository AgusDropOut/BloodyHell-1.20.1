package net.agusdropout.bloodyhell.entity.minions.ai;

import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LungeAttackGoal extends Goal {
    private final BastionOfTheUnknownEntity entity;
    private LivingEntity target;
    private final int lungeDuration;
    private final int cooldownDuration;
    private int currentCooldown;
    private int lungeTicks;
    private int postHitTicks;
    private boolean hitTarget;
    private static final float LUNGE_DAMAGE = 10.0F;

    public LungeAttackGoal(BastionOfTheUnknownEntity entity, int lungeDuration, int cooldownDuration) {
        this.entity = entity;
        this.lungeDuration = lungeDuration;
        this.cooldownDuration = cooldownDuration;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.currentCooldown > 0) {
            this.currentCooldown--;
            return false;
        }

        this.target = this.entity.getTarget();
        if (this.target == null || !this.target.isAlive()) {
            return false;
        }

        if (Math.abs(this.entity.getY() - this.target.getY()) > 1.5D) {
            return false;
        }

        double distance = this.entity.distanceToSqr(this.target);
        return distance >= 5.0D && distance <= 100.0D;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.target == null || !this.target.isAlive()) return false;
        if (this.postHitTicks > 0) return true;
        return !this.hitTarget && this.lungeTicks < this.lungeDuration;
    }

    @Override
    public void start() {
        this.lungeTicks = 0;
        this.postHitTicks = 0;
        this.hitTarget = false;
        this.entity.setLunging(true);
        this.entity.getNavigation().stop();
        this.entity.triggerAnim("action_controller", "lunge");

        Vec3 targetPos = this.target.position();
        Vec3 entityPos = this.entity.position();
        Vec3 direction = targetPos.subtract(entityPos).normalize();

        double dx = this.target.getX() - this.entity.getX();
        double dz = this.target.getZ() - this.entity.getZ();
        float yaw = (float)(Mth.atan2(dz, dx) * (double)(180F / (float)Math.PI)) - 90.0F;

        this.entity.setYRot(yaw);
        this.entity.yRotO = yaw;
        this.entity.setYBodyRot(yaw);
        this.entity.setYHeadRot(yaw);

        double lungeStrength = 1.9D;
        this.entity.setDeltaMovement(direction.x * lungeStrength, 0.0D, direction.z * lungeStrength);

        this.entity.level().playSound(null, this.entity.blockPosition(), ModSounds.SELIORA_CHARGE_ATTACK_SOUND.get(),
                SoundSource.HOSTILE, 1.5F, 0.9F + this.entity.level().random.nextFloat() * 0.2F);
        this.entity.level().playSound(null, this.entity.blockPosition(), SoundEvents.TRIDENT_RIPTIDE_3,
                SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (this.postHitTicks > 0) {
            this.postHitTicks--;
            return;
        }

        this.lungeTicks++;

        if (!this.hitTarget && this.entity.getBoundingBox().inflate(0.5D).intersects(this.target.getBoundingBox())) {
            this.hitTarget = true;
            this.postHitTicks = 15;
            this.entity.setDeltaMovement(Vec3.ZERO);

            this.target.hurt(this.entity.damageSources().mobAttack(this.entity), LUNGE_DAMAGE);

            double dx = this.target.getX() - this.entity.getX();
            double dz = this.target.getZ() - this.entity.getZ();
            this.target.knockback(2.5D, -dx, -dz);

            this.entity.level().playSound(null, this.entity.blockPosition(), SoundEvents.ANVIL_LAND,
                    SoundSource.HOSTILE, 1.2F, 0.8F);
            this.entity.level().playSound(null, this.entity.blockPosition(), SoundEvents.PLAYER_ATTACK_KNOCKBACK,
                    SoundSource.HOSTILE, 1.0F, 1.0F);
        } else if (this.lungeTicks >= this.lungeDuration && !this.hitTarget) {
            this.hitTarget = true;
            this.postHitTicks = 10;
            this.entity.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    public void stop() {
        this.currentCooldown = this.cooldownDuration;
        this.entity.setLunging(false);
        this.entity.setDeltaMovement(0, this.entity.getDeltaMovement().y, 0);
        this.entity.triggerAnim("action_controller", "reposition");
    }
}