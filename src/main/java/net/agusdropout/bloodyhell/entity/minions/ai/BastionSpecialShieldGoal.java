package net.agusdropout.bloodyhell.entity.minions.ai;

import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SphericalShieldParticleOptions;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.List;

public class BastionSpecialShieldGoal extends Goal {

    private final BastionOfTheUnknownEntity bastion;
    private final float radius;
    private final int activeDuration;
    private final int cooldownDuration;

    private int activeTicks;
    private int nextUseTick;

    public BastionSpecialShieldGoal(BastionOfTheUnknownEntity bastion, float radius, int duration, int cooldown) {
        this.bastion = bastion;
        this.radius = radius;
        this.activeDuration = duration;
        this.cooldownDuration = cooldown;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.bastion.getTarget() == null) {
            return false;
        }
        return this.bastion.tickCount >= this.nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return this.activeTicks < this.activeDuration;
    }

    @Override
    public void start() {
        this.activeTicks = 0;

        Vec3 center = this.bastion.position().add(0, this.bastion.getBbHeight() / 2.0, 0);
        bastion.setSpecialShielding(true);

        Vector3f color = ColorHelper.hexToVector3f( bastion.getStripeColor());

        ParticleHelper.spawn(this.bastion.level(),
                new SphericalShieldParticleOptions(color.x, color.y, color.z, this.radius, this.activeDuration*2),
                center.x, center.y, center.z,
                0.0, 0.0, 0.0
        );
    }

    @Override
    public void tick() {

        this.activeTicks++;
        Vec3 center = this.bastion.position().add(0, this.bastion.getBbHeight() / 2.0, 0);

        List<Entity> nearbyEntities = this.bastion.level().getEntitiesOfClass(
                Entity.class,
                this.bastion.getBoundingBox().inflate(this.radius)
        );

        double radiusSqr = this.radius * this.radius;

        boolean didBounce = false;

        for (Entity entity : nearbyEntities) {


           if( shouldSkip(entity)) continue;

           if (entity.distanceToSqr(center) <= radiusSqr) {
               Vec3 pushDirection = entity.position().subtract(center).normalize();
               if (entity instanceof Projectile projectile) {
                   projectile.setDeltaMovement(pushDirection.scale(1.5));
                   projectile.hasImpulse = true;
                   didBounce = true;
               } else if (entity instanceof LivingEntity living) {
                   didBounce = true;
                   living.hurt(this.bastion.damageSources().generic(), 0.3f);
                   living.knockback(1.0, -pushDirection.x, -pushDirection.z);
               }
           }
        }

            if(didBounce) {
                this.bastion.playSound(ModSounds.UNKNOWN_SHIELD_BOUNCE.get(), 1.0F, 1.2F + (this.bastion.getRandom().nextFloat() - 0.5F) * 0.2F);
            }
    }

    private boolean shouldSkip(Entity entity) {
        if (entity == this.bastion) return true;

        if(entity instanceof Projectile projectile ) {

            if(projectile.getOwner() != null && projectile.getOwner() instanceof OwnableEntity ownableEntity){
                if(ownableEntity.getOwner() != null && ownableEntity.getOwner().equals(this.bastion.getOwner())){
                    return true;
                }
            }

            if(projectile.getOwner() != null && projectile.getOwner().equals(this.bastion.getOwner())) {
                return true;
            }

            if (projectile.onGround() || projectile.isInWater() || projectile.isInLava()) {
                return true;
            }


        }

        if ( bastion.getOwner() != null && entity.isAlliedTo(bastion.getOwner()) ) {
            return true;
        }

        if( entity instanceof Player player && player.isCreative() ) {
            return true;
        }

        if(entity instanceof Player player && player.equals(bastion.getOwner())){
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        this.nextUseTick = this.bastion.tickCount + this.cooldownDuration;
        bastion.setSpecialShielding(false);
    }
}