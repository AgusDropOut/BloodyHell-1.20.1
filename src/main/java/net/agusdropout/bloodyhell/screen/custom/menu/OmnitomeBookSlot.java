package net.agusdropout.bloodyhell.screen.custom.menu;

import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class OmnitomeBookSlot extends SlotItemHandler {

    private final int requiredTier;
    private final OmnitomeMenu menu;

    public OmnitomeBookSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, int requiredTier, OmnitomeMenu menu) {
        super(itemHandler, index, xPosition, yPosition);
        this.requiredTier = requiredTier;
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.menu.getUpgradeTier() >= this.requiredTier && stack.getItem() instanceof BaseSpellBookItem<?>;
    }

    @Override
    public boolean isActive() {
        return this.menu.getUpgradeTier() >= this.requiredTier || this.hasItem();
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}