package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class BlasphemousSpearEntity extends Entity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // ANIMACIONES
    private static final RawAnimation ANIM_RISE = RawAnimation.begin().thenPlay("rise");
    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_RETRACT = RawAnimation.begin().thenPlay("retract");

    // ESTADOS (INT)
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(BlasphemousSpearEntity.class, EntityDataSerializers.INT);

    // --- ROTACIÓN SINCRONIZADA (IGUAL QUE BLOOD SLASH) ---
    private static final EntityDataAccessor<Float> FORCED_YAW = SynchedEntityData.defineId(BlasphemousSpearEntity.class, EntityDataSerializers.FLOAT);

    private static final int STATE_RISE = 0;
    private static final int STATE_IDLE = 1;
    private static final int STATE_RETRACT = 2;

    private static final int RISE_TICKS = 10;
    private static final int IDLE_TICKS = 50;
    private static final int RETRACT_TICKS = 15;
    private static final int LIFESPAN = RISE_TICKS + IDLE_TICKS + RETRACT_TICKS;

    private float damage = 8.0f;
    private UUID ownerUUID;

    public BlasphemousSpearEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public BlasphemousSpearEntity(Level level, double x, double y, double z, float damage, UUID ownerUUID, float yaw) {
        this(ModEntityTypes.BLASPHEMOUS_SPEAR.get(), level);
        this.setPos(x, y, z);
        this.damage = damage;
        this.ownerUUID = ownerUUID;

        // SETEAR ROTACIÓN INICIAL
        this.setForcedYaw(yaw);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ANIM_STATE, STATE_RISE);
        this.entityData.define(FORCED_YAW, 0f); // Valor por defecto
    }

    // --- GETTERS Y SETTERS MÁGICOS ---
    public float getForcedYaw() {
        return this.entityData.get(FORCED_YAW);
    }

    public void setForcedYaw(float yaw) {
        this.entityData.set(FORCED_YAW, yaw);
        this.setYRot(yaw); // Actualizamos también la rotación vanilla por si acaso
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            int age = this.tickCount;

            // 1. FASE DE SUBIDA (RISE)
            if (age < RISE_TICKS) {
                setAnimState(STATE_RISE);
                // Hace daño cada 5 ticks mientras sube
                if (age % 5 == 0) doDamage();
            }
            // 2. FASE DE ESPERA (IDLE)
            else if (age < (RISE_TICKS + IDLE_TICKS)) {
                setAnimState(STATE_IDLE);
                // Hace daño cada 10 ticks (0.5s) mientras está quieta
                if (age % 10 == 0) doDamage();
            }
            // 3. FASE DE RETRACCIÓN (RETRACT)
            else {
                setAnimState(STATE_RETRACT);

                // Efecto visual al retraerse (Solo una vez)
                if (age == (RISE_TICKS + IDLE_TICKS)) {
                    spawnRetractParticles();
                }
                // NOTA: Generalmente no hacen daño al bajar, pero si quieres, agrégalo aquí.
            }

            if (age >= LIFESPAN) this.discard();
        }
    }

    // Nuevo método helper dentro de la entidad
    private void spawnRetractParticles() {
        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            BlockPos underPos = this.blockPosition().below();
            BlockState state = this.level().getBlockState(underPos);

            if (state.isAir()) return; // Si está en el aire, no hacemos nada

            // Partícula del bloque del suelo
            net.minecraft.core.particles.ParticleOptions particle =
                    new net.minecraft.core.particles.BlockParticleOption(net.minecraft.core.particles.ParticleTypes.BLOCK, state);

            // Generamos un pequeño círculo concentrado en la base
            for (int i = 0; i < 8; i++) {
                double angle = (2 * Math.PI / 8) * i;
                double px = this.getX() + Math.cos(angle) * 0.4; // Radio pegado a la lanza
                double pz = this.getZ() + Math.sin(angle) * 0.4;

                // Pequeña explosión hacia arriba y afuera
                serverLevel.sendParticles(particle,
                        px, this.getY() + 0.1, pz,
                        1,
                        0.0, 0.1, 0.0, // Delta X, Y, Z (aleatoriedad)
                        0.15); // Velocidad
            }

            // Opcional: Un poco de humo "POOF" para enfatizar que desaparece
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    3, 0.2, 0.2, 0.2, 0.02);
        }
    }

    private void doDamage() {
        // Hitbox un poco más grande para asegurar el golpe
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(0.4, 0.5, 0.4));

        for (LivingEntity target : targets) {


            // 2. Evitar dañar al dueño (con protección Null)
            if (this.ownerUUID != null && target.getUUID().equals(ownerUUID)) continue;

            // 3. Aplicar daño
            target.hurt(this.level().damageSources().magic(), damage);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            int currentAnimState = this.entityData.get(ANIM_STATE);
            return switch (currentAnimState) {
                case STATE_RISE -> state.setAndContinue(ANIM_RISE);
                case STATE_RETRACT -> state.setAndContinue(ANIM_RETRACT);
                default -> state.setAndContinue(ANIM_IDLE);
            };
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    private void setAnimState(int state) { this.entityData.set(ANIM_STATE, state); }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) this.ownerUUID = pCompound.getUUID("Owner");
        if (pCompound.contains("ForcedYaw")) this.setForcedYaw(pCompound.getFloat("ForcedYaw"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) pCompound.putUUID("Owner", this.ownerUUID);
        pCompound.putFloat("ForcedYaw", getForcedYaw());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}