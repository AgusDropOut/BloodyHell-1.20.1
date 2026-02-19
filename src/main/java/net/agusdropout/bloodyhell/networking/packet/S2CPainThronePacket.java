package net.agusdropout.bloodyhell.networking.packet;


import net.agusdropout.bloodyhell.client.PainThroneRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CPainThronePacket {
    private final UUID entityId;
    private final int durationTicks;

    public S2CPainThronePacket(UUID entityId, int durationTicks) {
        this.entityId = entityId;
        this.durationTicks = durationTicks;
    }

    public S2CPainThronePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readUUID();
        this.durationTicks = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(entityId);
        buf.writeInt(durationTicks);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Register the entity with a specific "End Time"
            PainThroneRegistry.addVictim(entityId, durationTicks);
        });
        return true;
    }
}