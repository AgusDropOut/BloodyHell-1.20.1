package net.agusdropout.bloodyhell.block.entity.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.base.BaseGemSproutBlock;
import net.agusdropout.bloodyhell.block.custom.plant.BloodGemSproutBlock;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseGemSproutBlockEntity extends BlockEntity {

    protected int growthTimer = 0;
    private int renderTimer = 0;
    private static final int MAX_ITEM_INSIDE_RENDER_TIME = 50;
    private int gemColor = 0x3cff00;
    private String gemType = "empty";
    protected final int MAX_GROWTH_TOME;
    protected final int BLOOD_PER_STAGE;
    protected final float GROWTH_CHANCE;
    public static final int INSERT_GEM_AGE = 2;
    private ItemStack tempStoredItem = ItemStack.EMPTY;
    private static final TagKey<Fluid> FORGE_BLOOD_TAG = TagKey.create(
            ForgeRegistries.Keys.FLUIDS,
            new ResourceLocation("forge", "blood")
    );

    public BaseGemSproutBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState, int MAX_GROWTH_TIME, int BLOOD_PER_STAGE, float GROWTH_CHANCE) {
        super(entityType, blockPos, blockState);
        this.MAX_GROWTH_TOME = MAX_GROWTH_TIME;
        this.BLOOD_PER_STAGE = BLOOD_PER_STAGE;
        this.GROWTH_CHANCE = GROWTH_CHANCE;

    }

    private final FluidTank bloodTank = new FluidTank(2000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return isAcceptableFluid(stack);
        }
        @Override
        protected void onContentsChanged() { setChanged(); sync();  }
    };
    private boolean isAcceptableFluid(FluidStack stack) {
        if (stack.isEmpty()) return false;
        Fluid fluid = stack.getFluid();

        return fluid.isSame(getValidFluid()) || fluid.builtInRegistryHolder().is(FORGE_BLOOD_TAG);
    }

    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> bloodTank);

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("BloodTank", bloodTank.writeToNBT(new CompoundTag()));
        nbt.putInt("Growth", growthTimer);
        nbt.putInt("GemColor", gemColor);
        nbt.putString("GemType", gemType );
        nbt.putInt("RenderTimer", renderTimer);
        if (!this.tempStoredItem.isEmpty()) {
            nbt.put("RenderItem", this.tempStoredItem.save(new CompoundTag()));
        }

    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        bloodTank.readFromNBT(nbt.getCompound("BloodTank"));
        growthTimer = nbt.getInt("Growth");
        gemColor = nbt.getInt("GemColor");
        gemType = nbt.getString("GemType");
        renderTimer = nbt.getInt("RenderTimer");
        if (nbt.contains("RenderItem")) {
            this.tempStoredItem = ItemStack.of(nbt.getCompound("RenderItem"));
        } else {
            this.tempStoredItem = ItemStack.EMPTY;
        }
    }

    protected void sync() {
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide) return;
        int age = state.getValue(BloodGemSproutBlock.AGE);
        if (age >= BaseGemSproutBlock.MAX_AGE) return;

        if (hasBlood(1) && age != INSERT_GEM_AGE) {
            growthTimer++;
        }
        if (age == INSERT_GEM_AGE) {
            if(state.getValue(BloodGemSproutBlock.ITEM_INSIDE)){
                if (renderTimer < MAX_ITEM_INSIDE_RENDER_TIME ) {
                    renderTimer++;
                } else {
                    renderTimer = 0;
                    BlockState newState = state
                            .setValue(BloodGemSproutBlock.ITEM_INSIDE, false)
                            .setValue(BloodGemSproutBlock.AGE, 3);
                    this.setTempStoredItem(ItemStack.EMPTY);
                    level.setBlock(pos, newState, 3);
                    state = newState;
                    sync();
                }
            }
        }

        if (growthTimer >= MAX_GROWTH_TOME ) {
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

        boolean shouldBeFilled = hasBlood(BLOOD_PER_STAGE);
        boolean isCurrentlyFilled = state.getValue(BloodGemSproutBlock.FILLED);

        if (shouldBeFilled != isCurrentlyFilled) {
            level.setBlock(pos, state.setValue(BloodGemSproutBlock.FILLED, shouldBeFilled), 3);
        }
    }

    private boolean hasBlood(int amount) {
        return bloodTank.getFluidAmount() >= amount;
    }

    public boolean fillBlood(FluidStack fluidStack, boolean simulate) {
        if (isAcceptableFluid(fluidStack)) {
            FluidStack toFill = new FluidStack(fluidStack.getFluid(), fluidStack.getAmount());
            int filled = bloodTank.fill(toFill, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0 && !simulate) {
                setChanged();
                sync();
            }
            return filled > 0;
        }
        return false;
    }

    public void setGemColor(int color) {
        this.gemColor = color;
        setChanged();
        sync();
    }

    public int getGemColor() {
        return this.gemColor;
    }

    public abstract Fluid getValidFluid();

    public abstract void getRenderingGemShape(VertexConsumer consumer, PoseStack poseStack);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }

    public void setGemType(String gemType) {
        this.gemType = gemType;
        setChanged();
        sync();
    }

    public ItemStack getResultGem(RandomSource source) {
          GemType gem = GemType.byName(gemType);
          ItemStack gemStack = new ItemStack(gem.getResultGem());
          CompoundTag nbt = gemStack.getOrCreateTag();
          nbt.putDouble(gem.getBonusType(), gem.getBonusStat(source));
          if (gem != GemType.EMPTY) {
              return gemStack;
          }else {
              return ItemStack.EMPTY;
          }
    }

    public void setTempStoredItem(ItemStack stack) {
        this.tempStoredItem = stack;
        setChanged();
        sync();
    }
    public ItemStack getTempStoredItem() {
        return this.tempStoredItem;
    }

    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) { load(pkt.getTag()); }

}
