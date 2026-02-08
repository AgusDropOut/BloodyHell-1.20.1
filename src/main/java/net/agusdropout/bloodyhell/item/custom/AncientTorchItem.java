package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.effect.ModEffects;

import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncBloodFireEffectPacket;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class AncientTorchItem extends BlockItem  {

    // --- POSITION CONFIGURATION ---
    // Adjust these values to perfectly align the particles with the hand
    private static final double OFFSET_FORWARD = 0.15D; // How far in front of the player (Distance from face)
    private static final double OFFSET_SIDE    = 0.20D; // How far to the right/left (Width of shoulders)
    private static final double OFFSET_DOWN    = 0.00D;  // How far down from eye level (Arm height)

    public AncientTorchItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(!attacker.level().isClientSide) {
            target.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 200, 0));
            ModMessages.sendToPlayersTrackingEntity(new SyncBloodFireEffectPacket(target.getId(), 200, 0), target);
            if (target instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToPlayer(new SyncBloodFireEffectPacket(target.getId(), 200, 0), serverPlayer);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // 1. Client Side Check (Particles are visual only)
        if (!level.isClientSide) {
            return;
        }

        // 2. Player Check
        if (!(entity instanceof Player player)) {
            return;
        }

        // 3. CRITICAL: "Camera" Check
        // isControlledByLocalInstance() returns true ONLY for the client's main player.
        // It returns false for other players (RemotePlayers) walking around.
        if (!player.isControlledByLocalInstance()) {
            return;
        }

        boolean isHolding = player.getMainHandItem() == stack || player.getOffhandItem() == stack;

        if (isHolding) {
            if( Minecraft.getInstance().options.getCameraType().isFirstPerson()){
            spawnHeldParticles(level, player, stack);
            }
        }
    }

    private void spawnHeldParticles(Level level, Player player, ItemStack stack) {
        // Reduced chance slightly to avoid overwhelming the screen since we spawn multiple particles now
        if (level.random.nextFloat() > 0.20f) return;

        boolean isRightHand = player.getMainHandItem() == stack;
        double currentSideOffset = isRightHand ? OFFSET_SIDE : -OFFSET_SIDE;

        if (player.getOffhandItem() == stack) {
            currentSideOffset = -OFFSET_SIDE;
        }

        // --- 1. Vector Calculation (Same as before) ---
        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 viewVec = player.getViewVector(1.0f);
        Vec3 rightVec = viewVec.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 downVec = rightVec.cross(viewVec).normalize();

        // Base Position (The "Hand")
        Vec3 basePos = eyePos
                .add(viewVec.scale(OFFSET_FORWARD))
                .add(rightVec.scale(currentSideOffset))
                .add(downVec.scale(OFFSET_DOWN));

        // --- 2. Gradient Magic Particles ---
        // We calculate a small random offset for "volume"
        double offsetX = (level.random.nextDouble() - 0.5) * 0.1;
        double offsetY = (level.random.nextDouble() - 0.5) * 0.1;
        double offsetZ = (level.random.nextDouble() - 0.5) * 0.1;

        // Calculate distance from "center" of the flame for gradient logic
        // 0.0 = Center, 1.0 = Edge (approx)
        double dist = Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ) / 0.1; // Normalize roughly 0-1
        float ratio = (float) Mth.clamp(dist, 0.0, 1.0);

        // Gradient Colors:
        // Center (0.0) -> Pink/White (Hot)
        // Mid   (0.5) -> Bright Red
        // Edge  (1.0) -> Dark Blood Red (Cool)
        Vector3f color = ParticleHelper.gradient3(ratio,
                new Vector3f(1.0f, 0.6f, 0.8f), // START: Pink
                new Vector3f(0.9f, 0.1f, 0.1f), // MID: Red
                new Vector3f(0.3f, 0.0f, 0.0f)  // END: Dark Red
        );

        float size = 0.15f + level.random.nextFloat() * 0.1f; // Slightly smaller for hand view

        // Spawn Magic Particle
        ParticleHelper.spawn(level, new MagicParticleOptions(
                color,
                size,
                false, // Emissive looks better for fire
                25    // Short life
        ), basePos.x + offsetX, basePos.y + offsetY, basePos.z + offsetZ, 0.0, 0.015, 0.0);


        // --- 3. Smoke Particles (Occasional) ---
        // Adds detail and makes it feel like a real burning torch
        if (level.random.nextFloat() < 0.3f) { // 30% chance when the main particle spawns
            ParticleHelper.spawn(level, ParticleTypes.SMOKE,
                    basePos.x + (level.random.nextDouble() - 0.5) * 0.05,
                    basePos.y + 0.1, // Spawn slightly above the flame
                    basePos.z + (level.random.nextDouble() - 0.5) * 0.05,
                    0.0, 0.03, 0.0); // Floats up faster
        }
    }



}