package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerJoinEvent {

    private static final String GIVEN_BOOK_TAG = "bloodyhell_given_guide_book";

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag playerData = persistentData.getCompound(Player.PERSISTED_NBT_TAG);

            if (!playerData.getBoolean(GIVEN_BOOK_TAG)) {
                playerData.putBoolean(GIVEN_BOOK_TAG, true);
                persistentData.put(Player.PERSISTED_NBT_TAG, playerData);

                player.getInventory().add(new ItemStack(ModItems.UNKNOWN_GUIDE_BOOK.get()));
            }
        }
    }
}