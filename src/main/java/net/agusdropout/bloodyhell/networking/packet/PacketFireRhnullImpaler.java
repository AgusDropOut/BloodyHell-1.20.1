package net.agusdropout.bloodyhell.networking.packet;

import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketFireRhnullImpaler {

    // 1. Empty Constructor (Required for creating the packet instance before sending)
    public PacketFireRhnullImpaler() {
    }

    // 2. Decoder Constructor (Required for reading from the buffer on the receiving side)
    // Even if empty, it MUST exist so Forge knows how to reconstruct the object.
    public PacketFireRhnullImpaler(FriendlyByteBuf buf) {
        // No data to read
    }

    // 3. Encoder (Required for writing to the buffer)
    // Even if empty, it MUST exist.
    public void toBytes(FriendlyByteBuf buf) {
        // No data to write
    }

    // 4. Handler (The Logic)
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                // Find unlaunched spears owned by the player
                List<RhnullImpalerEntity> spears = player.level().getEntitiesOfClass(
                        RhnullImpalerEntity.class,
                        player.getBoundingBox().inflate(10), // Radius check
                        e -> e.getOwner() == player && !e.isLaunched()
                );

                if (!spears.isEmpty()) {
                    // Fire the first one found (FIFO logic)
                    RhnullImpalerEntity spear = spears.get(0);
                    spear.launch(player.getLookAngle());

                    // Sound Effect
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
        });

        // Mark packet as handled
        context.setPacketHandled(true);
        return true;
    }
}