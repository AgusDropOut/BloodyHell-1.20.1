package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.SelioraEntity;
import net.agusdropout.bloodyhell.entity.projectile.StarfallProjectile;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StarFallGoal extends Goal {
    private SelioraEntity entity;
    private int animationTicks;

    public StarFallGoal(SelioraEntity entity) {
        this.entity = entity;
        this.animationTicks = entity.getStarFallTicksDuration();
    }

    @Override
    public boolean canUse() {
        return entity.canUseStarFallGoal();
    }

    @Override
    public void start() {
        this.animationTicks = entity.getStarFallTicksDuration();
        Level level = entity.level();
        LivingEntity target = entity.getTarget();
        if (target != null) {
            entity.setStarFallActive(true);
            entity.level().playSound(null, this.entity.blockPosition(), ModSounds.SELIORA_THROW_SOUND.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

            StarfallProjectile starfallProjectile = new StarfallProjectile(level, entity.getX(), entity.getY() + 4, entity.getZ(), 20, entity, entity.getTarget());

            // CONFIGURACIÓN IMPORTANTE: No romper bloques en la arena del jefe
            starfallProjectile.setBreaksBlocks(false);

            level.addFreshEntity(starfallProjectile);
        }
    }

    @Override
    public void tick() {
        if (animationTicks > 0) {
            entity.setDeltaMovement(Vec3.ZERO);
            LivingEntity target = entity.getTarget();
            if (target != null && target.isAlive()) {
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
            }
            animationTicks--;
        } else {
            stop();
        }
    }

    @Override
    public void stop() {
        entity.setStarFallActive(false);
        animationTicks = entity.getStarFallTicksDuration();
        entity.setStarFallCooldown(entity.getStarFallMaxCooldown());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}