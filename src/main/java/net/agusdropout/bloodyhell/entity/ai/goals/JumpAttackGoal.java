package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.SelioraEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake; // Importamos tu clase
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class JumpAttackGoal extends Goal {
    private SelioraEntity entity;
    private int chargeTicks;
    private boolean hasJumped = false;

    public JumpAttackGoal(SelioraEntity entity) {
        this.entity = entity;
        this.chargeTicks = entity.getJumpAttackChargeTicks();
    }

    @Override
    public boolean canUse() {
        return entity.canUseJumpAttack();
    }

    @Override
    public void start() {
        LivingEntity target = entity.getTarget();
        if (target != null) {
            entity.setJumpAttackActive(true);
        }
    }

    @Override
    public void tick() {
        if (chargeTicks > 0) {
            // Fase de carga
            entity.setDeltaMovement(Vec3.ZERO);
            chargeTicks--;
        } else if (chargeTicks == 0 && !hasJumped) {
            // Salto
            LivingEntity target = entity.getTarget();
            if (target != null) {
                Vec3 targetDirection = target.position().subtract(entity.position()).normalize();
                entity.setDeltaMovement(targetDirection.add(0, 1.2, 0));

                // Sonidos de despegue
                entity.level().playSound(null, entity.getOnPos(), ModSounds.SELIORA_JUMP_ATTACK_SOUND.get(),
                        SoundSource.HOSTILE, 1.5F, 0.9F + entity.level().random.nextFloat() * 0.2F);
                entity.level().playSound(null, entity.getOnPos(), SoundEvents.ENDER_DRAGON_FLAP,
                        SoundSource.HOSTILE, 2.0F, 0.5F);
            }
            hasJumped = true;
        } else if (!entity.onGround()) {
            // En el aire
            LivingEntity target = entity.getTarget();
            if (target != null && target.isAlive()) {
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
            }
        } else {
            // Aterrizaje
            doImpactAttack();
            stop();
        }
    }

    @Override
    public void stop() {
        entity.setJumpAttackActive(false);
        hasJumped = false;
        chargeTicks = entity.getJumpAttackChargeTicks();
        entity.setJumpAttackCooldown(entity.getJumpAttackMaxCooldown());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void doImpactAttack() {
        Level level = entity.level();
        if (level.isClientSide) return;

        double radius = 4.0;
        double damage = 20.0;

        // --- 1. CAMERA SHAKE ---
        // Radio: 30 bloques | Magnitud: 0.8 (fuerte) | Duración: 10 ticks | Fade: 10 ticks
        EntityCameraShake.cameraShake(level, entity.position(), 30.0f, 0.8f, 10, 10);

        // --- 2. SONIDOS DE IMPACTO ---
        // Sonido pesado de yunque + explosión genérica para dar "peso"
        level.playSound(null, entity.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.0F, 0.5F);
        level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.5F, 0.5F);

        // --- 3. DAÑO Y EMPUJE ---
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(radius),
                e -> e != entity && e.isAlive())) {
            e.hurt(entity.damageSources().mobAttack(entity), (float) damage);
            // Empuje vertical
            e.push(0, 0.6, 0);
        }

        // --- 4. EFECTOS VISUALES ---
        spawnImpactEffects(level, this.entity.getOnPos());
    }

    private void spawnImpactEffects(Level level, BlockPos impactPos) {
        Random random = new Random();
        int maxRadius = 3;

        for (int r = 0; r <= maxRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    BlockPos pos = impactPos.offset(dx, 0, dz);
                    BlockState blockState = level.getBlockState(pos);

                    if (blockState.isAir()) {
                        blockState = Blocks.STONE.defaultBlockState();
                    }

                    float baseVelocity = 0.1f + (0.1f * r);
                    float velocity = baseVelocity + (random.nextFloat() * 0.1f - 0.05f);

                    EntityFallingBlock fallingBlock = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(),
                            level, blockState, velocity);
                    fallingBlock.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    fallingBlock.setDuration(30 + random.nextInt(10));
                    level.addFreshEntity(fallingBlock);

                    if (level instanceof ServerLevel server) {
                        server.sendParticles(
                                new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                                10, 0.2, 0.2, 0.2, 0.15
                        );
                        if (r == 0) {
                            // Explosión visual en el centro
                            server.sendParticles(ParticleTypes.EXPLOSION,
                                    entity.getX(), entity.getY(), entity.getZ(),
                                    2, 0.5, 0.0, 0.5, 0.0);
                        }
                    }
                }
            }
        }
    }
}