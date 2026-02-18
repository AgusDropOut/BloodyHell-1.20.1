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


    public PacketFireRhnullImpaler() {
    }


    public PacketFireRhnullImpaler(FriendlyByteBuf buf) {

    }


    public void toBytes(FriendlyByteBuf buf) {

    }

    // 4. Handler (The Logic)
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {

                List<RhnullImpalerEntity> spears = player.level().getEntitiesOfClass(
                        RhnullImpalerEntity.class,
                        player.getBoundingBox().inflate(10), // Radius check
                        e -> e.getOwner() == player && !e.isLaunched()
                );

                if (!spears.isEmpty()) {

                    RhnullImpalerEntity spear = spears.get(0);
                    spear.launch(player.getLookAngle());


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