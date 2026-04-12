package net.agusdropout.bloodyhell.screen.custom.menu;

import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.item.custom.reliquary.RuneType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ReliquaryRuneSlot extends SlotItemHandler {
    private final int requiredTier;
    private final IItemHandler inventory;
    private final ReliquaryMenu menu;

    public ReliquaryRuneSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, int requiredTier, ReliquaryMenu menu) {
        super(itemHandler, index, xPosition, yPosition);
        this.inventory = itemHandler;
        this.requiredTier = requiredTier;
        this.menu = menu;
    }

    @Override
    public boolean isActive() {
        ItemStack upgradeStack = this.inventory.getStackInSlot(12);
        int currentTier = getUpgradeTier(upgradeStack);
        return currentTier >= this.requiredTier;
    }

    private int getUpgradeTier(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Item item = stack.getItem();
        if (item.equals(ModItems.ANCIENT_OCULAR_LENSE.get())) {
            return 1;
        } else if (item.equals(ModItems.BLASPHEMOUS_OCULAR_LENSE.get())) {
            return 2;
        }
        return 0;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        int incomingCost = 0;
        int requiredInsight = 0;
        RuneType type = RuneType.getByItem(stack.getItem());

        if (type != null) {
            incomingCost = type.getCapacityCost();
            requiredInsight = type.getRequiredInsight();
        }


        return (this.menu.getUsedCapacity() + incomingCost) <= this.menu.getMaxCapacity() && this.menu.getMaxCapacity() >= requiredInsight;
    }
}