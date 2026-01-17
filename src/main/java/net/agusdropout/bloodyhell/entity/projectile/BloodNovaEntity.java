package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BloodNovaDebrisEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class BloodNovaEntity extends Projectile {

    private int lifeTicks = 180;
    private float damage;
    private int tickCounter = 0;

    private static final int T_STOP = 20;
    private static final int T_LIFT_START = 30;
    private static final int T_LIFT_END = 100;
    private static final int T_COLLAPSE = 150;

    public BloodNovaEntity(EntityType<? extends BloodNovaEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public BloodNovaEntity(Level level, double x, double y, double z, float damage, LivingEntity owner, float yaw, float pitch) {
        this(ModEntityTypes.BLOOD_NOVA_ENTITY.get(), level);
        this.damage = damage;
        this.setOwner(owner);
        this.setPos(x, y, z);
        float speed = 0.8f;
        float xMotion = -Mth.sin(yaw * (float) Math.PI / 180F) * Mth.cos(pitch * (float) Math.PI / 180F);
        float yMotion = -Mth.sin(pitch * (float) Math.PI / 180F);
        float zMotion = Mth.cos(yaw * (float) Math.PI / 180F) * Mth.cos(pitch * (float) Math.PI / 180F);
        this.setDeltaMovement(new Vec3(xMotion * speed, yMotion * speed, zMotion * speed));
    }

    @Override
    public void tick() {
        super.tick();

        // 1. Movement Phase
        if (tickCounter < T_STOP) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else if (tickCounter == T_STOP) {
            this.setDeltaMovement(Vec3.ZERO);
            if (!level().isClientSide) {
                EntityCameraShake.cameraShake(level(), position(), 15, 0.3f, 10, 10);
                this.playSound(SoundEvents.BEACON_ACTIVATE, 2.0f, 0.5f);
            }
        }

        // 2. Server Mechanics
        if (!this.level().isClientSide) {
            if (tickCounter > T_LIFT_START && tickCounter < T_LIFT_END && tickCounter % 4 == 0) {
                spawnRisingBlock();
            }

            controlDebris();
            pullAndDamageEnemies();

            if (tickCounter > T_COLLAPSE && tickCounter % 5 == 0) {
                EntityCameraShake.cameraShake(level(), position(), 20, 0.5f + ((tickCounter-T_COLLAPSE)*0.05f), 5, 2);
            }
        }

        // 3. Client Visuals
        if (this.level().isClientSide) {
            spawnAmbientParticles();
        }

        // 4. End
        if (this.lifeTicks-- <= 0) {
            explode();
        }
        this.tickCounter++;
    }

    // --- EXPLOSION LOGIC FIXES ---

    private void explode() {
        if (!this.level().isClientSide) {
            // A. Physical Explosion
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 4.0f, Level.ExplosionInteraction.NONE);

            // B. Visual Sync (Crucial: Tells client to run particle logic immediately)
            this.level().broadcastEntityEvent(this, (byte) 3);

            // C. Damage Enemies
            AABB explosionArea = this.getBoundingBox().inflate(6);
            List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, explosionArea, e -> e != this.getOwner());

            for (LivingEntity victim : victims) {
                double dist = victim.distanceTo(this);
                float dmgScale = (float) Math.max(0.5, 1.0 - (dist / 10.0));
                victim.hurt(this.level().damageSources().explosion(this, this.getOwner()), this.damage * 2.5f * dmgScale);

                Vec3 away = victim.position().subtract(this.position()).normalize();
                victim.knockback(1.8, -away.x, -away.z);
            }

            // D. Fling Debris
            AABB area = this.getBoundingBox().inflate(12);
            List<BloodNovaDebrisEntity> blocks = this.level().getEntitiesOfClass(BloodNovaDebrisEntity.class, area);
            for(BloodNovaDebrisEntity b : blocks) {
                Vec3 dir = b.position().subtract(this.position()).normalize();
                b.setDeltaMovement(dir.scale(1.6).add(0, 0.6, 0));
            }

            EntityCameraShake.cameraShake(level(), position(), 30, 2.5f, 10, 15);
            this.discard();
        }
        // Client side logic handled via handleEntityEvent now
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            spawnExplosionParticles();
            spawnExplosionParticles(); // Double density for impact
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnExplosionParticles() {
        int particleCount = 150;
        // 1. FAST SHOCKWAVE (Ring)
        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            // Flattened ring expansion
            double speed = 1.5 + random.nextDouble() * 1.0;
            double vx = Math.cos(angle) * speed;
            double vy = (random.nextDouble() - 0.5) * 0.5; // Slight vertical spread
            double vz = Math.sin(angle) * speed;

            this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    this.getX(), this.getY(), this.getZ(),
                    vx, vy, vz);
        }

        // 2. CORE BURST (Sphere)
        for (int i = 0; i < 60; i++) {
            double speed = 0.5 + random.nextDouble() * 0.5;
            double dx = (random.nextDouble() - 0.5) * 2.0;
            double dy = (random.nextDouble() - 0.5) * 2.0;
            double dz = (random.nextDouble() - 0.5) * 2.0;
            Vec3 dir = new Vec3(dx, dy, dz).normalize().scale(speed);

            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(),
                    this.getX(), this.getY(), this.getZ(),
                    dir.x, dir.y, dir.z);
        }
    }

    // --- STANDARD MECHANICS (Kept your edits) ---

    private void spawnRisingBlock() {
        double angle = random.nextDouble() * Math.PI * 2;
        double radius = 1.0 + random.nextDouble() * 2.0;
        int x = Mth.floor(this.getX() + Math.cos(angle) * radius);
        int z = Mth.floor(this.getZ() + Math.sin(angle) * radius);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, this.getY(), z);
        for (int i = 0; i < 10; i++) {
            if (!this.level().isEmptyBlock(pos)) break;
            pos.move(0, -1, 0);
        }

        BlockState state = this.level().getBlockState(pos);
        if (!state.isAir() && state.getDestroySpeed(this.level(), pos) >= 0) {
            BloodNovaDebrisEntity debris = new BloodNovaDebrisEntity(this.level(), state, 200);
            debris.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
            debris.setDeltaMovement(0, 0.2, 0);
            this.level().addFreshEntity(debris);
            this.level().destroyBlock(pos, false);
        }
    }

    private void controlDebris() {
        AABB area = this.getBoundingBox().inflate(15);
        List<BloodNovaDebrisEntity> blocks = this.level().getEntitiesOfClass(BloodNovaDebrisEntity.class, area);

        for (BloodNovaDebrisEntity block : blocks) {
            block.setDuration(200);
            applyOrbitalPhysics(block, 0.0);
        }
    }

    private void pullAndDamageEnemies() {
        AABB area = this.getBoundingBox().inflate(15);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this.getOwner());

        for (LivingEntity t : targets) {
            double resistance = t.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            boolean isWeak = resistance < 0.5;

            if (tickCounter >= T_COLLAPSE) {
                Vec3 toCenter = this.position().subtract(t.position());
                double suckPower = isWeak ? 0.8 : 0.2 * (1.0 - resistance);
                if (toCenter.length() < 0.5) {
                    t.setDeltaMovement(Vec3.ZERO);
                    t.setPos(this.getX(), this.getY(), this.getZ());
                } else {
                    t.setDeltaMovement(toCenter.normalize().scale(suckPower));
                }
                t.hurtMarked = true;
                continue;
            }

            if (isWeak) {
                applyOrbitalPhysics(t, 0.4);
                t.hasImpulse = true;
            } else {
                Vec3 toCenter = this.position().subtract(t.position());
                Vec3 pull = toCenter.normalize().scale(0.15 * (1.0 - resistance));
                if (t.onGround()) pull = pull.add(0, 0.1, 0);
                t.setDeltaMovement(t.getDeltaMovement().add(pull));
            }

            if (tickCounter % 10 == 0) {
                t.hurt(this.level().damageSources().magic(), 1.0f);
            }
        }
    }

    private void applyOrbitalPhysics(net.minecraft.world.entity.Entity entity, double gravityComp) {
        Vec3 toCenter = this.position().subtract(entity.position());
        double dist = toCenter.horizontalDistance();
        double yDiff = this.getY() - entity.getY();

        if (tickCounter >= T_COLLAPSE) {
            Vec3 targetVel = toCenter.normalize().scale(0.9);
            entity.setDeltaMovement(entity.getDeltaMovement().lerp(targetVel, 0.2));
            if (toCenter.length() < 0.5 && entity instanceof BloodNovaDebrisEntity) {
                entity.discard();
            }
            return;
        }

        double verticalForce;
        if (Math.abs(yDiff) > 1.0) verticalForce = Math.signum(yDiff) * 0.1;
        else verticalForce = -entity.getDeltaMovement().y * 0.3;

        double targetRadius = 3.5;
        Vec3 radial = new Vec3(toCenter.x, 0, toCenter.z).normalize();
        Vec3 tangent = new Vec3(-radial.z, 0, radial.x);

        Vec3 orbitVel = tangent.scale(0.45);
        Vec3 radiusVel;
        if (dist > targetRadius + 1.5) radiusVel = radial.scale(0.2);
        else if (dist < targetRadius - 0.5) radiusVel = radial.scale(-0.1);
        else radiusVel = Vec3.ZERO;

        if (dist > 8.0) radiusVel = radial.scale(0.5);

        Vec3 targetMotion = orbitVel.add(radiusVel).add(0, verticalForce + gravityComp, 0);
        Vec3 smoothedMotion = entity.getDeltaMovement().lerp(targetMotion, 0.15);

        entity.setDeltaMovement(smoothedMotion);
    }

    private void spawnAmbientParticles() {
        if (random.nextFloat() < 0.4f) {
            double radius = 3.5;
            double angle = random.nextDouble() * Math.PI * 2;
            double x = this.getX() + Math.cos(angle) * radius;
            double z = this.getZ() + Math.sin(angle) * radius;
            double vx = (this.getX() - x) * 0.08;
            double vz = (this.getZ() - z) * 0.08;
            this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    x, this.getY() + (random.nextDouble() - 0.5), z,
                    vx, 0, vz);
        }

        if (random.nextFloat() < 0.3f) {
            BlockPos groundPos = new BlockPos((int)this.getX(), (int)this.getY() - 2, (int)this.getZ());
            BlockState state = this.level().getBlockState(groundPos);
            if (!state.isAir()) {
                double angle = random.nextDouble() * Math.PI * 2;
                double radius = 3.0;
                double px = this.getX() + Math.cos(angle) * radius;
                double pz = this.getZ() + Math.sin(angle) * radius;
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        px, this.getY(), pz,
                        (random.nextDouble()-0.5)*0.2, 0.1, (random.nextDouble()-0.5)*0.2);
            }
        }
    }

    @Override protected void defineSynchedData() {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}