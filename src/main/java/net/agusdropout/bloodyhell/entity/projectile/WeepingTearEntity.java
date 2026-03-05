package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.entity.minions.custom.WeepingOcularEntity;
import net.agusdropout.bloodyhell.entity.projectile.base.AbstractColoredProjectile;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ShockwaveParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.UUID;

public class WeepingTearEntity extends AbstractColoredProjectile {

    private UUID minionOwnerUUID;

    public WeepingTearEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.setDamage(3.0f);
    }

    public WeepingTearEntity(Level level, double x, double y, double z, WeepingOcularEntity owner) {
        super(ModEntityTypes.WEEPING_TEAR_PROJECTILE.get(), level);
        this.setPos(x, y, z);
        this.setDamage(3.0f);
        this.setOwners(owner);
    }

    public void setOwners(WeepingOcularEntity minion) {
        this.setOwner(minion);
        this.minionOwnerUUID = minion.getOwnerUUID();

        int baseHex = minion.getStripeColor();
        Vector3f brightened = ColorHelper.brighten(ColorHelper.hexToVector3f(baseHex), 1.3f);
        int highlightHex = ColorHelper.vector3fToHex(brightened);

        this.setProjectileColors(baseHex, highlightHex);
    }

    @Override
    protected boolean canHitEntity(net.minecraft.world.entity.Entity target) {
        if (!super.canHitEntity(target)) return false;
        if (target.equals(this.getOwner())) return false;
        if (this.minionOwnerUUID != null && target.getUUID().equals(this.minionOwnerUUID)) return false;
        if (target instanceof AbstractMinionEntity otherMinion && this.minionOwnerUUID != null && this.minionOwnerUUID.equals(otherMinion.getOwnerUUID())) return false;

        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            this.explode();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide && result.getType() == HitResult.Type.BLOCK) {
            this.explode();
        }
    }

    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0f, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    @Override
    protected void handleClientEffects() {
        if (this.random.nextFloat() < 0.6f) {
            Vector3f color = this.getBaseColor();
            ChillFallingParticleOptions tearParticle = new ChillFallingParticleOptions(color, 0.04f, 20, 5);
            ParticleHelper.spawn(this.level(), tearParticle, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        if (this.random.nextFloat() < 0.7f) {
            Vector3f color = this.getBaseColor();
            Vec3 motion = this.getDeltaMovement();
            ShockwaveParticleOptions shockWaveParticle = new ShockwaveParticleOptions(color, 0.1f, 0.3f);
            ParticleHelper.spawn(this.level(), shockWaveParticle, this.getX(), this.getY(), this.getZ(),motion.x , motion.y, motion.z);
        }



        if (this.random.nextFloat() < 0.3f) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }
}