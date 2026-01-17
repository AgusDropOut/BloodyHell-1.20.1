package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class BloodStainEntity extends Entity {

    private static final EntityDataAccessor<Direction> ATTACH_FACE = SynchedEntityData.defineId(BloodStainEntity.class, EntityDataSerializers.DIRECTION);

    private static final int MAX_LIFE = 600; // 30 Seconds
    private int lifeTicks = MAX_LIFE;

    public BloodStainEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public BloodStainEntity(Level level, double x, double y, double z, Direction face) {
        this(ModEntityTypes.BLOOD_STAIN_ENTITY.get(), level);
        this.setPos(x, y, z);
        this.setAttachFace(face);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACH_FACE, Direction.UP);
    }

    @Override
    public void tick() {
        super.tick();

        // --- 1. SPAWN AUDIO (First Tick) ---
        // Plays a "squelch" sound when the stain is created
        if (this.tickCount == 1) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.SLIME_BLOCK_BREAK, SoundSource.AMBIENT, 1.0f, 0.5f);
        }

        // --- 2. CLIENT VISUALS (Dripping) ---
        // If on walls/ceiling, drip particles downwards occasionally
        if (this.level().isClientSide && this.tickCount % 15 == 0) {
            spawnDrippingParticles();
        }

        // --- 3. SERVER LOGIC ---
        if (!this.level().isClientSide) {
            if (this.lifeTicks-- <= 0) {
                this.discard();
                return;
            }

            // Collision / Mechanic Logic (Every 5 ticks)
            if (this.tickCount % 5 == 0) {
                AABB box = this.getBoundingBox().inflate(0.2);
                List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, box);

                for (LivingEntity victim : victims) {
                    // Apply Bleeding Effect
                    victim.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 100, 0));

                    // STICKY FLOOR MECHANIC
                    // Only applies if the stain is on the floor (UP)
                    if (this.getAttachFace() == Direction.UP) {
                        // Slow down movement (0.7 multiplier = 30% slow)
                        victim.makeStuckInBlock(this.level().getBlockState(this.blockPosition()), new Vec3(0.7, 0.75, 0.7));
                    }
                }
            }
        }
    }

    private void spawnDrippingParticles() {
        Direction face = this.getAttachFace();

        // Only drip if NOT on the floor (Walls or Ceiling)
        if (face != Direction.UP) {
            double x = this.getX() + (random.nextDouble() - 0.5) * 0.6;
            double y = this.getY() + (random.nextDouble() - 0.5) * 0.6;
            double z = this.getZ() + (random.nextDouble() - 0.5) * 0.6;

            // Standard blood particle falling down
            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), x, y, z, 0, -0.05, 0);
        }
    }

    // --- BOILERPLATE ---
    public void setAttachFace(Direction face) { this.entityData.set(ATTACH_FACE, face); }
    public Direction getAttachFace() { return this.entityData.get(ATTACH_FACE); }

    public float getAlpha() {
        // Linearly fade out during the last 20% of life
        float fadeStart = MAX_LIFE * 0.2f;
        if (lifeTicks < fadeStart) return lifeTicks / fadeStart;
        return 1.0f;
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) { this.lifeTicks = tag.getInt("Life"); this.setAttachFace(Direction.from3DDataValue(tag.getInt("Face"))); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("Life", lifeTicks); tag.putInt("Face", getAttachFace().get3DDataValue()); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}