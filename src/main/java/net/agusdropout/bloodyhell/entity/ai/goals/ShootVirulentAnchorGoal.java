package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.projectile.BlasphemousWhirlwindEntity;
import net.agusdropout.bloodyhell.entity.projectile.StarfallProjectile;
import net.agusdropout.bloodyhell.entity.projectile.VirulentAnchorProjectileEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ShootVirulentAnchorGoal extends Goal {
    private final Mob shooter;
    private int attackCooldown;
    private final double speed;
    private final float damage;
    private final int cooldownTime;

    public ShootVirulentAnchorGoal(Mob shooter, double speed, float damage, int cooldown) {
        this.shooter = shooter;
        this.speed = speed;
        this.damage = damage;
        this.cooldownTime = cooldown;
        this.attackCooldown = 0;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = shooter.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        shooter.swing(InteractionHand.MAIN_HAND);
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = shooter.getTarget();
        if (target == null) return;

        shooter.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (attackCooldown > 0) {
            attackCooldown--;
        } else {
            shootProjectile(target);
            attackCooldown = cooldownTime;
        }
    }

    private void shootProjectile(LivingEntity target) {
        Level level = shooter.level();
        if (!level.isClientSide) {
            Vec3 pos = shooter.position().add(0, shooter.getBbHeight() * 0.8, 0);
            float yaw = shooter.getYRot();
            float pitch = shooter.getXRot();

            VirulentAnchorProjectileEntity projectile = new VirulentAnchorProjectileEntity(
                    level, pos.x, pos.y, pos.z,5, shooter,  yaw, pitch
            );

            Vec3 direction = target.position().subtract(pos).normalize().scale(speed);
            projectile.setDeltaMovement(direction);

            level.addFreshEntity(projectile);
        }
    }
}
