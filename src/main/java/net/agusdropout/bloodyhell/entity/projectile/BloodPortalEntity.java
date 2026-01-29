package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BloodPortalEntity extends Entity {

    @Nullable private UUID ownerUUID;
    private int lifeTicks = 200;
    private final float radius = 4.0f;
    private float spawnHeight = 5.0f; // Default offset if not provided

    public BloodPortalEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public BloodPortalEntity(Level level, double x, double y, double z, LivingEntity owner, float heightOffset) {
        this(ModEntityTypes.BLOOD_PORTAL_ENTITY.get(), level);
        if (owner != null) this.ownerUUID = owner.getUUID();
        this.setPos(x, y, z); // Position is on the GROUND
        this.spawnHeight = heightOffset;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (this.lifeTicks-- <= 0) {
                this.discard();
                return;
            }
            if (this.tickCount % 2 == 0) spawnRain();
        } else {
            spawnBorderParticles();
            spawnInteriorSparkles();
            spawnSkyRift();
        }
    }

    private void spawnRain() {
        RandomSource r = this.level().getRandom();
        double dist = radius * Math.sqrt(r.nextDouble());
        double theta = r.nextDouble() * 2 * Math.PI;

        double sx = this.getX() + dist * Math.cos(theta);
        double sz = this.getZ() + dist * Math.sin(theta);

        // Rain starts at Ground Y + Height Offset - small buffer
        double sy = this.getY() + spawnHeight - 0.5;

        SmallCrimsonDagger dagger = new SmallCrimsonDagger(this.level(), sx, sy, sz, this.getOwner());
        dagger.setDeltaMovement(0, -1.5, 0);
        dagger.setXRot(90f);
        this.level().addFreshEntity(dagger);
    }

    private void spawnSkyRift() {
        RandomSource r = this.level().getRandom();
        double absolutePortalY = this.getY() + spawnHeight;

        for (int i = 0; i < 3; i++) {
            double dist = radius * Math.sqrt(r.nextDouble());
            double theta = r.nextDouble() * 2 * Math.PI;
            double px = this.getX() + dist * Math.cos(theta);
            double pz = this.getZ() + dist * Math.sin(theta);

            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), px, absolutePortalY, pz, 0, 0.05, 0);
        }

        double theta = (this.tickCount * 0.15) + (r.nextDouble() * 0.5);
        double px = this.getX() + radius * Math.cos(theta);
        double pz = this.getZ() + radius * Math.sin(theta);

        this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), px, absolutePortalY, pz, 0, 0, 0);
    }

    private void spawnBorderParticles() {
        if (this.tickCount % 40 == 0) {
            this.level().addParticle(ModParticles.BLOOD_RUNE_PARTICLE.get(),
                    this.getX(), this.getY() + 0.05, this.getZ(),
                    0, 0, 0);
        }

        RandomSource r = this.level().getRandom();
        for(int i = 0; i < 2; i++) {
            double theta = r.nextDouble() * 2 * Math.PI;
            double px = this.getX() + radius * Math.cos(theta);
            double pz = this.getZ() + radius * Math.sin(theta);

            // Logic simplifed: We know this entity is already on the ground
            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), px, this.getY() + 0.1, pz, 0, 0.05, 0);
        }
    }

    private void spawnInteriorSparkles() {
        RandomSource r = this.level().getRandom();
        if (r.nextFloat() < 0.3f) {
            double dist = radius * Math.sqrt(r.nextDouble());
            double theta = r.nextDouble() * 2 * Math.PI;

            double px = this.getX() + dist * Math.cos(theta);
            double pz = this.getZ() + dist * Math.sin(theta);

            this.level().addParticle(ModParticles.LIGHT_PARTICLES.get(),
                    px, this.getY() + 0.2, pz,
                    0, 0.02, 0);
        }
    }

    @Nullable public LivingEntity getOwner() { if (ownerUUID != null && level() instanceof ServerLevel s) return (LivingEntity) s.getEntity(ownerUUID); return null; }
    @Override protected void defineSynchedData() {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) { this.lifeTicks = tag.getInt("Life"); this.spawnHeight = tag.getFloat("Height"); if(tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner"); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("Life", lifeTicks); tag.putFloat("Height", spawnHeight); if(ownerUUID != null) tag.putUUID("Owner", ownerUUID); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}