package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.PatchouliAPI;

public class UnknownGuideBookItem extends Item {

    public UnknownGuideBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // This directly opens your specific Patchouli book!
            PatchouliAPI.get().openBookGUI(serverPlayer, new ResourceLocation(BloodyHell.MODID, "into_the_unknown_guide"));
        }

        return InteractionResultHolder.success(stack);
    }
}