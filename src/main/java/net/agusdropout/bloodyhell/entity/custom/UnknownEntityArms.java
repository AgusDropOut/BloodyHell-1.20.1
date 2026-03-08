package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UnknownEntityArms extends Entity implements TraceableEntity, GeoEntity {

    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Nullable
    private LivingEntity target;
    private boolean emergePhase = true;
    private boolean retractPhase = false;
    private boolean idlePhase = false;
    private int lifeTicks = 200;
    private boolean isTargetAlive = true;
    public static final float DEFAULT_DAMAGE = 3.0F;
    private float damage = DEFAULT_DAMAGE;
    public AnimationState emergeAnimationState = new AnimationState();
    public AnimationState retractAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    private static final EntityDataAccessor<Boolean> RETRACT_PHASE = SynchedEntityData.defineId(UnknownEntityArms.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EMERGE_PHASE = SynchedEntityData.defineId(UnknownEntityArms.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IDLE_PHASE = SynchedEntityData.defineId(UnknownEntityArms.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_TARGET_ALIVE = SynchedEntityData.defineId(UnknownEntityArms.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFE_TICKS = SynchedEntityData.defineId(UnknownEntityArms.class, EntityDataSerializers.INT);

    public UnknownEntityArms(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }


    public UnknownEntityArms(Level level, double d, double e, double f, float g, int warmupDelayTicks, float damage, LivingEntity livingEntity, LivingEntity target) {
        this(ModEntityTypes.UNKNOWN_ENTITY_ARMS.get(), level);
        this.damage = damage;
        this.target = target;
        this.setYRot(g * 57.295776f);
        this.setPos(d, e, f);
        level().playLocalSound(d, e, f, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.2f + 0.85f, false);
    }

    public UnknownEntityArms(Level level, double d, double e, double f, float g, int warmupDelayTicks, float damage, LivingEntity livingEntity, ItemStack itemStack, LivingEntity target) {
        this(ModEntityTypes.UNKNOWN_ENTITY_ARMS.get(), level);
        this.damage = damage;
        this.setYRot(g * 57.295776f);
        this.target = target;
        this.setPos(d, e, f);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RETRACT_PHASE, false);
        this.entityData.define(EMERGE_PHASE, true);
        this.entityData.define(IDLE_PHASE, false);
        this.entityData.define(IS_TARGET_ALIVE, true);
        this.entityData.define(LIFE_TICKS, 200);
    }



    @Override
    public void tick() {
        super.tick();

        if(!this.level().isClientSide()){
            if (!target.isAlive()){
                entityData.set(IS_TARGET_ALIVE, false);
            }
        }

        isTargetAlive = entityData.get(IS_TARGET_ALIVE);
        if (this.level().isClientSide) {
            emergePhase = entityData.get(EMERGE_PHASE);
            idlePhase = entityData.get(IDLE_PHASE);
            retractPhase = entityData.get(RETRACT_PHASE);
            entityData.set(LIFE_TICKS, entityData.get(LIFE_TICKS) - 1);
            lifeTicks = entityData.get(LIFE_TICKS);
        }

        List<Arrow> arrows = this.level().getEntitiesOfClass(Arrow.class, this.getBoundingBox().inflate(1.2D));
        for (Arrow arrow : arrows) {
            Vec3 selfPos = this.position().add(0, 1.6f, 0);
            Vec3 enemyPos = arrow.getEyePosition().subtract(selfPos);
            Vec3 normalizedDirection = enemyPos.normalize();
            double knockback = 0.15;
            arrow.setDeltaMovement(arrow.getDeltaMovement().add(
                    normalizedDirection.x() * knockback,
                    normalizedDirection.y() * knockback,
                    normalizedDirection.z() * knockback
            ));
        }




        if (!level().isClientSide()) {

            if(target == null){
                this.discard();
                return;
            }
            if (!this.target.isAlive() && lifeTicks == 1) {
                    ItemStack dropItem = new ItemStack(ModItems.UNKNOWN_ENTITY_FINGER.get(), 1);
                    ItemEntity itemEntity = new ItemEntity(level(), this.getX(), this.getY() + 0.5, this.getZ(), dropItem);
                    level().addFreshEntity(itemEntity);
                    itemEntity.setPickUpDelay(40);
            }
            target.setDeltaMovement(0,0,0);
            target.hasImpulse = false;
            target.teleportTo(this.getX(), this.getY(), this.getZ());
            if (!target.isAlive() && lifeTicks > 50) {
                idlePhase = false;
                emergePhase = false;
                retractPhase = true;
                lifeTicks = 40;
            } else {

                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
                for (LivingEntity livingEntity : list) {
                    this.dealDamageTo(livingEntity);
                }


                if (emergePhase && lifeTicks < 150) {
                    idlePhase = true;
                    emergePhase = false;
                }
                if (idlePhase && lifeTicks < 30) {
                    retractPhase = true;
                    idlePhase = false;
                }


                if (lifeTicks == 5) {
                    if (this.level().isClientSide) {
                        spawnExpandingCircleParticles(this.level(), this, 1.5, 25, 0.1);
                    }
                    level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.2f + 0.85f, false);
                }
            }


            entityData.set(EMERGE_PHASE, emergePhase);
            entityData.set(RETRACT_PHASE, retractPhase);
            entityData.set(IDLE_PHASE, idlePhase);
            entityData.set(LIFE_TICKS, lifeTicks);

            // 🔹 Manejo de la fase de retracción
            if (retractPhase && lifeTicks < 0) {
                this.discard();
            }
        }

        lifeTicks--;

    }




    private void dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.target;
        if (livingEntity2 == null) {
            livingEntity.hurt(this.damageSources().magic(), this.damage);
        } else {
            if (livingEntity2.isAlliedTo(livingEntity)) return;
            livingEntity.hurt(this.damageSources().indirectMagic(this, livingEntity2), this.damage);

        }
    }


    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.getEntity() instanceof Arrow) return false;
        return super.hurt(damageSource, f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("emerge_phase")) {
            this.entityData.set(EMERGE_PHASE, compoundTag.getBoolean("emerge_phase"));
        }
        if (compoundTag.contains("retract_phase")) {
            this.entityData.set(RETRACT_PHASE, compoundTag.getBoolean("retract_phase"));
        }
        if (compoundTag.contains("idle_phase")) {
            this.entityData.set(IDLE_PHASE, compoundTag.getBoolean("idle_phase"));
        }
        if (compoundTag.contains("is_target_alive")) {
            this.entityData.set(IS_TARGET_ALIVE, compoundTag.getBoolean("is_target_alive"));
        }
        if(compoundTag.contains("life_ticks")){
            this.entityData.set(LIFE_TICKS, compoundTag.getInt("life_ticks"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("emerge_phase", this.entityData.get(EMERGE_PHASE));
        compoundTag.putBoolean("retract_phase", this.entityData.get(RETRACT_PHASE));
        compoundTag.putBoolean("idle_phase", this.entityData.get(IDLE_PHASE));
        compoundTag.putBoolean("is_target_alive", this.entityData.get(IS_TARGET_ALIVE));
        compoundTag.putInt("life_ticks", this.entityData.get(LIFE_TICKS));

    }


    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState animationState) {



        if(entityData.get(IDLE_PHASE)){
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if(entityData.get(EMERGE_PHASE)){
            animationState.getController().setAnimation(RawAnimation.begin().then("emerge", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if(entityData.get(RETRACT_PHASE)){
            animationState.getController().setAnimation(RawAnimation.begin().then("retract", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",
                0, this::predicate));

    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    public void spawnExpandingCircleParticles(Level level, Entity entity, double radius, int particleCount, double speed) {
        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double offsetX = radius * Math.cos(angle);
            double offsetZ = radius * Math.sin(angle);

            double particleX = entity.getX() + offsetX;
            double particleY = entity.getY() + 1.0;
            double particleZ = entity.getZ() + offsetZ;


            double velocityX = speed * Math.cos(angle);
            double velocityZ = speed * Math.sin(angle);

            level.addParticle(ParticleTypes.FIREWORK, particleX, particleY-1, particleZ, velocityX, 0, velocityZ);
        }
    }


    @Nullable
    @Override
    public Entity getOwner() {
        return null;
    }
}

