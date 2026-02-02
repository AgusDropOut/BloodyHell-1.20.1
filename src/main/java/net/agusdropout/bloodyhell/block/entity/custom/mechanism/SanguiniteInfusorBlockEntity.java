package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
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

    // --- CONFIG ---
    private int progress = 0;
    private int maxProgress = 200;
    private float rotation = 0;

    // --- CACHED RECIPE DATA (For Client Visuals) ---
    // We store the costs here so the client can access them for rendering
    // without needing the full recipe object synced perfectly every tick.
    private int clientBloodCost = 0;
    private int clientVisceralCost = 0;

    // --- INVENTORY ---
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

    // --- TANKS ---
    private final FluidTank bloodTank = new FluidTank(4000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.BLOOD_SOURCE.get();
        }
        @Override
        protected void onContentsChanged() { setChanged(); sync(); }
    };

    private final FluidTank visceralTank = new FluidTank(4000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.VISCERAL_BLOOD_SOURCE.get();
        }
        @Override
        protected void onContentsChanged() { setChanged(); sync(); }
    };

    // --- CAPABILITIES ---
    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() { return 2; }
        @Override
        public @NotNull FluidStack getFluidInTank(int tank) { return tank == 0 ? bloodTank.getFluid() : visceralTank.getFluid(); }
        @Override
        public int getTankCapacity(int tank) { return 4000; }
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 ? bloodTank.isFluidValid(stack) : visceralTank.isFluidValid(stack);
        }
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (bloodTank.isFluidValid(resource)) return bloodTank.fill(resource, action);
            if (visceralTank.isFluidValid(resource)) return visceralTank.fill(resource, action);
            return 0;
        }
        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    });

    public SanguiniteInfusorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_INFUSOR_BE.get(), pos, state);
    }

    // --- TICK LOGIC ---
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
            // Update cached costs for sync
            this.clientBloodCost = recipe.getBloodCost();
            this.clientVisceralCost = recipe.getVisceralCost();

            progress++;

            if (progress % 40 == 1) {
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.5f, 1.5f);
            }

            if (progress >= maxProgress) {
                processItem(recipe);
                progress = 0;
                // Reset costs on finish
                this.clientBloodCost = 0;
                this.clientVisceralCost = 0;
            }
        } else {
            progress = 0;
            this.clientBloodCost = 0;
            this.clientVisceralCost = 0;
        }

        if (progress > 0 && progress % 20 == 0) sync();
    }

    // --- VISUALS ---
    private void spawnWorkingParticles(BlockPos pos) {
        double offset = 0.35;
        double startY = pos.getY() + 0.9;

        double[][] corners = {
                {pos.getX() + 0.5 - offset, pos.getZ() + 0.5 - offset}, // 0: NW
                {pos.getX() + 0.5 + offset, pos.getZ() + 0.5 - offset}, // 1: NE
                {pos.getX() + 0.5 - offset, pos.getZ() + 0.5 + offset}, // 2: SW
                {pos.getX() + 0.5 + offset, pos.getZ() + 0.5 + offset}  // 3: SE
        };

        // Gradients
        Vector3f bloodStart = new Vector3f(0.8f, 0.0f, 0.0f);
        Vector3f bloodEnd = new Vector3f(1.0f, 0.4f, 0.6f);
        Vector3f infectedStart = new Vector3f(0.1f, 0.8f, 0.2f);
        Vector3f infectedEnd = new Vector3f(0.6f, 0.8f, 0.1f);

        // Determine Mode
        boolean useBlood = clientBloodCost > 0;
        boolean useVisceral = clientVisceralCost > 0;

        for (int i = 0; i < 4; i++) {
            if (level.random.nextFloat() < 0.2f) {
                double[] corner = corners[i];
                double velX = (pos.getX() + 0.5 - corner[0]) * 0.05;
                double velZ = (pos.getZ() + 0.5 - corner[1]) * 0.05;
                double velY = 0.1;

                Vector3f color;
                float ratio = level.random.nextFloat();

                // LOGIC: Determine Color based on Corner + Recipe Requirement
                if (useBlood && useVisceral) {
                    // Mixed Mode: Diagonal Split
                    if (i == 0 || i == 3) {
                        color = lerpColor(bloodStart, bloodEnd, ratio);
                    } else {
                        color = lerpColor(infectedStart, infectedEnd, ratio);
                    }
                } else if (useVisceral) {
                    // Visceral Only: All Green
                    color = lerpColor(infectedStart, infectedEnd, ratio);
                } else {
                    // Blood Only (or Default): All Red
                    color = lerpColor(bloodStart, bloodEnd, ratio);
                }

                level.addParticle(
                        new MagicParticleOptions(color, 0.5f, false, 20),
                        corner[0], startY, corner[1],
                        velX, velY, velZ
                );
            }
        }

        if (level.random.nextFloat() < 0.3f) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.25;
            double z = pos.getZ() + 0.5;
            level.addParticle(ModParticles.BLOOD_PULSE_PARTICLE.get(), x, y, z, 0, 0, 0);
        }
    }

    private Vector3f lerpColor(Vector3f start, Vector3f end, float ratio) {
        return new Vector3f(
                start.x + (end.x - start.x) * ratio,
                start.y + (end.y - start.y) * ratio,
                start.z + (end.z - start.z) * ratio
        );
    }

    private void spawnResultParticles(BlockPos pos) {
        if (level.random.nextFloat() < 0.15f) {
            double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
            double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 1.0;
            Vector3f goldColor = new Vector3f(1.0f, 0.84f, 0.0f);
            level.addParticle(new MagicParticleOptions(goldColor, 0.4f, false, 40), x, y, z, 0.0, 0.03, 0.0);
        }
    }

    // --- RENDERER HELPERS ---
    public ItemStack getRenderStack() {
        ItemStack input = itemHandler.getStackInSlot(0);
        return !input.isEmpty() ? input : itemHandler.getStackInSlot(1);
    }

    public float getRotation() { return rotation; }
    public boolean isWorking() { return progress > 0; }

    /**
     * Calculates the Color Vector for the Atlas Heart based on current recipe.
     */
    public Vector3f getHeartColor() {
        if (clientBloodCost > 0 && clientVisceralCost > 0) {
            return new Vector3f(1.0f, 0.5f, 0.0f); // Orange/Gold (Mixed)
        } else if (clientVisceralCost > 0) {
            return new Vector3f(0.2f, 1.0f, 0.2f); // Green (Infected)
        } else {
            return new Vector3f(1.0f, 0.1f, 0.1f); // Red (Blood)
        }
    }

    // --- RECIPE LOGIC ---
    private SanguiniteInfusorRecipe getCurrentRecipe() {
        SimpleContainer temp = new SimpleContainer(1);
        temp.setItem(0, itemHandler.getStackInSlot(0));
        return level.getRecipeManager().getRecipeFor(SanguiniteInfusorRecipe.Type.INSTANCE, temp, level).orElse(null);
    }

    private boolean canProcess(SanguiniteInfusorRecipe recipe) {
        if (bloodTank.getFluidAmount() < recipe.getBloodCost()) return false;
        if (visceralTank.getFluidAmount() < recipe.getVisceralCost()) return false;
        ItemStack result = recipe.getResultItem(level.registryAccess());
        ItemStack out = itemHandler.getStackInSlot(1);
        if (out.isEmpty()) return true;
        return out.is(result.getItem()) && out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    private void processItem(SanguiniteInfusorRecipe recipe) {
        itemHandler.extractItem(0, 1, false);
        bloodTank.drain(recipe.getBloodCost(), IFluidHandler.FluidAction.EXECUTE);
        visceralTank.drain(recipe.getVisceralCost(), IFluidHandler.FluidAction.EXECUTE);
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

    // --- BOILERPLATE ---
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("Inventory", itemHandler.serializeNBT());
        nbt.put("BloodTank", bloodTank.writeToNBT(new CompoundTag()));
        nbt.put("VisceralTank", visceralTank.writeToNBT(new CompoundTag()));
        nbt.putInt("Progress", progress);
        // Save client data for sync
        nbt.putInt("ClientBlood", clientBloodCost);
        nbt.putInt("ClientVisceral", clientVisceralCost);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
        bloodTank.readFromNBT(nbt.getCompound("BloodTank"));
        visceralTank.readFromNBT(nbt.getCompound("VisceralTank"));
        progress = nbt.getInt("Progress");
        clientBloodCost = nbt.getInt("ClientBlood");
        clientVisceralCost = nbt.getInt("ClientVisceral");
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