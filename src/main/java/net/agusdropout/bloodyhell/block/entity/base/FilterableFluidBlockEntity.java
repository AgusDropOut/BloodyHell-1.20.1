package net.agusdropout.bloodyhell.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FilterableFluidBlockEntity extends BlockEntity implements IFluidFilterable {

    protected Fluid filter = Fluids.EMPTY;
    protected final FluidTank fluidTank;
    protected final LazyOptional<IFluidHandler> lazyFluidHandler;

    public FilterableFluidBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);
        this.fluidTank = createTank(capacity);
        this.lazyFluidHandler = LazyOptional.of(() -> fluidTank);
    }

    private FluidTank createTank(int capacity) {
        return new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return isFluidAllowed(stack);
            }
        };
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public Fluid getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Fluid fluid) {
        this.filter = fluid;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt = fluidTank.writeToNBT(nbt);
        nbt.putString("Filter", ForgeRegistries.FLUIDS.getKey(filter).toString());
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        fluidTank.readFromNBT(nbt);
        if (nbt.contains("Filter")) {
            this.filter = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("Filter")));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    public FluidStack getFluidInTank() {
        return fluidTank.getFluid();
    }
}