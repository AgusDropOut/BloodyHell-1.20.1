package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.BloodFireBlockEntity;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.interfaces.BloodFlammable;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BloodFireSoulProjectile extends Projectile implements BloodFlammable {

    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(BloodFireSoulProjectile.class, EntityDataSerializers.INT);

    private static final int MAX_LIFETIME = 300;
    private static final double HOMING_SPEED = 0.55;
    private static final double HOMING_SEARCH_RANGE = 30.0;
    private static final double HOMING_CONE_THRESHOLD = 0.85;
    private static final double HOMING_TURN_FACTOR = 0.15;
    private static final float COLLISION_DAMAGE = 8.0F;
    private static final int FIRE_EFFECT_DURATION = 200;
    private static final int BURN_AURA_DURATION = 60;
    private static final float EXPLOSION_RADIUS = 1.5F;
    private static final int FIRE_SPREAD_RADIUS = 2;
    private static final int NO_TARGET = -1;

    public BloodFireSoulProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public BloodFireSoulProjectile(EntityType<? extends Projectile> type, Level level, LivingEntity owner) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.4, owner.getZ());

        // Give initial forward impulse so it doesn't spawn static
        this.setDeltaMovement(owner.getLookAngle().scale(HOMING_SPEED));

        this.selectInitialTarget(owner);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_ID, NO_TARGET);
    }

    private void selectInitialTarget(LivingEntity owner) {
        if (owner instanceof Player player) {
            Vec3 lookVec = player.getLookAngle();
            List<LivingEntity> candidates = this.level().getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(HOMING_SEARCH_RANGE),
                    e -> e != player && !e.isAlliedTo(player) && e.isAlive() && !e.isSpectator());

            Optional<LivingEntity> bestTarget = candidates.stream()
                    .filter(e -> {
                        Vec3 toTarget = e.position().subtract(player.position()).normalize();
                        return toTarget.dot(lookVec) > HOMING_CONE_THRESHOLD;
                    })
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)));

            bestTarget.ifPresent(livingEntity -> this.entityData.set(TARGET_ID, livingEntity.getId()));

        } else if (owner instanceof Mob mob) {
            LivingEntity mobTarget = mob.getTarget();
            if (mobTarget != null && mobTarget.isAlive()) {
                this.entityData.set(TARGET_ID, mobTarget.getId());
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        if (!this.level().isClientSide) {
            performMovementLogic();
            burnNearbyEntities();

            if (this.tickCount > MAX_LIFETIME) {
                this.discard();
            }
        } else {
            generateFireParticles();
        }

        moveProjectile();

        if (this.tickCount % 10 == 0) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.FIRE_AMBIENT, SoundSource.HOSTILE, 0.5F, 0.8F);
        }
    }

    private void moveProjectile() {
        Vec3 currentMovement = this.getDeltaMovement();
        this.setPos(this.getX() + currentMovement.x, this.getY() + currentMovement.y, this.getZ() + currentMovement.z);
    }

    private void performMovementLogic() {
        int targetId = this.entityData.get(TARGET_ID);

        // FALLBACK: If no target, maintain speed in current direction
        if (targetId == NO_TARGET) {
            Vec3 currentMotion = this.getDeltaMovement();
            if (currentMotion.lengthSqr() < 0.01) {
                // If stopped, force forward
                currentMotion = this.getLookAngle().scale(HOMING_SPEED);
            }
            this.setDeltaMovement(currentMotion.normalize().scale(HOMING_SPEED));
            return;
        }

        Entity targetEntity = this.level().getEntity(targetId);

        if (targetEntity instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
            // HOMING LOGIC
            Vec3 targetPos = livingTarget.getEyePosition().subtract(0, 0.5, 0);
            Vec3 desiredDirection = targetPos.subtract(this.position()).normalize();
            Vec3 currentDirection = this.getDeltaMovement().normalize();

            // Lerp the DIRECTION, not the velocity vector, to avoid speed loss
            Vec3 newDirection = currentDirection.lerp(desiredDirection, HOMING_TURN_FACTOR).normalize();

            this.setDeltaMovement(newDirection.scale(HOMING_SPEED));
        } else {
            // Target lost/dead -> Switch to dumb fire mode
            this.entityData.set(TARGET_ID, NO_TARGET);
        }
    }

    private void burnNearbyEntities() {
        List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.5D),
                entity -> entity != this.getOwner() && entity.isAlive());

        for (LivingEntity victim : victims) {
            victim.hurt(this.damageSources().inFire(), 1.0F);
            victim.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), BURN_AURA_DURATION, 0));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) return;

        this.level().explode(this, this.getX(), this.getY(), this.getZ(), EXPLOSION_RADIUS, Level.ExplosionInteraction.NONE);
        spawnFireLogic();
        this.discard();
    }

    private void spawnFireLogic() {
        BlockPos impactPos = this.blockPosition();

        for (int x = -FIRE_SPREAD_RADIUS; x <= FIRE_SPREAD_RADIUS; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -FIRE_SPREAD_RADIUS; z <= FIRE_SPREAD_RADIUS; z++) {
                    if (x * x + y * y + z * z <= FIRE_SPREAD_RADIUS * FIRE_SPREAD_RADIUS) {
                        BlockPos targetPos = impactPos.offset(x, y, z);
                        attemptPlaceFire(targetPos);
                    }
                }
            }
        }
    }

    private void attemptPlaceFire(BlockPos pos) {
        if (this.level().getBlockState(pos).isAir()) {
            BlockState belowState = this.level().getBlockState(pos.below());

            if (!belowState.isAir() && !belowState.is(ModBlocks.BLOOD_FIRE.get())) {
                this.level().setBlockAndUpdate(pos, ModBlocks.BLOOD_FIRE.get().defaultBlockState());
                BlockEntity be = this.level().getBlockEntity(pos);

                if (be instanceof BloodFireBlockEntity fireBe && this.getOwner() instanceof LivingEntity livingOwner) {
                    fireBe.setOwner(livingOwner);
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        target.hurt(this.damageSources().magic(), COLLISION_DAMAGE);
        if (target instanceof LivingEntity living) {
            setOnBloodFire(living, FIRE_EFFECT_DURATION, 0);
        }
    }

    private void generateFireParticles() {
        Vec3 motion = this.getDeltaMovement();
        Vec3 tailDir = motion.length() > 0 ? motion.normalize().scale(-1.0) : new Vec3(0, -1, 0);
        double baseX = this.getX();
        double baseY = this.getY() + 0.25D;
        double baseZ = this.getZ();

        for (int i = 0; i < 3; i++) {
            spawnCoreParticle(baseX, baseY, baseZ, tailDir);
        }

        for (int i = 0; i < 5; i++) {
            spawnMidParticle(baseX, baseY, baseZ, tailDir);
        }

        if (this.tickCount % 2 == 0) {
            this.level().addParticle(ModParticles.CHILL_FLAME_PARTICLE.get(), baseX, baseY, baseZ, 0, 0, 0);
        }
    }

    private void spawnCoreParticle(double x, double y, double z, Vec3 dir) {
        double spread = 0.1;
        double ox = (this.random.nextDouble() - 0.5D) * spread;
        double oy = (this.random.nextDouble() - 0.5D) * spread;
        double oz = (this.random.nextDouble() - 0.5D) * spread;

        float pink = 0.6f + this.random.nextFloat() * 0.4f;
        Vector3f color = new Vector3f(1.0f, pink, pink);

        this.level().addParticle(new MagicParticleOptions(color, 0.3f, false, 10),
                x + ox, y + oy, z + oz,
                dir.x * 0.3, dir.y * 0.3, dir.z * 0.3);
    }

    private void spawnMidParticle(double x, double y, double z, Vec3 dir) {
        double spread = 0.3;
        double ox = (this.random.nextDouble() - 0.5D) * spread;
        double oy = (this.random.nextDouble() - 0.5D) * spread;
        double oz = (this.random.nextDouble() - 0.5D) * spread;

        Vector3f color = new Vector3f(1.0f, 0.1f, 0.0f);

        this.level().addParticle(new MagicParticleOptions(color, 0.4f, false, 20),
                x + ox, y + oy, z + oz,
                dir.x * 0.15 + ox * 0.1, dir.y * 0.15 + oy * 0.1, dir.z * 0.15 + oz * 0.1);
    }

    @Override
    public Level getLevel() {
        return this.level();
    }
}