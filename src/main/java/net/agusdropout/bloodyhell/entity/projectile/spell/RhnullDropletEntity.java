package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.particle.ParticleOptions.RadialDistortionParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class RhnullDropletEntity extends Projectile {

    private float damage = 4.0f;
    private int lifeTicks = 0;

    public RhnullDropletEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public RhnullDropletEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.RHNULL_DROPLET_PROJECTILE.get(), level);
        this.setPos(x, y, z);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();
        this.lifeTicks++;

        Vec3 movement = this.getDeltaMovement();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);

        if (this.level().isClientSide) {
            // Spawn the radial distortion shockwave every 3 ticks.
            // A lifespan of 10 ticks keeps it snappy and prevents screen clutter.
           // if (this.tickCount % 3 == 0) {
           //     this.level().addParticle(
           //             new RadialDistortionParticleOptions(this.getXRot(), this.getYRot(), 10),
           //             this.getX(), this.getY(), this.getZ(),
           //             0.0, 0.0, 0.0
           //     );
           // }
        }

        this.setDeltaMovement(movement.scale(0.99f));

        if (this.tickCount > 60) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            target.hurt(this.damageSources().magic(), this.damage);
            target.invulnerableTime = 0;
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    public int getLifeTicks() {
        return this.lifeTicks;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}