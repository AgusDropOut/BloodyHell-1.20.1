package net.agusdropout.bloodyhell.entity.projectile;



import net.agusdropout.bloodyhell.entity.ModEntityTypes;
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
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class BlasphemousSpinesEntity extends Entity implements GeoEntity {
    private final SingletonAnimatableInstanceCache cache = (SingletonAnimatableInstanceCache) GeckoLibUtil.createInstanceCache(this, true);

    // ESTADOS DE ANIMACIÓN
    private static final int STATE_RISE = 0;
    private static final int STATE_IDLE = 1;
    private static final int STATE_RETRACT = 2;

    // SINCRONIZACIÓN
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(BlasphemousSpinesEntity.class, EntityDataSerializers.INT);

    // CONFIGURACIÓN DE TIEMPO (Ticks)
    private static final int RISE_TICKS = 10;     // 0.5s subiendo
    private static final int IDLE_TICKS = 50;     // 2.5s quieta (Antes 20) -> Viven más
    private static final int RETRACT_TICKS = 15;  // 0.75s bajando (Un poco más lento el final)
    private static final int LIFESPAN = RISE_TICKS + IDLE_TICKS + RETRACT_TICKS;

    private float damage = 6.0f;
    private UUID ownerUUID;

    public BlasphemousSpinesEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    // Constructor personalizado para spawnearla fácil
    public BlasphemousSpinesEntity(Level level, double x, double y, double z, float damage, UUID ownerUUID) {
        this(ModEntityTypes.BLASPHEMOUS_SPINES.get(), level); // Asegúrate que el registro se llame así
        this.setPos(x, y, z);
        this.damage = damage;
        this.ownerUUID = ownerUUID;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ANIM_STATE, STATE_RISE);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // --- LÓGICA SERVIDOR ---
            int age = this.tickCount;

            // 1. Control de Estados
            if (age < RISE_TICKS) {
                setAnimState(STATE_RISE);
                // Hacemos daño solo durante la subida (o cada x ticks)
                if (age % 5 == 0) doDamage();
            } else if (age < (RISE_TICKS + IDLE_TICKS)) {
                setAnimState(STATE_IDLE);
            } else {
                setAnimState(STATE_RETRACT);
            }

            // 2. Muerte
            if (age >= LIFESPAN) {
                this.discard();
            }
        }
    }

    private void doDamage() {
        if (this.tickCount % 10 != 0 && this.tickCount > RISE_TICKS) return;
        // Hitbox pequeña (0.5 bloques alrededor)
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.5, 0, 0.5));

        for (LivingEntity target : targets) {
            // No dañar al dueño ni a otras espinas
            if (target.getUUID().equals(ownerUUID)) continue;

            target.hurt(this.level().damageSources().magic(), damage);
        }
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }



    // --- GECKOLIB ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            int currentAnimState = this.entityData.get(ANIM_STATE);

            // Mapeamos el int a la animación correspondiente
            return switch (currentAnimState) {
                case STATE_RISE -> state.setAndContinue(RawAnimation.begin().thenPlay("rise"));
                case STATE_RETRACT -> state.setAndContinue(RawAnimation.begin().thenPlay("retract"));
                default -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            };
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private void setAnimState(int state) {
        this.entityData.set(ANIM_STATE, state);
    }

    // --- BOILERPLATE ---
    @Override protected void readAdditionalSaveData(CompoundTag pCompound) {}
    @Override protected void addAdditionalSaveData(CompoundTag pCompound) {}
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}