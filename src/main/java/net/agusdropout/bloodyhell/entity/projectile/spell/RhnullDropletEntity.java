package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.particle.ParticleOptions.RadialDistortionParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SmallGlitterParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
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
import org.joml.Vector3f;

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

            handleClientEffects();
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
    private void handleClientEffects(){
        if(this.lifeTicks == 2){
            ParticleHelper.spawn(this.level(), new RadialDistortionParticleOptions(this.getXRot(), this.getYRot(), 10), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }


        if(this.random.nextFloat() < 0.9f){

            double vxPercent = this.getDeltaMovement().x / 15;
            double vyPercent = this.getDeltaMovement().y / 15;
            double vzPercent = this.getDeltaMovement().z / 15;

            Vector3f gradientColor = ParticleHelper.gradient3(random.nextFloat(), new Vector3f(1f, 0.97f, 0.0f), new Vector3f(1.0f, 0.8f, 0.0f), new Vector3f(1f, 0.5f, 0.0f));

            ParticleHelper.spawn(this.level(),new SmallGlitterParticleOptions(gradientColor, 0.7f, false, 10), this.getX(), this.getY(), this.getZ(), vxPercent , vyPercent, vzPercent);
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