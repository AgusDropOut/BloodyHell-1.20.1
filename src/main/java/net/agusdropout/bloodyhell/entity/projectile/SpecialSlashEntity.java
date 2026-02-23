package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes; // Asegurate de tener esto
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class SpecialSlashEntity extends Projectile {

    private int lifeTime = 0;
    private static final int MAX_LIFE = 20;
    private float damage = 10.0f;

    public SpecialSlashEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public SpecialSlashEntity(Level level, LivingEntity shooter, float damage) {
        this(ModEntityTypes.SPECIAL_SLASH.get(), level);
        this.setOwner(shooter);
        this.damage = damage;


        this.setPos(shooter.getX(), shooter.getEyeY() - 0.5, shooter.getZ());

        this.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();


        if (this.lifeTime++ >= MAX_LIFE) {
            this.discard();
            return;
        }


        Vec3 currentPos = this.position();
        Vec3 motion = this.getDeltaMovement();
        Vec3 nextPos = currentPos.add(motion);

        if (this.level().isClientSide) {

            int steps = 5;

            for (int i = 0; i < steps; i++) {
                double progress = (double) i / steps;
                double tx = Mth.lerp(progress, currentPos.x, nextPos.x);
                double ty = Mth.lerp(progress, currentPos.y, nextPos.y);
                double tz = Mth.lerp(progress, currentPos.z, nextPos.z);


                int particlesPerStep = 3;

                for (int j = 0; j < particlesPerStep; j++) {

                    double spread = 1.5;
                    double offX = (this.random.nextDouble() - 0.5) * spread;
                    double offY = (this.random.nextDouble() - 0.5) * spread;
                    double offZ = (this.random.nextDouble() - 0.5) * spread;


                    double speed = 0.2;
                    double vX = (this.random.nextDouble() - 0.5) * speed;
                    double vY = (this.random.nextDouble() - 0.5) * speed;
                    double vZ = (this.random.nextDouble() - 0.5) * speed;

                    this.level().addParticle(ModParticles.MAGIC_LINE_PARTICLE.get(),
                            tx + offX, ty + offY, tz + offZ,
                            vX, vY, vZ);
                }
            }
        }


        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.onHit(hitResult);
        }
        else if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.discard();
        }


        this.setPos(nextPos.x, nextPos.y, nextPos.z);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = this.getOwner();


        if (target == owner) return;


        target.hurt(this.level().damageSources().mobAttack((LivingEntity) owner), this.damage);



    }


    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.lifeTime = tag.getInt("LifeTime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeTime", this.lifeTime);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}