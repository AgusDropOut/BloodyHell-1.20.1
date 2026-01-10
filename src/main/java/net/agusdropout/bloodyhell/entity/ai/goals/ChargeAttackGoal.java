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

public class ChargeAttackGoal extends Goal {
    private SelioraEntity entity;
    private int chargeTicks;
    private boolean hasCharged = false;
    private int minimunChargeTicks = 60;

    public ChargeAttackGoal(SelioraEntity entity) {
        this.entity = entity;
        this.chargeTicks = entity.getChargeAttackChargeTicks();
    }

    @Override
    public boolean canUse() {
        return entity.canUseChargeAttack();
    }

    @Override
    public void start() {
        LivingEntity target = entity.getTarget();
        if (target != null) {
            entity.setChargeAttackActive(true);
        }
    }

    @Override
    public void tick() {
        if (chargeTicks > 0) {
            // Fase de preparación
            entity.setDeltaMovement(Vec3.ZERO);
            LivingEntity target = entity.getTarget();
            if (target != null && target.isAlive()) {
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
            }
            chargeTicks--;

            // Sonido de "cargando energía"
            if(chargeTicks == 5) {
                entity.level().playSound(null, entity.getOnPos(), SoundEvents.TRIDENT_THROW,
                        SoundSource.HOSTILE, 1.0F, 0.5F);
            }

        } else if (chargeTicks == 0 && !hasCharged) {
            // Inicio de la carrera
            LivingEntity target = entity.getTarget();
            if (target != null) {
                Vec3 targetDirection = target.position().subtract(entity.position()).normalize();
                entity.setDeltaMovement(targetDirection.scale(4)); // Alta velocidad

                // Sonidos iniciales
                entity.level().playSound(null, entity.getOnPos(), ModSounds.SELIORA_CHARGE_ATTACK_SOUND.get(),
                        SoundSource.HOSTILE, 1.5F, 0.9F + entity.level().random.nextFloat() * 0.2F);
                entity.level().playSound(null, entity.getOnPos(), SoundEvents.TRIDENT_RIPTIDE_3,
                        SoundSource.HOSTILE, 1.0F, 1.0F);
            }
            hasCharged = true;
        } else if (this.minimunChargeTicks > 0) {
            // Durante la carrera
            doAttackDamage();
            minimunChargeTicks--;
        } else {
            stop();
        }
    }

    @Override
    public void stop() {
        entity.setChargeAttackActive(false);
        hasCharged = false;
        minimunChargeTicks = 20;
        chargeTicks = entity.getChargeAttackChargeTicks();
        entity.setChargeAttackCooldown(entity.getChargeAttackMaxCooldown());
    }

    public void doAttackDamage() {
        Level level = entity.level();
        if (level.isClientSide) return;

        double radius = 2;
        double damage = 20;

        // --- 1. CAMERA SHAKE RÍTMICO ---
        // Ejecutamos cada 4 ticks para simular pisadas fuertes sin marear al jugador
        if (minimunChargeTicks % 4 == 0) {
            // Radio: 15 | Magnitud: 0.15 (suave) | Duración: 3 | Fade: 2
            EntityCameraShake.cameraShake(level, entity.position(), 15.0f, 0.15f, 3, 2);

            // Sonido de pisada fuerte
            level.playSound(null, entity.blockPosition(), SoundEvents.RAVAGER_STEP, SoundSource.HOSTILE, 2.0F, 0.8F);
        }

        BlockPos centerPos = entity.blockPosition();

        // --- 2. DAÑO Y EMPUJE ---
        boolean hitSomeone = false;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(radius),
                e -> e != entity && e.isAlive())) {

            boolean hurt = e.hurt(entity.damageSources().mobAttack(entity), (float) damage);
            if (hurt) {
                hitSomeone = true;
                Vec3 pushDir = entity.position().subtract(e.position()).normalize();
                e.push(pushDir.x * 0.8, 0.4, pushDir.z * 0.8);
            }
        }

        if (hitSomeone) {
            level.playSound(null, centerPos, SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.HOSTILE, 1.0F, 0.8F);
        }

        // --- 3. EFECTOS VISUALES ---
        if (level instanceof ServerLevel server) {
            BlockState belowBlock = level.getBlockState(centerPos.below());
            // Partículas de tierra levantándose
            server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, belowBlock),
                    entity.getX(), entity.getY() + 0.1, entity.getZ(),
                    10, 0.5, 0.2, 0.5, 0.1);

            // Estela de velocidad (nubes)
            server.sendParticles(ParticleTypes.CLOUD,
                    entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    2, 0.5, 0.1, 0.5, 0.05);
        }

        // Falling Blocks (cada 5 ticks para no saturar)
        if (minimunChargeTicks % 5 == 0) {
            spawnRadialFallingBlocks(level, centerPos);
        }
    }

    private void spawnRadialFallingBlocks(Level level, BlockPos impactPos) {
        Random random = new Random();
        int maxRadius = 2;

        for (int r = 0; r <= maxRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    BlockPos pos = impactPos.offset(dx, 0, dz);
                    BlockState blockState = level.getBlockState(pos);

                    if (blockState.isAir()) continue;

                    float baseVelocity = 0.1f + 0.1f * r;
                    float velocity = baseVelocity + (random.nextFloat() * 0.1f - 0.05f);

                    EntityFallingBlock fb = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(),
                            level, blockState, velocity);
                    fb.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    fb.setDuration(20 + random.nextInt(10));

                    level.addFreshEntity(fb);
                }
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}