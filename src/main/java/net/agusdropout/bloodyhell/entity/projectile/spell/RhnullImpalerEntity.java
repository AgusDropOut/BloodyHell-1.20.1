package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class RhnullImpalerEntity extends Projectile implements IGemSpell {

    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> SPELL_SCALE = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.FLOAT);

    // Core Synced Data needed for Client prediction
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> OFFSET_INDEX = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TOTAL_SPEARS = SynchedEntityData.defineId(RhnullImpalerEntity.class, EntityDataSerializers.INT);

    // Removed custom YAW/PITCH. We use the standard Entity rotations (getYRot/getXRot) which are auto-synced.

    private double damage = 8.0;
    private int lifeTimeTicks = 600;

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

        // Start behind player immediately
        Vec3 startPos = owner.getEyePosition().subtract(owner.getLookAngle().scale(1.5));
        this.setPos(startPos.x, startPos.y, startPos.z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(SPELL_SCALE, 1.0f);
        this.entityData.define(OWNER_ID, -1);
        this.entityData.define(OFFSET_INDEX, 0);
        this.entityData.define(TOTAL_SPEARS, 1);
    }

    @Override
    public void tick() {
        // Run logic on BOTH sides.
        // Client needs to calculate orbit to prevent "Rubber Banding".
        if (isLaunched()) {
            super.tick();
            projectileLogic();
        } else {
            baseTick(); // Basic timer updates
            orbitLogic();
        }
    }

    private void orbitLogic() {
        // 1. Safe Owner Retrieval
        Entity owner = getOwner();
        if (owner == null) {
            int id = this.entityData.get(OWNER_ID);
            if (id != -1) owner = this.level().getEntity(id);
        }

        if (owner == null || !owner.isAlive()) {
            if (!this.level().isClientSide) this.discard();
            return;
        }

        // 2. Setup Variables
        int index = this.entityData.get(OFFSET_INDEX);
        int total = this.entityData.get(TOTAL_SPEARS);
        if (total == 0) total = 1;

        // 3. Define the "Screen" Plane (Coordinate System relative to Player Look)
        Vec3 lookVec = owner.getLookAngle();
        Vec3 upVec = new Vec3(0, 1, 0);
        Vec3 rightVec = lookVec.cross(upVec).normalize();
        Vec3 relativeUp = rightVec.cross(lookVec).normalize();

        // 4. Calculate Target Position (BEHIND PLAYER)
        double circleRadius = 1.2 + (total * 0.1);
        double distanceBehind = 1.0; // How far back from the head

        double angle = (2 * Math.PI * index) / total;
        double xOffset = Math.cos(angle) * circleRadius;
        double yOffset = Math.sin(angle) * circleRadius;

        // Logic: EyePos - (Look * Distance) = Point Behind Player
        Vec3 origin = owner.getEyePosition().subtract(lookVec.scale(distanceBehind));
        Vec3 targetPos = origin.add(rightVec.scale(xOffset)).add(relativeUp.scale(yOffset));

        // 5. Smooth Movement
        // We calculate this on Client too, so it looks perfectly smooth (60+ FPS)
        Vec3 current = this.position();
        Vec3 nextPos = current.lerp(targetPos, 0.25); // 0.25 = snappy but smooth
        this.setPos(nextPos);
        this.setDeltaMovement(Vec3.ZERO);

        // 6. AIMING LOGIC (Phalanx Effect)
        // Instead of facing parallel to player, face exactly what the player is looking at.

        // A. Find what player is looking at (Raycast)
        Vec3 aimStart = owner.getEyePosition();
        Vec3 aimEnd = aimStart.add(lookVec.scale(50.0)); // Look 50 blocks out
        BlockHitResult ray = this.level().clip(new ClipContext(aimStart, aimEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
        Vec3 aimTarget = ray.getType() != HitResult.Type.MISS ? ray.getLocation() : aimEnd;

        // B. Calculate Vector from Spear -> Target
        double dx = aimTarget.x - this.getX();
        double dy = aimTarget.y - this.getY();
        double dz = aimTarget.z - this.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        // C. Convert Vector to Angles
        // Minecraft Yaw: atan2(z, x) converted to degrees, -90 offset standard
        float targetYaw = (float)(Mth.atan2(dz, dx) * (double)(180F / (float)Math.PI)) - 90.0F;
        // Minecraft Pitch: atan2(y, horizontal) converted to degrees
        float targetPitch = (float)(Mth.atan2(dy, horizontalDist) * (double)(180F / (float)Math.PI));

        // D. Apply Rotation
        this.setYRot(targetYaw);
        this.setXRot(targetPitch); // Note: Projectile pitch is usually inverted, we check this in Renderer

        // Critical: Update Previous Rotation to prevent flickering
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        if (!this.level().isClientSide && this.tickCount > lifeTimeTicks) this.discard();
    }

    private void projectileLogic() {
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS) {
            this.onHit(hitresult);
        }

        Vec3 movement = this.getDeltaMovement();
        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);

        if (this.level().isClientSide) {
            this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    // ... Standard Launch & Hits ...
    public void launch(Vec3 direction) {
        this.entityData.set(LAUNCHED, true);
        this.setDeltaMovement(direction.normalize().scale(3.0));
        this.shoot(direction.x, direction.y, direction.z, 3.0f, 0);
    }

    public boolean isLaunched() { return this.entityData.get(LAUNCHED); }
    @Override public void increaseSpellDamage(double amount) { this.damage += amount; }
    @Override public void increaseSpellSize(double amount) { this.entityData.set(SPELL_SCALE, Math.min(3.0f, this.entityData.get(SPELL_SCALE) + (float)amount)); }
    @Override public void increaseSpellDuration(int amount) { this.lifeTimeTicks += amount; }

    @Override protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            result.getEntity().hurt(this.level().damageSources().magic(), (float)this.damage);
            ParticleHelper.spawnDirectionalSpray(this.level(), ModParticles.BLOOD_PARTICLES.get(),
                    this.position(), this.getDeltaMovement().reverse(), 15, 0.2, 0.5);
            this.discard();
        }
    }
    @Override protected void onHitBlock(BlockHitResult result) { if (!this.level().isClientSide) this.discard(); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}