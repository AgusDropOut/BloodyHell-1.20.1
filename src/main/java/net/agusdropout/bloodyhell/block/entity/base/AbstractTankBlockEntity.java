package net.agusdropout.bloodyhell.block.entity.base;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTankBlockEntity extends FilterableFluidBlockEntity {

    private static final int CAPACITY_PER_BLOCK = 16000;
    private static final int MAX_HEIGHT = 32;

    private BlockPos controllerPos;
    private boolean isStructureValid = false;
    private int structureHeight = 1;
    private int structureWidth = 1;
    private long lastScanTime = -1;

    public AbstractTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, CAPACITY_PER_BLOCK);


        this.fluidTank = new FluidTank(CAPACITY_PER_BLOCK) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                // 1. Must be supported by this specific tank type
                if (!isFluidSupported(stack)) return false;

                // 2. Must pass the user-defined filter (if any is set)
                return AbstractTankBlockEntity.this.getFilter() == null
                        || AbstractTankBlockEntity.this.getFilter().isSame(stack.getFluid());
            }
        };
        this.lazyFluidHandler = LazyOptional.of(() -> this.fluidTank);
    }

    /**
     * Define which fluids this specific tank type is allowed to hold.
     * This blocks insertion from Pipes, Flasks, and Buckets simultaneously.
     */
    public abstract boolean isFluidSupported(FluidStack stack);


    @Override
    public Fluid getFilter() {
        if (!isController()) {
            return getController().getFilter();
        }
        return super.getFilter();
    }

    @Override
    public void setFilter(Fluid fluid) {
        if (!isController()) {
            getController().setFilter(fluid);
            return;
        }
        super.setFilter(fluid);
    }

    @Override
    public void cycleFilter(Level level, BlockPos pos, Player player) {
        if (!isController()) {
            getController().cycleFilter(level, controllerPos, player);
            return;
        }
        super.cycleFilter(level, pos, player);
    }


    public FluidStack getFluid() {
        if (level == null) return FluidStack.EMPTY;
        if (isController()) {
            return fluidTank.getFluid();
        }
        AbstractTankBlockEntity controller = getController();
        if (controller == this || controller == null) {
            return FluidStack.EMPTY;
        }
        return controller.getFluid();
    }

    public AbstractTankBlockEntity getController() {
        if (level == null) return this;
        if (controllerPos == null || level.getGameTime() - lastScanTime > 20) {
            validateStructure();
        }
        if (controllerPos != null) {
            BlockEntity be = level.getBlockEntity(controllerPos);
            if (be instanceof AbstractTankBlockEntity controller) {
                return controller;
            }
        }
        return this;
    }

    public boolean isController() {
        if (level == null) return true;
        if (controllerPos == null) return true;
        return this.worldPosition.equals(controllerPos);
    }

    private void validateStructure() {
        if (level == null) return;
        this.lastScanTime = level.getGameTime();

        BlockPos corner = findBottomLeftCorner(this.worldPosition);

        int width = 0;
        int depth = 0;

        while (width < 3 && isSameBlock(corner.offset(width + 1, 0, 0))) width++;
        width++;

        while (depth < 3 && isSameBlock(corner.offset(0, 0, depth + 1))) depth++;
        depth++;

        if (width != depth) {
            invalidateStructure();
            return;
        }

        int height = 0;
        while (height < MAX_HEIGHT && isSameBlock(corner.offset(0, height + 1, 0))) height++;
        height++;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                for (int y = 0; y < height; y++) {
                    if (!isSameBlock(corner.offset(x, y, z))) {
                        invalidateStructure();
                        return;
                    }
                }
            }
        }

        this.controllerPos = corner;
        this.structureWidth = width;
        this.structureHeight = height;
        this.isStructureValid = true;
    }

    private void invalidateStructure() {
        this.isStructureValid = false;
        this.controllerPos = this.worldPosition;
        this.structureWidth = 1;
        this.structureHeight = 1;
    }

    private BlockPos findBottomLeftCorner(BlockPos start) {
        BlockPos p = start;
        while (isSameBlock(p.below())) p = p.below();
        while (isSameBlock(p.west())) p = p.west();
        while (isSameBlock(p.north())) p = p.north();
        return p;
    }

    private boolean isSameBlock(BlockPos pos) {
        return level != null && level.getBlockState(pos).getBlock() == this.getBlockState().getBlock();
    }

    public int getCapacity() {
        if (!isController()) {
            AbstractTankBlockEntity controller = getController();
            if (controller != this && controller != null) {
                return controller.getCapacity();
            }
        }

        if (!isStructureValid) return CAPACITY_PER_BLOCK;
        int totalBlocks = structureWidth * structureWidth * structureHeight;
        return totalBlocks * CAPACITY_PER_BLOCK;
    }

    public int getLocalHeight() {
        if (!isStructureValid) return 1;
        return structureHeight;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            AbstractTankBlockEntity controller = getController();

            if (!controller.isStructureValid && controller.structureWidth > 1) {
                return LazyOptional.empty();
            }

            int targetCapacity = controller.getCapacity();
            if (controller.fluidTank.getCapacity() != targetCapacity) {
                controller.fluidTank.setCapacity(targetCapacity);
            }
            return controller.lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}