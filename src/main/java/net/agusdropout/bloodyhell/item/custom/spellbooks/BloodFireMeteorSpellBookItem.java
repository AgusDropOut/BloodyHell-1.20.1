package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodFireMeteorEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BloodFireMeteorSpellBookItem extends BaseSpellBookItem<BloodFireMeteorSpellBookItem> {
    private static final int COST = 50;

    @Override
    public int getCrimsonCost() {
        return COST;
    }

    public BloodFireMeteorSpellBookItem(Properties properties) {
        super(properties);
    }



    @Override
    public void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        if (!level.isClientSide) {
            List<Gem> gems = super.getGemsFromItemStack(itemStack);
            List<BloodFireMeteorEntity> meteorEntities = new ArrayList<>();
            int projectileCount = 1 + getProjectileAdditionalFromGems(gems);
            RandomSource random = level.random;

            for (int i = 0; i < projectileCount; i++) {
                BloodFireMeteorEntity meteor = new BloodFireMeteorEntity(
                        level,
                        player,
                        20.0f,
                        1.5f,
                        1.5f,
                        random.nextInt(0,100),
                        gems
                );
                meteorEntities.add(meteor);
                level.addFreshEntity(meteor);
            }
            placeMeteors(player, level, meteorEntities);
        }

        // Impact sound on fire
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    @Override
    public void spawnProgressiveParticles(Level level, Player player, int tick) {
        // Spin speed increases with charge time
        double speed = 0.2 + (tick * 0.01);
        double radius = 2.0;

        // Base rotating circle
        double angle = (tick * speed) % (2 * Math.PI);

        // Spawn 3 particles per tick for a trail effect
        for (int i = 0; i < 3; i++) {
            double currentAngle = angle + (i * (Math.PI * 2 / 3));
            double xOffset = radius * Math.cos(currentAngle);
            double zOffset = radius * Math.sin(currentAngle);

            // Particles spiral up slightly
            double yOffset = 0.1 + (tick * 0.02);
            if (yOffset > 1.5) yOffset = 0.1; // Reset height if too high

            level.addParticle(ModParticles.BLOOD_SIGIL_PARTICLE.get(),
                    player.getX() + xOffset,
                    player.getY() + yOffset,
                    player.getZ() + zOffset,
                    0, 0.05, 0); // Slight upward velocity
        }

        // Burst visual when fully charged
        if (tick == getMinChargeTime()) {
            spawnParticleCircle(level, player, ModParticles.BLOOD_SIGIL_PARTICLE.get(), 2.5, 40);
        }
    }

    @Override
    public void playChargeSound(Level level, Player player, int tick) {
        // Play sound every 5 ticks, pitch increases as charge builds
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

    private void placeMeteors(LivingEntity owner, Level level, List<BloodFireMeteorEntity> meteorEntities) {
        Vec3 start = owner.getEyePosition();
        double targetHeight = 5.0;
        Vec3 end = start.add(0, targetHeight, 0);

        // Raycast upwards to find the ceiling
        BlockHitResult result = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));

        double spawnY;
        if (result.getType() == HitResult.Type.BLOCK) {
            spawnY = result.getLocation().y - 1.0;
        } else {
            spawnY = start.y + targetHeight;
        }

        // Safety check to prevent spawning inside the owner
        if (spawnY < owner.getEyeY()) {
            spawnY = owner.getEyeY() + 0.5;
        }

        for (BloodFireMeteorEntity meteor : meteorEntities){

        }

        int count = meteorEntities.size();

        // Configurable: How far from the center they spawn
        double radius = 2.0;

        // Configurable: Rotate the whole ring so it aligns with where player is looking
        // (Optional: remove this line to align to North/South grid)
        float startRotation = -owner.getYRot() * ((float)Math.PI / 180F);

        for (int i = 0; i < count; i++) {
            BloodFireMeteorEntity meteor = meteorEntities.get(i);

            // EDGE CASE: If only 1 meteor, spawn it perfectly centered
            if (count == 1) {
                meteor.setPos(owner.getX(), spawnY, owner.getZ());
            }
            else {
                // 1. Calculate Angle
                // StartAngle + (Step * Index)
                double angle = startRotation + (i * (Math.PI * 2 / count));

                // 2. Calculate Offset
                double offsetX = Math.sin(angle) * radius;
                double offsetZ = Math.cos(angle) * radius;

                // 3. Set Position
                meteor.setPos(owner.getX() + offsetX, spawnY, owner.getZ() + offsetZ);
            }
        }
    }


    @Override
    public int getMinChargeTime() {
        return 20; // 1 second charge
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public String getSpellBookId() {
        return "bloodfire_meteor_spellbook";
    }
}