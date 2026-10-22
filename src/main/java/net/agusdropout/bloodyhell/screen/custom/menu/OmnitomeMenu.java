package net.agusdropout.bloodyhell.screen.custom.menu;

import net.agusdropout.bloodyhell.item.registry.ReliquaryItems;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItemStackHandler;
import net.agusdropout.bloodyhell.screen.ModMenuTypes;
import net.agusdropout.bloodyhell.screen.base.BasePlayerInventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class OmnitomeMenu extends BasePlayerInventoryMenu {

    private static final int TE_INVENTORY_SLOT_COUNT = 10;
    private final ItemStack itemStack;

    public OmnitomeMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.getItemInHand(extraData.readEnum(InteractionHand.class)));
    }

    public OmnitomeMenu(int containerId, Inventory playerInv, ItemStack stack) {
        super(ModMenuTypes.OMNITOME_MENU.get(), containerId, playerInv, TE_INVENTORY_SLOT_COUNT);
        this.itemStack = stack;

        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler instanceof OmnitomeItemStackHandler omnitomeHandler) {
                omnitomeHandler.loadFromStack();
            }


            this.addSlot(new OmnitomeBookSlot(handler, 0, 80, 6, 0, this));
            this.addSlot(new OmnitomeBookSlot(handler, 1, 104, 12, 1, this));
            this.addSlot(new OmnitomeBookSlot(handler, 2, 110, 36, 0, this));
            this.addSlot(new OmnitomeBookSlot(handler, 3, 104, 60, 2, this));
            this.addSlot(new OmnitomeBookSlot(handler, 4, 80, 66, 0, this));
            this.addSlot(new OmnitomeBookSlot(handler, 5, 56, 60, 1, this));
            this.addSlot(new OmnitomeBookSlot(handler, 6, 50, 36, 0, this));
            this.addSlot(new OmnitomeBookSlot(handler, 7, 56, 12, 2, this));


            this.addSlot(new SlotItemHandler(handler, 8, 80, 36) {
                @Override
                public boolean mayPlace(@NotNull ItemStack upgradeStack) {
                    return upgradeStack.is(ReliquaryItems.ANCIENT_OCULAR_LENSE.get()) || upgradeStack.is(ReliquaryItems.BLASPHEMOUS_OCULAR_LENSE.get());
                }

                @Override
                public boolean mayPickup(Player playerIn) {
                    int maxRequiredTier = 0;
                    int[] requiredTiers = new int[]{0, 1, 0, 2, 0, 1, 0, 2};


                    for (int i = 0; i < 8; i++) {
                        if (!handler.getStackInSlot(i).isEmpty()) {
                            maxRequiredTier = Math.max(maxRequiredTier, requiredTiers[i]);
                        }
                    }

                    if (maxRequiredTier == 0) return true;


                    ItemStack cursorStack = OmnitomeMenu.this.getCarried();
                    int incomingTier = 0;
                    if (cursorStack.is(ReliquaryItems.ANCIENT_OCULAR_LENSE.get())) incomingTier = 1;
                    else if (cursorStack.is(ReliquaryItems.BLASPHEMOUS_OCULAR_LENSE.get())) incomingTier = 2;


                    return incomingTier >= maxRequiredTier;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });


            this.addSlot(new SlotItemHandler(handler, 9, 141, 36));
        });
    }

    public int getUpgradeTier() {
        AtomicInteger tier = new AtomicInteger(0);

        this.itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack upgradeStack = handler.getStackInSlot(8);

            if (upgradeStack.is(ReliquaryItems.ANCIENT_OCULAR_LENSE.get())) {
                tier.set(1);
            } else if (upgradeStack.is(ReliquaryItems.BLASPHEMOUS_OCULAR_LENSE.get())) {
                tier.set(2);
            }
        });

        return tier.get();
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            if (this.slots.get(slotId).getItem() == this.itemStack) {
                return;
            }
        }
        if (clickType == ClickType.SWAP && player.getInventory().getItem(button) == this.itemStack) {
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler instanceof OmnitomeItemStackHandler omnitomeHandler) {
                omnitomeHandler.saveToStack();
            } else {
                OmnitomeItem.syncActiveSpell(this.itemStack, handler);
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem() == itemStack || player.getOffhandItem() == itemStack;
    }
}