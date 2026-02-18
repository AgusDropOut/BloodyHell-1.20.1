package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.HollowRectangleOptions; // IMPORT YOUR NEW CLASS
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class RhnullHeavySwordEntity extends Projectile implements IGemSpell {
    // ... (Previous fields remain the same) ...
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(RhnullHeavySwordEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> SPELL_SCALE = SynchedEntityData.defineId(RhnullHeavySwordEntity.class, EntityDataSerializers.FLOAT);

    public AnimationState fallAnimationState = new AnimationState();

    private static final float DEFAULT_DAMAGE = 25.0f;
    private static final float DEFAULT_SIZE = 5.0f;
    private static final int DEFAULT_DURATION = 100;
    private static final int SIZE_FACTOR_ON_VISUAL_EFFECTS = 10;
    private static final float DEFAULT_GRAVITY_FACTOR = 0.0005f;

    private static final Vector3f COLOR_CORE = new Vector3f(1.0f, 0.6f, 0.0f);
    private static final Vector3f COLOR_FADE = new Vector3f(0.5f, 0.0f, 0.0f);

    private float damage = DEFAULT_DAMAGE;
    private float size = DEFAULT_SIZE;
    private int lifeTimeTicks = DEFAULT_DURATION;
    private int lifeTicks = 0;

    public RhnullHeavySwordEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.setNoGravity(false);
    }

    // ... (Constructors remain the same) ...
    public RhnullHeavySwordEntity(Level level, LivingEntity owner, int index, int total) {
        this(ModEntityTypes.RHNULL_HEAVY_SWORD_PROJECTILE.get(), level);
        this.setOwner(owner);
        this.entityData.set(OWNER_ID, owner.getId());
        Vec3 startPos = owner.getEyePosition().add(owner.getLookAngle().scale(4.0));
        this.setPos(startPos.x, startPos.y, startPos.z);
        if(this.getOwner() != null) {
            this.setYRot(this.getOwner().getYRot());
            this.setXRot(this.getOwner().getXRot());
        }
        this.setDeltaMovement(owner.getLookAngle().scale(0.02));
        this.entityData.set(SPELL_SCALE, DEFAULT_SIZE);
    }

    public RhnullHeavySwordEntity(Level level, LivingEntity owner, int index, int total, List<Gem> gems) {
        this(level, owner, index, total);
        configureSpell(gems);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPELL_SCALE, DEFAULT_SIZE);
        this.entityData.define(OWNER_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if(lifeTicks == 0){
            this.level().broadcastEntityEvent(this, (byte) 4);
        }

        if(this.level().isClientSide) {
            RandomSource rand = this.level().getRandom();
            float currentSize = this.entityData.get(SPELL_SCALE);

            for(int i=0; i<3; i++) {
                spawnReEntryParticles(rand, currentSize);
            }

            if (this.lifeTicks % 10 == 0) {
                ParticleHelper.spawnRing(this.level(),
                        new MagicFloorParticleOptions(COLOR_CORE, currentSize * 1.5f, true, 40),
                        this.position(),
                        currentSize * 2.0, 30, 0.5
                );
            }

            // 3. GROUND PRESSURE & IMPACT MARKER
            Vec3 hitPos = predictImpactPosition();
            if (hitPos != null) {
                double dist = this.position().distanceTo(hitPos);
                // Intensity (0.0 to 1.0) based on distance
                float intensity = (float) Math.max(0, 1.0 - (dist / 40.0));



                    // --- NEW HOLLOW RECTANGLE USAGE ---
                    // Calculates width dynamically: Base size + pulsing intensity
                    float rectWidth = currentSize * (1.5f + intensity);
                    float rectHeight = 10.0f; // Tall column fading up
                    System.out.println("Spawning Hollow Rectangle at " + hitPos + " with width " + rectWidth + " and intensity " + intensity);
                    ParticleHelper.spawn(this.level(),
                            new HollowRectangleOptions(COLOR_FADE, rectWidth, rectHeight, 3), // Life 3 ticks so it updates pos
                            hitPos.x, hitPos.y + 5, hitPos.z,
                            0, 0, 0
                    );
                    // ----------------------------------

                    // Rising Pressure Particles
                    int particleCount = (int)(5 * intensity) + 1;
                    for(int k=0; k < particleCount; k++) {
                        double ox = (rand.nextDouble() - 0.5) * currentSize * 3.0;
                        double oz = (rand.nextDouble() - 0.5) * currentSize * 3.0;
                        this.level().addParticle(
                                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                hitPos.x + ox, hitPos.y + 0.2, hitPos.z + oz,
                                0, 0.1 + (intensity * 0.3), 0
                        );
                    }

            }
        }

        // Physics
        Vec3 movement = this.getDeltaMovement();
        double nextX = this.getX() + movement.x;
        double nextY = this.getY() + movement.y;
        double nextZ = this.getZ() + movement.z;
        this.setPos(nextX, nextY, nextZ);

        this.setDeltaMovement(this.getDeltaMovement().add(0, -DEFAULT_GRAVITY_FACTOR, 0));

        this.lifeTicks++;

        if(this.lifeTicks == lifeTimeTicks) {
            explode(this.position());
        }
    }

    // ... (Rest of the methods: spawnReEntryParticles, predictImpactPosition, explode, etc. remain unchanged) ...
    private void spawnReEntryParticles(RandomSource rand, float scale) {
        double radius = scale * 0.4;
        double angle = rand.nextDouble() * Math.PI * 2;
        double xOff = Math.cos(angle) * radius;
        double zOff = Math.sin(angle) * radius;
        double yOff = (rand.nextDouble() - 0.5) * scale * 3.0;
        Vec3 localPos = new Vec3(xOff, yOff, zOff);
        Vec3 rotatedPos = localPos
                .xRot(-this.getXRot() * ((float)Math.PI / 180F))
                .yRot(-this.getYRot() * ((float)Math.PI / 180F));

        this.level().addParticle(
                new MagicParticleOptions(COLOR_CORE, 0.8f, true, 20),
                this.getX() + rotatedPos.x,
                this.getY() + rotatedPos.y + 2.0,
                this.getZ() + rotatedPos.z,
                0, 0, 0
        );

        if(rand.nextFloat() < 0.3f) {
            this.level().addParticle(ParticleTypes.LARGE_SMOKE,
                    this.getX() + rotatedPos.x, this.getY() + rotatedPos.y + 2.0, this.getZ() + rotatedPos.z, 0, 0, 0);
        }
    }

    private Vec3 predictImpactPosition() {
        Vec3 simPos = this.position();
        Vec3 simVel = this.getDeltaMovement();
        int remainingTicks = this.lifeTimeTicks - this.lifeTicks;
        int steps = Math.min(remainingTicks, 100);

        for (int i = 0; i < steps; i++) {
            Vec3 nextPos = simPos.add(simVel);
            BlockHitResult hit = this.level().clip(new ClipContext(simPos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.MISS) {
                return hit.getLocation();
            }
            simPos = nextPos;
            simVel = simVel.add(0, -DEFAULT_GRAVITY_FACTOR, 0);
        }
        return null;
    }

    @Override protected void readAdditionalSaveData(CompoundTag data) { super.readAdditionalSaveData(data); }
    @Override protected void addAdditionalSaveData(CompoundTag data) { super.addAdditionalSaveData(data); }

    private void explode(Vec3 pos) {
        if (!this.level().isClientSide) {
            float blastRadius = this.size * 2.0f;
            this.level().explode(this, pos.x, pos.y, pos.z, blastRadius, Level.ExplosionInteraction.NONE);
            EntityCameraShake.cameraShake(this.level(), pos, blastRadius * 8.0f, 0.8f, 30, 10);
            float scaleRatio = this.size / DEFAULT_SIZE;

            ParticleHelper.spawnExplosion(this.level(),
                    new MagicParticleOptions(COLOR_CORE, 2.0f * scaleRatio, false, 50),
                    pos, 100 + (int)(this.size * SIZE_FACTOR_ON_VISUAL_EFFECTS), 1.5, 1.0);

            ParticleHelper.spawnRing(this.level(),
                    new MagicFloorParticleOptions(COLOR_FADE, 5.0f * scaleRatio, false, 60),
                    pos.add(0, 0.1, 0), this.size * 2.0, 80 + (int)(this.size * SIZE_FACTOR_ON_VISUAL_EFFECTS), 0.8);

            ParticleHelper.spawnCylinder(this.level(),
                    new MagicParticleOptions(COLOR_CORE, 1.5f * scaleRatio, false, 40),
                    pos, this.size * 0.5, this.size * 3.0, 40 + (int)(this.size * SIZE_FACTOR_ON_VISUAL_EFFECTS), 0.8);

            this.discard();
        }
    }

    public int getLifeTicks(){ return this.lifeTicks; }
    public int getLifeTimeTicks(){ return this.lifeTimeTicks; }
    @Override public void increaseSpellDamage(double amount) { this.damage += amount; }
    @Override public void increaseSpellSize(double amount) { this.size += (float) amount; this.entityData.set(SPELL_SCALE, (Float) this.size); }
    @Override public void handleEntityEvent(byte b) { super.handleEntityEvent(b); if (b == 4) { this.fallAnimationState.start(this.tickCount); } }
    @Override public void increaseSpellDuration(int amount) { this.lifeTimeTicks += amount; }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}