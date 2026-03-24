package net.agusdropout.bloodyhell.block.entity.base;

import net.agusdropout.bloodyhell.block.base.ISingleItemRenderBlock;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.recipe.CondenserRecipe;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Optional;

public abstract class AbstractCondenserBlockEntity extends BaseGeckoBlockEntity implements IFluidBlockHolder, IGeoFluidBlock, ISingleItemRenderBlock {

    public static final int MAX_CAPACITY = 10000;

    private int progress = 0;
    private int maxProgress = 80;
    private boolean isCrafting = false;

    // --- NEW: Caches the registry name of the last fluid added ---
    private String lastFluidName = "";

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
                if (!this.getFluid().isEmpty()) {
                    lastFluidName = ForgeRegistries.FLUIDS.getKey(this.getFluid().getFluid()).toString();
                }

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

        boolean wasCrafting = this.isCrafting;
        Optional<CondenserRecipe> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            this.isCrafting = true;
            this.progress++;

            if (this.progress >= this.maxProgress) {
                craftItem(recipe.get());
                this.progress = 0;
            }
        } else {
            this.progress = 0;
            this.isCrafting = false;
        }

        if (wasCrafting != this.isCrafting) {
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private Optional<CondenserRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, this.itemHandler.getStackInSlot(0));

        return this.level.getRecipeManager()
                .getAllRecipesFor(CondenserRecipe.Type.INSTANCE).stream()
                .filter(recipe -> recipe.matches(inventory, this.level) && recipe.matchesFluid(this.fluidTank.getFluid()))
                .findFirst();
    }

    private void craftItem(CondenserRecipe recipe) {
        this.fluidTank.drain(recipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        this.itemHandler.extractItem(0, 1, false);

        ItemStack result = recipe.getResultItem(this.level.registryAccess());

        ItemEntity itemEntity = new ItemEntity(
                this.level,
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 1.2,
                this.worldPosition.getZ() + 0.5,
                result.copy()
        );
        itemEntity.setDeltaMovement(0, 0.2, 0);
        this.level.addFreshEntity(itemEntity);

        this.level.playSound(null, this.worldPosition, net.minecraft.sounds.SoundEvents.LAVA_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 1.5F);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public boolean isCrafting() {
        return this.isCrafting;
    }

    public int getProgress() {
        return this.progress;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<AbstractCondenserBlockEntity> controller = new AnimationController<>(this, "controller", 5, state -> {
            if (this.isCrafting()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("active"));
            } else {
                return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
        });

        controller.setCustomInstructionKeyframeHandler(event -> {
            for (String instruction : event.getKeyframeData().getInstructions().split(";")) {
                switch (instruction.trim()) {
                    case "pumpStart":
                        handlePumpStart();
                        break;
                    case "pump":
                        handlePump();
                        break;
                }
            }
        });

        controllers.add(controller);
    }

    private void handlePumpStart() {
        if (this.level != null && this.level.isClientSide()) {
            EntityCameraShake.clientCameraShake(this.level, this.worldPosition.getCenter(), 6.0f, 0.05f, 5, 5);
            this.level.playLocalSound(
                    this.worldPosition.getX() ,
                    this.worldPosition.getY() + 1.0,
                    this.worldPosition.getZ(),
                    ModSounds.CONDENSER_PUMP.get(),
                    SoundSource.BLOCKS,
                    0.2f,
                    0.7f,
                    false
            );
        }
    }

    private void handlePump() {
        if (this.level != null && this.level.isClientSide()) {
            EntityCameraShake.clientCameraShake(this.level, this.worldPosition.getCenter(), 6.0f, 0.05f, 5, 5);

            FluidStack currentFluid = this.fluidTank.getFluid();


            if (currentFluid.isEmpty() && !this.lastFluidName.isEmpty()) {
                Fluid cachedFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(this.lastFluidName));
                if (cachedFluid != null && cachedFluid != net.minecraft.world.level.material.Fluids.EMPTY) {
                    currentFluid = new FluidStack(cachedFluid, 1000);
                }
            }

            if (!currentFluid.isEmpty()) {
                int fluidColorInt = IClientFluidTypeExtensions.of(currentFluid.getFluid()).getTintColor(currentFluid);

                ChillFallingParticleOptions options =
                        new ChillFallingParticleOptions(ColorHelper.hexToVector3f(fluidColorInt), 0.02f, 40, 0);

                this.level.playLocalSound(
                        this.worldPosition.getX() ,
                        this.worldPosition.getY() + 1.0,
                        this.worldPosition.getZ(),
                        ModSounds.HARVESTER_PUMP.get(),
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f,
                        false
                );

                ParticleHelper.spawnExplosion(
                        this.level,
                        options,
                        this.worldPosition.getCenter().add(0, 0.5, 0),
                        15,
                        0.15,
                        0.1
                );
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(1.0D, 2.0D, 1.0D);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();
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
        tag.putInt("condenser.progress", progress);
        tag.putBoolean("condenser.isCrafting", isCrafting);
        tag.putString("condenser.lastFluidName", lastFluidName);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidTank.readFromNBT(tag.getCompound("FluidTank"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        this.progress = tag.getInt("condenser.progress");
        this.isCrafting = tag.getBoolean("condenser.isCrafting");
        // Load the cached fluid
        this.lastFluidName = tag.getString("condenser.lastFluidName");
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

    public FluidTank getInputTank(){ return this.fluidTank; }
    public float getFluidHeight(){ return 10f; }
    public float getFluidRadius(){ return 0.18f; }
    public float getFluidHeightOffset(){ return 0.1f; }

    @Override
    public String getFluidBoneName() {
        return "pump";
    }
    @Override
    public ItemStack getRenderItemStack() {
        return this.itemHandler.getStackInSlot(0);
    }

    @Override
    public String getItemBoneName() {
        return "pump";
    }
}