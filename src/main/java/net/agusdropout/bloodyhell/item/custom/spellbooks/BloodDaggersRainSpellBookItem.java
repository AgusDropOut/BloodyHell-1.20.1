package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodPortalEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.SpellBookDefinition;
import net.agusdropout.bloodyhell.item.custom.base.SpellCastContext;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class BloodDaggersRainSpellBookItem extends BaseSpellBookItem<BloodDaggersRainSpellBookItem> {

    private static final double MAX_CAST_RANGE = 20.0;
    private static final float IDEAL_PORTAL_HEIGHT = 6.0f;
    private static final int CEILING_CHECK_RADIUS = 10;

    public BloodDaggersRainSpellBookItem(Properties properties, SpellBookDefinition definition) {
        super(properties, definition);
    }

    @Override
    public void performSpell(SpellCastContext ctx) {
        List<Gem> gems = ctx.gems();

        if (!ctx.level().isClientSide) {
            Vec3 targetPos = calculateTargetPosition(ctx.level(), ctx.player());
            float heightOffset = calculatePortalOffset(ctx.level(), targetPos);

            BloodPortalEntity portal = new BloodPortalEntity(ctx.level(), targetPos.x, targetPos.y, targetPos.z, ctx.player(), heightOffset, gems);
            ctx.spawn(portal);
        }

        ctx.playSound(SoundEvents.PORTAL_TRAVEL, 0.8f, 1.2f);
        ctx.playSound(SoundEvents.WITHER_SPAWN, 0.5f, 1.5f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        float progress = (float) tick / getMinChargeTime();

        Vec3 targetPos = calculateTargetPosition(level, player);
        float heightOffset = calculatePortalOffset(level, targetPos);
        Vec3 portalPos = targetPos.add(0, heightOffset, 0);

        if (tick % 2 == 0) {
            ParticleHelper.spawnCylinder(level,
                    new MagicParticleOptions(new Vector3f(0.3f, 0.0f, 0.0f), 0.6f, false, 20),
                    player.position(), 0.8, 2.0, 3, 0.05);
        }

        if (tick % 2 == 0) {
            ParticleHelper.spawnRing(level,
                    ModParticles.BLOOD_PULSE_PARTICLE.get(),
                    targetPos.add(0, 0.2, 0), 3.5, 20, 0);
        }

        double vortexRadius = 1.0 + (progress * 3.0);
        int particleCount = 2 + (int)(progress * 3);

        for (int i = 0; i < particleCount; i++) {
            double angle = (tick * 0.4) + (i * (Math.PI * 2 / particleCount));
            double x = portalPos.x + Math.cos(angle) * vortexRadius;
            double z = portalPos.z + Math.sin(angle) * vortexRadius;

            Vec3 motion = portalPos.subtract(x, portalPos.y, z).normalize().scale(0.1);

            level.addParticle(ModParticles.CHILL_FLAME_PARTICLE.get(),
                    x, portalPos.y, z,
                    motion.x, motion.y, motion.z);
        }

        if (tick % 3 == 0) {
            double r = (level.random.nextDouble() * 2.5);
            double theta = level.random.nextDouble() * Math.PI * 2;
            level.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                    portalPos.x + Math.cos(theta) * r,
                    portalPos.y - 0.5,
                    portalPos.z + Math.sin(theta) * r,
                    0, -0.2, 0);
        }

        if (tick == getMinChargeTime()) {
            ParticleHelper.spawnDisc(level, ModParticles.BLOOD_PULSE_PARTICLE.get(), portalPos, 4.0, 50);
            ParticleHelper.spawnExplosion(level, ParticleTypes.PORTAL, portalPos, 50, 0.5, 1.0);
        }
    }

    private Vec3 calculateTargetPosition(Level level, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0f);
        Vec3 end = start.add(look.scale(MAX_CAST_RANGE));

        HitResult result = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        if (result.getType() == HitResult.Type.BLOCK) {
            return result.getLocation();
        }

        Vec3 airPos = result.getLocation();
        BlockPos groundPos = BlockPos.containing(airPos);

        while (groundPos.getY() > level.getMinBuildHeight() && level.getBlockState(groundPos).isAir()) {
            groundPos = groundPos.below();
        }

        return new Vec3(airPos.x, groundPos.getY() + 1.0, airPos.z);
    }

    private float calculatePortalOffset(Level level, Vec3 targetPos) {
        BlockPos startPos = BlockPos.containing(targetPos);
        float actualHeight = IDEAL_PORTAL_HEIGHT;

        for (int i = 1; i <= IDEAL_PORTAL_HEIGHT; i++) {
            BlockPos checkPos = startPos.above(i);
            BlockState state = level.getBlockState(checkPos);

            if (!state.getCollisionShape(level, checkPos).isEmpty()) {
                actualHeight = i - 1.0f;
                break;
            }
        }

        return Math.max(actualHeight, 2.0f);
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        if (tick % 10 == 0 && tick < getMinChargeTime()) {
            float pitch = 0.5f + (tick / (float)getMinChargeTime());
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PORTAL_AMBIENT, SoundSource.PLAYERS, 0.4f, pitch);
        }
    }

}