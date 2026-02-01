package net.agusdropout.bloodyhell.screen;

import net.agusdropout.bloodyhell.entity.custom.VesperEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VesperMenu extends AbstractContainerMenu {
    public final VesperEntity vesperEntity;
    private ItemStackHandler lazyItemHandler = new ItemStackHandler(2);
    // Client menu constructor
    public VesperMenu(int containerId, Inventory playerinventory) {
        this(containerId, playerinventory,new ItemStackHandler(2), null);
        System.out.println("VesperMenu constructor");
    }


    public VesperMenu(int containerId, Inventory playerinv,IItemHandler dataInventory, VesperEntity vesperEntity) {
        super(ModMenuTypes.VESPER_MENU.get(), containerId);
        this.vesperEntity = vesperEntity;
        this.lazyItemHandler = (ItemStackHandler) dataInventory;
        this.addSlot(new SlotItemHandler(dataInventory, 0, 22, 56));
        this.addSlot(new SlotItemHandler(dataInventory, 1, 136, 56));
        addPlayerInventory(playerinv);
        addPlayerHotbar(playerinv);

    }








    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        if (index < 0 || index >= slots.size()) {
            // Índice fuera de los límites, manejar según sea necesario
            return ItemStack.EMPTY;
        }

        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Verificar si la ranura clickeada es una de las ranuras del inventario vanilla
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // Esta es una ranura del inventario vanilla, así que fusiona la pila en el inventario del bloque
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // Esta es una ranura del inventario del bloque, así que fusiona la pila en el inventario del jugador
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }

        // Si el tamaño de la pila es 0 (se movió toda la pila), establece el contenido de la ranura como nulo
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }


    private void addPlayerInventory(Inventory playerinventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerinventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerinventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerinventory, i, 8 + i * 18, 144));
        }
    }
}
