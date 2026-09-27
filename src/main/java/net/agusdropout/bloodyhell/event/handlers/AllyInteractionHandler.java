package net.agusdropout.bloodyhell.event.handlers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bloodyhell", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AllyInteractionHandler {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof LivingEntity entity) {
            Player player = event.getEntity();

            if (player.isCrouching() && player.getMainHandItem().isEmpty()) {
                net.minecraft.nbt.CompoundTag data = entity.getPersistentData();

                boolean hasSpeed = data.getBoolean("BlasphemousSpeed");
                boolean hasStrength = data.getBoolean("BlasphemousStrength");
                boolean hasRegen = data.getBoolean("BlasphemousRegen");
                boolean hasResist = data.getBoolean("BlasphemousResistance");
                boolean hasFire = data.getBoolean("BlasphemousFireResist");

                if (hasSpeed || hasStrength || hasRegen || hasResist || hasFire) {
                    if (!player.level().isClientSide) {
                        data.putInt("MutationDisplayTick", 1);
                        data.putUUID("MutationViewer", player.getUUID());
                    }

                    event.setCanceled(true);
                    event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
                }
            }
        }
    }
}