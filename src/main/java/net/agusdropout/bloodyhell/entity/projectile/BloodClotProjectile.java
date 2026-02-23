package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BloodStainEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class BloodClotProjectile extends Projectile {

    private int leechTimer = 0; // Tracks how long it has been stuck
    private boolean isLeeching = false;

    public BloodClotProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public BloodClotProjectile(Level level, double x, double y, double z) {
        this(ModEntityTypes.BLOOD_CLOT_PROJECTILE.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();


        if (this.isLeeching) {
            Entity vehicle = this.getVehicle();
            if (vehicle == null || !vehicle.isAlive() || leechTimer++ > 60) { // 3 Seconds max
                pop();
                return;
            }


            if (leechTimer % 20 == 0 && vehicle instanceof LivingEntity living) {
                living.hurt(this.level().damageSources().mobProjectile(this, (LivingEntity)getOwner()), 2.0f);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SLIME_BLOCK_STEP, this.getSoundSource(), 1.0f, 1.5f);
            }
            return;
        }


        if (this.level().isClientSide) {

            if (this.tickCount % 2 == 0) {
                this.level().addParticle(ModParticles.BLOOD_PARTICLES.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS) this.onHit(hitresult);

        Vec3 movement = this.getDeltaMovement();
        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);

        float drag = 0.98f;
        float gravity = 0.05f;
        this.setDeltaMovement(movement.scale(drag).subtract(0, gravity, 0));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level().isClientSide) return;

        Entity target = result.getEntity();


        if (target == this.getOwner() || target instanceof BloodClotProjectile) return;


        this.startRiding(target, true);
        this.isLeeching = true;


        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SLIME_BLOCK_HIT, this.getSoundSource(), 1.0f, 1.0f);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            Vec3 hitPos = result.getLocation();
            BloodStainEntity stain = new BloodStainEntity(this.level(), hitPos.x, hitPos.y, hitPos.z, result.getDirection());
            this.level().addFreshEntity(stain);
            pop();
        }
    }

    private void pop() {
        if (this.level().isClientSide) {
            for(int i=0; i<8; i++) {
                this.level().addParticle(ParticleTypes.ITEM_SLIME, this.getX(), this.getY(), this.getZ(), (random.nextDouble()-0.5)*0.3, 0.2, (random.nextDouble()-0.5)*0.3);
            }
        } else {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SLIME_BLOCK_BREAK, this.getSoundSource(), 1.0f, 0.5f);


            if (this.getVehicle() instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
                living.hurt(this.level().damageSources().mobProjectile(this, (LivingEntity)getOwner()), 4.0f);
            }
            this.discard();
        }
    }

    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}