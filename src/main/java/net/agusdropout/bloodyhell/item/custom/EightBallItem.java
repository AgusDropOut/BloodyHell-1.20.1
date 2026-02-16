package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EightBallItem extends Item {
    public EightBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // --- SPELL LOGIC: SUMMON SPEARS ---
            int quantity = 5; // How many spears to summon

            // Check if player already has spears (optional, prevents infinite stacking)
            /*
            List<RhnullImpalerEntity> existing = level.getEntitiesOfClass(RhnullImpalerEntity.class,
                player.getBoundingBox().inflate(5),
                e -> e.getOwner() == player && !e.isLaunched());
            if (!existing.isEmpty()) return InteractionResultHolder.fail(player.getItemInHand(hand));
            */

            for (int i = 0; i < quantity; i++) {
                // Create the entity passing the index and total count for correct spacing
                RhnullImpalerEntity spear = new RhnullImpalerEntity(level, player, i, quantity);

                // Optional: Customize stats here for testing
                spear.increaseSpellDamage(2.0);

                level.addFreshEntity(spear);
            }

            // Sound Effect
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 1.0f, 1.0f);
        }

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