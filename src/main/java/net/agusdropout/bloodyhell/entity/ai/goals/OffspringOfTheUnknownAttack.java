package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OffspringOfTheUnknownAttack extends Goal {
    private final OffspringOfTheUnknownEntity entity;
    private static final double JUMP_FORCE = 1.0;
    private static final double DAMAGE_RADIUS = 1.5;
    private int goalTime;

    public OffspringOfTheUnknownAttack(OffspringOfTheUnknownEntity entity) {
        this.entity = entity;
    }

    @Override
    public void start() {
        LivingEntity target = entity.getTarget();
        if (target == null) return;

        goalTime = 10;
        entity.setIsAttacking(true);

        if (entity.level() instanceof ServerLevel level) {
            level.playSound(null, entity.blockPosition(), ModSounds.OFFSPRING_ATTACK.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        }

        entity.swing(InteractionHand.MAIN_HAND);


        double dx = target.getX() - entity.getX();
        double dz = target.getZ() - entity.getZ();
        Vec3 direction = new Vec3(dx, 0, dz).normalize();


        float yaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        entity.setYRot(yaw);
        entity.yBodyRot = yaw;
        entity.yHeadRot = yaw;


        entity.setDeltaMovement(direction.scale(JUMP_FORCE).add(0, 0.5, 0));
    }

    @Override
    public void tick() {
        if (goalTime == 0) {
            stop();
        } else {
            damageEntitiesInPath();
        }
        goalTime--;
    }

    @Override
    public void stop() {
        entity.setIsAttacking(false);
        entity.setAttackCooldown(40);
        LivingEntity target = entity.getTarget();
        if (target == null) return;

        double dx = entity.getX() - target.getX();
        double dz = entity.getZ() - target.getZ();
        Vec3 retreatDirection = new Vec3(dx, 0, dz).normalize();


        entity.setDeltaMovement(retreatDirection.scale(1.1).add(0, 0.3, 0));


        float yaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        entity.setYRot(yaw);
        entity.yBodyRot = yaw;
        entity.yHeadRot = yaw;
    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null && entity.distanceTo(entity.getTarget()) < 5 && entity.getAttackCooldown() == 0;
    }

    private void damageEntitiesInPath() {
        AABB damageArea = entity.getBoundingBox().inflate(DAMAGE_RADIUS);
        List<LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(LivingEntity.class, damageArea);

        for (LivingEntity e : nearbyEntities) {
            if (e != entity) {
                e.hurt(entity.damageSources().mobAttack(entity), 5.0F);
            }
        }
    }
}
