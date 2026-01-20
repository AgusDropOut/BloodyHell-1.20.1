package net.agusdropout.bloodyhell.networking.packet;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBloodFireEffectPacket {
    private final int entityId;
    private final int duration;
    private final int amplifier;

    public SyncBloodFireEffectPacket(int entityId, int duration, int amplifier) {
        this.entityId = entityId;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public SyncBloodFireEffectPacket(FriendlyByteBuf buf) {
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
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);

                if (entity instanceof LivingEntity living) {
                    // FIX: Set ambient to FALSE (4th arg) and visible to FALSE (5th arg)
                    // This matches standard potion application so vanilla sync packets can overwrite/remove it correctly.
                    living.addEffect(new MobEffectInstance(
                            ModEffects.BLOOD_FIRE_EFFECT.get(),
                            duration,
                            amplifier,
                            false, // ambient (Set to FALSE to fix persistence bug)
                            false, // visible (particles handled by block)
                            true   // show icon
                    ));
                }
            }
        });
        return true;
    }
}