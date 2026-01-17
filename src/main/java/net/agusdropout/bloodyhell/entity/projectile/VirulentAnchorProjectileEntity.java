package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;

import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;


public class VirulentAnchorProjectileEntity extends Projectile implements GeoEntity {
    private int lifeTicks = 120;
    private float damage;
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(VirulentAnchorProjectileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(VirulentAnchorProjectileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> HAS_COLLIDED = SynchedEntityData.defineId(VirulentAnchorProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    private LivingEntity attachedEntity = null;
    private boolean isEmerging = false;
    private float yaw;
    private float pitch;

    public VirulentAnchorProjectileEntity(EntityType<? extends VirulentAnchorProjectileEntity> type, Level level) {
        super(type, level);
    }

    public VirulentAnchorProjectileEntity(Level level, double x, double y, double z, float damage, LivingEntity owner, float yaw, float pitch) {
        this(ModEntityTypes.VIRULENT_ANCHOR_PROJECTILE.get(), level);
        this.damage = damage;
        this.setOwner(owner);
        this.setPos(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
        float speed = 1.3f;
        float xMotion = -Mth.sin(yaw * (float) Math.PI / 180F) * Mth.cos(pitch * (float) Math.PI / 180F);
        float yMotion = -Mth.sin(pitch * (float) Math.PI / 180F);
        float zMotion = Mth.cos(yaw * (float) Math.PI / 180F) * Mth.cos(pitch * (float) Math.PI / 180F);
        this.setDeltaMovement(new Vec3(xMotion * speed, yMotion * speed, zMotion * speed));
    }

    private void initializeRotation() {
        Vec3 motion = this.getDeltaMovement();
        float yaw = (float) Math.toDegrees(Math.atan2(motion.x, motion.z));
        float pitch = (float) Math.toDegrees(Math.atan2(motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z)));
        pitch -= 40;
        yaw += 90;

        this.setYRot(yaw);
        this.setXRot(pitch);
    }

    @Override
    public void tick() {


        if(this.lifeTicks < 100) {
            if(lifeTicks == 60){
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARROW_SHOOT, this.getSoundSource(), 1.0F, 1.0F, false);
            }
            this.isEmerging = false;
            if (attachedEntity != null) {
                if (!attachedEntity.isAlive()) {
                    this.discard(); // Si la entidad muere, el proyectil desaparece
                    return;
                }
                this.setPos(attachedEntity.getX(), attachedEntity.getY() + attachedEntity.getBbHeight() / 2, attachedEntity.getZ());
            }

            if (!this.hasCollided()) {
                this.move(MoverType.SELF, this.getDeltaMovement());
                checkCollisions();
                spawnFlyingParticles();
            } else {
                this.setDeltaMovement(Vec3.ZERO);
                spawnImpactParticlesSphere();
                applyAreaDamage();
            }

            if (this.lifeTicks == 0) {
                this.discard();
            }

            initializeRotation();
        }else {
            this.setYRot(yaw);
            this.setXRot(pitch);
            this.isEmerging = true;
        }
        this.lifeTicks--;
    }

    private void checkCollisions() {
        if (!this.level().isClientSide) {
            AABB boundingBox = this.getBoundingBox().inflate(0.3);
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, boundingBox,
                    e -> e != this.getOwner() && e.isAlive());

            for (LivingEntity entity : entities) {
                attachToEntity(entity); // Se adhiere a la entidad impactada
                return;
            }
        }

        if (!level().noCollision(this, getBoundingBox().inflate(0.05))) {
            this.setHasCollided(true);
            this.setXRot(-90);
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    private void attachToEntity(LivingEntity entity) {
        this.attachedEntity = entity;
        this.setHasCollided(true);
        this.setDeltaMovement(Vec3.ZERO);
    }

    private void applyAreaDamage() {
        if (!this.level().isClientSide) {
            AABB area = this.getBoundingBox().inflate(3.5);
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, area,
                    e -> e != this.getOwner() && e.isAlive());

            for (LivingEntity entity : entities) {
                entity.hurt(this.damageSources().generic(), this.damage);
            }
        }
    }

    private void spawnImpactParticlesSphere() {
        if (this.level().isClientSide) {
            for (int i = 0; i < 100; i++) {
                double theta = this.random.nextDouble() * 2 * Math.PI;
                double phi = this.random.nextDouble() * Math.PI;
                double r = 3 + this.random.nextDouble();

                double xOffset = r * Math.sin(phi) * Math.cos(theta);
                double yOffset = r * Math.cos(phi);
                double zOffset = r * Math.sin(phi) * Math.sin(theta);

                this.level().addParticle(ModParticles.VICERAL_PARTICLE.get(),
                        this.getX() + xOffset,
                        this.getY() + yOffset,
                        this.getZ() + zOffset,
                        0, 0, 0);
            }
        }
    }

    private void spawnFlyingParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 0.1;
                double offsetY = (this.random.nextDouble() - 0.5) * 0.1 + 0.02;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.1;

                double speed = 1 + this.random.nextDouble() * 0.05;
                double vx = (this.random.nextDouble() - 0.5) * speed;
                double vy = (this.random.nextDouble() - 0.5) * speed;
                double vz = (this.random.nextDouble() - 0.5) * speed;

                serverLevel.sendParticles(ModParticles.VICERAL_PARTICLE.get(),
                        this.getX() + offsetX,
                        this.getY() + offsetY + 1,
                        this.getZ() + offsetZ, 1,
                        vx, vy, vz, 0.1);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(YAW, 0F);
        this.entityData.define(PITCH, 0F);
        this.entityData.define(HAS_COLLIDED, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller",
                0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState animationState) {
        if (this.isEmerging) {
            animationState.getController().setAnimation(RawAnimation.begin().then("forming", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("flying", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    public void setHasCollided(boolean collided) {
        this.entityData.set(HAS_COLLIDED, collided);
    }

    public boolean hasCollided() {
        return this.entityData.get(HAS_COLLIDED);
    }
}
