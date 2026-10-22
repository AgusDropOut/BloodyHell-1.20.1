package net.agusdropout.bloodyhell.item.custom.reliquary;

import net.agusdropout.bloodyhell.datagen.ModTags;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReliquaryCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final int SLOTS = 14;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            if (slot >= 0 && slot <= 11) {

                return stack.is(ModTags.Items.RELIQUARY_RUNE_ITEM);
            }

            if (slot == 12) {
                return stack.is(ModTags.Items.RELIQUARY_UPGRADE_ITEM);
            }

            if (slot == 13) {

                return true;
            }

            return super.isItemValid(slot, stack);
        }
    };

    private final LazyOptional<IItemHandler> optional = LazyOptional.of(() -> itemHandler);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return itemHandler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        itemHandler.deserializeNBT(nbt);
    }
}