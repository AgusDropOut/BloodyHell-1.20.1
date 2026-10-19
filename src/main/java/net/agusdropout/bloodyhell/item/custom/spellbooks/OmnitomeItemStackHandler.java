package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class OmnitomeItemStackHandler extends ItemStackHandler {

    public static final int TOTAL_SLOTS = 10;
    public static final int UPGRADE_SLOT = 8;
    public static final int EXTRA_SLOT = 9;
    private static final String NBT_INVENTORY_KEY = "OmnitomeInventory";

    private final ItemStack omnitomeStack;

    public OmnitomeItemStackHandler(ItemStack omnitomeStack) {
        super(TOTAL_SLOTS);
        this.omnitomeStack = omnitomeStack;
        loadFromStack();
    }

    public void loadFromStack() {
        CompoundTag tag = this.omnitomeStack.getTag();
        if (tag != null && tag.contains(NBT_INVENTORY_KEY, Tag.TAG_COMPOUND)) {
            this.deserializeNBT(tag.getCompound(NBT_INVENTORY_KEY));
        }
    }

    public void saveToStack() {
        this.omnitomeStack.getOrCreateTag().put(NBT_INVENTORY_KEY, this.serializeNBT());
        OmnitomeItem.syncActiveSpell(this.omnitomeStack, this);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        saveToStack();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot >= 0 && slot < 8) {
            return stack.getItem() instanceof BaseSpellBookItem<?>;
        }
        if (slot == UPGRADE_SLOT) {
            return stack.is(ModItems.ANCIENT_OCULAR_LENSE.get()) || stack.is(ModItems.BLASPHEMOUS_OCULAR_LENSE.get());
        }
        return true;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack slotStack = stacks.get(i);
            if (!slotStack.isEmpty()) {
                CompoundTag itemTag;
                if (slotStack.getItem() instanceof BaseSpellBookItem<?> spellBook) {
                    itemTag = spellBook.serializeBookData(slotStack);
                } else {
                    itemTag = new CompoundTag();
                    slotStack.save(itemTag);
                }
                itemTag.putInt("Slot", i);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, BaseSpellBookItem.deserializeBookData(itemTags));
            }
        }
        onLoad();
    }
}