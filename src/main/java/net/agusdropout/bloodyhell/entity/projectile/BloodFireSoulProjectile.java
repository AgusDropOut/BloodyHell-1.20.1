package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class BloodFireSoulProjectile extends Projectile {

    public BloodFireSoulProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public BloodFireSoulProjectile(EntityType<? extends Projectile> type, Level level, LivingEntity owner) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.4, owner.getZ());
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();

        // 1. MOVEMENT & COLLISION
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        Vec3 delta = this.getDeltaMovement();
        this.setPos(this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z);

        // 2. SERVER LOGIC
        if (!this.level().isClientSide) {
            homingLogic();
            burnNearbyEntities();

            if (this.tickCount > 300) {
                this.discard();
            }
        }

        // 3. AUDIO (Constant Fire Hum)
        if (this.tickCount % 10 == 0) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.FIRE_AMBIENT, SoundSource.HOSTILE, 0.5F, 0.8F);
        }

        // 4. VISUALS
        if (this.level().isClientSide) {
            generateFireParticles();
        }
    }

    private void homingLogic() {
        if (this.tickCount < 10) return;

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(15.0D),
                entity -> entity != this.getOwner() && entity.isAlive() && !entity.isSpectator());

        if (!targets.isEmpty()) {
            LivingEntity target = targets.get(0);
            Vec3 targetPos = target.getEyePosition().subtract(0, 0.5, 0);
            Vec3 currentPos = this.position();
            Vec3 direction = targetPos.subtract(currentPos).normalize();

            Vec3 currentMotion = this.getDeltaMovement();
            Vec3 newVelocity = currentMotion.lerp(direction.scale(0.35), 0.05);

            this.setDeltaMovement(newVelocity);
        }
    }

    private void burnNearbyEntities() {
        List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.5D),
                entity -> entity != this.getOwner() && entity.isAlive());

        for (LivingEntity victim : victims) {
            victim.hurt(this.damageSources().inFire(), 1.0F);
            victim.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 60, 0));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) return;

        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.5F, Level.ExplosionInteraction.NONE);

        BlockPos impactPos = this.blockPosition();
        int fireRadius = 2;

        for (int x = -fireRadius; x <= fireRadius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -fireRadius; z <= fireRadius; z++) {
                    if (x*x + y*y + z*z <= fireRadius*fireRadius) {
                        BlockPos targetPos = impactPos.offset(x, y, z);
                        if (this.level().getBlockState(targetPos).isAir()) {
                            if (!this.level().getBlockState(targetPos.below()).isAir()) {
                                this.level().setBlockAndUpdate(targetPos, ModBlocks.BLOOD_FIRE.get().defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        target.hurt(this.damageSources().magic(), 8.0F);
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 200, 1));
        }
    }

    // --- VISUALS: PARTICLES ---
    private void generateFireParticles() {
        Vec3 motion = this.getDeltaMovement();
        // Calculate the "tail" direction (opposite to movement)
        Vec3 tailDir = motion.length() > 0 ? motion.normalize().scale(-1.0) : new Vec3(0, -1, 0);

        // Base position for the exhaust (Slightly offset to center of the sphere)
        double baseX = this.getX();
        double baseY = this.getY() + 0.25D;
        double baseZ = this.getZ();

        // 1. CORE (White -> Pink) - High Heat, Center
        // Spawns very close to center, moves strictly backwards
        for (int i = 0; i < 3; i++) {
            double spread = 0.1; // Very tight
            double ox = (this.random.nextDouble() - 0.5D) * spread;
            double oy = (this.random.nextDouble() - 0.5D) * spread;
            double oz = (this.random.nextDouble() - 0.5D) * spread;

            // Interpolate White (1,1,1) to Pink (1, 0.6, 0.6)
            float pink = 0.6f + this.random.nextFloat() * 0.4f;
            Vector3f color = new Vector3f(1.0f, pink, pink);

            this.level().addParticle(new MagicParticleOptions(
                            color,
                            0.3f,   // Small size
                            false,  // No Jitter
                            10),    // Short life (Hot burns fast)
                    baseX + ox, baseY + oy, baseZ + oz,
                    tailDir.x * 0.3, tailDir.y * 0.3, tailDir.z * 0.3); // Fast backward ejection
        }

        // 2. MID LAYER (Bright Red) - Medium Heat
        // Spawns slightly wider, moves backward but with some turbulence
        for (int i = 0; i < 5; i++) {
            double spread = 0.3;
            double ox = (this.random.nextDouble() - 0.5D) * spread;
            double oy = (this.random.nextDouble() - 0.5D) * spread;
            double oz = (this.random.nextDouble() - 0.5D) * spread;

            // Pure Bright Red
            Vector3f color = new Vector3f(1.0f, 0.1f, 0.0f);

            this.level().addParticle(new MagicParticleOptions(
                            color,
                            0.4f,   // Medium size
                            false,
                            20),    // Medium life
                    baseX + ox, baseY + oy, baseZ + oz,
                    tailDir.x * 0.15 + ox * 0.1, // Follow tail + slight expansion
                    tailDir.y * 0.15 + oy * 0.1,
                    tailDir.z * 0.15 + oz * 0.1);
        }

        // 3. OUTER LAYER (Dark Red) - Cooling Down
        // Spawns on the perimeter, drags behind slowly
        for (int i = 0; i < 8; i++) {
            double spread = 0.6; // Wide
            double ox = (this.random.nextDouble() - 0.5D) * spread;
            double oy = (this.random.nextDouble() - 0.5D) * spread;
            double oz = (this.random.nextDouble() - 0.5D) * spread;

            // Dark/Deep Red (0.5 to 0.7 Red)
            float darkness = 0.5f + this.random.nextFloat() * 0.2f;
            Vector3f color = new Vector3f(darkness, 0.0f, 0.0f);

            this.level().addParticle(new MagicParticleOptions(
                            color,
                            0.5f,   // Large size
                            false,
                            30),    // Long life (Smoke/cooling ash lingers)
                    baseX + ox, baseY + oy, baseZ + oz,
                    tailDir.x * 0.05, // Very slow movement
                    tailDir.y * 0.05,
                    tailDir.z * 0.05);
        }

        // Keep the Chill Flame trail for the ghost effect
        if (this.tickCount % 2 == 0) {
            this.level().addParticle(ModParticles.CHILL_FLAME_PARTICLE.get(),
                    baseX, baseY, baseZ, 0, 0, 0);
        }
    }
}