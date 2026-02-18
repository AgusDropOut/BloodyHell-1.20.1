package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;
import java.util.UUID;

public class BlasphemousImpalerEntity extends AbstractArrow {

    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(BlasphemousImpalerEntity.class, EntityDataSerializers.BOOLEAN);

    private LivingEntity caughtEntity = null;
    private UUID caughtEntityUUID = null;
    private boolean isPinning = false;
    private int lifeTime = 0;
    public int clientShakeTime = 0;
    private int clientSideReturnTickCount;

    // --- CONFIGURACIÓN DE ZONA ---
    private static final int RETURN_THRESHOLD = 300;
    private static final double AOE_RADIUS = 3.5D; // Radio de daño
    private static final float AOE_DAMAGE = 2.0F;  // Daño por tick (muy rápido, bajo daño)

    public BlasphemousImpalerEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public BlasphemousImpalerEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(ModEntityTypes.BLASPHEMOUS_IMPALER_ENTITY.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RETURNING, false);
    }

    @Override
    public void tick() {
        super.tick(); // Física vanilla (actualiza inGround automáticamente)

        if (this.clientShakeTime > 0) this.clientShakeTime--;

        // 1. LÓGICA DE RETORNO (PRIORIDAD ALTA)
        if (this.entityData.get(RETURNING)) {
            handleReturnLogic();
            return;
        }

        // 2. LÓGICA CLIENTE (PARTÍCULAS DE VUELO)
        if (this.level().isClientSide) {
            handleClientFlightParticles();
        }

        // --- LÓGICA DE SERVIDOR ---
        if (!this.level().isClientSide) {

            // 3. NUEVA FUNCIONALIDAD: ZONA DE DAÑO (AoE)
            // Ejecutar si:
            // - Está clavada en un bloque (inGround es true por defecto en AbstractArrow al chocar)
            // - NO tiene a nadie atrapado (caughtEntity == null)
            // - NO está volviendo (ya chequeado arriba)
            if (this.inGround && this.caughtEntity == null) {
                tickGroundEffects();
            }

            // 4. Lógica de Pinning (Si tiene a alguien atrapado)
            // Aquí usamos isPinning O simplemente chequeamos caughtEntity != null
            if (this.caughtEntity != null && this.isPinning) {
                handlePinningLogic();
                // Nota: No hacemos return aquí para permitir que corra el lifetime logic
            }

            // 5. Arrastre en vuelo (Si tiene a alguien pero aún no choca pared)
            if (this.caughtEntity != null && !this.isPinning) {
                handleDraggingLogic();
            }

            // 6. Auto-Retorno / Despawn
            handleLifetimeLogic();
        }
    }

    private void tickGroundEffects() {
        // --- A. AREA OF EFFECT DAMAGE ---
        // Runs every 5 ticks (4 times/sec)
        if (this.tickCount % 5 == 0) {
            AABB area = this.getBoundingBox().inflate(AOE_RADIUS, 1.0D, AOE_RADIUS);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);

            Entity owner = this.getOwner();
            for (LivingEntity target : targets) {
                if (target != owner && (owner == null || !target.isAlliedTo(owner))) {
                    target.hurt(this.damageSources().magic(), AOE_DAMAGE);
                }
            }

            // --- B. VISUALS: EXPANDING WAVE ---
            // Using Helper: Ring Shape
            // Radius starts at 0.5, expands outward at speed 0.35
            ParticleHelper.spawnRing(this.level(), ModParticles.MAGIC_LINE_PARTICLE.get(),
                    this.position().add(0, 0.1, 0), 0.5, 16, 0.35);
        }

        // --- C. VISUALS: SPIRALS ---
        // Specific animation logic kept here, but spawning delegated to helper
        if (!this.level().isClientSide) {
            double spiralRadius = 0.8;
            double angle = (this.tickCount * 0.2) % (Math.PI * 2);

            // Bobbing Y offset calculation
            double yOffset1 = 0.2 + (Math.sin(this.tickCount * 0.1) * 0.5 + 0.5);
            double yOffset2 = 0.2 + (Math.cos(this.tickCount * 0.1) * 0.5 + 0.5);

            // Spiral 1
            double x1 = this.getX() + Math.cos(angle) * spiralRadius;
            double z1 = this.getZ() + Math.sin(angle) * spiralRadius;
            ParticleHelper.spawn(this.level(), ModParticles.MAGIC_LINE_PARTICLE.get(), x1, this.getY() + yOffset1, z1, 0, 0, 0);

            // Spiral 2 (Opposite side)
            double x2 = this.getX() + Math.cos(angle + Math.PI) * spiralRadius;
            double z2 = this.getZ() + Math.sin(angle + Math.PI) * spiralRadius;
            ParticleHelper.spawn(this.level(), ModParticles.MAGIC_LINE_PARTICLE.get(), x2, this.getY() + yOffset2, z2, 0, 0, 0);
        }
    }
    // --- MÓDULOS DE LÓGICA EXISTENTES (Refactorizados para limpieza) ---

    private void handleReturnLogic() {
        this.setNoPhysics(true);
        this.isPinning = false;
        this.inGround = false;

        if (this.caughtEntity != null) {
            this.caughtEntity = null;
            this.caughtEntityUUID = null;
        }

        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive()) {
            if (!this.level().isClientSide) this.discard();
            return;
        }

        Vec3 vecToOwner = owner.getEyePosition().subtract(this.position());
        double acceleration = 0.05 * 3.0;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vecToOwner.normalize().scale(acceleration)));
        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());

        if (this.clientSideReturnTickCount == 0) {
            this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
        }
        ++this.clientSideReturnTickCount;

        if (!this.level().isClientSide) {
            ((ServerLevel) this.level()).sendParticles(ModParticles.MAGIC_LINE_PARTICLE.get(),
                    this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        }

        if (this.distanceTo(owner) < 1.5f) {
            if (!this.level().isClientSide && owner instanceof Player player) {
                if (player.getInventory().add(this.getPickupItem())) {
                    this.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f);
                    this.discard();
                } else {
                    player.drop(this.getPickupItem(), false);
                    this.discard();
                }
            }
        }
    }

    private void handleClientFlightParticles() {
        if (!this.inGround && !this.isPinning && this.getDeltaMovement().lengthSqr() > 0.01) {
            if (this.tickCount % 2 == 0) {
                Vec3 motion = this.getDeltaMovement();
                this.level().addParticle(ModParticles.SHOCKWAVE_RING.get(),
                        this.getX(), this.getY(), this.getZ(),
                        motion.x, motion.y, motion.z);
            }
        }
    }

    private void handleLifetimeLogic() {
        if (this.pickup == Pickup.ALLOWED || this.pickup == Pickup.CREATIVE_ONLY) {
            if (lifeTime++ > RETURN_THRESHOLD && !this.entityData.get(RETURNING)) {
                this.entityData.set(RETURNING, true);
                this.clientSideReturnTickCount = 0;
            }
        } else if (lifeTime++ > 1200) {
            this.discard();
        }
    }

    private void handlePinningLogic() {
        if (caughtEntity != null) {
            if (!caughtEntity.isAlive()) caughtEntity = null;
            else {
                caughtEntity.setPos(this.getX(), this.getY() - caughtEntity.getBbHeight()/2, this.getZ());
                caughtEntity.setDeltaMovement(Vec3.ZERO);
                caughtEntity.hurtMarked = true;
                caughtEntity.hasImpulse = true;
            }
        }
    }

    private void handleDraggingLogic() {
        if (!caughtEntity.isAlive()) caughtEntity = null;
        else {
            caughtEntity.setPos(this.getX(), this.getY(), this.getZ());
            caughtEntity.fallDistance = 0;
        }
        if (this.tickCount > 60) caughtEntity = null;
    }

    // --- EVENTOS DE IMPACTO (Sin cambios drásticos) ---

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.entityData.get(RETURNING)) return;
        if (caughtEntity != null || result.getEntity() == this.getOwner()) return;

        if (result.getEntity() instanceof LivingEntity livingTarget) {
            float damage = (float) this.getBaseDamage() * 2.0f;
            Vec3 motion = this.getDeltaMovement().normalize();

            // Raycast para pared
            Vec3 start = livingTarget.position();
            Vec3 end = start.add(motion.scale(8.0));
            BlockHitResult wallCheck = this.level().clip(new net.minecraft.world.level.ClipContext(
                    start, end, net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, this));

            boolean hitWall = wallCheck.getType() != HitResult.Type.MISS;

            if (hitWall) {
                // PINNING (Impacto contra pared)
                this.caughtEntity = livingTarget;
                this.caughtEntityUUID = livingTarget.getUUID();
                livingTarget.hurt(this.damageSources().trident(this, this.getOwner()), damage);

                Vec3 wallPos = wallCheck.getLocation();
                this.setPos(wallPos.x, wallPos.y, wallPos.z);
                livingTarget.setPos(wallPos.x, wallPos.y - livingTarget.getBbHeight()/2, wallPos.z);

                this.isPinning = true;
                this.setDeltaMovement(Vec3.ZERO);
                this.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
                this.playSound(SoundEvents.ANVIL_LAND, 0.5f, 0.5f);

                // --- NUEVO: CAMERA SHAKE AL EMPALAR ---
                // Un poco más fuerte (0.8) porque es un golpe brutal
                EntityCameraShake.cameraShake(this.level(), this.position(), 20.0f, 0.8f, 10, 5);

                if (this.level() instanceof ServerLevel serverLevel) {
                    spawnWallImplosionParticles(serverLevel, wallPos, wallCheck.getDirection());
                }
            } else {
                // GOLPE NORMAL (Sin Shake o muy leve)
                livingTarget.hurt(this.damageSources().trident(this, this.getOwner()), damage);
                livingTarget.knockback(1.5, motion.x, motion.z);
                this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
                this.setYRot(this.getYRot() + 180.0F);
                this.yRotO += 180.0F;

                // Opcional: Shake muy leve al golpear entidad sin pared
                // EntityCameraShake.cameraShake(this.level(), this.position(), 10.0f, 0.2f, 5, 2);
            }
        }
    }
    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (this.entityData.get(RETURNING)) return;
        super.onHitBlock(result);

        if (this.isPinning && this.tickCount > 5) return;

        this.isPinning = true;
        this.clientShakeTime = 7;
        this.playSound(SoundEvents.ANVIL_LAND, 1.0f, 0.5f);

        BlockPos hitPos = result.getBlockPos();
        BlockState hitState = this.level().getBlockState(hitPos);
        Direction face = result.getDirection();

        if (this.level() instanceof ServerLevel serverLevel && !hitState.isAir()) {

            // --- NUEVO: CAMERA SHAKE ---
            // Radio: 15 bloques, Magnitud: 0.5 (medio), Duración: 10 ticks, Fade: 5 ticks
            EntityCameraShake.cameraShake(this.level(), this.position(), 15.0f, 0.5f, 10, 5);

            // Efecto Falling Blocks (Debris)
            for (int i = 0; i < 6; i++) {
                double spawnX = hitPos.getX() + 0.5 + face.getStepX() * 0.6;
                double spawnY = hitPos.getY() + 0.5 + face.getStepY() * 0.6;
                double spawnZ = hitPos.getZ() + 0.5 + face.getStepZ() * 0.6;

                EntityFallingBlock debris = new EntityFallingBlock(
                        ModEntityTypes.ENTITY_FALLING_BLOCK.get(), this.level(), 40, hitState);

                debris.setPos(spawnX, spawnY, spawnZ);
                double velX = face.getStepX() * 0.2 + (random.nextFloat() - 0.5) * 0.6;
                double velY = 0.4 + random.nextFloat() * 0.3;
                double velZ = face.getStepZ() * 0.2 + (random.nextFloat() - 0.5) * 0.6;

                debris.setDeltaMovement(velX, velY, velZ);
                this.level().addFreshEntity(debris);
            }
            // Partículas de impacto
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, hitState),
                    this.getX(), this.getY(), this.getZ(), 20, 0.3, 0.3, 0.3, 0.1);

            // Círculo Mágico en la pared
            if (this.caughtEntity == null) {
                spawnWallImplosionParticles(serverLevel, result.getLocation(), face);
            }
        }
    }

    private void spawnWallImplosionParticles(ServerLevel level, Vec3 pos, Direction face) {
        // Tu lógica de anillos de impacto (sin cambios)
        double ox = pos.x + face.getStepX() * 0.05;
        double oy = pos.y + face.getStepY() * 0.05;
        double oz = pos.z + face.getStepZ() * 0.05;
        int particleCount = 25;
        float radius = 1.0f;
        float speed = 0.15f;

        for (int i = 0; i < particleCount; i++) {
            float angle = (float) i / particleCount * 2 * (float) Math.PI;
            double pX = 0, pY = 0, pZ = 0, vX = 0, vY = 0, vZ = 0;

            if (face.getAxis() == Direction.Axis.Y) {
                pX = Math.cos(angle) * radius; pZ = Math.sin(angle) * radius;
                vX = Math.cos(angle) * speed; vZ = Math.sin(angle) * speed;
            } else if (face.getAxis() == Direction.Axis.X) {
                pY = Math.cos(angle) * radius; pZ = Math.sin(angle) * radius;
                vY = Math.cos(angle) * speed; vZ = Math.sin(angle) * speed;
            } else {
                pX = Math.cos(angle) * radius; pY = Math.sin(angle) * radius;
                vX = Math.cos(angle) * speed; vY = Math.sin(angle) * speed;
            }

            level.sendParticles(ModParticles.MAGIC_LINE_PARTICLE.get(), ox + pX, oy + pY, oz + pZ, 0, vX, vY, vZ, 1.0);
        }
        level.sendParticles(ParticleTypes.SONIC_BOOM, ox, oy, oz, 1, 0,0,0, 0);
    }

    // --- METODOS STANDARD ---
    @Override public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            if (this.entityData.get(RETURNING)) {
                if (!this.level().isClientSide) {
                    if (player.getInventory().add(this.getPickupItem())) {
                        this.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f);
                        this.discard();
                    } else player.drop(this.getPickupItem(), false);
                }
            }
        }
    }

    @Override protected ItemStack getPickupItem() { return new ItemStack(ModItems.BLASPHEMOUS_IMPALER.get()); }
    @Override protected SoundEvent getDefaultHitGroundSoundEvent() { return SoundEvents.TRIDENT_HIT_GROUND; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (caughtEntityUUID != null) tag.putUUID("CaughtEntity", caughtEntityUUID);
        tag.putBoolean("IsPinning", isPinning);
        tag.putBoolean("IsReturning", this.entityData.get(RETURNING));
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("CaughtEntity")) caughtEntityUUID = tag.getUUID("CaughtEntity");
        this.isPinning = tag.getBoolean("IsPinning");
        this.entityData.set(RETURNING, tag.getBoolean("IsReturning"));
    }

    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}