package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;

import net.agusdropout.bloodyhell.entity.projectile.spell.BloodFireColumnEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.SpellBookDefinition;
import net.agusdropout.bloodyhell.item.custom.base.SpellCastContext;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BloodFireColumnSpellBookItem extends BaseSpellBookItem {

    private static final double SPELL_RADIUS = 15.0;

    public BloodFireColumnSpellBookItem(Properties properties, SpellBookDefinition definition) {
        super(properties, definition);
    }

    @Override
    public void performSpell(SpellCastContext ctx) {
        if (!ctx.level().isClientSide) {

            List<Gem> gems = ctx.gems();
            int projectileCount = 1 + getProjectileAdditionalFromGems(gems);
            List <Vec3> targetPositions = findTargetPosition(ctx.level(), ctx.player(),projectileCount);

            if(targetPositions.isEmpty()){
                Vec3 fallbackPos = getPlayerColumnPosition(ctx.player(),ctx.level());
                targetPositions.add(fallbackPos);
            }

            for (Vec3 targetPos : targetPositions) {

                    BloodFireColumnEntity column = new BloodFireColumnEntity(
                            ModEntityTypes.BLOOD_FIRE_COLUMN_PROJECTILE.get(),
                            ctx.level(),
                            ctx.player(),
                            targetPos.x,
                            targetPos.y,
                            targetPos.z,
                            gems
                    );
                    ctx.spawn(column);

            }

        }

        ctx.playSound(SoundEvents.FIRECHARGE_USE, 1.0f, 0.8f);
    }

    private List<Vec3> findTargetPosition(Level level, Player player,int targetsCount) {
        List<Vec3> targetPositions = new ArrayList<>();

        AABB searchBox = player.getBoundingBox().inflate(SPELL_RADIUS, 5.0, SPELL_RADIUS);
        List<LivingEntity> potentialTargets = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != player && !entity.isAlliedTo(player) && entity.isAlive());

        Optional<LivingEntity> closestFoe = potentialTargets.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)));
        if(closestFoe.isPresent()){
            targetPositions.add(closestFoe.get().position());
        }
        for (LivingEntity entity : potentialTargets) {
            if (closestFoe.isPresent() && entity != closestFoe.get()) {
                targetPositions.add(entity.position());
                if (targetPositions.size() >= targetsCount) {
                    break;
                }
            }
        }
        return targetPositions;
    }

    private Vec3 getPlayerColumnPosition(Player player,Level level){

        Vec3 startPos = player.getEyePosition();
        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 endPos = startPos.add(viewVector.scale(SPELL_RADIUS));

        HitResult result = level.clip(new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        if (result.getType() == HitResult.Type.BLOCK) {
            if (result instanceof net.minecraft.world.phys.BlockHitResult blockHit) {
                if (blockHit.getDirection() == Direction.UP) {
                    return result.getLocation();
                }
            }
        }

        double targetX = result.getLocation().x;
        double targetZ = result.getLocation().z;

        double startY = Math.max(player.getEyeY(), result.getLocation().y) + 0.5;

        Vec3 floorRayStart = new Vec3(targetX, startY, targetZ);
        Vec3 floorRayEnd = new Vec3(targetX, level.getMinBuildHeight(), targetZ);

        HitResult floorResult = level.clip(new ClipContext(
                floorRayStart,
                floorRayEnd,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        if (floorResult.getType() == HitResult.Type.BLOCK) {
            return floorResult.getLocation();
        }

        return floorResult.getLocation();
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        double speed = 0.2 + (tick * 0.01);
        double radius = 2.0;
        double angle = (tick * speed) % (2 * Math.PI);

        for (int i = 0; i < 3; i++) {
            double currentAngle = angle + (i * (Math.PI * 2 / 3));
            double xOffset = radius * Math.cos(currentAngle);
            double zOffset = radius * Math.sin(currentAngle);
            double yOffset = 0.1 + (tick * 0.02);
            if (yOffset > 1.5) yOffset = 0.1;

            level.addParticle(ModParticles.BLOOD_SIGIL_PARTICLE.get(),
                    player.getX() + xOffset,
                    player.getY() + yOffset,
                    player.getZ() + zOffset,
                    0, 0.05, 0);
        }

        if (tick == getMinChargeTime()) {
            spawnParticleCircle(level, player, ModParticles.BLOOD_SIGIL_PARTICLE.get(), 2.5, 40);
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        if (tick % 5 == 0 && tick < getMinChargeTime()) {
            float pitch = 0.5f + (tick / (float)getMinChargeTime());
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.5f, pitch);
        }
    }

    protected void spawnParticleCircle(Level level, Player player, ParticleOptions particleData, double radius, int particleCount) {
        double y = player.getY() + 0.1;
        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            level.addParticle(particleData,
                    player.getX() + radius * Math.cos(angle),
                    y,
                    player.getZ() + radius * Math.sin(angle),
                    0, 0, 0);
        }
    }

}