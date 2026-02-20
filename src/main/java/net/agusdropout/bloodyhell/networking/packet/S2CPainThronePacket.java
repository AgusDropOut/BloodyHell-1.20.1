package net.agusdropout.bloodyhell.networking.packet;


import net.agusdropout.bloodyhell.client.PainThroneRegistry;
import net.agusdropout.bloodyhell.util.bones.BoneManipulation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CPainThronePacket {
    private final UUID entityId;
    private final int durationTicks;
    private final int manipulationOrdinal;

    public S2CPainThronePacket(UUID entityId, int durationTicks, BoneManipulation manipulation) {
        this.entityId = entityId;
        this.durationTicks = durationTicks;
        this.manipulationOrdinal = manipulation.ordinal();
    }

    public S2CPainThronePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readUUID();
        this.durationTicks = buf.readInt();
        this.manipulationOrdinal = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(entityId);
        buf.writeInt(durationTicks);
        buf.writeInt(manipulationOrdinal);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if(manipulationOrdinal ==  BoneManipulation.JITTER.ordinal()) {
                PainThroneRegistry.addVictim(entityId, durationTicks);
            } else {
                PainThroneRegistry.breakBone(entityId, durationTicks);
            }
        });
        return true;
    }
}