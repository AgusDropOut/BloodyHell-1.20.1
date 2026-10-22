package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.VeilStalkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class VeilStalkerHangGoal extends Goal {
    private final VeilStalkerEntity mob;
    private final double speed;
    private BlockPos hangPos;
    private int hangingTime;
    private boolean reachedWall;

    private final int minHangTicks = 20 * 6;
    private final int maxExtraHangTicks = 20 * 8;

    private int searchTicks;
    private final int maxSearchTicks = 200;

    public VeilStalkerHangGoal(VeilStalkerEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {

        if (mob.getTarget() != null) return false;
        if (!mob.isAlive()) return false;
        if (mob.isHanging()) return false;
        if (mob.isAttacking()) return false;
        if (mob.getHangCooldown() > 0) return false;

        hangPos = findNearbyWall();
        if( hangPos != null) {
            return true;
        } else {
            mob.setHangCooldown(mob.getMaxHangCooldown());
        }
        return false;
    }

    @Override
    public void start() {
        if (hangPos != null) {
            reachedWall = false;
            hangingTime = 0;
            searchTicks = 0;
        }
    }

    @Override
    public void tick() {

        if (mob.getTarget() != null) {
            stop();
            return;
        }

        if (!reachedWall && hangPos != null) {
            searchTicks++;

            double dx = (hangPos.getX() + 0.5) - mob.getX();
            double dy = ((hangPos.getY() + 0.5) - mob.getY()) * 0.2;
            double dz = (hangPos.getZ() + 0.5) - mob.getZ();

            Vec3 vec = new Vec3(dx, dy, dz).normalize().scale(Math.min(speed, 0.5));
            mob.setDeltaMovement(vec);
            mob.setYRot((float)(Math.toDegrees(Math.atan2(-vec.x, vec.z))));

            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq < 3 * 3 || searchTicks > maxSearchTicks) {
                reachedWall = true;
                mob.getNavigation().stop();
                mob.setDeltaMovement(0, 0, 0);
                mob.startHangingServerSide();
                hangingTime = 0;
            } else {

                mob.getLookControl().setLookAt(
                        hangPos.getX() + 0.5,
                        hangPos.getY() + 0.5,
                        hangPos.getZ() + 0.5,
                        30.0F,
                        30.0F
                );
            }
        }

        else if (reachedWall) {
            hangingTime++;
            if (hangingTime > minHangTicks) {
                stop();
            }
        } else {
            stop();
        }
    }

    @Override
    public boolean canContinueToUse() {

        return  mob.getTarget() == null && mob.isAlive() && hangingTime < minHangTicks;
    }

    @Override
    public void stop() {

        mob.stopHanging();
        hangPos = null;
        reachedWall = false;
        mob.setHangCooldown(mob.getMaxHangCooldown());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private BlockPos findNearbyWall() {
        BlockPos origin = mob.blockPosition();
        Level level = mob.level();

        int horizontalRange = 50;
        int verticalRangeDown = 5;

        ServerLevel serverLevel = null;
        if (level instanceof ServerLevel) {
            serverLevel = (ServerLevel) level;
        }

        int y = origin.getY();
        for (int dx = -horizontalRange; dx <= horizontalRange; dx++) {
            for (int dz = -horizontalRange; dz <= horizontalRange; dz++) {
                BlockPos pos = origin.offset(dx, 0, dz);

                BlockState state = level.getBlockState(pos);
                if (state.isSolidRender(level, pos)) {
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        BlockPos front = pos.relative(dir);
                        if (level.isEmptyBlock(front) && level.isEmptyBlock(front.above())) {
                            if (isPathClear(origin, front)) {
                                return front;
                            }
                        }
                    }
                }
            }
        }

        for (int dy = 1; dy <= verticalRangeDown; dy++) {
            int currentY = origin.getY() - dy;
            for (int dx = -horizontalRange; dx <= horizontalRange; dx++) {
                for (int dz = -horizontalRange; dz <= horizontalRange; dz++) {
                    BlockPos pos = new BlockPos(origin.getX() + dx, currentY, origin.getZ() + dz);

                    BlockState state = level.getBlockState(pos);
                    if (state.isSolidRender(level, pos)) {
                        for (Direction dir : Direction.Plane.HORIZONTAL) {
                            BlockPos front = pos.relative(dir);
                            if (level.isEmptyBlock(front) && level.isEmptyBlock(front.above())) {
                                if (isPathClear(origin, front)) {
                                    return front;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean isPathClear(BlockPos start, BlockPos end) {
        Level level = mob.level();
        int steps = 5;
        double dx = (end.getX() + 0.5 - start.getX()) / steps;
        double dy = (end.getY() + 0.5 - start.getY()) / steps;
        double dz = (end.getZ() + 0.5 - start.getZ()) / steps;
        for (int i = 1; i <= steps; i++) {
            double x = start.getX() + dx * i;
            double y = start.getY() + dy * i;
            double z = start.getZ() + dz * i;
            BlockPos checkPos = new BlockPos((int)x, (int)y, (int)z);
            if (!level.isEmptyBlock(checkPos)) return false;
        }
        return true;
    }
}