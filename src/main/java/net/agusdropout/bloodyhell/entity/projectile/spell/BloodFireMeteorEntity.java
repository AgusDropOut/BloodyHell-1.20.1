package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.custom.BloodFireBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BloodStainEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.entity.interfaces.IBloodFlammable;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ImpactParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class BloodFireMeteorEntity extends Projectile implements IBloodFlammable, IGemSpell {

    // --- FIELDS & DATA KEYS ---

    private static final EntityDataAccessor<Boolean> DATA_LAUNCHED = SynchedEntityData.defineId(BloodFireMeteorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(BloodFireMeteorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_GROW_TIME = SynchedEntityData.defineId(BloodFireMeteorEntity.class, EntityDataSerializers.INT);

    // Defaults
    private static final int DEFAULT_GROW_TIME = 40;
    private static final float DEFAULT_EXPLOSION_RADIUS = 4.0f;
    private static final int DEFAULT_TICKS_DELAY = 0;

    private float damage = 20.0f;
    private float speed = 1.5f;
    private float explosionRadius = DEFAULT_EXPLOSION_RADIUS;


    // --- CONSTRUCTORS & INIT ---

    public BloodFireMeteorEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    // Standard Constructor (Mobs)
    public BloodFireMeteorEntity(Level level, LivingEntity owner, float damage, float speed, float size) {
        this(ModEntityTypes.BLOOD_FIRE_METEOR_PROJECTILE.get(), level);
        this.setOwner(owner);
        this.damage = damage;
        this.speed = speed;

        // Initialize Synched Data
        this.entityData.set(DATA_SCALE, size);

        calculateSmartSpawnPosition(owner, level);
    }

    // Gem Constructor (Spellbook)
    public BloodFireMeteorEntity(Level level, LivingEntity owner, float damage, float speed, float size,int ticksDelay, List<Gem> gems) {
        this(ModEntityTypes.BLOOD_FIRE_METEOR_PROJECTILE.get(), level);
        this.setOwner(owner);
        this.damage = damage;
        this.speed = speed;


        this.setDataGrowTime(DEFAULT_GROW_TIME+ticksDelay);

        // Initialize Synched Data
        this.entityData.set(DATA_SCALE, size);
        configureSpell(gems);
    }

    private void calculateSmartSpawnPosition(LivingEntity owner, Level level) {
        Vec3 start = owner.getEyePosition();
        double targetHeight = 5.0;
        Vec3 end = start.add(0, targetHeight, 0);

        // Raycast upwards to find the ceiling
        BlockHitResult result = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));

        double spawnY;
        if (result.getType() == HitResult.Type.BLOCK) {
            spawnY = result.getLocation().y - 1.0;
        } else {
            spawnY = start.y + targetHeight;
        }

        // Safety check to prevent spawning inside the owner
        if (spawnY < owner.getEyeY()) {
            spawnY = owner.getEyeY() + 0.5;
        }

        this.setPos(owner.getX(), spawnY, owner.getZ());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_LAUNCHED, false);
        this.entityData.define(DATA_SCALE, 1.0f);
        this.entityData.define(DATA_GROW_TIME, DEFAULT_GROW_TIME);
    }

    // --- GETTERS ---

    public float getScale() { return this.entityData.get(DATA_SCALE); }
    public boolean isLaunched() { return this.entityData.get(DATA_LAUNCHED); }
    public int getGrowTime() { return this.entityData.get(DATA_GROW_TIME); }
    public void setDataGrowTime(int time) { this.entityData.set(DATA_GROW_TIME, time); }

    //  MAIN TICK LOOP ---

    @Override
    public void tick() {
        super.tick();

        boolean launched = isLaunched();
        float currentScale = getScale();
        int growTime = getGrowTime();

        // 1. Charging Phase
        if (!launched) {
            if (this.tickCount >= growTime) {
                if (!this.level().isClientSide) {
                    launch();
                }
            } else {
                // Pitch rises as it gets closer to launch
                if (this.tickCount % 5 == 0) {
                    float pitch = 1.0f + (tickCount / (float) growTime);
                    this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.HOSTILE, pitch, 0.5f);
                }
            }
        }
        // 2. Flight Phase
        else {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }
            Vec3 motion = this.getDeltaMovement();
            this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
        }

        // 3. Visuals
        if (this.level().isClientSide) {
            spawnParticles(currentScale);
        }

        // 4. Cleanup
        if (this.tickCount > 200 + growTime) discard();
    }

    // ---LOGIC & PHYSICS ---

    private void launch() {
        this.entityData.set(DATA_LAUNCHED, true);

        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3 dir;

            if (owner instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() != null) {
                // Robust AI Aiming
                LivingEntity target = mob.getTarget();
                Vec3 targetPos = target.getBoundingBox().getCenter();
                dir = targetPos.subtract(this.position()).normalize();
            } else {
                // Player / Default Aiming
                Vec3 look = owner.getLookAngle();
                Vec3 targetPos = owner.getEyePosition().add(look.scale(20.0));
                dir = targetPos.subtract(this.position()).normalize();
            }

            this.setDeltaMovement(dir.scale(this.speed));
            this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 2.0f, 0.5f);

        } else {
            // Dead Owner Fallback
            this.setDeltaMovement(new Vec3(0, -1, 0).normalize().scale(this.speed));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) return;

        float size = getScale();

        // 1. Effects
        EntityCameraShake.cameraShake(this.level(), this.position(), 20.0f * size, 1.5f, 15, 10);
        this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.0f, 0.6f);
        this.level().broadcastEntityEvent(this, (byte) 3);

        // 2. Interactions
        spawnFallingBlocks(size);
        spawnBloodStain(size);
        placeFire(size);
        damageArea(size);

        this.discard();
    }

    private void damageArea(float size) {
        // Area scales with size
        AABB area = this.getBoundingBox().inflate(explosionRadius * size);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity e : list) {
            if (e != this.getOwner()) {
                e.hurt(this.damageSources().explosion(this, this.getOwner()), this.damage);
                setOnBloodFire(e, 200, 0);
            }
        }
    }

    private void placeFire(float size) {
        BlockPos center = this.blockPosition();
        int radius = (int) size;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (random.nextBoolean()) {
                    BlockPos target = center.offset(x, 0, z);
                    if (this.level().getBlockState(target).isAir() && !this.level().getBlockState(target.below()).isAir()) {
                        this.level().setBlockAndUpdate(target, ModBlocks.BLOOD_FIRE.get().defaultBlockState());

                        BlockEntity be = this.level().getBlockEntity(target);
                        if (be instanceof BloodFireBlockEntity fireBe && this.getOwner() instanceof LivingEntity owner) {
                            fireBe.setOwner(owner);
                        }
                    }
                }
            }
        }
    }

    private void spawnBloodStain(float size) {
        BloodStainEntity stain = new BloodStainEntity(this.level(), getX(), getY(), getZ(), Direction.UP, size * 2.5f);
        this.level().addFreshEntity(stain);
        if (this.getOwner() instanceof LivingEntity owner) {
            stain.setOwner(owner);
        }
    }

    private void spawnFallingBlocks(float size) {
        int count = (int)(12 * size);
        BlockPos below = this.blockPosition().below();
        BlockState state = this.level().getBlockState(below);

        if(state.getRenderShape() != RenderShape.INVISIBLE) {
            for(int i=0; i<count; i++) {
                double offsetX = (random.nextDouble() - 0.5) * size * 3.0;
                double offsetZ = (random.nextDouble() - 0.5) * size * 3.0;

                EntityFallingBlock falling = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), this.level(), 40, state);
                falling.setPos(this.getX() + offsetX, this.getY() + 1, this.getZ() + offsetZ);

                falling.setDeltaMovement(
                        (random.nextDouble()-0.5)*1.2,
                        0.6 + random.nextDouble()*0.8,
                        (random.nextDouble()-0.5)*1.2
                );

                this.level().addFreshEntity(falling);
            }
        }
    }

    // --- CLIENT VISUALS ---

    private void spawnParticles(float size) {
        if (!isLaunched()) return;

        Vec3 motion = this.getDeltaMovement().normalize().scale(-1);

        Vector3f white = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f brightRed = new Vector3f(1.0f, 0.1f, 0.0f);

        ParticleHelper.spawnSphereGradient(level(), position().add(0, size * 0.5, 0), size * 1.5, 25, motion.scale(0.1), (ratio) -> {
            Vector3f color = ParticleHelper.gradient3(ratio, white, brightRed, brightRed);
            float pSize = (size * 0.4f) + (ratio * size * 0.5f);
            int life = 10 + (int)(ratio * 20);
            return new MagicParticleOptions(color, pSize, false, life);
        });

        ParticleHelper.spawnSphereGradient(level(), position().add(0, size * 0.5, 0), size * 0.8, 5, motion.scale(0.2), (r) ->
                ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get()
        );
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            spawnImpactParticles();
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnImpactParticles() {
        float size = getScale();
        Vec3 pos = position();

        ParticleHelper.spawn(level(),
                ImpactParticleOptions.create(255, 50, 0, size * 5.0f, 40, false, 0.2f),
                pos.x, pos.y + 0.1, pos.z,
                0, 0, 0
        );

        for(int i = 0; i < 15; i++) {
            double r = random.nextDouble() * size * 2.0;
            double a = random.nextDouble() * Math.PI * 2;
            double x = pos.x + Math.cos(a) * r;
            double z = pos.z + Math.sin(a) * r;
            double vy = 0.2 + random.nextDouble() * 0.3;

            ParticleHelper.spawn(level(),
                    new MagicFloorParticleOptions(new Vector3f(0.8f, 0, 0), 0.5f, false, 40),
                    x, pos.y + 0.1, z,
                    0, vy, 0
            );
        }

        Vector3f color = random.nextBoolean() ? new Vector3f(0.8f, 0f, 0f) : new Vector3f(0.3f, 0f, 0f);
        ParticleHelper.spawnHollowSphere(level(),
                new MagicParticleOptions(color, 0.8f, false, 50),
                pos.add(0, 0.5, 0),
                size * 3.0,
                30,
                0.1
        );
    }

    // --- IGemSpell IMPLEMENTATION ---

    @Override
    public void increaseSpellDamage(double amount) {
        this.damage += (float) amount;
    }

    @Override
    public void increaseSpellSize(double amount) {
        float current = this.entityData.get(DATA_SCALE);
        this.entityData.set(DATA_SCALE, current + (float)amount);
    }

    @Override
    public void increaseSpellDuration(int amount) {
        int current = this.entityData.get(DATA_GROW_TIME);
        this.entityData.set(DATA_GROW_TIME, current + amount);
    }

    // ---  BOILERPLATE ---

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if(tag.contains("Scale")) this.entityData.set(DATA_SCALE, tag.getFloat("Scale"));
        if(tag.contains("GrowTime")) this.entityData.set(DATA_GROW_TIME, tag.getInt("GrowTime"));
        if(tag.contains("Launched")) this.entityData.set(DATA_LAUNCHED, tag.getBoolean("Launched"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Damage", damage);
        tag.putFloat("Scale", getScale());
        tag.putInt("GrowTime", getGrowTime());
        tag.putBoolean("Launched", isLaunched());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public Level getLevel() {
        return this.level();
    }
}