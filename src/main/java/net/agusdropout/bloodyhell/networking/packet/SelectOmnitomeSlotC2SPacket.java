package net.agusdropout.bloodyhell.networking.packet;

import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.function.Supplier;

public class SelectOmnitomeSlotC2SPacket {

    private final InteractionHand hand;
    private final int slotIndex;

    public SelectOmnitomeSlotC2SPacket(InteractionHand hand, int slotIndex) {
        this.hand = hand;
        this.slotIndex = slotIndex;
    }

    public SelectOmnitomeSlotC2SPacket(FriendlyByteBuf buf) {
        this.hand = buf.readEnum(InteractionHand.class);
        this.slotIndex = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(this.hand);
        buf.writeInt(this.slotIndex);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getItemInHand(this.hand);
                if (stack.getItem() instanceof OmnitomeItem omnitome) {
                    OmnitomeItem.selectSlot(stack, this.slotIndex);
                    CompoundTag tag = stack.getOrCreateTag();
                    tag.putLong(OmnitomeItem.NBT_LAST_MORPH_TIME, player.level().getGameTime());

                    if (player.level() instanceof ServerLevel serverLevel) {
                        if (tag.contains(OmnitomeItem.NBT_ACTIVE_SPELL)) {
                            omnitome.triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "controller", "emerge");
                        }
                    }
                }
            }
        });
        return true;
    }
}