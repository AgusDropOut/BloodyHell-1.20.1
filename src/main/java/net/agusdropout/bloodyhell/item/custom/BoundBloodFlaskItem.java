package net.agusdropout.bloodyhell.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoundBloodFlaskItem extends Item {
    public BoundBloodFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (stack.hasTag()) {
            if (stack.getTag().contains("BloodOwnerName")) {
                String ownerName = stack.getTag().getString("BloodOwnerName");
                tooltip.add(Component.literal("Blood of: " + ownerName).withStyle(ChatFormatting.DARK_RED));
            } else if (stack.getTag().contains("FallenAllyName")) {
                String allyName = stack.getTag().getString("FallenAllyName");
                tooltip.add(Component.literal("Soul of: " + allyName).withStyle(ChatFormatting.AQUA));
            } else if (stack.getTag().contains("FallenAllyType")) {
                String allyType = stack.getTag().getString("FallenAllyType");
                String formattedName = allyType.contains(":") ? allyType.split(":")[1].replace("_", " ") : allyType;
                tooltip.add(Component.literal("Soul of: " + formattedName).withStyle(ChatFormatting.AQUA));
            } else {
                tooltip.add(Component.literal("Unbound Blood").withStyle(ChatFormatting.DARK_GRAY));
            }
        } else {
            tooltip.add(Component.literal("Unbound Blood").withStyle(ChatFormatting.DARK_GRAY));
        }

        tooltip.add(Component.literal("Catalyst for Parasitic Binding").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}