package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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

public class SanguineSacrificeEntity extends Entity implements TraceableEntity, GeoEntity {

    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Nullable
    private LivingEntity target;
    private boolean emergePhase = true;
    private boolean retractPhase = false;
    private boolean idlePhase = false;
    private boolean isTargetAlive=true;
    private boolean isSacrificeableTarget;
    private boolean isCorruptedSacrificeableTarget;
    private int lifeTicks = 200;
    public static final float DEFAULT_DAMAGE = 30.0F;
    private float damage = DEFAULT_DAMAGE;
    public AnimationState emergeAnimationState = new AnimationState();
    public AnimationState retractAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    private static final EntityDataAccessor<Boolean> RETRACT_PHASE = SynchedEntityData.defineId(SanguineSacrificeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EMERGE_PHASE = SynchedEntityData.defineId(SanguineSacrificeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IDLE_PHASE = SynchedEntityData.defineId(SanguineSacrificeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFE_TICKS = SynchedEntityData.defineId(SanguineSacrificeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_TARGET_ALIVE = SynchedEntityData.defineId(SanguineSacrificeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SACRIFICEABLE_TARGET = SynchedEntityData.defineId(SanguineSacrificeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_CORRUPTED_SACRIFICEABLE_TARGET = SynchedEntityData.defineId(SanguineSacrificeEntity.class, EntityDataSerializers.BOOLEAN);

    public SanguineSacrificeEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }


    public SanguineSacrificeEntity(Level level, double d, double e, double f, float g, int warmupDelayTicks, float damage, LivingEntity livingEntity, LivingEntity target) {
        this(ModEntityTypes.SANGUINE_SACRIFICE_ENTITY.get(), level);
        this.damage = damage;
        this.target = target;
        this.setYRot(g * 57.295776f);
        this.setPos(d, e, f);
        level().playLocalSound(d, e, f, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.2f + 0.85f, false);
    }

    public SanguineSacrificeEntity(Level level, double d, double e, double f, float g, int warmupDelayTicks, float damage, LivingEntity livingEntity, ItemStack itemStack, LivingEntity target) {
        this(ModEntityTypes.SANGUINE_SACRIFICE_ENTITY.get(), level);
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
        this.entityData.define(LIFE_TICKS, 200);
        this.entityData.define(IS_TARGET_ALIVE, true);
        this.entityData.define(IS_SACRIFICEABLE_TARGET, false);
        this.entityData.define(IS_CORRUPTED_SACRIFICEABLE_TARGET, false);
    }



    @Override
    public void tick() {
        super.tick();


        // 🔹 Actualizar fases en CLIENTE leyendo de SynchedEntityData
        if (this.level().isClientSide) {
            emergePhase = entityData.get(EMERGE_PHASE);
            idlePhase = entityData.get(IDLE_PHASE);
            retractPhase = entityData.get(RETRACT_PHASE);
            entityData.set(LIFE_TICKS, entityData.get(LIFE_TICKS) - 1);
            lifeTicks = entityData.get(LIFE_TICKS);
            isTargetAlive = entityData.get(IS_TARGET_ALIVE);
            isSacrificeableTarget = entityData.get(IS_SACRIFICEABLE_TARGET);
            isCorruptedSacrificeableTarget = entityData.get(IS_CORRUPTED_SACRIFICEABLE_TARGET);

        }

        // 🔹 Efecto sobre flechas cercanas
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


        if(lifeTicks == 195 && emergePhase){
            if (level().isClientSide) {
                spawnImpactParticles(this.level(), this);
            }
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0f, 0.8f + random.nextFloat() * 0.2f);
        }


        if (lifeTicks == 5) {
            if (this.level().isClientSide) {
                spawnExpandingCircleParticles(this.level(), this, 1.5, 25, 0.1);
            }
            level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.2f + 0.85f, false);
        }


        if (!level().isClientSide()) {
            if(!target.isAlive()) {
                isTargetAlive = false;
                entityData.set(IS_TARGET_ALIVE, false);
            }

            if(target == null){
                this.discard();
                return;
            }
            target.setDeltaMovement(0,0,0);
            target.hasImpulse = false;
            target.teleportTo(this.getX(), this.getY(), this.getZ());
            if (!target.isAlive() && lifeTicks > 15) {
                entityData.set(IS_TARGET_ALIVE, false);
                idlePhase = false;
                emergePhase = false;
                retractPhase = true;
                lifeTicks = 15;
            } else {
                // 🔹 Manejo de daño a entidades cercanas
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
                for (LivingEntity livingEntity : list) {
                    this.dealDamageTo(livingEntity);
                }

                // 🔹 Manejo de fases en SERVIDOR
                if (emergePhase && lifeTicks < 150) {
                    idlePhase = true;
                    emergePhase = false;
                }
                if (idlePhase && lifeTicks < 20) {
                    retractPhase = true;
                    idlePhase = false;
                }


            }

            if(lifeTicks == 200){
                if(target.getType().is(ModTags.Entities.SACRIFICEABLE_ENTITY)){
                    isSacrificeableTarget = true;
                }else if(target.getType().is(ModTags.Entities.CORRUPTED_SACRIFICEABLE_ENTITY)){
                    isCorruptedSacrificeableTarget = true;
                }
            }

            // 🔹 Sincronizar fases con SynchedEntityData (solo en servidor)
            entityData.set(EMERGE_PHASE, emergePhase);
            entityData.set(RETRACT_PHASE, retractPhase);
            entityData.set(IDLE_PHASE, idlePhase);
            entityData.set(LIFE_TICKS, lifeTicks);
            entityData.set(IS_TARGET_ALIVE, isTargetAlive);
            entityData.set(IS_SACRIFICEABLE_TARGET, isSacrificeableTarget);


        }

        // 🔹 Manejo de la fase de retracción
        if (retractPhase && lifeTicks==0) {
            if (!isTargetAlive){
                if(isSacrificeableTarget){
                    BloodySoulEntity bloodySoulEntity = new BloodySoulEntity(ModEntityTypes.BLOODY_SOUL_ENTITY.get(), this.level());
                    level().addFreshEntity(bloodySoulEntity);
                    bloodySoulEntity.setPos(this.getOnPos().getX(), this.getOnPos().getY()+1, this.getOnPos().getZ());
                }
                if(isCorruptedSacrificeableTarget){
                    CorruptedBloodySoulEntity CorruptedBloodySoulEntity = new CorruptedBloodySoulEntity(ModEntityTypes.CORRUPTED_BLOODY_SOUL_ENTITY.get(), this.level());
                    level().addFreshEntity(CorruptedBloodySoulEntity);
                    CorruptedBloodySoulEntity.setPos(this.getOnPos().getX(), this.getOnPos().getY()+1, this.getOnPos().getZ());
                }
            }

            this.discard();

        }

        lifeTicks--;


    }




    private void dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.target;
        if (livingEntity2 == null) {
            livingEntity.hurt(this.damageSources().magic(), this.damage);
        } else {
            if (livingEntity2.isAlliedTo(livingEntity)) return;
            livingEntity.hurt(this.damageSources().indirectMagic(this, livingEntity2), this.damage*2);

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
        if(compoundTag.contains("life_ticks")){
            this.entityData.set(LIFE_TICKS, compoundTag.getInt("life_ticks"));
        }
        if(compoundTag.contains("is_sacrificeable_target")){
            this.entityData.set(IS_SACRIFICEABLE_TARGET, compoundTag.getBoolean("is_sacrificeable_target"));
        }
        if(compoundTag.contains("is_corrupted_sacrificeable_target")){
            this.entityData.set(IS_CORRUPTED_SACRIFICEABLE_TARGET, compoundTag.getBoolean("is_corrupted_sacrificeable_target"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("emerge_phase", this.entityData.get(EMERGE_PHASE));
        compoundTag.putBoolean("retract_phase", this.entityData.get(RETRACT_PHASE));
        compoundTag.putBoolean("idle_phase", this.entityData.get(IDLE_PHASE));
        compoundTag.putInt("life_ticks", this.entityData.get(LIFE_TICKS));
        compoundTag.putBoolean("is_sacrificeable_target", this.entityData.get(IS_SACRIFICEABLE_TARGET));
        compoundTag.putBoolean("is_corrupted_sacrificeable_target", this.entityData.get(IS_CORRUPTED_SACRIFICEABLE_TARGET));


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
        ParticleHelper.spawnRing(level, ParticleTypes.FIREWORK, entity.position().add(0, 1.0, 0), radius, particleCount, speed);
    }
    public void spawnImpactParticles(Level level, Entity entity) {
        ParticleHelper.spawnExplosion(level, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                entity.position(), 30, 0.2, 1.5);
    }


    @Nullable
    @Override
    public Entity getOwner() {
        return null;
    }
}

