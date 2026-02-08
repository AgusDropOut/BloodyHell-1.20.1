package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BloodNovaDebrisEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.entity.projectile.BloodClotProjectile;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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

public class BloodNovaEntity extends Projectile implements IGemSpell {

    // --- SECT 1: FIELDS & DATA KEYS ---

    private static final EntityDataAccessor<Integer> DATA_LIFE_TICKS = SynchedEntityData.defineId(BloodNovaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLLAPSE_TICKS = SynchedEntityData.defineId(BloodNovaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(BloodNovaEntity.class, EntityDataSerializers.FLOAT);

    private static final float DEFAULT_RADIUS = 10.0f;
    private static final int DEFAULT_LIFE = 180;
    private static final int DEFAULT_COLLAPSE = 150;

    private int tickCounter = 0;
    private float damage;

    // Timing constants (Static relative to start)
    private final int tStop = 20;
    private final int tLiftStart = 30;
    private final int tLiftEnd = 100;

    // --- SECT 2: CONSTRUCTORS & INIT ---

    public BloodNovaEntity(EntityType<? extends BloodNovaEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public BloodNovaEntity(Level level, double x, double y, double z, float damage, LivingEntity owner, float yaw, float pitch, List<Gem> gems) {
        this(ModEntityTypes.BLOOD_NOVA_ENTITY.get(), level);
        this.damage = damage;
        this.setOwner(owner);
        this.setPos(x, y, z);

        float speed = 0.8f;
        float xMotion = -Mth.sin(yaw * (float) Math.PI / 180F) * Mth.cos(pitch * (float) Math.PI / 180F);
        float yMotion = -Mth.sin(pitch * (float) Math.PI / 180F);
        float zMotion = Mth.cos(yaw * (float) Math.PI / 180F) * Mth.cos(pitch * (float) Math.PI / 180F);
        this.setDeltaMovement(new Vec3(xMotion * speed, yMotion * speed, zMotion * speed));

        configureSpell(gems);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_LIFE_TICKS, DEFAULT_LIFE);
        this.entityData.define(DATA_COLLAPSE_TICKS, DEFAULT_COLLAPSE);
        this.entityData.define(DATA_RADIUS, DEFAULT_RADIUS);
    }

    public int getLifeTicks() {
        return this.entityData.get(DATA_LIFE_TICKS);
    }

    public int getCollapseTime() {
        return this.entityData.get(DATA_COLLAPSE_TICKS);
    }

    public float getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    // --- SECT 3: MAIN TICK LOOP ---

    @Override
    public void tick() {
        super.tick();

        // Phase 1: Initial Travel
        if (tickCounter < tStop) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else if (tickCounter == tStop) {
            this.setDeltaMovement(Vec3.ZERO);
            if (!level().isClientSide) {
                EntityCameraShake.cameraShake(level(), position(), getRadius() * 1.5f, 0.3f, 10, 10);
                this.playSound(SoundEvents.BEACON_ACTIVATE, 2.0f, 0.5f);
            }
        }

        int collapseTime = getCollapseTime();

        // Phase 2: Server Mechanics (Pulling & Lifting)
        if (!this.level().isClientSide) {
            if (tickCounter > tLiftStart && tickCounter < tLiftEnd && tickCounter % 4 == 0) {
                spawnRisingBlock();
            }

            performAreaLogic();

            if (tickCounter > collapseTime && tickCounter % 5 == 0) {
                EntityCameraShake.cameraShake(level(), position(), getRadius() * 2.0f, 0.5f + ((tickCounter - collapseTime) * 0.05f), 5, 2);
            }
        }

        // Phase 3: Client Visuals
        if (this.level().isClientSide) {
            spawnAmbientParticles();
        }

        // Phase 4: Explosion
        if (this.tickCounter >= getLifeTicks()) {
            explode();
        }
        this.tickCounter++;
    }

    // --- SECT 4: PHYSICS & LOGIC ---

    private void performAreaLogic() {
        float radius = getRadius();
        AABB area = this.getBoundingBox().inflate(radius);

        List<BloodNovaDebrisEntity> blocks = this.level().getEntitiesOfClass(BloodNovaDebrisEntity.class, area);
        for (BloodNovaDebrisEntity block : blocks) {
            block.setDuration(getLifeTicks() + 20);
            applyOrbitalPhysics(block, 0.0);
        }

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this.getOwner());
        for (LivingEntity t : targets) {
            handleEnemyPull(t);
        }
    }

    private void handleEnemyPull(LivingEntity t) {
        double resistance = t.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        boolean isWeak = resistance < 0.5;
        int collapseTime = getCollapseTime();
        float radius = getRadius();

        if (tickCounter >= collapseTime) {
            Vec3 toCenter = this.position().subtract(t.position());
            double suckPower = isWeak ? 0.8 : 0.2 * (1.0 - resistance);
            suckPower *= (1.0 + (radius / DEFAULT_RADIUS) * 0.2);

            if (toCenter.length() < 0.5) {
                t.setDeltaMovement(Vec3.ZERO);
                t.setPos(this.getX(), this.getY(), this.getZ());
            } else {
                t.setDeltaMovement(toCenter.normalize().scale(suckPower));
            }
            t.hurtMarked = true;
            return;
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

    private void applyOrbitalPhysics(net.minecraft.world.entity.Entity entity, double gravityComp) {
        Vec3 toCenter = this.position().subtract(entity.position());
        double dist = toCenter.horizontalDistance();
        double yDiff = this.getY() - entity.getY();
        int collapseTime = getCollapseTime();
        float radius = getRadius();

        if (tickCounter >= collapseTime) {
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

        double targetOrbitRadius = radius * 0.35;
        Vec3 radial = new Vec3(toCenter.x, 0, toCenter.z).normalize();
        Vec3 tangent = new Vec3(-radial.z, 0, radial.x);

        Vec3 orbitVel = tangent.scale(0.45);
        Vec3 radiusVel;

        if (dist > targetOrbitRadius + 1.5) radiusVel = radial.scale(0.2);
        else if (dist < targetOrbitRadius - 0.5) radiusVel = radial.scale(-0.1);
        else radiusVel = Vec3.ZERO;

        if (dist > radius * 0.8) radiusVel = radial.scale(0.5);

        Vec3 targetMotion = orbitVel.add(radiusVel).add(0, verticalForce + gravityComp, 0);
        entity.setDeltaMovement(entity.getDeltaMovement().lerp(targetMotion, 0.15));
    }

    // --- SECT 5: EXPLOSION & SPAWNING ---

    private void explode() {
        if (!this.level().isClientSide) {
            float radius = getRadius();
            float explosionPower = 4.0f + (radius - DEFAULT_RADIUS) * 0.2f;

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionPower, Level.ExplosionInteraction.NONE);
            this.level().broadcastEntityEvent(this, (byte) 3);

            AABB explosionArea = this.getBoundingBox().inflate(radius * 0.6);
            List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, explosionArea, e -> e != this.getOwner());

            for (LivingEntity victim : victims) {
                double dist = victim.distanceTo(this);
                float dmgScale = (float) Math.max(0.5, 1.0 - (dist / radius));
                victim.hurt(this.level().damageSources().explosion(this, this.getOwner()), this.damage * 2.5f * dmgScale);

                Vec3 away = victim.position().subtract(this.position()).normalize();
                victim.knockback(1.8, -away.x, -away.z);
            }

            AABB debrisArea = this.getBoundingBox().inflate(radius);
            List<BloodNovaDebrisEntity> blocks = this.level().getEntitiesOfClass(BloodNovaDebrisEntity.class, debrisArea);
            for (BloodNovaDebrisEntity b : blocks) {
                Vec3 dir = b.position().subtract(this.position()).normalize();
                b.setDeltaMovement(dir.scale(1.6).add(0, 0.6, 0));
            }

            EntityCameraShake.cameraShake(level(), position(), radius * 2.5f, 2.5f, 10, 15);
            spawnClots();
            this.discard();
        }
    }

    private void spawnClots() {
        for (int i = 0; i < 12; i++) {
            BloodClotProjectile clot = new BloodClotProjectile(this.level(), this.getX(), this.getY() + 1.0, this.getZ());
            if (this.getOwner() instanceof LivingEntity l) clot.setOwner(l);

            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 0.8 + random.nextDouble() * 0.8;
            double vx = Math.cos(angle) * speed;
            double vy = (random.nextDouble() - 0.5) * 0.8;
            double vz = Math.sin(angle) * speed;

            clot.setDeltaMovement(vx, vy, vz);
            this.level().addFreshEntity(clot);
        }
    }

    private void spawnRisingBlock() {
        float radius = getRadius();
        double angle = random.nextDouble() * Math.PI * 2;
        double spawnRadius = 1.0 + random.nextDouble() * (radius * 0.5);

        int x = Mth.floor(this.getX() + Math.cos(angle) * spawnRadius);
        int z = Mth.floor(this.getZ() + Math.sin(angle) * spawnRadius);

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

    // --- SECT 6: CLIENT EVENTS & PARTICLES ---

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            spawnExplosionParticles();
            spawnExplosionParticles();
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnExplosionParticles() {
        int particleCount = 150;
        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 1.5 + random.nextDouble() * 1.0;
            double vx = Math.cos(angle) * speed;
            double vy = (random.nextDouble() - 0.5) * 0.5;
            double vz = Math.sin(angle) * speed;

            this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), vx, vy, vz);
        }

        for (int i = 0; i < 60; i++) {
            double speed = 0.5 + random.nextDouble() * 0.5;
            double dx = (random.nextDouble() - 0.5) * 2.0;
            double dy = (random.nextDouble() - 0.5) * 2.0;
            double dz = (random.nextDouble() - 0.5) * 2.0;
            Vec3 dir = new Vec3(dx, dy, dz).normalize().scale(speed);

            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), this.getX(), this.getY(), this.getZ(), dir.x, dir.y, dir.z);
        }
    }

    private void spawnAmbientParticles() {
        float radius = getRadius();
        double orbitRadius = radius * 0.35;

        if (random.nextFloat() < 0.4f) {
            double angle = random.nextDouble() * Math.PI * 2;
            double x = this.getX() + Math.cos(angle) * orbitRadius;
            double z = this.getZ() + Math.sin(angle) * orbitRadius;
            double vx = (this.getX() - x) * 0.08;
            double vz = (this.getZ() - z) * 0.08;
            this.level().addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), x, this.getY() + (random.nextDouble() - 0.5), z, vx, 0, vz);
        }

        if (random.nextFloat() < 0.3f) {
            BlockPos groundPos = new BlockPos((int) this.getX(), (int) this.getY() - 2, (int) this.getZ());
            BlockState state = this.level().getBlockState(groundPos);
            if (!state.isAir()) {
                double angle = random.nextDouble() * Math.PI * 2;
                double r = 3.0;
                double px = this.getX() + Math.cos(angle) * r;
                double pz = this.getZ() + Math.sin(angle) * r;
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), px, this.getY(), pz, (random.nextDouble() - 0.5) * 0.2, 0.1, (random.nextDouble() - 0.5) * 0.2);
            }
        }
    }

    // --- SECT 7: INTERFACE & DATA ---

    @Override
    public void increaseSpellDamage(double amount) {
        this.damage += (float) amount;
    }

    @Override
    public void increaseSpellSize(double amount) {
        float current = this.entityData.get(DATA_RADIUS);
        this.entityData.set(DATA_RADIUS, current + (float) (DEFAULT_RADIUS * amount));
    }

    @Override
    public void increaseSpellDuration(int amount) {
        int currentCollapse = this.entityData.get(DATA_COLLAPSE_TICKS);
        int currentLife = this.entityData.get(DATA_LIFE_TICKS);
        this.entityData.set(DATA_COLLAPSE_TICKS, currentCollapse + amount);
        this.entityData.set(DATA_LIFE_TICKS, currentLife + amount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("LifeTicks")) this.entityData.set(DATA_LIFE_TICKS, tag.getInt("LifeTicks"));
        if (tag.contains("CollapseTicks")) this.entityData.set(DATA_COLLAPSE_TICKS, tag.getInt("CollapseTicks"));
        if (tag.contains("Radius")) this.entityData.set(DATA_RADIUS, tag.getFloat("Radius"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("LifeTicks", getLifeTicks());
        tag.putInt("CollapseTicks", getCollapseTime());
        tag.putFloat("Radius", getRadius());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}