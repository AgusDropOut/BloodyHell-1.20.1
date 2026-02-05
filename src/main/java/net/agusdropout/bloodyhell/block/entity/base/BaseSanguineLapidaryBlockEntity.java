package net.agusdropout.bloodyhell.block.entity.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.base.BaseGemSproutBlock;
import net.agusdropout.bloodyhell.block.custom.plant.BloodGemSproutBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.custom.base.BasePowerGemItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.screen.custom.menu.BloodWorkBenchMenu;
import net.agusdropout.bloodyhell.screen.custom.menu.SanguineLapidaryMenu;
import net.minecraft.core.BlockPos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseSanguineLapidaryBlockEntity extends BlockEntity  implements MenuProvider {


    private static final int SLOT_WEAPON = 3;
    private static final int SLOT_GEM_1 = 0;
    private static final int SLOT_GEM_2 = 1;
    private static final int SLOT_GEM_3 = 2;

    private static final int[] GEM_SLOTS = {SLOT_GEM_1, SLOT_GEM_2, SLOT_GEM_3};

    private boolean isHandlingSlotUpdate = false;



    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // Define rules per slot index
            return switch (slot) {
                case SLOT_WEAPON ->isWeapon(stack); // Slot 0: Only Weapons
                case SLOT_GEM_1 -> isGem(stack);    // Slot 1: Only Gems
                case SLOT_GEM_2 -> isGem(stack);    // Slot 2: Only Gems
                case SLOT_GEM_3 -> isGem(stack);    // Slot 3: Only Gems
                default -> super.isItemValid(slot, stack);
            };
        }

        // --- Helper Checks ---

        private boolean isWeapon(ItemStack stack) {
            return stack.getItem() instanceof BaseSpellBookItem<?>;
        }

        private boolean isGem(ItemStack stack) {
            return stack.getItem() instanceof BasePowerGemItem;
        }

        private boolean isSlotEmpty(int slot) {
            return getStackInSlot(slot).isEmpty();
        }

        @Override
        protected void onContentsChanged(int slot) {

            if (isHandlingSlotUpdate) return;

            if (slot == SLOT_WEAPON) {
                isHandlingSlotUpdate = true;
                unpackGemsFromBook();
                isHandlingSlotUpdate = false;
            }

            setChanged();
            sync();
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot == SLOT_WEAPON) {
                isHandlingSlotUpdate = true;
                packGemsIntoBook();
                isHandlingSlotUpdate = false;
            }
            setChanged();
            sync();
            return super.extractItem(slot, amount, simulate);
        }

    };



    public BaseSanguineLapidaryBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState) {
        super(entityType, blockPos, blockState);
    }


    public void onMenuClosed(Player player) {
        if(this.level.isClientSide) return;
        packGemsIntoBook();
    }

    private void packGemsIntoBook(){
        if (checkForValidRecipe()) {
            List<Gem> gemsToPack = getGemsInSlots();
            ItemStack weapon = getWeaponInSlot();
            GemType.putGemsIntoWeapon(weapon,gemsToPack);
            cleanGemItemsFromSlots();
            level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }




    private boolean checkForValidRecipe() {
        ItemStack weapon = itemHandler.getStackInSlot(SLOT_WEAPON);
        ItemStack gem = itemHandler.getStackInSlot(SLOT_GEM_1);
        ItemStack gem1 = itemHandler.getStackInSlot(SLOT_GEM_2);
        ItemStack gem2 = itemHandler.getStackInSlot(SLOT_GEM_3);
        return !weapon.isEmpty() && (!gem.isEmpty() || !gem1.isEmpty() || !gem2.isEmpty());
    }

    public void cleanGemItemsFromSlots() {
        itemHandler.setStackInSlot(SLOT_GEM_1, ItemStack.EMPTY);
        itemHandler.setStackInSlot(SLOT_GEM_2, ItemStack.EMPTY);
        itemHandler.setStackInSlot(SLOT_GEM_3, ItemStack.EMPTY);
    }


    private ItemStack getWeaponInSlot() {
        return itemHandler.getStackInSlot(SLOT_WEAPON);
    }

    private List<ItemStack> getGemsItemsInSlots() {
        return List.of(
                itemHandler.getStackInSlot(SLOT_GEM_1),
                itemHandler.getStackInSlot(SLOT_GEM_2),
                itemHandler.getStackInSlot(SLOT_GEM_3)
        );
    }

    private List<Gem> getGemsInSlots() {
        List<Gem> gems = new ArrayList<>();
        for (int slot : GEM_SLOTS) {
            ItemStack gemStack = itemHandler.getStackInSlot(slot);
            GemType gemType = GemType.getGemTypeFromGemStack(gemStack);
            if (!gemStack.isEmpty() && gemStack.getItem() instanceof BasePowerGemItem gemItem) {
                gems.add(new Gem(gemType, gemType.getBonusType(), GemType.getStatValueFromGemStack(gemStack)));
            }

        }
        return gems;
    }

    /**
     * Reads the NBT of the book in the Weapon Slot.
     * Converts stats into physical Gem Items in the Gem Slots.
     * Removes the stats from the book (to prevent duplication).
     */
    private void unpackGemsFromBook() {
        ItemStack weapon = itemHandler.getStackInSlot(SLOT_WEAPON);

        // Safety: If weapon is empty, just clear the grid so gems don't get stuck
        if (weapon.isEmpty()) {
            packGemsIntoBook();
            return;
        }
        List<Gem> unpackedGems = GemType.getGemsFromWeapon(weapon);
        setGemsIntoSlots(unpackedGems);
        GemType.cleanGemsFromWeapon(weapon);

    }

    public void setGemsIntoSlots(List<Gem> gems) {
        cleanGemItemsFromSlots();
        for (int i = 0; i < gems.size() && i < GEM_SLOTS.length; i++) {
            Gem gem = gems.get(i);
            ItemStack gemStack = GemType.getGemStackFromGem(gem);
            itemHandler.setStackInSlot(GEM_SLOTS[i], gemStack);
        }
    }

















    // --- SAVE / LOAD / SYNC ---
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
    }

    protected void sync() {
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }


    public void tick(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide) return;
    }





    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SanguineLapidaryMenu(id, inventory, itemHandler, this);
    }




    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }

    }



}
