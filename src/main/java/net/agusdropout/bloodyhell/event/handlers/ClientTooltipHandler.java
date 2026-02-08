package net.agusdropout.bloodyhell.event.handlers;

import com.mojang.datafixers.util.Either;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.item.custom.base.BasePowerGemItem;
import net.agusdropout.bloodyhell.screen.ModLabelTooltipData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
public class ClientTooltipHandler {

    // --- 1. HANDLE FRAME & BACKGROUND COLORS ---
    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getItemStack();

        // Check 1: Sanguinite OR Rhnull (Reddish-Gold Frame, Red Background)
        if (stack.is(ModTags.Items.SANGUINITE_TIER_ITEMS) || stack.is(ModTags.Items.RHNULL_TIER_ITEMS)) {
            int borderStart = 0xffffdd6b; // Light Gold
            int borderEnd = 0xffffc400;   // Darker Gold
            int background = 0xf0590000;  // Dark Red Transparency

            event.setBorderStart(borderStart);
            event.setBorderEnd(borderEnd);
            event.setBackground(background);
        }
        // Check 2: Blasphemous (Gold Frame, Very Dark Yellow/Black Background)
        else if (stack.is(ModTags.Items.BLASPHEMOUS_TIER_ITEMS)) {
            int borderStart = 0xffffe100; // Bright Gold
            int borderEnd = 0xffe6b800;   // Muted Gold
            int background = 0xf01a1600;  // Very Dark Yellow/Black (Almost Black with a yellow tint)

            event.setBorderStart(borderStart);
            event.setBorderEnd(borderEnd);
            event.setBackground(background);
        }
        // Check 3: Base Power Gem (Legacy check)
        else if (stack.getItem() instanceof BasePowerGemItem) {
            int borderStart = 0xffffdd6b;
            int borderEnd = 0xffffc400;
            event.setBorderStart(borderStart);
            event.setBorderEnd(borderEnd);
            event.setBackground(0xf0590000);
        }
    }

    // --- 2. HANDLE TEXT COLORS ---
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (event.getToolTip().isEmpty()) return;

        // Priority 1: Sanguinite -> Red Text
        if (stack.is(ModTags.Items.SANGUINITE_TIER_ITEMS)) {
            setColor(event, 0xAA0000); // Deep Red
            return; // Stop here so we don't overwrite with default
        }

        // Priority 2: Rhnull -> Gold Text
        if (stack.is(ModTags.Items.RHNULL_TIER_ITEMS)) {
            setColor(event, 0xFFAA00); // Gold
            return;
        }

        // Priority 3: Blasphemous -> Gold Text (Matches frame)
        if (stack.is(ModTags.Items.BLASPHEMOUS_TIER_ITEMS)) {
            setColor(event, 0xFFAA00); // Gold
            return;
        }

        // Priority 4: Default Mod Items -> Standard Bloody Hell Red
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (registryName != null && registryName.getNamespace().equals(BloodyHell.MODID)) {
            setColor(event, 0x880000); // Standard Mod Red
        }
    }

    @SubscribeEvent
    public static void onGatherComponents(RenderTooltipEvent.GatherComponents event) {
        // Ensure stack is valid before checking creator mod ID
        if (!event.getItemStack().isEmpty() &&
                event.getItemStack().getItem().getCreatorModId(event.getItemStack()) != null &&
                event.getItemStack().getItem().getCreatorModId(event.getItemStack()).equals(BloodyHell.MODID)) {

            event.getTooltipElements().add(Either.right(new ModLabelTooltipData()));
        }
    }

    private static void setColor(ItemTooltipEvent event, int hexColor) {
        Component originalName = event.getToolTip().get(0);
        Style newStyle = originalName.getStyle().withColor(hexColor);
        event.getToolTip().set(0, originalName.copy().withStyle(newStyle));
    }
}