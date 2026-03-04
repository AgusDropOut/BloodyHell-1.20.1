package net.agusdropout.bloodyhell.entity.minions.ai;

import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class FollowSummonerGoal extends Goal {
    private final AbstractMinionEntity minion;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;
    private net.minecraft.world.entity.LivingEntity owner;

    public FollowSummonerGoal(AbstractMinionEntity minion, double speedModifier, float startDistance, float stopDistance) {
        this.minion = minion;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(java.util.EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity targetOwner = this.minion.getOwner();
        if (targetOwner == null || targetOwner.isSpectator() || this.minion.getTarget() != null) {
            return false;
        }
        if (this.minion.distanceToSqr(targetOwner) < (double)(this.startDistance * this.startDistance)) {
            return false;
        }
        this.owner = targetOwner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.minion.getNavigation().isDone() && this.minion.distanceToSqr(this.owner) > (double)(this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
        this.minion.getNavigation().moveTo(this.owner, this.speedModifier);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.minion.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.minion.getLookControl().setLookAt(this.owner, 10.0F, (float)this.minion.getMaxHeadXRot());
        this.minion.getNavigation().moveTo(this.owner, this.speedModifier);
    }
}
