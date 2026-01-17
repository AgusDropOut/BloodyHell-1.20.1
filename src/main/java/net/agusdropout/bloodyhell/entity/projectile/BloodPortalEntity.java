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
    private final float radius = 5.0f;
    private final float spawnHeight = 10.0f; // Height of the rift above the portal center

    public BloodPortalEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public BloodPortalEntity(Level level, double x, double y, double z, LivingEntity owner) {
        this(ModEntityTypes.BLOOD_PORTAL_ENTITY.get(), level);
        if (owner != null) this.ownerUUID = owner.getUUID();
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        // --- SERVER LOGIC ---
        if (!level().isClientSide) {
            if (this.lifeTicks-- <= 0) {
                this.discard();
                return;
            }
            // Spawn Daggers rapidly (every 2 ticks)
            if (this.tickCount % 2 == 0) spawnRain();
        }

        // --- CLIENT VISUALS ---
        else {
            spawnBorderParticles();     // Floor Rune & Drips
            spawnInteriorSparkles();    // Bioluminescent dots
            spawnSkyRift();             // The source cloud above
        }
    }

    private void spawnRain() {
        RandomSource r = this.level().getRandom();
        // Random point in circle
        double dist = radius * Math.sqrt(r.nextDouble());
        double theta = r.nextDouble() * 2 * Math.PI;

        double sx = this.getX() + dist * Math.cos(theta);
        double sz = this.getZ() + dist * Math.sin(theta);
        double sy = this.getY() + spawnHeight;

        SmallCrimsonDagger dagger = new SmallCrimsonDagger(this.level(), sx, sy, sz, this.getOwner());
        dagger.setDeltaMovement(0, -1.8, 0); // Fast drop
        dagger.setXRot(90f); // Point down
        this.level().addFreshEntity(dagger);
    }

    private void spawnSkyRift() {
        RandomSource r = this.level().getRandom();
        double y = this.getY() + spawnHeight;

        // 1. Dark Cloud Base (Density)
        for (int i = 0; i < 3; i++) {
            double dist = radius * Math.sqrt(r.nextDouble());
            double theta = r.nextDouble() * 2 * Math.PI;
            double px = this.getX() + dist * Math.cos(theta);
            double pz = this.getZ() + dist * Math.sin(theta);

            // Dark particles floating up
            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), px, y, pz, 0, 0.05, 0);
        }

        // 2. Swirling Portal Edge
        double theta = (this.tickCount * 0.15) + (r.nextDouble() * 0.5);
        double px = this.getX() + radius * Math.cos(theta);
        double pz = this.getZ() + radius * Math.sin(theta);

        // Glowing rim particle
        this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), px, y, pz, 0, 0, 0);
    }

    private void spawnBorderParticles() {
        // 1. SPAWN THE MAGIC RUNE (FLOOR)
        // Only spawn once every 40 ticks to avoid over-rendering (particle lasts 80 ticks)
        if (this.tickCount % 40 == 0) {
            double floorY = findLocalGroundY(this.getX(), this.getY(), this.getZ());

            if (floorY != -999) {
                this.level().addParticle(ModParticles.BLOOD_RUNE_PARTICLE.get(),
                        this.getX(), floorY + 0.05, this.getZ(),
                        0, 0, 0);
            }
        }

        // 2. SPAWN BLOOD DRIPS (BORDER)
        // Visual indicator of the radius boundary
        RandomSource r = this.level().getRandom();
        for(int i = 0; i < 2; i++) {
            double theta = r.nextDouble() * 2 * Math.PI;
            double px = this.getX() + radius * Math.cos(theta);
            double pz = this.getZ() + radius * Math.sin(theta);

            double dripY = findLocalGroundY(px, this.getY(), pz);

            if (dripY != -999) {
                this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), px, dripY + 0.1, pz, 0, 0.05, 0);
            }
        }
    }

    private void spawnInteriorSparkles() {
        RandomSource r = this.level().getRandom();
        if (r.nextFloat() < 0.3f) {
            double dist = radius * Math.sqrt(r.nextDouble());
            double theta = r.nextDouble() * 2 * Math.PI;

            double px = this.getX() + dist * Math.cos(theta);
            double pz = this.getZ() + dist * Math.sin(theta);
            double floorY = findLocalGroundY(px, this.getY(), pz);

            if (floorY != -999) {
                // Bioluminescent dots floating up
                this.level().addParticle(ModParticles.LIGHT_PARTICLES.get(),
                        px, floorY + 0.2, pz,
                        0, 0.02, 0);
            }
        }
    }

    // Manual Scan: Scans down 20 blocks to find solid ground
    private double findLocalGroundY(double x, double startY, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, startY, z);
        for (int i = 0; i < 20; i++) {
            BlockState state = this.level().getBlockState(pos);
            if (!state.getCollisionShape(this.level(), pos, CollisionContext.empty()).isEmpty()) {
                return pos.getY() + 1.0;
            }
            pos.move(0, -1, 0);
        }
        return -999;
    }

    @Nullable public LivingEntity getOwner() { if (ownerUUID != null && level() instanceof ServerLevel s) return (LivingEntity) s.getEntity(ownerUUID); return null; }
    public float getRadius() { return radius; }
    @Override protected void defineSynchedData() {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) { this.lifeTicks = tag.getInt("Life"); if(tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner"); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("Life", lifeTicks); if(ownerUUID != null) tag.putUUID("Owner", ownerUUID); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}