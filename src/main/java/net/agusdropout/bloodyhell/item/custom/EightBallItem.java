package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullHeavySwordEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.HollowRectangleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class EightBallItem extends Item {
    public EightBallItem(Properties properties) {
        super(properties);
    }

    private static final Vector3f COLOR_CORE = new Vector3f(1.0f, 0.6f, 0.0f);
    private static final Vector3f COLOR_FADE = new Vector3f(0.5f, 0.0f, 0.0f);

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            double distanceInFront = 5.0; // Distance in blocks
            Vec3 lookVec = player.getLookAngle();

            // We only care about horizontal direction for the ground slam placement usually
            // but lookVec works fine. If you want it strictly flat, normalize (x, 0, z).
            double targetX = player.getX() + lookVec.x * distanceInFront;
            double targetY = player.getY(); // Keep it at player's feet level
            double targetZ = player.getZ() + lookVec.z * distanceInFront;

            float width = 3.0f;   // The "Thickness" of the slam area
            float length = 8.0f;  // The "Length" extending forward
            float height = 4.0f;  // How high the walls go

            // 2. Spawn the Rectangle
            // We pass '-player.getYRot()' so the rectangle rotates to match the player's facing direction.
            ParticleHelper.spawn(level,
                    new HollowRectangleOptions(
                            COLOR_FADE,
                            width,    // Width (X-axis relative to rotation)
                            length,   // Height/Length (Z-axis relative to rotation)
                            300,       // Life (ticks)
                            -player.getYRot(), // ROTATION: Rotates the box to face where player is looking
                            0.0f      // Jitter
                    ),
                    targetX, targetY, targetZ,
                    0, 0, 0 // Velocity (0 for static area)
            );

             // Sound Effect
             level.playSound(null, player.getX(), player.getY(), player.getZ(),
                     SoundEvents.EVOKER_PREPARE_ATTACK, SoundSource.PLAYERS, 1.0f, 1.0f);
        }//

        // Visual Feedback (Client Side)
        if (level.isClientSide) {
            ParticleHelper.spawnRing(level, ModParticles.BLOOD_PARTICLES.get(), player.position().add(0, 1, 0), 1.5, 20, 0.05);
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            components.add(Component.literal("Right Click: Summon Rhnull Impalers").withStyle(ChatFormatting.RED));
            components.add(Component.literal("Left Click (Empty Hand): Fire Spear").withStyle(ChatFormatting.GOLD));
        } else {
            components.add(Component.literal("Hold [SHIFT] for spell info").withStyle(ChatFormatting.DARK_RED));
        }
        super.appendHoverText(stack, level, components, flag);
    }
}