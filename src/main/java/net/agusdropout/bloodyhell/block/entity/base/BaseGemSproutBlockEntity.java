package net.agusdropout.bloodyhell.block.entity.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.custom.plant.BloodGemSproutBlock;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseGemSproutBlockEntity extends BlockEntity {

    protected int growthTimer = 0;
    protected int gemColor = 0xFFDC00;
    protected final int MAX_GROWTH_TOME;
    protected final int BLOOD_PER_STAGE;
    protected final float GROWTH_CHANCE;




    public BaseGemSproutBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState, int MAX_GROWTH_TIME, int BLOOD_PER_STAGE, float GROWTH_CHANCE) {
        super(entityType, blockPos, blockState);
        this.MAX_GROWTH_TOME = MAX_GROWTH_TIME;
        this.BLOOD_PER_STAGE = BLOOD_PER_STAGE;
        this.GROWTH_CHANCE = GROWTH_CHANCE;

    }

    private final FluidTank bloodTank = new FluidTank(2000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.BLOOD_SOURCE.get();
        }
        @Override
        protected void onContentsChanged() { setChanged(); sync();  }
    };

    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> bloodTank);


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

    protected void sync() {
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }


    public void tick(Level level, BlockPos pos, BlockState state) {
        int age = state.getValue(BloodGemSproutBlock.AGE);
        if (age >= 4) return;
        if (hasBlood(1)) {
            growthTimer++;
        }

        if (growthTimer >= MAX_GROWTH_TOME) {
            growthTimer = 0;
            if (bloodTank.getFluidAmount() >= BLOOD_PER_STAGE) {
                if (level.random.nextFloat() <GROWTH_CHANCE) {
                    bloodTank.drain(BLOOD_PER_STAGE, IFluidHandler.FluidAction.EXECUTE);
                    BlockState newState = state.setValue(BloodGemSproutBlock.AGE, age + 1);


                    level.setBlock(pos, newState, 3);


                    state = newState;
                    sync();
                }
            }
        }


        boolean shouldBeFilled = hasBlood(1);
        boolean isCurrentlyFilled = state.getValue(BloodGemSproutBlock.FILLED);

        if (shouldBeFilled != isCurrentlyFilled) {
            level.setBlock(pos, state.setValue(BloodGemSproutBlock.FILLED, shouldBeFilled), 3);
        }
    }

    private boolean hasBlood(int amount) {
        return bloodTank.getFluidAmount() >= amount;
    }

    public boolean fillBlood(FluidStack fluidStack, boolean simulate) {
        if(fluidStack.getFluid().isSame(this.getValidFluid())) {
            FluidStack toFill = new FluidStack(fluidStack.getFluid(), fluidStack.getAmount());
            int filled = bloodTank.fill(toFill, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0 && !simulate) {
                setChanged();
                sync();
            }
            return filled > 0;
        }
        return  false;

    }

    public abstract Fluid getValidFluid();

    public abstract int getGemColor();

    public abstract void getRenderingGemShape(VertexConsumer consumer, PoseStack poseStack);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) { load(pkt.getTag()); }


}
