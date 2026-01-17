package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BloodSlashDecalEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.particle.ModParticles;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BloodSlashEntity extends Projectile {

    private int lifeTime = 0;
    private static final int MAX_LIFE = 20;
    private float damage = 10.0f;
    private final List<UUID> hitEntities = new ArrayList<>();
    private int decalCooldown = 0;

    private static final EntityDataAccessor<Float> SYNCED_YAW = SynchedEntityData.defineId(BloodSlashEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SYNCED_PITCH = SynchedEntityData.defineId(BloodSlashEntity.class, EntityDataSerializers.FLOAT);

    public BloodSlashEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public BloodSlashEntity(Level level, double x, double y, double z, float damage, LivingEntity owner, float yaw, float pitch) {
        this(ModEntityTypes.BLOOD_SLASH_ENTITY.get(), level);
        this.setOwner(owner);
        this.damage = damage;
        this.setPos(x, y, z);

        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;

        this.entityData.set(SYNCED_YAW, yaw);
        this.entityData.set(SYNCED_PITCH, pitch);

        // Initial Shake on Spawn (Optional)
        if (!level.isClientSide) {
            // Radius 10, Magnitude 0.5, Duration 5 ticks, Fade 10 ticks
            EntityCameraShake.cameraShake(level, this.position(), 5.0f, 0.1f, 5, 10);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SYNCED_YAW, 0f);
        this.entityData.define(SYNCED_PITCH, 0f);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.lifeTime++ >= MAX_LIFE) {
            this.discard();
            return;
        }

        if (this.level().isClientSide) {
            this.setYRot(this.entityData.get(SYNCED_YAW));
            this.setXRot(this.entityData.get(SYNCED_PITCH));
        }

        Vec3 oldPos = this.position();

        // 1. Calculate Velocity & Move
        Vec3 moveVector = calculateMoveVector();
        this.setDeltaMovement(moveVector);

        // 2. Wall Collisions (Raytrace center point)
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) hitResult);
            this.discard();
            return;
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            // Handled by area check, but good for direct hits
            this.onHit(hitResult);
        }

        this.setPos(oldPos.x + moveVector.x, oldPos.y + moveVector.y, oldPos.z + moveVector.z);

        // 3. Logic & Effects
        spawnGroundInteractionEffects();

        if (this.level().isClientSide) {
            spawnTrailParticles(oldPos, this.position());
        } else {
            // Server Side: Check for entities in the WIDE path
            checkAreaCollisions();
        }
    }

    private void checkAreaCollisions() {
        // LARGE HITBOX: Inflate 3.0 blocks horizontally to catch enemies on the sides of the slash
        AABB scanArea = this.getBoundingBox().inflate(3.0, 1.0, 3.0);

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, scanArea);

        for (LivingEntity target : targets) {
            if (target == this.getOwner()) continue;
            if (!target.isAlive() || !target.isPickable()) continue;
            if (hitEntities.contains(target.getUUID())) continue;

            boolean hurt;
            if (this.getOwner() instanceof LivingEntity owner) {
                hurt = target.hurt(this.level().damageSources().mobAttack(owner), this.damage);
            } else {
                hurt = target.hurt(this.level().damageSources().magic(), this.damage);
            }

            if (hurt) {
                hitEntities.add(target.getUUID());

                // Camera Shake on Impact
                if (!this.level().isClientSide) {
                    EntityCameraShake.cameraShake(this.level(), target.position(), 15.0f, 1.0f, 2, 5);
                }

                // Knockback
                float yaw = this.getYRot();
                target.knockback(0.8F, Mth.sin(yaw * ((float) Math.PI / 180F)), -Mth.cos(yaw * ((float) Math.PI / 180F)));
            }
        }
    }

    private Vec3 calculateMoveVector() {
        float speed = 1.2f;
        float radianYaw = (float) Math.toRadians(this.getYRot());
        float radianPitch = (float) Math.toRadians(this.getXRot());

        double offsetX = -Math.sin(radianYaw) * Math.cos(radianPitch) * speed;
        double offsetZ = Math.cos(radianYaw) * Math.cos(radianPitch) * speed;
        double offsetY = -Math.sin(radianPitch) * speed;

        return new Vec3(offsetX, offsetY, offsetZ);
    }

    // Keep unused for structure compliance
    private void moveEntityForward() {}

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level().isClientSide) {
            Direction face = blockHitResult.getDirection();
            BlockPos pos = blockHitResult.getBlockPos();

            // Camera Shake on Wall Hit
            EntityCameraShake.cameraShake(this.level(), this.position(), 12.0f, 0.8f, 5, 10);

            double x = blockHitResult.getLocation().x + (face.getStepX() * 0.05);
            double y = blockHitResult.getLocation().y + (face.getStepY() * 0.05);
            double z = blockHitResult.getLocation().z + (face.getStepZ() * 0.05);

            BloodSlashDecalEntity decal = new BloodSlashDecalEntity(
                    this.level(), x, y, z, this.getYRot(), face
            );
            this.level().addFreshEntity(decal);
        }
    }

    private void spawnGroundInteractionEffects() {
        BlockPos center = this.blockPosition();
        BlockPos groundPos = null;
        BlockState groundState = null;

        // Scan downwards to find the "True Floor"
        // We scan 3 blocks down to handle tall grass + terrain changes
        for (int i = 0; i <= 3; i++) {
            BlockPos p = center.below(i);
            BlockState s = this.level().getBlockState(p);

            if (s.isAir()) continue;

            // CHECK: Is this block "Pass-through" (Grass, Flower, Tall Grass)?
            // We assume blocks with empty collision shapes are vegetation to be cut.
            if (s.getCollisionShape(this.level(), p).isEmpty()) {
                // If it's vegetation, DESTROY IT (Server Side Only)
                // This prevents the decal from spawning on it and adds gameplay impact
                if (!this.level().isClientSide) {
                    // true = drop items, false = just vanish
                    this.level().destroyBlock(p, true, this);
                }
                // Continue the loop to find what was UNDER the grass
                continue;
            }

            // CHECK: Is this a solid renderable block?
            // If we are here, it has collision (Dirt, Stone, Wood)
            if (s.getRenderShape() != RenderShape.INVISIBLE) {
                groundPos = p;
                groundState = s;
                break; // Found the floor, stop scanning
            }
        }

        // If we found a valid solid floor...
        if (groundPos != null && groundState != null) {

            // 1. CLIENT SIDE: Debris Particles (Dirt/Stone dust)
            if (this.level().isClientSide) {
                float yaw = this.getYRot();
                double wingX = Math.cos(Math.toRadians(yaw)) * 1.0;
                double wingZ = Math.sin(Math.toRadians(yaw)) * 1.0;

                int particleCount = 2;
                for (int i = 0; i < particleCount; i++) {
                    double offsetMult = (random.nextDouble() - 0.5) * 2.0;
                    double pX = this.getX() + (wingX * offsetMult);
                    double pZ = this.getZ() + (wingZ * offsetMult);

                    // Spawn particle at the floor height (groundPos.getY() + 1)
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState),
                            pX, groundPos.getY() + 1.1, pZ,
                            (random.nextDouble() - 0.5) * 0.1,
                            0.2,
                            (random.nextDouble() - 0.5) * 0.1);
                }
            }

            // 2. SERVER SIDE: Decal Entity (Continuous Trail)
            if (!this.level().isClientSide) {
                // Falling Block Debris (Chance based)
                if (random.nextFloat() < 0.2f) {
                    spawnFallingBlockDebris(groundPos, groundState);
                }

                if (decalCooldown <= 0) {
                    BloodSlashDecalEntity decal = new BloodSlashDecalEntity(
                            this.level(),
                            this.getX(),
                            groundPos.getY() + 1.02, // Spawn ON TOP of the solid block
                            this.getZ(),
                            this.getYRot(),
                            Direction.UP
                    );
                    this.level().addFreshEntity(decal);
                    decalCooldown = 1;
                }
                decalCooldown--;
            }
        }
    }

    private void spawnFallingBlockDebris(BlockPos pos, BlockState state) {
        // Random offset near the slash
        double x = this.getX() + (random.nextDouble() - 0.5) * 1.5;
        double z = this.getZ() + (random.nextDouble() - 0.5) * 1.5;
        double y = pos.getY() + 1.1;

        // Create the entity
        EntityFallingBlock fallingBlock = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), this.level(), state, 0.4f);
        fallingBlock.setPos(x, y, z);

        // Fling it away from the slash center
        float velocityX = (float) ((random.nextFloat() - 0.5f) * 0.3f);
        float velocityY = 0.4f + random.nextFloat() * 0.2f;
        float velocityZ = (float) ((random.nextFloat() - 0.5f) * 0.3f);

        fallingBlock.setDeltaMovement(velocityX, velocityY, velocityZ);

        this.level().addFreshEntity(fallingBlock);
    }

    // Standard Particle/Data methods...
    private void spawnTrailParticles(Vec3 start, Vec3 end) {
        int steps = 4;
        float yaw = this.getYRot();
        double wingX = Math.cos(Math.toRadians(yaw)) * 1.2;
        double wingZ = Math.sin(Math.toRadians(yaw)) * 1.2;
        for (int i = 0; i < steps; i++) {
            double progress = i / (double) steps;
            double x = Mth.lerp(progress, start.x, end.x);
            double y = Mth.lerp(progress, start.y, end.y);
            double z = Mth.lerp(progress, start.z, end.z);
            double lowY = y + 0.1;
            this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), x + wingX, lowY, z + wingZ, 0, 0, 0);
            this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), x - wingX, lowY, z - wingZ, 0, 0, 0);
            if (random.nextFloat() < 0.2f) {
                this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), x, lowY, z, 0, 0, 0);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.lifeTime = tag.getInt("LifeTime");
        this.entityData.set(SYNCED_YAW, tag.getFloat("SyncedYaw"));
        this.entityData.set(SYNCED_PITCH, tag.getFloat("SyncedPitch"));
        this.setYRot(this.entityData.get(SYNCED_YAW));
        this.setXRot(this.entityData.get(SYNCED_PITCH));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeTime", this.lifeTime);
        tag.putFloat("SyncedYaw", this.entityData.get(SYNCED_YAW));
        tag.putFloat("SyncedPitch", this.entityData.get(SYNCED_PITCH));
    }

    public float getYawSynced() {
        return this.entityData.get(SYNCED_YAW);
    }

    public float getPitchSynced() {
        return this.entityData.get(SYNCED_PITCH);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}