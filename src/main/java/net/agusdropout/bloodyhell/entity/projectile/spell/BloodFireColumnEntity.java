package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.effects.BloodStainEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.interfaces.IBloodFlammable;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ImpactParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class BloodFireColumnEntity extends Projectile implements IBloodFlammable, IGemSpell {

    // --- FIELDS & DATA KEYS ---

    private static final EntityDataAccessor<Boolean> ERUPTED = SynchedEntityData.defineId(BloodFireColumnEntity.class, EntityDataSerializers.BOOLEAN);
    // Synced Stats for Visuals
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(BloodFireColumnEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT = SynchedEntityData.defineId(BloodFireColumnEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_CHARGE_TIME = SynchedEntityData.defineId(BloodFireColumnEntity.class, EntityDataSerializers.INT);

    // Defaults
    private static final float DEFAULT_RADIUS = 1.5f;
    private static final float DEFAULT_HEIGHT = 7.0f;
    private static final int DEFAULT_CHARGE_TIME = 40;

    // Server-Only Stats
    private static final int LINGER_TIME = 30;
    private float damage = 16.0f; // Base explosion damage

    // --- CONSTRUCTORS ---

    public BloodFireColumnEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    // Standard Constructor (Mobs)
    public BloodFireColumnEntity(EntityType<? extends Projectile> type, Level level, LivingEntity owner, double x, double y, double z) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(x, y, z);
    }

    // Gem Constructor (Spellbook)
    public BloodFireColumnEntity(EntityType<? extends Projectile> type, Level level, LivingEntity owner, double x, double y, double z, List<Gem> gems) {
        this(type, level, owner, x, y, z);
        configureSpell(gems);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ERUPTED, false);
        this.entityData.define(DATA_RADIUS, DEFAULT_RADIUS);
        this.entityData.define(DATA_HEIGHT, DEFAULT_HEIGHT);
        this.entityData.define(DATA_CHARGE_TIME, DEFAULT_CHARGE_TIME);
    }

    // ---GETTERS ---

    public float getRadius() { return this.entityData.get(DATA_RADIUS); }
    public float getHeight() { return this.entityData.get(DATA_HEIGHT); }
    public int getChargeTime() { return this.entityData.get(DATA_CHARGE_TIME); }
    public boolean hasErupted() { return this.entityData.get(ERUPTED); }

    // --- MAIN TICK LOOP ---

    @Override
    public void tick() {
        super.tick();

        int chargeTime = getChargeTime();

        // 1. CHARGING PHASE
        if (this.tickCount < chargeTime) {
            if (!this.level().isClientSide) {
                if (this.tickCount == 1) {
                    // Shake duration matches charge time
                    EntityCameraShake.cameraShake(this.level(), this.position(), getRadius() * 6.0f, 0.1f, chargeTime, 3);
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.HOSTILE, 2.0f, 0.5f);
                }
                if (this.tickCount % 5 == 0) {
                    damageArea(2.0f); // Ticking damage while charging
                }
            }
            if (this.level().isClientSide) {
                spawnChargingParticles(chargeTime);
            }
        }
        // 2. ERUPTION PHASE
        else if (this.tickCount == chargeTime) {
            if (!this.level().isClientSide) {
                this.entityData.set(ERUPTED, true);
                performEruptionLogic();
            } else {
                // Client-side instant impact visual
                spawnImpactVisuals();
            }
        }
        // 3. LINGERING VISUALS
        else if (this.tickCount < chargeTime + LINGER_TIME) {
            if (this.level().isClientSide) {
                spawnEruptionParticles();
            }
        }
        // 4. CLEANUP
        else {
            if (!this.level().isClientSide) {
                this.discard();
            }
        }
    }

    // --- LOGIC ---

    private void damageArea(float tickDamage) {
        float r = getRadius();
        AABB area = this.getBoundingBox().inflate(r, 1.0, r);

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this.getOwner() && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().magic(), tickDamage);
        }
    }

    private void performEruptionLogic() {
        float r = getRadius();
        float h = getHeight();

        // Massive Shake
        EntityCameraShake.cameraShake(this.level(), this.position(), r * 10.0f, 2f, 5, 12);

        // Sounds
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.5f, 0.8f);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.5f, 0.5f);

        // Explosion Logic
        AABB area = this.getBoundingBox().inflate(r, h, r);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this.getOwner() && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().explosion(this, this.getOwner()), this.damage);
            target.setDeltaMovement(target.getDeltaMovement().add(0, 1.2, 0)); // Launch
            target.hurtMarked = true;
            setOnBloodFire(target, 200, 0);
        }

        // Stain
        BloodStainEntity stain = new BloodStainEntity(this.level(), this.getX(), this.getY(), this.getZ(), Direction.UP, r * 2.0f);
        if (this.getOwner() instanceof LivingEntity owner) {
            stain.setOwner(owner);
        }
        this.level().addFreshEntity(stain);
    }

    // ---VISUALS ---

    private void spawnImpactVisuals() {
        float r = getRadius();
        this.level().addParticle(
                ImpactParticleOptions.create(255, 0, 0, r * 2.0f, 30, false, 0.05f),
                this.getX(), this.getY() + 0.05, this.getZ(), 0, 0, 0
        );
    }

    private void spawnChargingParticles(int maxChargeTime) {
        float progress = (float) this.tickCount / (float) maxChargeTime;
        float r = getRadius();

        // 1. FLOOR MAGIC (Accelerating)
        if (this.random.nextFloat() < 0.5f + (progress * 0.5f)) {
            double dist = this.random.nextDouble() * r;
            double angle = this.random.nextDouble() * Math.PI * 2;
            double x = this.getX() + Math.cos(angle) * dist;
            double z = this.getZ() + Math.sin(angle) * dist;

            double risingSpeed = 0.05 + (progress * 0.4);

            this.level().addParticle(new MagicFloorParticleOptions(
                            new Vector3f(0.8f, 0.0f, 0.0f), 0.4f, false, 20),
                    x, this.getY() + 0.05, z,
                    0, risingSpeed, 0);
        }

        // 2. BLOCK CRUMBS (Massive Increase)
        int debrisCount = 1 + (int)(progress * 4);
        BlockPos belowPos = this.blockPosition().below();
        BlockState state = this.level().getBlockState(belowPos);

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < debrisCount; i++) {
                double dist = this.random.nextDouble() * (r * 1.5);
                double angle = this.random.nextDouble() * Math.PI * 2;

                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        this.getX() + Math.cos(angle) * dist,
                        this.getY() + 0.1,
                        this.getZ() + Math.sin(angle) * dist,
                        0.0,
                        0.1 + (progress * 0.3),
                        0.0);
            }
        }

        // 3. Pulse Ring
        if (this.tickCount % 5 == 0) {
            double ringR = r * (1.0f - progress);
            ParticleHelper.spawnRing(level(), ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    position().add(0, 0.1, 0), ringR, 8, 0);
        }
    }

    private void spawnEruptionParticles() {
        Vec3 pos = position();
        float r = getRadius();
        float h = getHeight();

        // COLORS
        Vector3f core = new Vector3f(1.0f, 0.9f, 0.9f);
        Vector3f mid = new Vector3f(1.0f, 0.1f, 0.0f);

        // GRADIENT PILLAR
        ParticleHelper.spawnCylinderGradient(level(), pos, r * 1.2, h, 30, 0.4, (ratio) -> {
            Vector3f color = ParticleHelper.gradient3(ratio, core, mid, mid);
            float pSize = 0.5f + (ratio * 0.8f);
            return new MagicParticleOptions(color, pSize, false, 40);
        });

        // IMPACT RING
        if (this.tickCount == getChargeTime()) {
            ParticleHelper.spawnRing(level(), ImpactParticleOptions.create(255, 50, 0, 4.0f, 40, false, 0.2f),
                    pos.add(0, 0.1, 0), r * 2.0, 40, 0);
        }
    }

    // --- IGemSpell IMPLEMENTATION ---

    @Override
    public void increaseSpellDamage(double amount) {
        this.damage += (float) amount;
    }

    @Override
    public void increaseSpellSize(double amount) {
        // Increases both Radius and Height for a larger explosion
        float currentR = this.entityData.get(DATA_RADIUS);
        float currentH = this.entityData.get(DATA_HEIGHT);

        this.entityData.set(DATA_RADIUS, currentR + (DEFAULT_RADIUS * (float)amount));
        this.entityData.set(DATA_HEIGHT, currentH + (DEFAULT_HEIGHT * (float)amount));
    }

    @Override
    public void increaseSpellDuration(int amount) {
        int currentCharge = this.entityData.get(DATA_CHARGE_TIME);
        this.entityData.set(DATA_CHARGE_TIME, currentCharge + amount);
    }

    // --- BOILERPLATE ---

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains("Radius")) this.entityData.set(DATA_RADIUS, tag.getFloat("Radius"));
        if(tag.contains("Height")) this.entityData.set(DATA_HEIGHT, tag.getFloat("Height"));
        if(tag.contains("ChargeTime")) this.entityData.set(DATA_CHARGE_TIME, tag.getInt("ChargeTime"));
        if(tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if(tag.contains("Erupted")) this.entityData.set(ERUPTED, tag.getBoolean("Erupted"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Radius", getRadius());
        tag.putFloat("Height", getHeight());
        tag.putInt("ChargeTime", getChargeTime());
        tag.putFloat("Damage", damage);
        tag.putBoolean("Erupted", hasErupted());
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