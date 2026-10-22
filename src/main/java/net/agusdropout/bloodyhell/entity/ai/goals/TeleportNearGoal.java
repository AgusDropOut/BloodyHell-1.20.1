package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.SelioraEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class TeleportNearGoal extends Goal {
    private final SelioraEntity entity;
    private final Random random = new Random();

    public TeleportNearGoal(SelioraEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.canTeleportNear();
    }

    @Override
    public void start() {
        LivingEntity target = entity.getTarget();
        if (target == null) {
            stop();
            return;
        }

        boolean success = false;

        for (int i = 0; i < 10; i++) {
            Vec3 targetPos = target.position();

            double xOffset = (random.nextDouble() * 8.0) - 4.0;
            double zOffset = (random.nextDouble() * 8.0) - 4.0;

            BlockPos targetBlockPos = new BlockPos((int)(targetPos.x + xOffset), (int)targetPos.y, (int)(targetPos.z + zOffset));

            BlockPos safePos = findGround(targetBlockPos);

            if (safePos != null) {

                if (canSeePosition(entity.position(), Vec3.atCenterOf(safePos))) {
                    executeTeleport(safePos);
                    success = true;
                    break;
                }
            }
        }

        stop();
    }

    private boolean canSeePosition(Vec3 start, Vec3 end) {

        HitResult result = entity.level().clip(new ClipContext(
                start.add(0, entity.getEyeHeight(), 0),
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        ));

        return result.getType() == HitResult.Type.MISS;
    }

    private BlockPos findGround(BlockPos pos) {

        BlockPos.MutableBlockPos mutable = pos.mutable();

        int safetyCounter = 0;
        while (entity.level().getBlockState(mutable).blocksMotion() && safetyCounter < 5) {
            mutable.move(Direction.UP);
            safetyCounter++;
        }

        safetyCounter = 0;
        while (!entity.level().getBlockState(mutable.below()).blocksMotion() && mutable.getY() > entity.level().getMinBuildHeight() && safetyCounter < 5) {
            mutable.move(Direction.DOWN);
            safetyCounter++;
        }

        if (!entity.level().getBlockState(mutable).blocksMotion() &&
                !entity.level().getBlockState(mutable.above()).blocksMotion()) {
            return mutable.immutable();
        }

        return null;
    }

    private void executeTeleport(BlockPos pos) {
        if (entity.level() instanceof ServerLevel serverLevel) {

            serverLevel.sendParticles(
                    ModParticles.BLASPHEMOUS_MAGIC_RING.get(),
                    pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                    1, 0.0, 0.0, 0.0, 0.0
            );
            serverLevel.sendParticles(
                    ModParticles.CYLINDER_PARTICLE.get(),
                    pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                    1, 0.0, 0.0, 0.0, 0.0
            );

            entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            spawnBlasphemousParticles();
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

    public void spawnBlasphemousParticles() {
        if (entity.level() instanceof ServerLevel serverLevel) {
            double radiusXZ = 2.0;
            double height = 1.5;
            for (int i = 0; i < 20; i++) {
                double offsetX = (random.nextDouble() - 0.5) * radiusXZ;
                double offsetY = random.nextDouble() * height;
                double offsetZ = (random.nextDouble() - 0.5) * radiusXZ;

                serverLevel.sendParticles(ModParticles.MAGIC_LINE_PARTICLE.get(),
                        entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ,
                        1, 0.0, 0.05 + random.nextDouble() * 0.05, 0.0, 0.0
                );
            }
        }
    }

    @Override
    public void stop() {
        entity.setTeleportCooldown(entity.getTeleportMaxCooldown());
    }
}