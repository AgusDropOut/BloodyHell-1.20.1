package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.custom.mechanism.SanguiniteTankBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SanguiniteTankBlockEntity extends BlockEntity {

    private static final int CAPACITY_PER_BLOCK = 16000;
    private static final int MAX_HEIGHT = 32;

    // The internal tank. Only the CONTROLLER's tank actually holds fluid.
    private final FluidTank tank = new FluidTank(CAPACITY_PER_BLOCK) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> tank);

    // Multiblock Data
    private BlockPos controllerPos;
    private boolean isStructureValid = false;
    private int structureHeight = 1;
    private int structureWidth = 1; // 1, 2, or 3
    private long lastScanTime = -1;

    public SanguiniteTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_TANK_BE.get(), pos, state);
    }

    // --- CRASH FIX: Safe Fluid Access ---
    public FluidStack getFluid() {
        if (level == null) return FluidStack.EMPTY;

        // 1. If I am the controller (or a single block), return my tank
        if (isController()) {
            return tank.getFluid();
        }

        // 2. If I am a slave, ask the controller
        SanguiniteTankBlockEntity controller = getController();

        // RECURSION GUARD: If getController returns 'this' but isController is false,
        // something is broken (likely block breaking). Return empty to prevent crash.
        if (controller == this || controller == null) {
            return FluidStack.EMPTY;
        }

        return controller.getFluid();
    }

    // --- CONTROLLER & VALIDATION LOGIC ---

    public SanguiniteTankBlockEntity getController() {
        if (level == null) return this;

        // Re-scan periodically or if invalid
        if (controllerPos == null || level.getGameTime() - lastScanTime > 20) {
            validateStructure();
        }

        if (controllerPos != null) {
            BlockEntity be = level.getBlockEntity(controllerPos);
            if (be instanceof SanguiniteTankBlockEntity controller) {
                return controller;
            }
        }

        return this; // Default to self if structure is broken
    }

    public boolean isController() {
        if (level == null) return true;
        if (controllerPos == null) return true; // Treat as controller until proven otherwise
        return this.worldPosition.equals(controllerPos);
    }

    /**
     * Scans the shape to find the bottom-most, lowest-coordinate block.
     * Validates that the shape is a perfect 1x1, 2x2, or 3x3 prism.
     */
    private void validateStructure() {
        if (level == null) return;
        this.lastScanTime = level.getGameTime();

        // 1. Find the absolute bottom-left-front corner of this connected cluster
        BlockPos corner = findBottomLeftCorner(this.worldPosition);

        // 2. Determine Width (X) and Depth (Z)
        int width = 0;
        int depth = 0;

        // Measure X
        while (width < 3 && isSameBlock(corner.offset(width + 1, 0, 0))) width++;
        width++; // Convert offset to count (0->1)

        // Measure Z
        while (depth < 3 && isSameBlock(corner.offset(0, 0, depth + 1))) depth++;
        depth++;

        // 3. Strict Square Check (1x1, 2x2, 3x3)
        if (width != depth) {
            invalidateStructure();
            return;
        }

        // 4. Measure Height (Y)
        int height = 0;
        while (height < MAX_HEIGHT && isSameBlock(corner.offset(0, height + 1, 0))) height++;
        height++;

        // 5. Verify the entire volume is filled (No holes!)
        // If we found a 2x2x2 box, we check all 8 positions.
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

        // 6. Structure is VALID
        this.controllerPos = corner;
        this.structureWidth = width;
        this.structureHeight = height;
        this.isStructureValid = true;
    }

    private void invalidateStructure() {
        this.isStructureValid = false;
        this.controllerPos = this.worldPosition; // Fallback to self
        this.structureWidth = 1;
        this.structureHeight = 1;
    }

    // Helper: recursively finds the minimum coordinate block connected to this one
    private BlockPos findBottomLeftCorner(BlockPos start) {
        BlockPos p = start;
        // Go Down
        while (isSameBlock(p.below())) p = p.below();
        // Go West
        while (isSameBlock(p.west())) p = p.west();
        // Go North
        while (isSameBlock(p.north())) p = p.north();
        // Double check we didn't miss a connection due to "L" shapes
        // (For strict squares, this simple search is usually enough,
        // but for robustness in weird shapes, it finds the "Master" candidate)
        return p;
    }

    private boolean isSameBlock(BlockPos pos) {
        return level != null && level.getBlockState(pos).getBlock() instanceof SanguiniteTankBlock;
    }

    // --- CAPABILITY & DATA ---

    public int getCapacity() {
        // Only the controller knows the true capacity
        if (!isController()) return getController().getCapacity();

        if (!isStructureValid) return CAPACITY_PER_BLOCK; // 1 block if broken

        // Volume = Width * Width * Height (since it's square)
        int totalBlocks = structureWidth * structureWidth * structureHeight;
        return totalBlocks * CAPACITY_PER_BLOCK;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            SanguiniteTankBlockEntity controller = getController();

            // FEATURE: If structure is invalid, refuse access (unless it's a valid 1x1)
            if (!controller.isStructureValid && controller.structureWidth > 1) {
                return LazyOptional.empty();
            }

            // Update capability of the controller
            int targetCapacity = controller.getCapacity();
            if (controller.tank.getCapacity() != targetCapacity) {
                controller.tank.setCapacity(targetCapacity);
            }

            return controller.lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    // Used by Renderer
    public int getLocalHeight() {
        if (!isStructureValid) return 1;
        return structureHeight;
    }

    // ... Save/Load NBT (Standard) ...
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt = tank.writeToNBT(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        tank.readFromNBT(nbt);
    }

    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }
}