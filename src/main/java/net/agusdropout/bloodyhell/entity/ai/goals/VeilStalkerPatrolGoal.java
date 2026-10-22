package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.VeilStalkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class VeilStalkerPatrolGoal extends Goal {
    private final VeilStalkerEntity mob;
    private final double speed;
    private final Random random = new Random();

    private Vec3 centerPos;
    private double angle;
    private double radius;
    private double targetAltitude;

    public VeilStalkerPatrolGoal(VeilStalkerEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return  mob.isAlive()  && mob.getHangCooldown() > 0;
    }

    @Override
    public void start() {

        this.centerPos = mob.position();
        this.radius = 20.0 + random.nextDouble() * 30.0;
        this.angle = random.nextDouble() * 2 * Math.PI;

        Level level = mob.level();
        BlockPos groundPos = level.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlockPos.containing(centerPos)
        );
        double groundY = groundPos.getY();
        this.targetAltitude = groundY + 20.0 + random.nextDouble() * 10.0;
    }

    @Override
    public void tick() {

        Level level = mob.level();
        BlockPos headPos = mob.blockPosition().above();
        BlockState above = level.getBlockState(headPos);
        BlockState aboveAbove = level.getBlockState(headPos.above());

        if (!aboveAbove.isAir() || !above.isAir()) {

            this.targetAltitude = mob.getY() - 8.0;
        } else {
            double groundY = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mob.blockPosition()).getY();
            double minAllowed = groundY + 20.0;
            if (targetAltitude < minAllowed) {
                targetAltitude += 0.1;
            }
        }

        this.angle += 0.05;

        double destX = centerPos.x + radius * Math.cos(angle);
        double destZ = centerPos.z + radius * Math.sin(angle);
        double destY = targetAltitude + Math.sin(angle * 2) * 2.0;

        Vec3 direction = new Vec3(destX - mob.getX(), destY - mob.getY(), destZ - mob.getZ()).normalize();

        mob.setDeltaMovement(direction.scale(speed));
        mob.setYRot((float)(Math.toDegrees(Math.atan2(-direction.x, direction.z))));
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}