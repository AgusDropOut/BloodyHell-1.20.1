package net.agusdropout.bloodyhell.block.entity.base;

import net.agusdropout.bloodyhell.block.base.AbstractPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractPipeBlockEntity extends FilterableFluidBlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean isPullMode = false;

    public AbstractPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state, capacity);
    }

    protected abstract boolean isFluidSupported(FluidStack stack);
    protected abstract void handleUnsupportedFluidTick(Level level, BlockPos pos, BlockState state);
    protected abstract void onSupportedFluidTick(Level level, BlockPos pos, BlockState state);

    public void togglePullMode() {
        this.isPullMode = !this.isPullMode;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean isPullMode() {
        return isPullMode;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        FluidStack myFluid = fluidTank.getFluid();

        if (!myFluid.isEmpty() && !isFluidSupported(myFluid)) {
            handleUnsupportedFluidTick(level, pos, state);
        } else {
            onSupportedFluidTick(level, pos, state);
        }

        for (Direction dir : Direction.values()) {
            BooleanProperty property = getPropertyByDirection(dir);
            if (!state.getValue(property)) continue;

            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);

            if (neighborBE != null) {
                neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {
                    boolean isNeighborPipe = neighborBE instanceof AbstractPipeBlockEntity;

                    if (isNeighborPipe && !myFluid.isEmpty()) {
                        FluidStack neighborFluid = ((AbstractPipeBlockEntity) neighborBE).getFluidTank().getFluid();
                        if (myFluid.getAmount() > neighborFluid.getAmount()) {
                            if (neighborFluid.isEmpty() || neighborFluid.isFluidEqual(myFluid)) {
                                int diff = myFluid.getAmount() - neighborFluid.getAmount();
                                int toTransfer = Math.min(diff / 2, 200);
                                moveFluid(this.fluidTank, handler, toTransfer);
                            }
                        }
                    } else if (!isNeighborPipe) {
                        if (isPullMode) {
                            if (fluidTank.getSpace() > 0) {
                                FluidStack drained = handler.drain(200, IFluidHandler.FluidAction.SIMULATE);
                                if (!drained.isEmpty() && fluidTank.isFluidValid(drained)) {
                                    int filled = fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                                    handler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        } else {
                            if (!myFluid.isEmpty()) {
                                moveFluid(this.fluidTank, handler, 500);
                            }
                        }
                    }
                });
            }
        }
    }

    private void moveFluid(IFluidHandler source, IFluidHandler dest, int maxTransfer) {
        FluidStack simulatedDrain = source.drain(maxTransfer, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.getAmount() > 0) {
            int filledAmount = dest.fill(simulatedDrain, IFluidHandler.FluidAction.EXECUTE);
            if (filledAmount > 0) {
                source.drain(filledAmount, IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    private BooleanProperty getPropertyByDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> AbstractPipeBlock.NORTH;
            case SOUTH -> AbstractPipeBlock.SOUTH;
            case EAST -> AbstractPipeBlock.EAST;
            case WEST -> AbstractPipeBlock.WEST;
            case UP -> AbstractPipeBlock.UP;
            case DOWN -> AbstractPipeBlock.DOWN;
        };
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("IsPullMode", isPullMode);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        isPullMode = nbt.getBoolean("IsPullMode");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}