package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;

public class SanguiniteInfusorBlockEntity extends BlockEntity {

    private int progress = 0;
    private int maxProgress = 200;
    private float rotation = 0;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            sync();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? 1 : 64;
        }
    };

    private final FluidTank tank1 = new FluidTank(4000) {
        @Override protected void onContentsChanged() { setChanged(); sync(); }
    };

    private final FluidTank tank2 = new FluidTank(4000) {
        @Override protected void onContentsChanged() { setChanged(); sync(); }
    };

    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override public int getTanks() { return 2; }
        @Override public @NotNull FluidStack getFluidInTank(int tank) { return tank == 0 ? tank1.getFluid() : tank2.getFluid(); }
        @Override public int getTankCapacity(int tank) { return 4000; }
        @Override public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return true; }
        @Override
        public int fill(FluidStack resource, FluidAction action) {

            if (tank1.isEmpty() || tank1.isFluidValid(resource) && tank1.getFluid().isFluidEqual(resource)) {
                return tank1.fill(resource, action);
            }
            return tank2.fill(resource, action);
        }
        @Override public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    });

    public SanguiniteInfusorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_INFUSOR_BE.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            float speed = progress > 0 ? 15.0f : 1.0f;
            rotation = (rotation + speed) % 360;

            if (progress > 0) {
                spawnWorkingParticles(pos);
            }
            if (!itemHandler.getStackInSlot(1).isEmpty()) {
                spawnResultParticles(pos);
            }
            return;
        }

        SanguiniteInfusorRecipe recipe = getCurrentRecipe();
        if (recipe != null && canProcess(recipe)) {
            progress++;

            if (progress % 40 == 1) {
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.5f, 1.5f);
            }

            if (progress >= maxProgress) {
                processItem(recipe);
                progress = 0;
            }
        } else {
            progress = 0;
        }

        if (progress > 0 && progress % 20 == 0) sync();
    }

    private SanguiniteInfusorRecipe getCurrentRecipe() {
        SimpleContainer temp = new SimpleContainer(1);
        temp.setItem(0, itemHandler.getStackInSlot(0));
        return level.getRecipeManager().getRecipeFor(SanguiniteInfusorRecipe.Type.INSTANCE, temp, level).orElse(null);
    }

    public Vector3f getHeartColor() {
        boolean t1Empty = tank1.isEmpty();
        boolean t2Empty = tank2.isEmpty();

        if (t1Empty && t2Empty) return new Vector3f(0.5f, 0.0f, 0.0f);

        if (!t1Empty && t2Empty) {
            return ColorHelper.hexToVector3f(IClientFluidTypeExtensions.of(tank1.getFluid().getFluid()).getTintColor());
        } else if (t1Empty) {
            return ColorHelper.hexToVector3f(IClientFluidTypeExtensions.of(tank2.getFluid().getFluid()).getTintColor());
        } else {

            Vector3f c1 = ColorHelper.hexToVector3f(IClientFluidTypeExtensions.of(tank1.getFluid().getFluid()).getTintColor());
            Vector3f c2 = ColorHelper.hexToVector3f(IClientFluidTypeExtensions.of(tank2.getFluid().getFluid()).getTintColor());
            return ColorHelper.blend(c1, c2);
        }
    }

    private boolean canProcess(SanguiniteInfusorRecipe recipe) {

        if (!recipe.getFluid1().isEmpty()) {
            if (!tank1.getFluid().isFluidEqual(recipe.getFluid1()) || tank1.getFluidAmount() < recipe.getFluid1().getAmount()) {
                return false;
            }
        }

        if (!recipe.getFluid2().isEmpty()) {
            if (!tank2.getFluid().isFluidEqual(recipe.getFluid2()) || tank2.getFluidAmount() < recipe.getFluid2().getAmount()) {
                return false;
            }
        }

        ItemStack result = recipe.getResultItem(level.registryAccess());
        ItemStack out = itemHandler.getStackInSlot(1);
        if (out.isEmpty()) return true;
        return out.is(result.getItem()) && out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    private void processItem(SanguiniteInfusorRecipe recipe) {
        itemHandler.extractItem(0, 1, false);

        if (!recipe.getFluid1().isEmpty()) tank1.drain(recipe.getFluid1().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        if (!recipe.getFluid2().isEmpty()) tank2.drain(recipe.getFluid2().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
        ItemStack currentOutput = itemHandler.getStackInSlot(1);
        if (currentOutput.isEmpty()) {
            itemHandler.setStackInSlot(1, result);
        } else {
            currentOutput.grow(result.getCount());
            itemHandler.setStackInSlot(1, currentOutput);
        }
        level.playSound(null, worldPosition, SoundEvents.TOTEM_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
        sync();
    }

    private void spawnWorkingParticles(BlockPos pos) {
        double offset = 0.35;
        double startY = pos.getY() + 0.9;

        double[][] corners = {
                {pos.getX() + 0.5 - offset, pos.getZ() + 0.5 - offset},
                {pos.getX() + 0.5 + offset, pos.getZ() + 0.5 - offset},
                {pos.getX() + 0.5 - offset, pos.getZ() + 0.5 + offset},
                {pos.getX() + 0.5 + offset, pos.getZ() + 0.5 + offset}
        };

        boolean useTank1 = !tank1.isEmpty();
        boolean useTank2 = !tank2.isEmpty();

        Vector3f color1 = new Vector3f(0.8f, 0.0f, 0.0f);
        if (useTank1) {
            int tint1 = IClientFluidTypeExtensions.of(tank1.getFluid().getFluid()).getTintColor();
            color1 = ColorHelper.hexToVector3f(tint1);
        }

        Vector3f color2 = new Vector3f(0.1f, 0.8f, 0.2f);
        if (useTank2) {
            int tint2 = IClientFluidTypeExtensions.of(tank2.getFluid().getFluid()).getTintColor();
            color2 = ColorHelper.hexToVector3f(tint2);
        }

        for (int i = 0; i < 4; i++) {
            if (level.random.nextFloat() < 0.2f) {
                double[] corner = corners[i];
                double velX = (pos.getX() + 0.5 - corner[0]) * 0.05;
                double velZ = (pos.getZ() + 0.5 - corner[1]) * 0.05;
                double velY = 0.1;

                Vector3f finalColor;

                if (useTank1 && useTank2) {
                    finalColor = (i == 0 || i == 3) ? color1 : color2;
                } else if (useTank2) {
                    finalColor = color2;
                } else {
                    finalColor = color1;
                }

                level.addParticle(new MagicParticleOptions(finalColor, 0.5f, false, 20),
                        corner[0], startY, corner[1], velX, velY, velZ);
            }
        }

        if (level.random.nextFloat() < 0.3f) {
            level.addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5, 0, 0, 0);
        }
    }

    private void spawnResultParticles(BlockPos pos) {
        if (level.random.nextFloat() < 0.15f) {
            double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
            double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
            level.addParticle(new MagicParticleOptions(new Vector3f(1.0f, 0.84f, 0.0f), 0.4f, false, 40), x, pos.getY() + 1.0, z, 0.0, 0.03, 0.0);
        }
    }

    public ItemStack getRenderStack() {
        ItemStack input = itemHandler.getStackInSlot(0);
        return !input.isEmpty() ? input : itemHandler.getStackInSlot(1);
    }

    public float getRotation() { return rotation; }
    public boolean isWorking() { return progress > 0; }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("Inventory", itemHandler.serializeNBT());
        nbt.put("Tank1", tank1.writeToNBT(new CompoundTag()));
        nbt.put("Tank2", tank2.writeToNBT(new CompoundTag()));
        nbt.putInt("Progress", progress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
        tank1.readFromNBT(nbt.getCompound("Tank1"));
        tank2.readFromNBT(nbt.getCompound("Tank2"));
        progress = nbt.getInt("Progress");
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
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }
}