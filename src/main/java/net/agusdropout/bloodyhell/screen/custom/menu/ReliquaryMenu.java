package net.agusdropout.bloodyhell.screen.custom.menu;

import net.agusdropout.bloodyhell.capability.insight.PlayerInsight;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.screen.ModMenuTypes;
import net.agusdropout.bloodyhell.screen.base.BasePlayerInventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class ReliquaryMenu extends BasePlayerInventoryMenu {

    private static final int TE_INVENTORY_SLOT_COUNT = 14;
    private final ItemStack itemStack;

    public ReliquaryMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.getItemInHand(extraData.readEnum(InteractionHand.class)));
    }

    public ReliquaryMenu(int containerId, Inventory playerInv, ItemStack stack) {
        super(ModMenuTypes.RELIQUARY_MENU.get(), containerId, playerInv, TE_INVENTORY_SLOT_COUNT);
        this.itemStack = stack;

        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {

            this.addSlot(new ReliquaryRuneSlot(handler, 0, 22, 37, 0, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 1, 41, 37, 0, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 6, 22, 56, 0, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 7, 41, 56, 0, this));

            this.addSlot(new ReliquaryRuneSlot(handler, 2, 60, 37, 1, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 8, 60, 56, 1, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 3, 79, 37, 1, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 9, 79, 56, 1, this));

            this.addSlot(new ReliquaryRuneSlot(handler, 4, 98, 37, 2, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 10, 98, 56, 2, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 5, 117, 37, 2, this));
            this.addSlot(new ReliquaryRuneSlot(handler, 11, 117, 56, 2, this));


            this.addSlot(new SlotItemHandler(handler, 12, 145, 37) {
                @Override
                public boolean mayPlace(@NotNull ItemStack upgradeStack) {
                    return upgradeStack.is(ModItems.ANCIENT_OCULAR_LENSE.get()) || upgradeStack.is(ModItems.BLASPHEMOUS_OCULAR_LENSE.get());
                }

                @Override
                public boolean mayPickup(Player playerIn) {
                    int maxRequiredTier = 0;

                    int[] tier1Slots = {2, 3, 8, 9};
                    int[] tier2Slots = {4, 5, 10, 11};

                    for (int slot : tier1Slots) {
                        if (!handler.getStackInSlot(slot).isEmpty()) maxRequiredTier = Math.max(maxRequiredTier, 1);
                    }
                    for (int slot : tier2Slots) {
                        if (!handler.getStackInSlot(slot).isEmpty()) maxRequiredTier = Math.max(maxRequiredTier, 2);
                    }

                    if (maxRequiredTier == 0) return true;


                    ItemStack cursorStack = ReliquaryMenu.this.getCarried();
                    int incomingTier = 0;
                    if (cursorStack.is(ModItems.ANCIENT_OCULAR_LENSE.get())) incomingTier = 1;
                    else if (cursorStack.is(ModItems.BLASPHEMOUS_OCULAR_LENSE.get())) incomingTier = 2;

                    return incomingTier >= maxRequiredTier;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });

            this.addSlot(new SlotItemHandler(handler, 13, 145, 56));
        });
    }

    public int getUpgradeTier() {
        AtomicInteger tier = new AtomicInteger(0);

        this.itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack upgradeStack = handler.getStackInSlot(12);

            if (upgradeStack.is(ModItems.ANCIENT_OCULAR_LENSE.get())) {
                tier.set(1);
            } else if (upgradeStack.is(ModItems.BLASPHEMOUS_OCULAR_LENSE.get())) {
                tier.set(2);
            }
        });

        return tier.get();
    }

    public int getUsedCapacity() {
        int[] totalCapacity = {0};

        this.itemStack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < 12; i++) {
                net.minecraft.world.item.ItemStack slotStack = handler.getStackInSlot(i);

                if (!slotStack.isEmpty()) {
                    net.agusdropout.bloodyhell.item.custom.reliquary.RuneType type = net.agusdropout.bloodyhell.item.custom.reliquary.RuneType.getByItem(slotStack.getItem());
                    if (type != null) {
                        totalCapacity[0] += type.getCapacityCost();
                    }
                }
            }
        });

        return totalCapacity[0];
    }

    public int getMaxCapacity() {
        return ClientInsightData.getPlayerInsight();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem() == itemStack || player.getOffhandItem() == itemStack;
    }
}