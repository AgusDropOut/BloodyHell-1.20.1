package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.PacketFireRhnullImpaler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
public class ClientSpellInputHandler {
    @SubscribeEvent
    public static void onLeftClick(InputEvent.MouseButton.Pre event) {

        if (event.getButton() == 0 && event.getAction() == 1) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (mc.screen != null) return;
            if (mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
                return;
            }
            ModMessages.sendToServer(new PacketFireRhnullImpaler());
        }
    }
}