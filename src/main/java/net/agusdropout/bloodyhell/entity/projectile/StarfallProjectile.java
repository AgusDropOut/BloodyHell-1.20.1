package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake; // Importar Camera Shake
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SimpleBlockParticleOptions;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class StarfallProjectile extends Projectile implements GeoEntity {

    private int radius = 8;
    private int chargingCooldown = 40;
    private int lifeTicks = 300;
    private float damage = 10.0f;
    private LivingEntity target;
    private int soundCooldown = 0;

    private static final EntityDataAccessor<Boolean> SHOULD_EXPLODE = SynchedEntityData.defineId(StarfallProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EXPLODED = SynchedEntityData.defineId(StarfallProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EXPLODED_CLIENTSIDE = SynchedEntityData.defineId(StarfallProjectile.class, EntityDataSerializers.BOOLEAN);

    // NUEVO FLAG
    private static final EntityDataAccessor<Boolean> BREAKS_BLOCKS = SynchedEntityData.defineId(StarfallProjectile.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public StarfallProjectile(EntityType<? extends StarfallProjectile> type, Level level) {
        super(type, level);
    }

    public StarfallProjectile(Level level, double x, double y, double z, float damage, LivingEntity owner, LivingEntity target) {
        this(ModEntityTypes.STARFALL_PROJECTILE.get(), level);
        this.damage = damage;
        this.setOwner(owner);
        this.target = target;
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SHOULD_EXPLODE, false);
        this.entityData.define(HAS_EXPLODED, false);
        this.entityData.define(HAS_EXPLODED_CLIENTSIDE, false);
        // Por defecto true, pero lo cambiaremos en el Goal
        this.entityData.define(BREAKS_BLOCKS, true);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        if(chargingCooldown > 0) {
            chargingCooldown--;
        } else {
            if(isShouldExplode()) {
                explode();
            }
            if (!isHasExplode()) {
                checkCollisions();
                // Lógica de movimiento guiado (Homing)
                Vec3 current = this.getDeltaMovement();
                Vec3 desiredHorizontal = Vec3.ZERO;
                if (target != null && target.isAlive()) {
                    Vec3 direction = new Vec3(target.getX() - this.getX(), 0, target.getZ() - this.getZ()).normalize();
                    double horizontalSpeed = 0.3;
                    desiredHorizontal = direction.scale(horizontalSpeed);
                    double lerpFactor = 0.01;
                    Vec3 horizontalMotion = new Vec3(
                            Mth.lerp(lerpFactor, current.x, desiredHorizontal.x),
                            0,
                            Mth.lerp(lerpFactor, current.z, desiredHorizontal.z)
                    );

                    double decay = -0.03;
                    Vec3 motion = new Vec3(horizontalMotion.x, decay, horizontalMotion.z);
                    this.setDeltaMovement(motion);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }

                spawnSuckingParticles();
                if (soundCooldown <= 0) {
                    level().playSound(null, this.blockPosition(), ModSounds.STARFALL_AMBIENT_SOUND.get(), SoundSource.PLAYERS, 1.0f, 1.0f + (this.random.nextFloat() - 0.5f) * 0.2f);
                    soundCooldown = 20;
                } else {
                    soundCooldown--;
                }
            } else {
                if(isHasExplodeClientSide()) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }
            lifeTicks--;
        }

        if (lifeTicks <= 0) {
            this.discard();
        }
    }

    public void explode() {
        // Objeto explosión base para cálculos de resistencia
        Explosion explosion = new Explosion(
                this.level(),
                this,
                null,
                null,
                this.getOnPos().getX(),
                this.getOnPos().getY(),
                this.getOnPos().getZ(),
                (float) radius,
                false,
                Explosion.BlockInteraction.KEEP // NONE aquí para controlar destrucción manualmente abajo
        );

        spawnStarFallParticle();

        // 1. EFECTO CAMERA SHAKE
        EntityCameraShake.cameraShake(this.level(), this.position(), 25.0f, 0.4f, 10, 10);

        if (!this.level().isClientSide) {
            spawnDustWave();
            spawnSmokeParticles();

            // 2. LOGICA DE BLOQUES (SOLO SI ESTÁ ACTIVADA)
            if (shouldBreakBlocks()) {
                BlockPos center = this.blockPosition();
                int radius = 8;
                List<BlockPos> nearbyBlocks = new ArrayList<>();

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos pos = center.offset(x, y, z);
                            if (!this.level().isEmptyBlock(pos)) {
                                nearbyBlocks.add(pos);
                            }
                        }
                    }
                }

                // Romper algunos bloques aleatorios (Efecto visual de impacto central)
                for (int i = 0; i < Math.min(nearbyBlocks.size(), 24); i++) {
                    BlockPos pos = nearbyBlocks.get(this.random.nextInt(nearbyBlocks.size()));
                    BlockState state = this.level().getBlockState(pos);

                    if (state.getDestroySpeed(this.level(), pos) >= 0 && state.getExplosionResistance(this.level(), pos, explosion) < radius) {
                        this.level().removeBlock(pos, false);
                        EntityFallingBlock block = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), this.level(), 60, state);
                        block.setPos(this.getX(), this.getY() + 1, this.getZ());
                        double speed = 0.5 + this.random.nextDouble() * 0.5;
                        double angle = this.random.nextDouble() * Math.PI * 2;
                        block.setDeltaMovement(Math.cos(angle) * speed, 0.5 + this.random.nextDouble() * 0.5, Math.sin(angle) * speed);
                        this.level().addFreshEntity(block);
                    }
                }
            }
        }

        level().playSound(null, this.blockPosition(), ModSounds.STARFALL_EXPLOSION_SOUND.get(), SoundSource.PLAYERS, 2.0f, 2.0f);

        // Daño de área y destrucción masiva
        this.customExplosion(radius);

        this.setHasExploded(true);
        this.setShouldExplode(false);

        if(this.level().isClientSide()) {
            setHasExplodedClientSide(true);
        }
    }

    private void customExplosion(double radius) {
        if (this.level().isClientSide) return;

        // 1. DAÑO A ENTIDADES (Siempre ocurre)
        List<LivingEntity> entities = this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(radius)
        );

        for (Entity e : entities) {
            if (e instanceof LivingEntity living && e != this.getOwner()) {
                living.hurt(this.damageSources().explosion(this, living), damage);
            }
        }

        // 2. DESTRUCCION DE BLOQUES (Solo si shouldBreakBlocks es true)
        if (!shouldBreakBlocks()) return;

        BlockPos center = this.blockPosition();
        List<BlockPos> toRemove = new ArrayList<>();
        List<BlockPos> extraFragments = new ArrayList<>();

        double[] shootThetas = {0, Math.PI / 2, Math.PI, 3 * Math.PI / 2};
        double maxDist = radius + 2;

        for (int x = -(int) maxDist; x <= maxDist; x++) {
            for (int y = -(int) maxDist; y <= maxDist; y++) {
                for (int z = -(int) maxDist; z <= maxDist; z++) {
                    double dx = x + 0.5; double dy = y + 0.5; double dz = z + 0.5;
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist > maxDist) continue;

                    BlockPos pos = center.offset(x, y, z);
                    if (this.level().isEmptyBlock(pos)) continue;

                    double theta = Math.atan2(dz, dx);
                    double phi = Math.acos(dy / (dist + 0.0001));
                    double r = radius + 0.8 * Math.sin(6 * theta) * Math.sin(3 * phi);

                    if (dist <= r) {
                        toRemove.add(pos);
                    } else {
                        // Lógica de fragmentos extra
                        for (double shootTheta : shootThetas) {
                            double angleDiff = Math.abs(theta - shootTheta);
                            angleDiff = Math.min(angleDiff, 2 * Math.PI - angleDiff);
                            if (angleDiff < Math.PI / 12) {
                                double maxFragmentRadius = 3.0 * (1 - dist / radius);
                                double perpendicularDist = Math.sqrt(Math.pow(dist * Math.sin(phi), 2));
                                if (perpendicularDist < maxFragmentRadius) {
                                    extraFragments.add(pos);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        Explosion explosion = new Explosion(this.level(), this, null, null, this.getOnPos().getX(), this.getOnPos().getY(), this.getOnPos().getZ(), (float) radius, false, Explosion.BlockInteraction.DESTROY);

        for (BlockPos pos : toRemove) {
            BlockState state = this.level().getBlockState(pos);
            if (state.getDestroySpeed(this.level(), pos) >= 0 && state.getExplosionResistance(this.level(), pos, explosion) < radius) {
                this.level().removeBlock(pos, false);
                if (this.random.nextInt(100) < 50) {
                    spawnFlyingBlock(pos, state, 0.5, 0.5);
                }
            }
        }

        for (BlockPos pos : extraFragments) {
            BlockState state = this.level().getBlockState(pos);
            if (state.getDestroySpeed(this.level(), pos) >= 0 && state.getExplosionResistance(this.level(), pos, explosion) < radius) {
                this.level().removeBlock(pos, false);
                if (this.random.nextInt(100) < 30) {
                    spawnFlyingBlock(pos, state, 0.3, 0.3);
                }
            }
        }
    }

    private void checkCollisions() {
        if (!level().noCollision(this, getBoundingBox().inflate(1))) {
            this.setShouldExplode(true);
        }
    }

    private void spawnStarFallParticle(){
        if (!(this.level() instanceof ClientLevel clientLevel)) return;
        clientLevel.addParticle(ModParticles.STAR_EXPLOSION_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
    }

    private void spawnDustWave() {
        int count = 80;
        double speed = 40;
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            BlockState[] candidates = new BlockState[]{
                    Blocks.YELLOW_STAINED_GLASS.defaultBlockState(),
                    Blocks.ORANGE_STAINED_GLASS.defaultBlockState(),
                    Blocks.GLOWSTONE.defaultBlockState(),
                    Blocks.MAGMA_BLOCK.defaultBlockState()
            };
            BlockState state = candidates[this.random.nextInt(candidates.length)];
            SimpleBlockParticleOptions options = new SimpleBlockParticleOptions(state, 1, true);
            ((ServerLevel) this.level()).sendParticles(options, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0.0);
        }
    }

    public void spawnSmokeParticles(){
        int smokeCount = 60;
        for (int i = 0; i < smokeCount; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 25;
            double offsetZ = (this.random.nextDouble() - 0.5) * 55;
            double vy = 0.01 + this.random.nextDouble() * 0.005;
            ServerLevel sl = (ServerLevel) this.level();
            sl.sendParticles(ParticleTypes.SMOKE, this.getX() + offsetX, this.getY()+0.1, this.getZ() + offsetZ, 1, 0, vy, 0, 0.0);
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() + offsetX, this.getY()+0.1, this.getZ() + offsetZ, 1, 0, vy, 0, 0.0);
            sl.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getX() + offsetX, this.getY()+0.1, this.getZ() + offsetZ, 1, 0, vy, 0, 0.0);
        }
    }

    private void spawnSuckingParticles() {
        if (!(this.level() instanceof ClientLevel)) return;
        int particleCount = 5;
        double radius = 3;
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * radius * 2;
            double offsetY = (this.random.nextDouble() - 0.5) * radius * 2;
            double offsetZ = (this.random.nextDouble() - 0.5) * radius * 2;
            double px = this.getX() + offsetX;
            double py = this.getY() + offsetY;
            double pz = this.getZ() + offsetZ;
            double dx = this.getX() - px;
            double dy = this.getY()+1 - py;
            double dz = this.getZ() - pz;
            double speedFactor = 0.1;
            this.level().addParticle(ModParticles.MAGIC_LINE_PARTICLE.get(), px, py, pz, dx * speedFactor, dy * speedFactor, dz * speedFactor);
        }
    }

    private void spawnFlyingBlock(BlockPos pos, BlockState state, double baseSpeed, double baseVy) {
        EntityFallingBlock block = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), this.level(), 60, state);
        block.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        double speed = baseSpeed + this.random.nextDouble() * baseSpeed;
        double angle = this.random.nextDouble() * Math.PI * 2;
        double vy = baseVy + this.random.nextDouble() * baseVy;
        block.setDeltaMovement(Math.cos(angle) * speed, vy, Math.sin(angle) * speed);
        this.level().addFreshEntity(block);
    }

    // Getters y Setters
    public boolean isShouldExplode() { return this.entityData.get(SHOULD_EXPLODE); }
    public void setShouldExplode(boolean value) { this.entityData.set(SHOULD_EXPLODE, value); }
    public boolean isHasExplode() { return this.entityData.get(HAS_EXPLODED); }
    public void setHasExploded(boolean value) { this.entityData.set(HAS_EXPLODED, value); }
    public boolean isHasExplodeClientSide() { return this.entityData.get(HAS_EXPLODED_CLIENTSIDE); }
    public void setHasExplodedClientSide(boolean value) { this.entityData.set(HAS_EXPLODED_CLIENTSIDE, value); }

    // Getter y Setter para la destrucción de bloques
    public boolean shouldBreakBlocks() { return this.entityData.get(BREAKS_BLOCKS); }
    public void setBreaksBlocks(boolean value) { this.entityData.set(BREAKS_BLOCKS, value); }

    // Geckolib
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
    }
    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("moving", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return factory; }

    public int getLifeTicks(){
        return lifeTicks;
    }
}