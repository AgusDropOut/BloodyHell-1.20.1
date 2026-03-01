package net.agusdropout.bloodyhell.networking.packet;

import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CDataSyncInsightPacket {
    private final int insight;

    public S2CDataSyncInsightPacket(int insight) {
        this.insight = insight;
    }

    public S2CDataSyncInsightPacket(FriendlyByteBuf buf) {
        this.insight = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(insight);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientInsightData.set(this.insight);
        });
        context.setPacketHandled(true);
        return true;
    }
}