package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.BlasphemousMalformationEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class KeepDistanceGoal extends Goal {
    private final BlasphemousMalformationEntity entity;
    private final double moveSpeed;
    private final double minDistance;
    private final double maxDistance;

    public KeepDistanceGoal(BlasphemousMalformationEntity entity, double moveSpeed, double minDistance, double maxDistance) {
        this.entity = entity;
        this.moveSpeed = moveSpeed;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target == null) return;

        double distance = entity.distanceTo(target);

        if (distance < minDistance) {

            Vec3 direction = entity.position().subtract(target.position()).normalize().scale(1.5);
            Vec3 newPos = entity.position().add(direction);
            entity.getNavigation().moveTo(newPos.x, newPos.y, newPos.z, moveSpeed);
        } else if (distance > maxDistance) {

            entity.getNavigation().moveTo(target, moveSpeed);
        } else {

            entity.getNavigation().stop();
        }
    }
}