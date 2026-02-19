package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ParticleOptions.HollowRectangleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class RhnullHeavySwordEntity extends Projectile implements IGemSpell {
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(RhnullHeavySwordEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> SPELL_SCALE = SynchedEntityData.defineId(RhnullHeavySwordEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> DELAY_TICKS = SynchedEntityData.defineId(RhnullHeavySwordEntity.class, EntityDataSerializers.INT);

    public AnimationState fallAnimationState = new AnimationState();

    private static final float DEFAULT_DAMAGE = 200.0f;
    private static final float DEFAULT_SIZE = 80.0f;
    private static final int DEFAULT_DURATION = 80;
    private static final int SIZE_FACTOR_ON_VISUAL_EFFECTS = 10;
    private static final float DEFAULT_GRAVITY_FACTOR = 0.0005f;

    private static final Vector3f COLOR_GOLD = new Vector3f(1.0f, 0.85f, 0.1f);
    private static final Vector3f COLOR_DARK_GOLD = new Vector3f(0.6f, 0.45f, 0.0f);

    private float damage = DEFAULT_DAMAGE;
    private float size = DEFAULT_SIZE;
    private int lifeTimeTicks = DEFAULT_DURATION;
    private int lifeTicks = 0;
    private Vec3 ownerLookAngle = Vec3.ZERO;
    private float ownerYRot = 0;
    private float ownerXRot = 0;



    public RhnullHeavySwordEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public RhnullHeavySwordEntity(Level level, LivingEntity owner, int delayTicks) {
        this(ModEntityTypes.RHNULL_HEAVY_SWORD_PROJECTILE.get(), level);
        this.setOwner(owner);
        this.entityData.set(OWNER_ID, owner.getId());
        this.setDelayTicks(delayTicks);



        this.hasImpulse = false;
        this.setNoGravity(true);
        ownerLookAngle = owner.getLookAngle();
        Vec3 clampedLookVec = new Vec3(Mth.clamp(ownerLookAngle.x, 0f, Mth.PI/30f),ownerLookAngle.y, ownerLookAngle.z);
        this.ownerXRot = owner.getXRot();
        this.ownerYRot = owner.getYRot();


        Vec3 startPos = owner.getEyePosition().add(clampedLookVec.scale(4.0));
        this.setPos(startPos.x, startPos.y + 2.0, startPos.z);
        this.entityData.set(SPELL_SCALE, DEFAULT_SIZE);
    }

    public RhnullHeavySwordEntity(Level level, LivingEntity owner,int delayTicks, List<Gem> gems) {
        this(level, owner, delayTicks);
        configureSpell(gems);
        this.setDelayTicks(delayTicks);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPELL_SCALE, DEFAULT_SIZE);
        this.entityData.define(OWNER_ID, -1);
        this.entityData.define(DELAY_TICKS, 0);
    }

    @Override
    public void tick() {


        if(this.getDelayTicks() > 0) {
            this.setDelayTicks(this.getDelayTicks()-1);
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }

        super.tick();

        if(lifeTicks == 0) {
            if(this.getOwner() != null && this.getOwner() instanceof Player owner) {
                this.setYRot(ownerYRot);
                this.setXRot(ownerXRot);
                this.setDeltaMovement(ownerLookAngle.scale(0.02));
            }


            this.level().broadcastEntityEvent(this, (byte) 4);
        }

        RandomSource rand = this.level().getRandom();
        float currentSize = this.entityData.get(SPELL_SCALE);
        float lifeRatio = (float) lifeTicks / (float) lifeTimeTicks;

        for (int i = 0; i < 3; i++) spawnGoldReEntry(rand, currentSize);



        Vec3 hitPos = predictImpactPosition();
        if (hitPos != null) {
            double dist = this.position().distanceTo(hitPos);
            float intensity = (float) Math.max(0, 1.0 - (dist / 35.0));

            if(lifeTicks == 0 && this.getOwner() != null) {
                ParticleHelper.spawn(this.level(), new HollowRectangleOptions(COLOR_GOLD, currentSize*0.5f, currentSize , this.lifeTimeTicks, -this.getYRot(), 0.1f),
                        hitPos.x, hitPos.y + 0.1, hitPos.z, 0, 0, 0);
            }

            int floorCount = (int) (1 + (lifeRatio * 12));
            float yawRad = (float) Math.toRadians(this.getYRot());

            for (int j = 0; j < floorCount; j++) {
                float rectWidth = currentSize;
                float rectLength = currentSize * 2.0f;

                double localZ = (rand.nextDouble() - 0.5) * rectLength;
                double localX = (rand.nextDouble() - 0.5) * rectWidth;

                double rx = localX * Math.cos(yawRad) - localZ * Math.sin(yawRad);
                double rz = localX * Math.sin(yawRad) + localZ * Math.cos(yawRad);

                this.level().addParticle(new MagicFloorParticleOptions(COLOR_GOLD, 0.4f + (rand.nextFloat() * 0.4f), false, 20),
                        hitPos.x + rx, hitPos.y + 0.05, hitPos.z + rz, 0, 0.05 + (intensity * 0.1), 0);
            }
        }

        this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
        this.setDeltaMovement(this.getDeltaMovement().add(0, -DEFAULT_GRAVITY_FACTOR * (1 + lifeTicks * 0.05), 0));

        this.lifeTicks++;
        if(this.lifeTicks >= lifeTimeTicks) explode(this.position());
    }

    private void spawnGoldReEntry(RandomSource rand, float scale) {
        double radius = scale * 0.3;
        double angle = rand.nextDouble() * Math.PI * 2;
        Vec3 rotatedPos = new Vec3(Math.cos(angle) * radius, (rand.nextDouble() - 0.5) * scale * 2.0, Math.sin(angle) * radius)
                .xRot(-this.getXRot() * ((float)Math.PI / 180F)).yRot(-this.getYRot() * ((float)Math.PI / 180F));

        this.level().addParticle(new MagicParticleOptions(COLOR_GOLD, 0.6f, false, 10),
                this.getX() + rotatedPos.x, this.getY() + rotatedPos.y, this.getZ() + rotatedPos.z, 0, 0, 0);
    }

    private Vec3 predictImpactPosition() {
        Vec3 simPos = this.position();
        Vec3 simVel = this.getDeltaMovement();
        int steps = Math.min(this.lifeTimeTicks - this.lifeTicks, 500);
        for (int i = 0; i < steps; i++) {
            Vec3 nextPos = simPos.add(simVel);
            BlockHitResult hit = this.level().clip(new ClipContext(simPos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.MISS) return hit.getLocation();
            simPos = nextPos;
            simVel = simVel.add(0, -DEFAULT_GRAVITY_FACTOR*5, 0);
        }
        return null;
    }

    private void explode(Vec3 pos) {
        if (!this.level().isClientSide) {
            float blastRadius = this.size * 2.0f;
            float rectLength = this.size * 2.0f;
            float rectWidth = this.size;

            AABB damageArea = new AABB(pos.x - rectLength, pos.y - 2, pos.z - rectLength, pos.x + rectLength, pos.y + 4, pos.z + rectLength);
            List<Entity> targets = this.level().getEntities(this, damageArea);

            for (Entity target : targets) {
                if (target instanceof LivingEntity living) {
                    float distSq = (float) living.distanceToSqr(pos);
                    float damageMult = 1.0f - Mth.clamp(distSq / (blastRadius * blastRadius), 0, 0.7f);
                    living.hurt(this.damageSources().explosion(this, this.getOwner()), this.damage * damageMult);
                }
            }

            this.level().explode(this, pos.x, pos.y, pos.z, blastRadius * 0.5f, Level.ExplosionInteraction.NONE);
            EntityCameraShake.cameraShake(this.level(), pos, blastRadius * 8.0f, 0.8f, 30, 10);

            float scaleRatio = this.size / DEFAULT_SIZE;
            ParticleHelper.spawnExplosion(this.level(), new MagicParticleOptions(COLOR_DARK_GOLD, 2.0f * scaleRatio, false, 50),
                    pos, 120 + (int)(this.size * SIZE_FACTOR_ON_VISUAL_EFFECTS), 1.5, 1.0);
            ParticleHelper.spawnRing(this.level(), new MagicFloorParticleOptions(COLOR_GOLD, 5.0f * scaleRatio, false, 60),
                    pos.add(0, 0.1, 0), this.size * 2.0, 100, 0.8);

            this.discard();
        }
    }

    public int getDelayTicks() { return this.entityData.get(DELAY_TICKS); }
    public void setDelayTicks(int ticks) { this.entityData.set(DELAY_TICKS, ticks); }
    public int getLifeTicks() { return this.lifeTicks; }
    public int getLifeTimeTicks() { return this.lifeTimeTicks; }


    @Override public void increaseSpellDamage(double amount) { this.damage += amount; }
    @Override public void increaseSpellSize(double amount) { this.size += (float) amount; this.entityData.set(SPELL_SCALE, this.size); }
    @Override public void increaseSpellDuration(int amount) { this.lifeTimeTicks += amount; }
    @Override public void handleEntityEvent(byte b) { if (b == 4) this.fallAnimationState.start(this.tickCount); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }


    @Override
    public boolean ignoreExplosion() {
        return true;
    }
    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}