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

        // Intentamos 10 veces encontrar una posición válida
        for (int i = 0; i < 10; i++) {
            Vec3 targetPos = target.position();
            // Offset aleatorio pequeño (cerca del jugador)
            double xOffset = (random.nextDouble() * 8.0) - 4.0; // -4 a 4
            double zOffset = (random.nextDouble() * 8.0) - 4.0;

            BlockPos targetBlockPos = new BlockPos((int)(targetPos.x + xOffset), (int)targetPos.y, (int)(targetPos.z + zOffset));

            // Buscar suelo firme (para no aparecer en el aire o dentro de un bloque)
            BlockPos safePos = findGround(targetBlockPos);

            if (safePos != null) {
                // RAYCAST CHECK: ¿Podemos ver el destino desde donde estamos?
                // Esto evita atravesar paredes para salir de la arena.
                if (canSeePosition(entity.position(), Vec3.atCenterOf(safePos))) {
                    executeTeleport(safePos);
                    success = true;
                    break; // Éxito, salimos del bucle
                }
            }
        }

        // Siempre activamos el cooldown, incluso si falló, para no spamear intentos
        stop();
    }

    private boolean canSeePosition(Vec3 start, Vec3 end) {
        // Lanza un rayo desde el origen al destino buscando bloques colisionables
        HitResult result = entity.level().clip(new ClipContext(
                start.add(0, entity.getEyeHeight(), 0), // Desde los ojos
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        ));
        // Si el resultado es MISS, significa que no chocó con nada -> Hay línea de visión
        return result.getType() == HitResult.Type.MISS;
    }

    private BlockPos findGround(BlockPos pos) {
        // Ajuste vertical simple: buscar suelo hacia abajo o espacio hacia arriba
        BlockPos.MutableBlockPos mutable = pos.mutable();

        // Si empezamos dentro de un bloque sólido, subimos hasta encontrar aire
        int safetyCounter = 0;
        while (entity.level().getBlockState(mutable).blocksMotion() && safetyCounter < 5) {
            mutable.move(Direction.UP);
            safetyCounter++;
        }

        // Si estamos en aire, bajamos hasta encontrar suelo
        safetyCounter = 0;
        while (!entity.level().getBlockState(mutable.below()).blocksMotion() && mutable.getY() > entity.level().getMinBuildHeight() && safetyCounter < 5) {
            mutable.move(Direction.DOWN);
            safetyCounter++;
        }

        // Verificamos espacio para la entidad (2 bloques de altura)
        if (!entity.level().getBlockState(mutable).blocksMotion() &&
                !entity.level().getBlockState(mutable.above()).blocksMotion()) {
            return mutable.immutable();
        }

        return null;
    }

    private void executeTeleport(BlockPos pos) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            // Partículas PRE teleport (origen)
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

            // Teletransporte
            entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            // Partículas POST teleport
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