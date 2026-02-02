package net.agusdropout.bloodyhell.block.entity.custom.plant;


import net.agusdropout.bloodyhell.block.custom.plant.BloodGemSproutBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
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

public class BloodGemSproutBlockEntity extends BlockEntity {

    // --- CONFIG ---
    private static final int BLOOD_PER_STAGE = 250;

    // --- STATE ---
    private int growthTimer = 0;
    // Default to RED (0xFF0000). You can change this via a "Seed Item" later.
    private int gemColor = 0xFFDC00;

    private final FluidTank bloodTank = new FluidTank(2000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.BLOOD_SOURCE.get();
        }
        @Override
        protected void onContentsChanged() { setChanged(); sync(); }
    };

    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> bloodTank);

    public BloodGemSproutBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.BLOOD_GEM_SPROUT_BE.get(), pPos, pState);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        int age = state.getValue(BloodGemSproutBlock.AGE);
        if (age >= 3) return; // Fully grown

        growthTimer++;
        if (growthTimer >= 100) { // Check every 5 seconds
            growthTimer = 0;
            if (bloodTank.getFluidAmount() >= BLOOD_PER_STAGE) {
                if (level.random.nextInt(3) == 0) {
                    bloodTank.drain(BLOOD_PER_STAGE, IFluidHandler.FluidAction.EXECUTE);
                    level.setBlock(pos, state.setValue(BloodGemSproutBlock.AGE, age + 1), 3);
                    sync();
                }
            }
        }
    }

    public void resetGrowth() {
        this.growthTimer = 0;
    }

    // Called by Renderer
    public int getGemColor() {
        return gemColor;
    }

    public void setGemColor(int color) {
        this.gemColor = color;
        setChanged();
        sync();
    }

    // --- SAVE / LOAD / SYNC ---
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("BloodTank", bloodTank.writeToNBT(new CompoundTag()));
        nbt.putInt("Growth", growthTimer);
        nbt.putInt("GemColor", gemColor);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        bloodTank.readFromNBT(nbt.getCompound("BloodTank"));
        growthTimer = nbt.getInt("Growth");
        gemColor = nbt.getInt("GemColor");
    }

    private void sync() {
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) { load(pkt.getTag()); }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }
}