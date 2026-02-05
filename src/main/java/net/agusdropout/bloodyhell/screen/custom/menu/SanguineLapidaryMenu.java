package net.agusdropout.bloodyhell.screen.custom.menu;

import net.agusdropout.bloodyhell.block.entity.base.BaseSanguineLapidaryBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.SanguineLapidaryBlockEntity;
import net.agusdropout.bloodyhell.screen.ModMenuTypes;
import net.agusdropout.bloodyhell.screen.base.BasePlayerInventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SanguineLapidaryMenu extends BasePlayerInventoryMenu {

    private final BaseSanguineLapidaryBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private static final int TE_INVENTORY_SLOT_COUNT = 4;

    // Client Constructor
    public SanguineLapidaryMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, new ItemStackHandler(TE_INVENTORY_SLOT_COUNT), null);
    }

    // Server Constructor
    public SanguineLapidaryMenu(int containerId, Inventory playerInv, IItemHandler dataInventory, BaseSanguineLapidaryBlockEntity blockEntity) {
        super(ModMenuTypes.SANGUINE_LAPIDARY_MENU.get(), containerId, playerInv, TE_INVENTORY_SLOT_COUNT);

        this.blockEntity = blockEntity;
        this.levelAccess = blockEntity != null ?
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()) :
                ContainerLevelAccess.NULL;

        // Custom Slots (Index 36, 37, 38, 39)
        this.addSlot(new SlotItemHandler(dataInventory, 0, 22, 56));
        this.addSlot(new SlotItemHandler(dataInventory, 1, 136, 56));
        this.addSlot(new SlotItemHandler(dataInventory, 2, 79, 12));
        this.addSlot(new SlotItemHandler(dataInventory, 3, 79, 56));

    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, this.blockEntity.getBlockState().getBlock());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (blockEntity != null && !blockEntity.getLevel().isClientSide) {

            if (blockEntity instanceof SanguineLapidaryBlockEntity lapidary) {
                lapidary.onMenuClosed(player);
            }
        }
    }


}