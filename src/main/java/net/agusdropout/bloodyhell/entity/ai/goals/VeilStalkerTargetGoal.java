package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.VeilStalkerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;

public class VeilStalkerTargetGoal<T extends LivingEntity> extends Goal {

    private final VeilStalkerEntity stalker;
    private final Class<T> targetClass;
    private final TargetingConditions targeting = TargetingConditions.forCombat().ignoreLineOfSight();

    public VeilStalkerTargetGoal(VeilStalkerEntity stalker, Class<T> targetClass) {
        this.stalker = stalker;
        this.targetClass = targetClass;
    }

    @Override
    public boolean canUse() {
        LivingEntity currentTarget = stalker.getTarget();


        if (currentTarget != null && !currentTarget.isAlive()) {
            stalker.setTarget(null);
            currentTarget = null;
        }


        if (currentTarget != null) return false;

        double range = stalker.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB searchBox = stalker.getBoundingBox().inflate(range);

        List<T> potentialTargets = stalker.level().getEntitiesOfClass(targetClass, searchBox,
                entity -> targeting.test(stalker, entity)
        );

        if (potentialTargets.isEmpty()) return false;


        potentialTargets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(stalker)));
        stalker.setTarget(potentialTargets.get(0));
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = stalker.getTarget();


        if (target != null && !target.isAlive()) {
            stalker.setTarget(null);
            return false;
        }

        double range = stalker.getAttributeValue(Attributes.FOLLOW_RANGE);
        return target != null && stalker.distanceToSqr(target) <= range * range;
    }
}