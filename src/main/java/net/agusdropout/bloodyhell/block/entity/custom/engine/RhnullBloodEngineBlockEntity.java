package net.agusdropout.bloodyhell.block.entity.custom.engine;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ParticleOptions.BloodDropParticleOption;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.visuals.types.IBloodBlobEmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashSet;
import java.util.Set;

public class RhnullBloodEngineBlockEntity extends BaseGeckoBlockEntity implements IBloodBlobEmitter {

    public static final Set<IBloodBlobEmitter> ACTIVE_ENGINES = new HashSet<>();

    public static final BlockPos[] PILLAR_OFFSETS = {
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1)
    };

    private static final int TANK_CAPACITY = 10000;
    private final double orbYOffset = 2.5D;

    private final FluidTank bloodTank = createTank();
    private final FluidTank corruptedTank = createTank();
    private final FluidTank viscousTank = createTank();
    private final FluidTank visceralTank = createTank();

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            updateMode();
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IItemHandler> optionalItemHandler = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IFluidHandler> lateralFluidHandler = LazyOptional.of(this::createLateralHandler);

    private float heatProgress = 0.0f;
    private float stabilizationLevel = 0.0f;
    private boolean isActive = false;
    private boolean isHeating = false;
    private boolean isStabilized = false;
    private boolean isCraftingFinished = false;

    private int soundTimer = 0;
    private int activationSequenceTimer = -1;
    private boolean hasPlayedFullyCharged = false;
    private int heartbeatTimer = 0;
    private int ambientSoundTimer = 0;


    private int clientRitualTick = 0;
    private int explosionTimer = -1;
    private float heartbeatPulse = 0.0f;

    private Vector3f currentBaseColor = new Vector3f(0.5f, 0.0f, 0.05f);
    private Vector3f currentGlowColor = new Vector3f(1.0f, 0.1f, 0.1f);

    private IEngineMode currentMode = null;

    public RhnullBloodEngineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RHNULL_BLOOD_ENGINE.get(), pos, state);
    }

    private void updateMode() {
        ItemStack stack = this.itemHandler.getStackInSlot(0);

        if (this.currentMode != null) {
            this.currentMode.resetProcess();
        }
        this.currentMode = null;

        if (stack.isEmpty()) return;

        if (stack.getItem() == ModItems.BOUND_BLOOD_FLASK.get()) {
            CompoundTag nbt = stack.getTag();
            if (nbt != null) {
                if (nbt.contains("FallenAllyData")) {
                    this.currentMode = new ResurrectionEngineMode();
                } else if (nbt.contains("BloodOwnerName")) {
                    this.currentMode = new SoulTetherEngineMode();
                }
            }
        }
    }

    public void setCraftingFinished(boolean finished) {
        this.isCraftingFinished = finished;
        if (this.level != null && !this.level.isClientSide) {
            this.setChanged();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack stackInSlot = itemHandler.getStackInSlot(0);

        if (heldItem.isEmpty()) {
            if (!stackInSlot.isEmpty()) {
                player.setItemInHand(hand, itemHandler.extractItem(0, 64, false));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            if (stackInSlot.isEmpty()) {
                ItemStack remainder = itemHandler.insertItem(0, heldItem.copy(), false);
                player.setItemInHand(hand, remainder);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    public ItemStack getRenderStack() { return itemHandler.getStackInSlot(0); }

    public void clearInventory() {
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public FluidTank getBloodTank() { return bloodTank; }
    public FluidTank getCorruptedTank() { return corruptedTank; }
    public FluidTank getViscousTank() { return viscousTank; }
    public FluidTank getVisceralTank() { return visceralTank; }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;

            if (!active) {
                this.isHeating = false;
                this.isStabilized = false;
                this.isCraftingFinished = false;
                this.heatProgress = 0.0f;
                this.stabilizationLevel = 0.0f;
                this.soundTimer = 0;
                this.activationSequenceTimer = -1;
                this.hasPlayedFullyCharged = false;
                this.heartbeatTimer = 0;
                this.ambientSoundTimer = 0;

                this.clientRitualTick = 0;
                this.explosionTimer = -1;
                this.heartbeatPulse = 0.0f;

                if (currentMode != null) currentMode.resetProcess();
            } else {
                this.activationSequenceTimer = 0;
                this.hasPlayedFullyCharged = false;
            }

            updatePillars(active);
            this.setChanged();
            if (this.level != null && !this.level.isClientSide) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    private void updatePillars(boolean active) {
        if (this.level == null || this.level.isClientSide) return;
        for (BlockPos offset : PILLAR_OFFSETS) {
            BlockPos targetPos = this.worldPosition.offset(offset);
            if (this.level.getBlockEntity(targetPos) instanceof RhnullBloodEnginePillarBlockEntity pillar) {
                pillar.setEngineActive(active);
            }
        }
    }

    @Override
    public boolean isActive() { return this.isActive; }

    public void tick(Level level, BlockPos pos, BlockState state) {
        int totalFluid = bloodTank.getFluidAmount() + corruptedTank.getFluidAmount() + viscousTank.getFluidAmount() + visceralTank.getFluidAmount();
        boolean hasFluid = totalFluid > 0;

        if (hasFluid || currentMode != null) {
            updateColors(totalFluid);
        }


        if (this.isActive) {
            if (this.activationSequenceTimer >= 0) {
                this.activationSequenceTimer++;
                if (this.activationSequenceTimer >= 65) {
                    this.activationSequenceTimer = -1;
                    this.isHeating = true;
                }
            } else if (this.isHeating && !this.isStabilized) {
                if (hasFluid) {
                    heatProgress += 0.002f;
                    if (heatProgress >= 1.0f) {
                        heatProgress = 1.0f;
                        stabilizationLevel += 0.01f;
                        if (stabilizationLevel >= 1.0f) {
                            stabilizationLevel = 1.0f;
                            this.isStabilized = true;
                        }
                    }
                } else {
                    heatProgress = Math.max(0.0f, heatProgress - 0.005f);
                    stabilizationLevel = Math.max(0.0f, stabilizationLevel - 0.01f);
                }
            }
        } else {
            this.isHeating = false;
            this.isStabilized = false;
            this.isCraftingFinished = false;
            this.heatProgress = Math.max(0.0f, heatProgress - 0.05f);
            this.stabilizationLevel = Math.max(0.0f, stabilizationLevel - 0.1f);
            this.explosionTimer = -1;
            this.clientRitualTick = 0;
            this.heartbeatPulse = 0.0f;
        }


        if (level.isClientSide) {
            if (this.heartbeatPulse > 0.0f) {
                this.heartbeatPulse = Math.max(0.0f, this.heartbeatPulse - 0.05f);
            }

            if (this.isActive) {
                if (this.isCraftingFinished) {
                    if (this.explosionTimer == -1) this.explosionTimer = 20;
                    if (this.explosionTimer > 0) {
                        this.explosionTimer--;
                        this.heartbeatPulse = 1.0f;
                    }
                } else if (this.isStabilized && this.currentMode != null && this.currentMode.canProcess(this)) {
                    int oldTick = this.clientRitualTick;
                    this.clientRitualTick++;

                    int heartbeatFrequency = Math.max(5, 20 - (this.clientRitualTick / 10));
                    if (this.clientRitualTick % heartbeatFrequency == 0 && oldTick % heartbeatFrequency != 0) {
                        this.heartbeatPulse = 1.0f;
                    }
                } else {
                    this.clientRitualTick = 0;
                }
            }
            return;
        }


        if (this.isActive) {

            if (this.isCraftingFinished) {
                if (this.explosionTimer == -1) {
                    this.explosionTimer = 20;
                    level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.4f, 1.5f);
                }
                if (this.explosionTimer > 0) {
                    this.explosionTimer--;
                } else {
                    this.setActive(false);
                    this.explosionTimer = -1;
                }
                return;
            }

            if (this.activationSequenceTimer == 20 || this.activationSequenceTimer == 35 || this.activationSequenceTimer == 55) {
                level.playSound(null, pos, ModSounds.HARVESTER_PUMP.get(), SoundSource.BLOCKS, 1.2f, 0.8f);
                setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }

            if (this.isHeating && !this.isStabilized) {
                if (this.soundTimer <= 0) {
                    level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.15f, 1.2f + (this.heatProgress * 0.8f));
                    this.soundTimer = 20;
                } else {
                    this.soundTimer--;
                }

                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel && level.getGameTime() % 3 == 0) {
                    net.minecraft.world.phys.Vec3 center = new net.minecraft.world.phys.Vec3(pos.getX() + 0.5D, pos.getY() + orbYOffset, pos.getZ() + 0.5D);
                    serverLevel.sendParticles(new net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions(this.currentBaseColor, 0.4f),
                            center.x, center.y, center.z, 1, 0.3D, 0.3D, 0.3D, 0.01D);
                }
            }

            if (this.isStabilized) {
                if (!this.hasPlayedFullyCharged) {
                    level.playSound(null, pos, ModSounds.RHNULL_BLOOD_ENGINE_FULLY_CHARGED.get(), SoundSource.BLOCKS, 1.0f, 0.9f);
                    this.hasPlayedFullyCharged = true;

                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.phys.Vec3 center = new net.minecraft.world.phys.Vec3(pos.getX() + 0.5D, pos.getY() + orbYOffset, pos.getZ() + 0.5D);
                        net.agusdropout.bloodyhell.entity.effects.EntityCameraShake.cameraShake(serverLevel, center, 8.0f, 0.6f, 15, 10);
                        serverLevel.sendParticles(new net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions(this.currentGlowColor, 1.2f, false, 40, true),
                                center.x, center.y, center.z, 30, 0.4D, 0.4D, 0.4D, 0.1D);
                        serverLevel.sendParticles(new net.agusdropout.bloodyhell.particle.ParticleOptions.GlitterParticleOptions(this.currentBaseColor, 1.0f, false, 60, true),
                                center.x, center.y, center.z, 20, 0.5D, 0.5D, 0.5D, 0.05D);
                        serverLevel.sendParticles(new net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions(this.currentGlowColor, 0.8f),
                                center.x, center.y, center.z, 10, 0.2D, 0.2D, 0.2D, 0.02D);
                    }
                }

                if (this.ambientSoundTimer <= 0) {
                    level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.4f, 0.7f);
                    this.ambientSoundTimer = 80;
                } else {
                    this.ambientSoundTimer--;
                }

                if (currentMode == null || !currentMode.canProcess(this)) {
                    if (this.heartbeatTimer <= 0) {
                        level.playSound(null, pos, SoundEvents.WARDEN_HEARTBEAT, SoundSource.BLOCKS, 0.7f, 1.2f);
                        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            net.minecraft.world.phys.Vec3 center = new net.minecraft.world.phys.Vec3(pos.getX() + 0.5D, pos.getY() + orbYOffset, pos.getZ() + 0.5D);
                            serverLevel.sendParticles(new net.agusdropout.bloodyhell.particle.ParticleOptions.SmallGlitterParticleOptions(this.currentGlowColor, 0.8f, false, 20, false),
                                    center.x, center.y, center.z, 4, 0.6D, 0.6D, 0.6D, 0.02D);
                        }
                        this.heartbeatTimer = 10;
                    } else {
                        this.heartbeatTimer--;
                    }
                }

                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    if (level.random.nextFloat() < 0.25f) {
                        net.minecraft.world.phys.Vec3 center = new net.minecraft.world.phys.Vec3(pos.getX() + 0.5D, pos.getY() + orbYOffset, pos.getZ() + 0.5D);
                        int amount = level.random.nextInt(3) + 1;
                        for (int i = 0; i < amount; i++) {
                            double offsetX = (level.random.nextDouble() - 0.5) * 0.2;
                            double offsetZ = (level.random.nextDouble() - 0.5) * 0.2;
                            double speedX = (level.random.nextDouble() - 0.5) * 0.2;
                            double speedY = level.random.nextDouble() * 0.15 + 0.05;
                            double speedZ = (level.random.nextDouble() - 0.5) * 0.2;
                            serverLevel.sendParticles(new BloodDropParticleOption(this.currentBaseColor),
                                    center.x + offsetX, center.y - 0.5D, center.z + offsetZ,
                                    0, speedX, speedY, speedZ, 1.0D);
                        }
                    }
                }

                if (currentMode != null) {
                    if (currentMode.canProcess(this)) {
                        currentMode.tickProcess(level, pos, state, this);

                        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel && level.getGameTime() % 3 == 0) {
                            net.minecraft.world.phys.Vec3 center = new net.minecraft.world.phys.Vec3(pos.getX() + 0.5D, pos.getY() + orbYOffset, pos.getZ() + 0.5D);
                            serverLevel.sendParticles(new net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions(this.currentGlowColor, 1.2f, false, 40, true),
                                    center.x, center.y, center.z, 5, 0.4D, 0.4D, 0.4D, 0.1D);
                        }
                    } else {
                        currentMode.resetProcess();
                    }
                }
            }
        }
    }


    @Override
    public float getExplosionProgress() {
        return explosionTimer < 0 ? 0.0f : 1.0f - (explosionTimer / 20.0f);
    }

    @Override
    public float getSpasmIntensity() {
        return Math.min(1.0f, (float)this.clientRitualTick / 200.0f);
    }

    @Override
    public float getHeartbeatPulse() {
        return this.heartbeatPulse;
    }

    private void updateColors(int totalFluid) {
        if (this.currentMode != null) {
            Vector3f customBase = this.currentMode.getCustomBaseColor();
            Vector3f customGlow = this.currentMode.getCustomGlowColor();

            if (customBase != null && customGlow != null) {
                this.currentBaseColor = customBase;
                this.currentGlowColor = customGlow;
                return;
            }
        }

        if (totalFluid <= 0) return;

        Vector3f cBlood = new Vector3f(0.5f, 0.0f, 0.05f);
        Vector3f cCorrupted = new Vector3f(0.2f, 0.0f, 0.3f);
        Vector3f cViscous = new Vector3f(0.1f, 0.1f, 0.1f);
        Vector3f cVisceral = new Vector3f(0.3f, 0.4f, 0.1f);

        float wBlood = (float) bloodTank.getFluidAmount() / totalFluid;
        float wCorrupted = (float) corruptedTank.getFluidAmount() / totalFluid;
        float wViscous = (float) viscousTank.getFluidAmount() / totalFluid;
        float wVisceral = (float) visceralTank.getFluidAmount() / totalFluid;

        this.currentBaseColor = new Vector3f(
                cBlood.x() * wBlood + cCorrupted.x() * wCorrupted + cViscous.x() * wViscous + cVisceral.x() * wVisceral,
                cBlood.y() * wBlood + cCorrupted.y() * wCorrupted + cViscous.y() * wViscous + cVisceral.y() * wVisceral,
                cBlood.z() * wBlood + cCorrupted.z() * wCorrupted + cViscous.z() * wViscous + cVisceral.z() * wVisceral
        );

        this.currentGlowColor = new Vector3f(
                Math.min(1.0f, currentBaseColor.x() * 2.0f),
                Math.min(1.0f, currentBaseColor.y() * 2.0f),
                Math.min(1.0f, currentBaseColor.z() * 2.0f)
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    @Override
    public String getAssetPathName() {
        return "rhnull_blood_engine_block";
    }

    private PlayState predicate(AnimationState<RhnullBloodEngineBlockEntity> state) {
        if (!this.isActive) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            state.getController().setAnimationSpeed(1.0);
        } else {
            if (this.activationSequenceTimer >= 0) {
                state.getController().setAnimation(RawAnimation.begin().thenPlay("activation"));
                state.getController().setAnimationSpeed(1.0);
            } else if (this.isStabilized) {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("active"));
                state.getController().setAnimationSpeed(1.0);
            } else {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("charging"));
                state.getController().setAnimationSpeed(Math.max(0.1, heatProgress));
            }
        }
        return PlayState.CONTINUE;
    }

    private FluidTank createTank() {
        return new FluidTank(TANK_CAPACITY) {
            @Override
            protected void onContentsChanged() { setChanged(); }
        };
    }

    private IFluidHandler createLateralHandler() {
        return new IFluidHandler() {
            @Override
            public int getTanks() { return 4; }

            @Override
            public @NotNull FluidStack getFluidInTank(int tank) {
                return switch (tank) {
                    case 0 -> bloodTank.getFluid();
                    case 1 -> corruptedTank.getFluid();
                    case 2 -> viscousTank.getFluid();
                    case 3 -> visceralTank.getFluid();
                    default -> FluidStack.EMPTY;
                };
            }

            @Override
            public int getTankCapacity(int tank) { return TANK_CAPACITY; }

            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return true; }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) return 0;
                int filled = 0;
                if (resource.getFluid() == ModFluids.BLOOD_SOURCE.get()) filled = bloodTank.fill(resource, action);
                else if (resource.getFluid() == ModFluids.CORRUPTED_BLOOD_SOURCE.get()) filled = corruptedTank.fill(resource, action);
                else if (resource.getFluid() == ModFluids.VISCOUS_BLASPHEMY_SOURCE.get()) filled = viscousTank.fill(resource, action);
                else if (resource.getFluid() == ModFluids.VISCERAL_BLOOD_SOURCE.get()) filled = visceralTank.fill(resource, action);

                if (filled > 0 && action.execute()) {
                    setChanged();
                    if (level != null && !level.isClientSide) {
                        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    }
                }
                return filled;
            }

            @Override
            public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
            @Override
            public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return optionalItemHandler.cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER && side != null && side.getAxis().isHorizontal()) return lateralFluidHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        optionalItemHandler.invalidate();
        lateralFluidHandler.invalidate();
    }

    @Override
    public Vector3f getBlobCenter() {
        return new Vector3f(
                this.worldPosition.getX() + 0.5f,
                (float)(this.worldPosition.getY() + orbYOffset),
                this.worldPosition.getZ() + 0.5f
        );
    }
    @Override
    public Vector3f getBloodBaseColor() { return currentBaseColor; }
    @Override
    public Vector3f getBloodGlowColor() { return currentGlowColor; }
    @Override
    public float getChargeLevel() { return heatProgress; }
    @Override
    public float getStabilizationLevel() { return stabilizationLevel; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putBoolean("IsActive", isActive);
        tag.putBoolean("IsStabilized", isStabilized);
        tag.putBoolean("IsCraftingFinished", isCraftingFinished);
        tag.putFloat("HeatProgress", heatProgress);
        tag.putFloat("StabilizationLevel", stabilizationLevel);
        tag.putInt("SoundTimer", soundTimer);
        tag.putInt("ActivationSequenceTimer", activationSequenceTimer);
        tag.putBoolean("HasPlayedFullyCharged", hasPlayedFullyCharged);

        tag.put("BloodTank", bloodTank.writeToNBT(new CompoundTag()));
        tag.put("CorruptedTank", corruptedTank.writeToNBT(new CompoundTag()));
        tag.put("ViscousTank", viscousTank.writeToNBT(new CompoundTag()));
        tag.put("VisceralTank", visceralTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        isActive = tag.getBoolean("IsActive");
        isStabilized = tag.getBoolean("IsStabilized");
        isCraftingFinished = tag.getBoolean("IsCraftingFinished");
        heatProgress = tag.getFloat("HeatProgress");
        stabilizationLevel = tag.getFloat("StabilizationLevel");
        soundTimer = tag.getInt("SoundTimer");
        activationSequenceTimer = tag.getInt("ActivationSequenceTimer");
        hasPlayedFullyCharged = tag.getBoolean("HasPlayedFullyCharged");

        bloodTank.readFromNBT(tag.getCompound("BloodTank"));
        corruptedTank.readFromNBT(tag.getCompound("CorruptedTank"));
        viscousTank.readFromNBT(tag.getCompound("ViscousTank"));
        visceralTank.readFromNBT(tag.getCompound("VisceralTank"));
        updateMode();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && level.isClientSide) ACTIVE_ENGINES.add(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null && level.isClientSide) ACTIVE_ENGINES.remove(this);
    }
}