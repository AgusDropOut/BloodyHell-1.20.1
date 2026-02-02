package net.agusdropout.bloodyhell.networking.packet;



import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncVisceralEffectPacket {
    private final int entityId;
    private final int duration;
    private final int amplifier;

    public SyncVisceralEffectPacket(int entityId, int duration, int amplifier) {
        this.entityId = entityId;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public SyncVisceralEffectPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.duration = buf.readInt();
        this.amplifier = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(duration);
        buf.writeInt(amplifier);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Client-Side Logic
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);

                if (entity instanceof LivingEntity living) {
                    // Force apply the effect on the client side immediately
                    // This updates the 'hasEffect' check instantly for the Renderer
                    living.addEffect(new MobEffectInstance(
                            ModEffects.VISCERAL_EFFECT.get(),
                            duration,
                            amplifier,
                            false, // ambient
                            false, // visible (particles handled by block/packet)
                            true   // show icon
                    ));
                }
            }
        });
        return true;
    }
}