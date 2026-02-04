package net.agusdropout.bloodyhell.event.handlers;



import com.mojang.datafixers.util.Either;
import net.agusdropout.bloodyhell.BloodyHell;
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

    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        if (event.getItemStack().getItem() instanceof BasePowerGemItem) {
            int borderStart = 0xffffdd6b;
            int borderEnd = 0xffffc400;

            event.setBorderStart(borderStart);
            event.setBorderEnd(borderEnd);
            event.setBackground(0xf0590000);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (event.getToolTip().isEmpty()) return;

        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (registryName != null && registryName.getNamespace().equals(BloodyHell.MODID)) {
            setColor(event, 0x880000);
        }
    }

    @SubscribeEvent
    public static void onGatherComponents(RenderTooltipEvent.GatherComponents event) {
        if (event.getItemStack().getItem().getCreatorModId(event.getItemStack()).equals(BloodyHell.MODID)) {
            event.getTooltipElements().add(Either.right(new ModLabelTooltipData()));
        }
    }

    private static void setColor(ItemTooltipEvent event, int hexColor) {
        Component originalName = event.getToolTip().get(0);
        Style newStyle = originalName.getStyle().withColor(hexColor);
        event.getToolTip().set(0, originalName.copy().withStyle(newStyle));
    }
}