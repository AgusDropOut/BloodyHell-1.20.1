package net.agusdropout.bloodyhell.screen.custom.menu;

import net.agusdropout.bloodyhell.entity.custom.VesperEntity;
import net.agusdropout.bloodyhell.screen.ModMenuTypes;
import net.agusdropout.bloodyhell.screen.base.BasePlayerInventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VesperMenu extends BasePlayerInventoryMenu {

    public final VesperEntity vesperEntity;
    private static final int TE_INVENTORY_SLOT_COUNT = 2;

    // Client Constructor
    public VesperMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, new ItemStackHandler(TE_INVENTORY_SLOT_COUNT), null);
    }

    // Server Constructor
    public VesperMenu(int containerId, Inventory playerInv, IItemHandler dataInventory, VesperEntity vesperEntity) {
        super(ModMenuTypes.VESPER_MENU.get(), containerId, playerInv, TE_INVENTORY_SLOT_COUNT);
        this.vesperEntity = vesperEntity;

        // Custom Slots (Index 36, 37)
        this.addSlot(new SlotItemHandler(dataInventory, 0, 22, 56));
        this.addSlot(new SlotItemHandler(dataInventory, 1, 136, 56));
    }

    @Override
    public boolean stillValid(Player player) {
        // Add specific validation logic for VesperEntity if needed (e.g., distance to entity)
        return this.vesperEntity != null && this.vesperEntity.isAlive() && this.vesperEntity.distanceTo(player) < 8.0f;
    }
}