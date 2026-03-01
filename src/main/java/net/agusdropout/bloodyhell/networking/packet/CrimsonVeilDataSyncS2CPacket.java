package net.agusdropout.bloodyhell.networking.packet;

import net.agusdropout.bloodyhell.client.data.ClientCrimsonVeilData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrimsonVeilDataSyncS2CPacket {
    public final int crimsonVeil;
    public CrimsonVeilDataSyncS2CPacket(int thirst){
        this.crimsonVeil = thirst;
    }
    public CrimsonVeilDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.crimsonVeil = buf.readInt();
    }
    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(crimsonVeil);
    }
    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            //HERE WE ARE ON THE CLIENT!
            ClientCrimsonVeilData.set(crimsonVeil);
        });
        return true;
    }
}
