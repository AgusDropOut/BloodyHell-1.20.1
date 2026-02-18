package net.agusdropout.bloodyhell.block.entity.base;

import net.agusdropout.bloodyhell.item.custom.base.BasePowerGemItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.screen.custom.menu.SanguineLapidaryMenu;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSanguineLapidaryBlockEntity extends BlockEntity implements MenuProvider {

    private static final int SLOT_WEAPON = 3;
    private static final int SLOT_GEM_1 = 0;
    private static final int SLOT_GEM_2 = 1;
    private static final int SLOT_GEM_3 = 2;
    private static final int[] GEM_SLOTS = {SLOT_GEM_1, SLOT_GEM_2, SLOT_GEM_3};

    // CHANGED: Added ghost items for client-side rendering
    private ItemStack ghostWeapon = ItemStack.EMPTY;
    private ItemStack ghostGem1 = ItemStack.EMPTY;
    private ItemStack ghostGem2 = ItemStack.EMPTY;
    private ItemStack ghostGem3 = ItemStack.EMPTY;

    // CHANGED: Added shared animation tick state
    public int animationTick = 0;
    private static final int ANIM_DURATION = 40;

    private boolean isHandlingSlotUpdate = false;

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case SLOT_WEAPON -> isWeapon(stack);
                case SLOT_GEM_1, SLOT_GEM_2, SLOT_GEM_3 -> isGem(stack);
                default -> super.isItemValid(slot, stack);
            };
        }

        private boolean isWeapon(ItemStack stack) {
            return stack.getItem() instanceof BaseSpellBookItem<?>;
        }

        private boolean isGem(ItemStack stack) {
            return stack.getItem() instanceof BasePowerGemItem;
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
            if (slot == SLOT_WEAPON) {
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
        if (this.level.isClientSide) return;
    }

    private void packGemsIntoBook() {
        if (checkForValidRecipe()) {

            this.ghostWeapon = itemHandler.getStackInSlot(SLOT_WEAPON).copy();
            this.ghostGem1 = itemHandler.getStackInSlot(SLOT_GEM_1).copy();
            this.ghostGem2 = itemHandler.getStackInSlot(SLOT_GEM_2).copy();
            this.ghostGem3 = itemHandler.getStackInSlot(SLOT_GEM_3).copy();


            this.animationTick = ANIM_DURATION;

            List<Gem> gemsToPack = getGemsInSlots();
            ItemStack weapon = getWeaponInSlot();
            GemType.putGemsIntoWeapon(weapon, gemsToPack);
            cleanGemItemsFromSlots();

            level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);


            sync();
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

    private List<Gem> getGemsInSlots() {
        List<Gem> gems = new ArrayList<>();
        for (int slot : GEM_SLOTS) {
            ItemStack gemStack = itemHandler.getStackInSlot(slot);
            GemType gemType = GemType.getGemTypeFromGemStack(gemStack);
            if (!gemStack.isEmpty() && gemStack.getItem() instanceof BasePowerGemItem) {
                gems.add(new Gem(gemType, gemType.getBonusType(), GemType.getStatValueFromGemStack(gemStack)));
            }
        }
        return gems;
    }

    private void unpackGemsFromBook() {
        ItemStack weapon = itemHandler.getStackInSlot(SLOT_WEAPON);
        if (weapon.isEmpty()) {
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

    // CHANGED: Tick logic now runs on both sides to countdown animation and clean up
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.animationTick > 0) {
            this.animationTick--;

            if (level.isClientSide && this.animationTick == 0) {
                spawnFusionParticles(level, pos);
            }

            if (this.animationTick == 0) {
                this.ghostWeapon = ItemStack.EMPTY;
                this.ghostGem1 = ItemStack.EMPTY;
                this.ghostGem2 = ItemStack.EMPTY;
                this.ghostGem3 = ItemStack.EMPTY;
            }
        }
    }


    public ItemStack getRenderWeapon() {
        return animationTick > 0 ? ghostWeapon : itemHandler.getStackInSlot(SLOT_WEAPON);
    }

    public ItemStack getRenderGem(int index) {
        if (animationTick > 0) {
            return switch (index) {
                case 0 -> ghostGem1;
                case 1 -> ghostGem2;
                case 2 -> ghostGem3;
                default -> ItemStack.EMPTY;
            };
        }
        return itemHandler.getStackInSlot(GEM_SLOTS[index]);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Inventory", itemHandler.serializeNBT());

        if (this.animationTick > 0) {
            nbt.putInt("AnimTick", animationTick);
            nbt.put("GhostWeapon", ghostWeapon.save(new CompoundTag()));
            nbt.put("GhostGem1", ghostGem1.save(new CompoundTag()));
            nbt.put("GhostGem2", ghostGem2.save(new CompoundTag()));
            nbt.put("GhostGem3", ghostGem3.save(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Inventory")) {
            itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
        }

        // CHANGED: Load animation state if present
        if (nbt.contains("AnimTick")) {
            this.animationTick = nbt.getInt("AnimTick");
            ghostWeapon = ItemStack.of(nbt.getCompound("GhostWeapon"));
            ghostGem1 = ItemStack.of(nbt.getCompound("GhostGem1"));
            ghostGem2 = ItemStack.of(nbt.getCompound("GhostGem2"));
            ghostGem3 = ItemStack.of(nbt.getCompound("GhostGem3"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    protected void sync() {
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SanguineLapidaryMenu(id, inventory, itemHandler, this);
    }


    private void spawnFusionParticles(Level level, BlockPos pos) {
        Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5);

        spawnGemBurst(level, center, ghostGem1);
        spawnGemBurst(level, center, ghostGem2);
        spawnGemBurst(level, center, ghostGem3);

        ParticleHelper.spawnBurst(level,
                new MagicParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 0.5f, true, 20),
                center, 15, 0.2);

        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0f, 1.5f, false);
    }

    private void spawnGemBurst(Level level, Vec3 center, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BasePowerGemItem) {
            GemType type = GemType.getGemTypeFromGemStack(stack);

            int hexColor = type.getColor();

            float r = ((hexColor >> 16) & 0xFF) / 255.0f;
            float g = ((hexColor >> 8) & 0xFF) / 255.0f;
            float b = (hexColor & 0xFF) / 255.0f;
            Vector3f colorVec = new Vector3f(r, g, b);

            MagicParticleOptions particle = new MagicParticleOptions(colorVec, 0.4f, false, 30);

            ParticleHelper.spawnBurst(level, particle, center, 12, 0.4);
        }
    }
}