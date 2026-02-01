package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.custom.BloodFireBlockEntity; // Import your BlockEntity
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EightBallItem extends Item {
    public EightBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            BlockPos pos = player.blockPosition();

            // 1. Place the Block
            level.setBlockAndUpdate(pos, ModBlocks.BLOOD_FIRE.get().defaultBlockState());

            // 2. Get the Block Entity
            BlockEntity be = level.getBlockEntity(pos);

            // 3. Set Owner (This makes it safe for YOU)
            if (be instanceof BloodFireBlockEntity fireEntity) {
                fireEntity.setOwner(player);
                player.sendSystemMessage(Component.literal("Placed Fire bound to UUID: " + player.getName().getString())
                        .withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.literal("Failed to find Block Entity!")
                        .withStyle(ChatFormatting.RED));
            }
        }

        return super.use(level, player, hand);
    }

    // ... (Keep existing onUseTick and appendHoverText methods) ...
    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
        super.onUseTick(level, player, stack, count);
        if (Math.random() < 0.5) {
            double offsetX = Math.random() - 0.5;
            double offsetZ = Math.random() - 0.5;
            level.addParticle(ModParticles.MAGIC_LINE_PARTICLE.get(),
                    player.getX() + offsetX, player.getY(), player.getZ() + offsetZ,
                    0, 0.05, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if(Screen.hasShiftDown()){
            components.add(Component.literal("Right click to spawn Safe Fire (UUID)!").withStyle(ChatFormatting.RED));
        } else {
            components.add(Component.literal("Press shift for more info!").withStyle(ChatFormatting.DARK_RED));
        }
        super.appendHoverText(stack, level, components, flag);
    }
}