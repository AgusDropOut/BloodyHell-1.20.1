package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BloodPortalEntity extends Entity implements IGemSpell {

    // --- SECT 1: FIELDS & DATA KEYS ---

    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(BloodPortalEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT = SynchedEntityData.defineId(BloodPortalEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_LIFE = SynchedEntityData.defineId(BloodPortalEntity.class, EntityDataSerializers.INT);

    private static final float DEFAULT_RADIUS = 4.0f;
    private static final float DEFAULT_HEIGHT = 5.0f;
    private static final int DEFAULT_LIFE = 200;
    private int daggerCountperCycle = 1;

    @Nullable private UUID ownerUUID;
    private float damageBonus = 0.0f;

    // --- SECT 2: CONSTRUCTORS ---

    public BloodPortalEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // Standard Constructor (Mobs / No Gems)
    public BloodPortalEntity(Level level, double x, double y, double z, LivingEntity owner, float heightOffset) {
        this(ModEntityTypes.BLOOD_PORTAL_ENTITY.get(), level);
        if (owner != null) this.ownerUUID = owner.getUUID();
        this.setPos(x, y, z);

        this.entityData.set(DATA_HEIGHT, heightOffset);
    }

    // Spell Book Constructor (With Gems)
    public BloodPortalEntity(Level level, double x, double y, double z, LivingEntity owner, float heightOffset, List<Gem> gems) {
        this(ModEntityTypes.BLOOD_PORTAL_ENTITY.get(), level);
        if (owner != null) this.ownerUUID = owner.getUUID();
        this.setPos(x, y, z);

        this.entityData.set(DATA_HEIGHT, heightOffset);

        configureSpell(gems);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, DEFAULT_RADIUS);
        this.entityData.define(DATA_HEIGHT, DEFAULT_HEIGHT);
        this.entityData.define(DATA_LIFE, DEFAULT_LIFE);
    }

    public float getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public float getSpawnHeight() {
        return this.entityData.get(DATA_HEIGHT);
    }

    // --- SECT 3: MAIN TICK LOOP ---

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            int currentLife = this.entityData.get(DATA_LIFE);
            if (currentLife <= 0) {
                this.discard();
                return;
            }
            this.entityData.set(DATA_LIFE, currentLife - 1);

            if (this.tickCount % 2 == 0) spawnRain();
        } else {
            spawnBorderParticles();
            spawnInteriorSparkles();
            spawnSkyRift();
        }
    }

    // --- SECT 4: SERVER LOGIC ---

    private void spawnRain() {
        for(int i = 0; i < daggerCountperCycle; i++) {
            RandomSource r = this.level().getRandom();
            float currentRadius = getRadius();

            double dist = currentRadius * Math.sqrt(r.nextDouble());
            double theta = r.nextDouble() * 2 * Math.PI;

            double sx = this.getX() + dist * Math.cos(theta);
            double sz = this.getZ() + dist * Math.sin(theta);

            double sy = this.getY() + getSpawnHeight() - 0.5;

            SmallCrimsonDagger dagger = new SmallCrimsonDagger(this.level(), sx, sy, sz, this.getOwner());


            dagger.increaseDamage(damageBonus);

            dagger.setDeltaMovement(0, -1.5, 0);
            dagger.setXRot(90f);
            this.level().addFreshEntity(dagger);
        }
    }

    // --- SECT 5: CLIENT VISUALS ---

    private void spawnSkyRift() {
        RandomSource r = this.level().getRandom();
        float currentRadius = getRadius();
        double absolutePortalY = this.getY() + getSpawnHeight();

        for (int i = 0; i < 3; i++) {
            double dist = currentRadius * Math.sqrt(r.nextDouble());
            double theta = r.nextDouble() * 2 * Math.PI;
            double px = this.getX() + dist * Math.cos(theta);
            double pz = this.getZ() + dist * Math.sin(theta);

            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), px, absolutePortalY, pz, 0, 0.05, 0);
        }

        double theta = (this.tickCount * 0.15) + (r.nextDouble() * 0.5);
        double px = this.getX() + currentRadius * Math.cos(theta);
        double pz = this.getZ() + currentRadius * Math.sin(theta);

        this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), px, absolutePortalY, pz, 0, 0, 0);
    }

    private void spawnBorderParticles() {
        float currentRadius = getRadius();

        if (this.tickCount % 40 == 0) {
            // Passing Radius via VelocityX (vx) to the particle
            this.level().addParticle(ModParticles.BLOOD_RUNE_PARTICLE.get(),
                    this.getX(), this.getY() + 0.05, this.getZ(),
                    currentRadius, 0, 0);
        }

        RandomSource r = this.level().getRandom();
        for(int i = 0; i < 2; i++) {
            double theta = r.nextDouble() * 2 * Math.PI;
            double px = this.getX() + currentRadius * Math.cos(theta);
            double pz = this.getZ() + currentRadius * Math.sin(theta);

            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), px, this.getY() + 0.1, pz, 0, 0.05, 0);
        }
    }

    private void spawnInteriorSparkles() {
        RandomSource r = this.level().getRandom();
        float currentRadius = getRadius();

        if (r.nextFloat() < 0.3f) {
            double dist = currentRadius * Math.sqrt(r.nextDouble());
            double theta = r.nextDouble() * 2 * Math.PI;

            double px = this.getX() + dist * Math.cos(theta);
            double pz = this.getZ() + dist * Math.sin(theta);

            this.level().addParticle(ModParticles.LIGHT_PARTICLES.get(), px, this.getY() + 0.2, pz, 0, 0.02, 0);
        }
    }

    // --- SECT 6: INTERFACE & BOILERPLATE ---

    @Override
    public void increaseSpellDamage(double amount) {
        this.damageBonus += (float) amount;
    }

    @Override
    public void increaseSpellSize(double amount) {
        float current = this.entityData.get(DATA_RADIUS);
        this.entityData.set(DATA_RADIUS, current + (DEFAULT_RADIUS * (float)amount));
    }

    @Override
    public void increaseSpellDuration(int amount) {
        int current = this.entityData.get(DATA_LIFE);
        this.entityData.set(DATA_LIFE, current + amount);
    }

    @Override
    public void increaseSpellQuantity(double amount) {
        this.daggerCountperCycle += (int) amount;
    }

    @Nullable public LivingEntity getOwner() { if (ownerUUID != null && level() instanceof ServerLevel s) return (LivingEntity) s.getEntity(ownerUUID); return null; }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Life")) this.entityData.set(DATA_LIFE, tag.getInt("Life"));
        if (tag.contains("Radius")) this.entityData.set(DATA_RADIUS, tag.getFloat("Radius"));
        if (tag.contains("Height")) this.entityData.set(DATA_HEIGHT, tag.getFloat("Height"));
        if (tag.contains("DmgBonus")) this.damageBonus = tag.getFloat("DmgBonus");
        if (tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", this.entityData.get(DATA_LIFE));
        tag.putFloat("Radius", getRadius());
        tag.putFloat("Height", getSpawnHeight());
        tag.putFloat("DmgBonus", damageBonus);
        if(ownerUUID != null) tag.putUUID("Owner", ownerUUID);
    }

    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}