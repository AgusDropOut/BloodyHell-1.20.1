package net.agusdropout.bloodyhell.block.entity.base;

import net.agusdropout.bloodyhell.recipe.CondenserRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;


public abstract class AbstractCondenserBlockEntity extends BlockEntity {

    public static final int MAX_CAPACITY = 10000;

    public final FluidTank fluidTank;
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    protected final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public AbstractCondenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.fluidTank = new FluidTank(MAX_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return AbstractCondenserBlockEntity.this.isValidFluid(stack);
            }
        };
    }

    protected abstract boolean isValidFluid(FluidStack stack);

    public void tick() {
        if (level == null || level.isClientSide()) return;
        processRecipes();
    }

    protected void processRecipes() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        FluidStack currentFluid = this.fluidTank.getFluid();
        ItemStack currentItem = this.itemHandler.getStackInSlot(0);

        if (currentFluid.isEmpty() || currentItem.isEmpty()) {
            return;
        }


        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, currentItem);

       Optional<CondenserRecipe> match = this.level.getRecipeManager()
                .getAllRecipesFor(CondenserRecipe.Type.INSTANCE).stream()
                .filter(recipe -> recipe.matches(inventory, this.level) && recipe.matchesFluid(currentFluid))
                .findFirst();

        if (match.isPresent()) {
            net.agusdropout.bloodyhell.recipe.CondenserRecipe recipe = match.get();

            this.fluidTank.drain(recipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);


            this.itemHandler.extractItem(0, 1, false);


            ItemStack result = recipe.getResultItem(this.level.registryAccess());


            net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                    this.level,
                    this.worldPosition.getX() + 0.5,
                    this.worldPosition.getY() + 1.5,
                    this.worldPosition.getZ() + 0.5,
                    result.copy()
            );
            itemEntity.setDeltaMovement(0, 0.1, 0);
            this.level.addFreshEntity(itemEntity);

            this.level.playSound(null, this.worldPosition, net.minecraft.sounds.SoundEvents.LAVA_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 1.5F);

            this.setChanged();
        }
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(1.0D, 2.0D, 1.0D);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyFluidHandler = LazyOptional.of(() -> fluidTank);
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("FluidTank", fluidTank.writeToNBT(new CompoundTag()));
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidTank.readFromNBT(tag.getCompound("FluidTank"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}