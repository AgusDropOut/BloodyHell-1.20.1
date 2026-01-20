package net.agusdropout.bloodyhell.networking.packet;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncRemoveBloodFirePacket {
    private final int entityId;

    public SyncRemoveBloodFirePacket(int entityId) {
        this.entityId = entityId;
    }

    public SyncRemoveBloodFirePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // CLIENT SIDE: Forcefully remove the effect
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);
                if (entity instanceof LivingEntity living) {
                    living.removeEffect(ModEffects.BLOOD_FIRE_EFFECT.get());
                }
            }
        });
        return true;
    }
}