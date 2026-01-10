package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.SelioraEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class TeleportFarGoal extends Goal {
    private final SelioraEntity entity;
    private final Random random = new Random();

    public TeleportFarGoal(SelioraEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.canTeleportFar();
    }

    @Override
    public void start() {
        LivingEntity target = entity.getTarget();
        if (target == null) {
            stop();
            return;
        }

        Vec3 targetPos = target.position();
        Vec3 entityPos = entity.position();
        Vec3 awayDir = entityPos.subtract(targetPos).normalize();
        double distance = 10 + random.nextInt(5);

        // Intentamos 10 veces encontrar un punto válido
        for (int i = 0; i < 10; i++) {
            Vec3 farPos = entityPos.add(awayDir.scale(distance));

            // Variación aleatoria
            farPos = farPos.add(
                    (random.nextDouble() - 0.5) * 6.0,
                    0,
                    (random.nextDouble() - 0.5) * 6.0
            );

            BlockPos teleportPosCandidate = new BlockPos((int)farPos.x, (int)entity.getY(), (int)farPos.z);
            BlockPos safePos = findSafeTeleportPos(teleportPosCandidate);

            if (safePos != null) {
                // CHECK RAYCAST: Verificar paredes entre la posición actual y la futura
                if (canSeePosition(entity.position(), Vec3.atCenterOf(safePos))) {
                    executeTeleport(safePos);
                    break; // Éxito
                }
            }
        }

        stop();
    }

    // --- Nuevo Método para Raycasting ---
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

    private void executeTeleport(BlockPos teleportPos) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.BLASPHEMOUS_MAGIC_RING.get(),
                    teleportPos.getX(), teleportPos.getY() + 0.1, teleportPos.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);

            entity.teleportTo(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
            spawnBlasphemousParticles();

            entity.level().playSound(null, entity.blockPosition(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

    private BlockPos findSafeTeleportPos(BlockPos pos) {
        ServerLevel level = (ServerLevel) entity.level();

        // Pequeño ajuste para no iterar demasiado si el punto base es muy malo
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

        // Bajar hasta encontrar suelo
        while (mutable.getY() > level.getMinBuildHeight() && !level.getBlockState(mutable).blocksMotion()) {
            mutable.move(Direction.DOWN);
        }

        BlockState ground = level.getBlockState(mutable);
        if (!ground.blocksMotion() || ground.getFluidState().is(FluidTags.WATER)) return null;

        boolean safe = true;
        // Check espacio arriba
        for (int i = 1; i <= 3 && safe; i++) { // Reduje a 3 bloques de altura requerida para ser menos estricto pero seguro
            if (!level.isEmptyBlock(mutable.above(i))) safe = false;
        }

        // Check alrededores inmediatos (para no quedar bugeado en una esquina)
        for (int dx = -1; dx <= 1 && safe; dx++) {
            for (int dz = -1; dz <= 1 && safe; dz++) {
                BlockPos checkPos = mutable.offset(dx, 1, dz); // Verificar bloque donde estarán los pies
                if (level.getBlockState(checkPos).blocksMotion()) safe = false;
            }
        }

        if (safe) {
            return mutable.above();
        }
        return null;
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
                        entity.getX() + offsetX,
                        entity.getY() + offsetY,
                        entity.getZ() + offsetZ,
                        1,
                        0.0, 0.05 + random.nextDouble() * 0.05, 0.0,
                        0.0
                );
            }
        }
    }

    @Override
    public void stop() {
        entity.setTeleportCooldown(entity.getTeleportMaxCooldown());
    }
}