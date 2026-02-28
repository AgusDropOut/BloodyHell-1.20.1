package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ParticleOptions.GlitterParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SmallGlitterParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class RhnullImpalerEntity extends Projectile implements IGemSpell {

    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> SPELL_SCALE = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> OFFSET_INDEX = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TOTAL_SPEARS = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.INT);

    private static final float DEFAULT_DAMAGE = 8.0f;
    private static final float DEFAULT_SIZE = 1.0f;
    private static final int DEFAULT_DURATION = 600;
    private static final int SIZE_FACTOR_ON_VISUAL_EFFECTS = 5;

    private float damage = DEFAULT_DAMAGE;
    private int lifeTimeTicks = DEFAULT_DURATION;
    private int lifeTicks = 0;

    public RhnullImpalerEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.setNoGravity(true);
    }

    public RhnullImpalerEntity(Level level, LivingEntity owner, int index, int total) {
        this(ModEntityTypes.RHNULL_IMPALER_PROJECTILE.get(), level);
        this.setOwner(owner);
        this.entityData.set(OWNER_ID, owner.getId());
        this.entityData.set(OFFSET_INDEX, index);
        this.entityData.set(TOTAL_SPEARS, total);

        Vec3 startPos = owner.getEyePosition().subtract(owner.getLookAngle().scale(1.5));
        this.setPos(startPos.x, startPos.y, startPos.z);
    }

    public RhnullImpalerEntity(Level level, LivingEntity owner, int index, int total, List<Gem> gems) {
        this(level, owner, index, total);
        configureSpell(gems);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(SPELL_SCALE, DEFAULT_SIZE);
        this.entityData.define(OWNER_ID, -1);
        this.entityData.define(OFFSET_INDEX, 0);
        this.entityData.define(TOTAL_SPEARS, 1);
    }

    @Override
    public void tick() {
        this.lifeTicks++;
        if (isLaunched()) {
            projectileLogic();
        } else {
            orbitLogic();
        }

        if (this.level().isClientSide) {
            handleClientEffects();
        }
    }

    private void orbitLogic() {
        Entity owner = getOwner();
        if (owner == null) {
            int id = this.entityData.get(OWNER_ID);
            if (id != -1) owner = this.level().getEntity(id);
        }

        if (owner == null || !owner.isAlive()) {
            if (!this.level().isClientSide) this.discard();
            return;
        }

        int index = this.entityData.get(OFFSET_INDEX);
        int total = this.entityData.get(TOTAL_SPEARS);
        if (total == 0) total = 1;

        Vec3 lookVec = owner.getLookAngle();
        Vec3 upVec = new Vec3(0, 1, 0);
        Vec3 rightVec = lookVec.cross(upVec).normalize();
        Vec3 relativeUp = rightVec.cross(lookVec).normalize();

        double circleRadius = 1.2 + (total * 0.1);
        double distanceBehind = 0;

        double angle = (2 * Math.PI * index) / total;
        double xOffset = Math.cos(angle) * circleRadius;
        double yOffset = Math.sin(angle) * circleRadius;

        Vec3 origin = owner.getEyePosition().subtract(lookVec.scale(distanceBehind)).add(0, 0.5, 0);
        Vec3 targetPos = origin.add(rightVec.scale(xOffset)).add(relativeUp.scale(yOffset));

        Vec3 current = this.position();
        Vec3 nextPos = current.lerp(targetPos, 0.25);
        this.setPos(nextPos);
        this.setDeltaMovement(Vec3.ZERO);

        Vec3 aimStart = owner.getEyePosition();
        Vec3 aimEnd = aimStart.add(lookVec.scale(50.0));
        BlockHitResult ray = this.level().clip(new ClipContext(aimStart, aimEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
        Vec3 aimTarget = ray.getType() != HitResult.Type.MISS ? ray.getLocation() : aimEnd;

        double dx = aimTarget.x - this.getX();
        double dy = aimTarget.y - this.getY();
        double dz = aimTarget.z - this.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float)(Mth.atan2(dz, dx) * (double)(180F / (float)Math.PI)) - 90.0F;
        float targetPitch = (float)(Mth.atan2(dy, horizontalDist) * (double)(180F / (float)Math.PI));

        this.setYRot(targetYaw);
        this.setXRot(targetPitch);

        if (!this.level().isClientSide && this.tickCount > lifeTimeTicks) this.discard();
    }

    private void projectileLogic() {
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS) {
            this.onHit(hitresult);
        }

        Vec3 movement = this.getDeltaMovement();
        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
    }

    private void explode(Vec3 pos) {
        if (!this.level().isClientSide) {
            double radius = this.getSize() * 5.0;
            AABB explosionBox = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                    pos.x + radius, pos.y + radius, pos.z + radius);

            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, explosionBox);
            for (LivingEntity target : targets) {
                if (target != this.getOwner() && this.distanceToSqr(target) <= radius * radius) {
                    target.hurt(this.level().damageSources().magic(), this.damage);
                }
            }

            EntityCameraShake.cameraShake(this.level(), pos, this.getSize() * 10.0f, 0.5f, 15, 5);
            level().playSound(this, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 0.5f);
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            spawnExplosionParticles();
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnExplosionParticles() {
        float scaleRatio = this.getSize() / DEFAULT_SIZE;
        Vec3 pos = this.position();

        ParticleHelper.spawnHemisphereExplosion(this.level(),
                new MagicParticleOptions(SpellPalette.RHNULL.getColor(0), 1.5f * scaleRatio, false, 40, true),
                pos, 25 + (int)(this.getSize() * SIZE_FACTOR_ON_VISUAL_EFFECTS), 0.3 * scaleRatio, 0.2 * scaleRatio);

        ParticleHelper.spawnRing(this.level(),
                new MagicFloorParticleOptions(SpellPalette.RHNULL.getColor(1), 3.0f * scaleRatio, false, 50),
                pos.add(0, 0.1, 0), this.getSize() * 0.8, 30 + (int)(this.getSize() * SIZE_FACTOR_ON_VISUAL_EFFECTS), 0.15 * scaleRatio);

        ParticleHelper.spawnRisingBurst(this.level(),
                new GlitterParticleOptions(SpellPalette.RHNULL.getColor(2), 0.8f * scaleRatio, false, 35, false),
                pos, 20 + (int)(this.getSize() * SIZE_FACTOR_ON_VISUAL_EFFECTS), 0.5 * scaleRatio, 0.1 * scaleRatio, 0.15 * scaleRatio);
    }

    private void handleClientEffects() {
        if (!isLaunched()) {
            if (this.tickCount % 10 == 0) {
                ParticleHelper.spawnSphereVolume(
                        this.level(),
                        new SmallGlitterParticleOptions(SpellPalette.RHNULL.getColor(1), 0.3f, false, 40, false),
                        this.position(),
                        0.6,
                        2,
                        new Vec3(0, 0.01, 0)
                );
            }
        } else {
            if (this.tickCount % 2 == 0) {
                ParticleHelper.spawnLine(
                        this.level(),
                        new MagicParticleOptions(SpellPalette.RHNULL.getColor(0), 0.6f, false, 20, true),
                        this.position(),
                        this.position().subtract(this.getDeltaMovement().normalize().scale(0.8)),
                        4,
                        new Vec3(0.05, 0.05, 0.05)
                );
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            result.getEntity().hurt(this.level().damageSources().magic(), this.damage);
            explode(result.getLocation());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        explode(result.getLocation());
    }

    public void launch(Vec3 direction) {
        this.entityData.set(LAUNCHED, true);
        this.setDeltaMovement(direction.normalize().scale(3.0));
    }

    public int getLifeTicks(){ return this.lifeTicks; }
    public int getLifeTimeTicks(){ return this.lifeTimeTicks; }
    public float getSize(){ return this.entityData.get(SPELL_SCALE); }
    public boolean isLaunched() { return this.entityData.get(LAUNCHED); }

    @Override public void increaseSpellDamage(double amount) { this.damage += amount; }
    @Override public void increaseSpellSize(double amount) { this.entityData.set(SPELL_SCALE, (float)(this.getSize()+amount)); }
    @Override public void increaseSpellDuration(int amount) { this.lifeTimeTicks += amount; }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}