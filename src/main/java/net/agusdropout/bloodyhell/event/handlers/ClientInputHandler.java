package net.agusdropout.bloodyhell.event.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class ClientInputHandler {

    public static final KeyMapping INSPECT_KEY = new KeyMapping(
            "key.bloodyhell.inspect",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.bloodyhell.keys"
    );

    @Mod.EventBusSubscriber(modid = "bloodyhell", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(INSPECT_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = "bloodyhell", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                while (INSPECT_KEY.consumeClick()) {
                    Player player = Minecraft.getInstance().player;
                    if (player != null) {
                        ItemStack mainHandItem = player.getMainHandItem();

                        if (mainHandItem.getItem() instanceof BaseStaffItem staffItem) {
                            staffItem.triggerInspect(player, mainHandItem);
                        }
                    }
                }
            }
        }
    }
}