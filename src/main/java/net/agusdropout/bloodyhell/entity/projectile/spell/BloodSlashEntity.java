package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BloodSlashDecalEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BloodSlashEntity extends Projectile implements IGemSpell {

    // --- FIELDS & DATA KEYS ---

    private static final EntityDataAccessor<Float> SYNCED_YAW = SynchedEntityData.defineId(BloodSlashEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SYNCED_PITCH = SynchedEntityData.defineId(BloodSlashEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(BloodSlashEntity.class, EntityDataSerializers.FLOAT);

    private static final int DEFAULT_MAX_LIFE = 20;
    private static final float DEFAULT_SCALE = 1.0f;

    private int lifeTime = 0;
    private int maxLife = DEFAULT_MAX_LIFE;
    private float damage = 10.0f;
    private final List<UUID> hitEntities = new ArrayList<>();
    private int decalCooldown = 0;

    // --- CONSTRUCTORS ---

    public BloodSlashEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noPhysics = false;
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

        if (!level.isClientSide) {
            EntityCameraShake.cameraShake(level, this.position(), 5.0f, 0.1f, 5, 10);
        }
    }

    public BloodSlashEntity(Level level, double x, double y, double z, float damage, LivingEntity owner, float yaw, float pitch, List<Gem> gems) {
        this(level, x, y, z, damage, owner, yaw, pitch);
        configureSpell(gems);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SYNCED_YAW, 0f);
        this.entityData.define(SYNCED_PITCH, 0f);
        this.entityData.define(DATA_SCALE, DEFAULT_SCALE);
    }

    public float getScale() {
        return this.entityData.get(DATA_SCALE);
    }

    // --- MAIN TICK LOOP ---

    @Override
    public void tick() {
        super.tick();

        if (this.lifeTime++ >= maxLife) {
            this.discard();
            return;
        }

        if (this.level().isClientSide) {
            this.setYRot(this.entityData.get(SYNCED_YAW));
            this.setXRot(this.entityData.get(SYNCED_PITCH));
        }

        Vec3 oldPos = this.position();
        Vec3 moveVector = calculateMoveVector();
        this.setDeltaMovement(moveVector);

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) hitResult);
            this.discard();
            return;
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.onHit(hitResult);
        }

        this.setPos(oldPos.x + moveVector.x, oldPos.y + moveVector.y, oldPos.z + moveVector.z);

        spawnGroundInteractionEffects();

        if (this.level().isClientSide) {
            spawnTrailParticles(oldPos, this.position());
        } else {
            checkAreaCollisions();
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

    // --- COLLISION & LOGIC ---

    private void checkAreaCollisions() {
        double width = 3.0 * getScale();
        AABB scanArea = this.getBoundingBox().inflate(width, 1.0, width);

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

                if (!this.level().isClientSide) {
                    EntityCameraShake.cameraShake(this.level(), target.position(), 15.0f, 1.0f, 2, 5);
                }

                float yaw = this.getYRot();
                target.knockback(0.8F, Mth.sin(yaw * ((float) Math.PI / 180F)), -Mth.cos(yaw * ((float) Math.PI / 180F)));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level().isClientSide) {
            Direction face = blockHitResult.getDirection();
            EntityCameraShake.cameraShake(this.level(), this.position(), 12.0f, 0.8f, 5, 10);

            double x = blockHitResult.getLocation().x + (face.getStepX() * 0.05);
            double y = blockHitResult.getLocation().y + (face.getStepY() * 0.05);
            double z = blockHitResult.getLocation().z + (face.getStepZ() * 0.05);

            BloodSlashDecalEntity decal = new BloodSlashDecalEntity(this.level(), x, y, z, this.getYRot(), face);

            // Pass Stats to Decal
            decal.setSize(getScale());
            decal.setDuration(this.maxLife * 10);

            this.level().addFreshEntity(decal);

            if (this.getOwner() instanceof LivingEntity owner) {
                decal.setOwner(owner);
            }
        }
    }

    // --- VISUALS ---

    private void spawnGroundInteractionEffects() {
        BlockPos center = this.blockPosition();
        BlockPos groundPos = null;
        BlockState groundState = null;

        for (int i = 0; i <= 3; i++) {
            BlockPos p = center.below(i);
            BlockState s = this.level().getBlockState(p);

            if (s.isAir()) continue;

            if (s.getCollisionShape(this.level(), p).isEmpty()) {
                if (!this.level().isClientSide) {
                    this.level().destroyBlock(p, true, this);
                }
                continue;
            }

            if (s.getRenderShape() != RenderShape.INVISIBLE) {
                groundPos = p;
                groundState = s;
                break;
            }
        }

        if (groundPos != null && groundState != null) {
            if (this.level().isClientSide) {
                float yaw = this.getYRot();
                double wingX = Math.cos(Math.toRadians(yaw)) * 1.0 * getScale();
                double wingZ = Math.sin(Math.toRadians(yaw)) * 1.0 * getScale();

                for (int i = 0; i < 2; i++) {
                    double offsetMult = (random.nextDouble() - 0.5) * 2.0;
                    double pX = this.getX() + (wingX * offsetMult);
                    double pZ = this.getZ() + (wingZ * offsetMult);

                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState),
                            pX, groundPos.getY() + 1.1, pZ,
                            (random.nextDouble() - 0.5) * 0.1, 0.2, (random.nextDouble() - 0.5) * 0.1);
                }
            }

            if (!this.level().isClientSide) {
                if (random.nextFloat() < 0.2f) {
                    spawnFallingBlockDebris(groundPos, groundState);
                }

                if (decalCooldown <= 0) {
                    BloodSlashDecalEntity decal = new BloodSlashDecalEntity(
                            this.level(), this.getX(), groundPos.getY() + 1.02, this.getZ(), this.getYRot(), Direction.UP
                    );
                    decal.setSize(getScale());
                    decal.setDuration(this.maxLife * 10);

                    this.level().addFreshEntity(decal);
                    decalCooldown = 1;
                }
                decalCooldown--;
            }
        }
    }

    private void spawnFallingBlockDebris(BlockPos pos, BlockState state) {
        double x = this.getX() + (random.nextDouble() - 0.5) * 1.5;
        double z = this.getZ() + (random.nextDouble() - 0.5) * 1.5;
        double y = pos.getY() + 1.1;

        EntityFallingBlock fallingBlock = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), this.level(), state, 0.4f);
        fallingBlock.setPos(x, y, z);

        float velocityX = (float) ((random.nextFloat() - 0.5f) * 0.3f);
        float velocityY = 0.4f + random.nextFloat() * 0.2f;
        float velocityZ = (float) ((random.nextFloat() - 0.5f) * 0.3f);

        fallingBlock.setDeltaMovement(velocityX, velocityY, velocityZ);
        this.level().addFreshEntity(fallingBlock);
    }

    private void spawnTrailParticles(Vec3 start, Vec3 end) {
        int steps = 4;
        float yaw = this.getYRot();
        double wingX = Math.cos(Math.toRadians(yaw)) * 1.2 * getScale();
        double wingZ = Math.sin(Math.toRadians(yaw)) * 1.2 * getScale();

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

    // --- INTERFACE & SYNC ---

    @Override
    public void increaseSpellDamage(double amount) {
        this.damage += (float) amount;
    }

    @Override
    public void increaseSpellSize(double amount) {
        // Percentage increase (e.g., 0.5 = +50%)
        float current = this.entityData.get(DATA_SCALE);
        this.entityData.set(DATA_SCALE, current + (DEFAULT_SCALE * (float)amount));
    }

    @Override
    public void increaseSpellDuration(int amount) {
        // Amount is seconds
        this.maxLife += amount;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.lifeTime = tag.getInt("LifeTime");
        this.maxLife = tag.getInt("MaxLife");
        this.entityData.set(SYNCED_YAW, tag.getFloat("SyncedYaw"));
        this.entityData.set(SYNCED_PITCH, tag.getFloat("SyncedPitch"));
        if(tag.contains("Scale")) this.entityData.set(DATA_SCALE, tag.getFloat("Scale"));

        this.setYRot(this.entityData.get(SYNCED_YAW));
        this.setXRot(this.entityData.get(SYNCED_PITCH));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeTime", this.lifeTime);
        tag.putInt("MaxLife", this.maxLife);
        tag.putFloat("SyncedYaw", this.entityData.get(SYNCED_YAW));
        tag.putFloat("SyncedPitch", this.entityData.get(SYNCED_PITCH));
        tag.putFloat("Scale", getScale());
    }

    public float getYawSynced() { return this.entityData.get(SYNCED_YAW); }
    public float getPitchSynced() { return this.entityData.get(SYNCED_PITCH); }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}