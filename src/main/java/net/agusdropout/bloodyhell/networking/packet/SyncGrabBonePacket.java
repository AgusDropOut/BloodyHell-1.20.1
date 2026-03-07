package net.agusdropout.bloodyhell.networking.packet;

import net.agusdropout.bloodyhell.entity.custom.HostileUnknownEntityArms;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGrabBonePacket {
    private final int entityId;
    private final float x;
    private final float y;
    private final float z;

    public SyncGrabBonePacket(int entityId, float x, float y, float z) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SyncGrabBonePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerLevel level = context.getSender().serverLevel();
            Entity entity = level.getEntity(this.entityId);

            if (entity instanceof HostileUnknownEntityArms armsEntity) {
                armsEntity.updateGrabBonePosition(this.x, this.y, this.z);
            }
        });
        return true;
    }
}