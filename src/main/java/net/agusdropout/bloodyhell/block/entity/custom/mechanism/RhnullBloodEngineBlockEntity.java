package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.particle.ModParticles;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
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
    private static final int TANK_CAPACITY = 10000;


    private final double orbYOffset = 2.5D;

    private final FluidTank bloodTank = createTank();
    private final FluidTank corruptedTank = createTank();
    private final FluidTank viscousTank = createTank();
    private final FluidTank visceralTank = createTank();

    private final LazyOptional<IFluidHandler> lateralFluidHandler = LazyOptional.of(this::createLateralHandler);

    private float heatProgress = 0.0f;
    private float stabilizationLevel = 0.0f;
    private boolean isActive = false;
    private boolean isHeating = false;
    private boolean isStabilized = false;

    private int soundTimer = 0;
    private int activationSequenceTimer = -1;
    private boolean hasPlayedFullyCharged = false;
    private int heartbeatTimer = 0;
    private int ambientSoundTimer = 0;

    private Vector3f currentBaseColor = new Vector3f(0.5f, 0.0f, 0.05f);
    private Vector3f currentGlowColor = new Vector3f(1.0f, 0.1f, 0.1f);

    public RhnullBloodEngineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RHNULL_BLOOD_ENGINE.get(), pos, state);
    }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;

            if (!active) {
                this.isHeating = false;
                this.isStabilized = false;
                this.heatProgress = 0.0f;
                this.stabilizationLevel = 0.0f;
                this.soundTimer = 0;
                this.activationSequenceTimer = -1;
                this.hasPlayedFullyCharged = false;
                this.heartbeatTimer = 0;
                this.ambientSoundTimer = 0;
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

        BlockPos[] offsets = {
                worldPosition.offset(1, 0, 1), worldPosition.offset(1, 0, -1),
                worldPosition.offset(-1, 0, 1), worldPosition.offset(-1, 0, -1)
        };

        for (BlockPos offsetPos : offsets) {
            if (this.level.getBlockEntity(offsetPos) instanceof RhnullBloodEnginePillarBlockEntity pillar) {
                pillar.setEngineActive(active);
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        if (this.isActive && this.activationSequenceTimer >= 0) {
            this.activationSequenceTimer++;

            if (this.activationSequenceTimer == 20) {
                level.playSound(null, pos, ModSounds.HARVESTER_PUMP.get(), SoundSource.BLOCKS, 1.2f, 0.8f);
            } else if (this.activationSequenceTimer == 35) {
                level.playSound(null, pos, ModSounds.HARVESTER_PUMP.get(), SoundSource.BLOCKS, 1.2f, 0.85f);
            } else if (this.activationSequenceTimer == 55) {
                level.playSound(null, pos, ModSounds.HARVESTER_PUMP.get(), SoundSource.BLOCKS, 1.2f, 0.9f);
            } else if (this.activationSequenceTimer == 65) {
                level.playSound(null, pos, ModSounds.HARVESTER_PUMP.get(), SoundSource.BLOCKS, 1.2f, 0.95f);
                this.activationSequenceTimer = -1;
            }
        }

        int totalFluid = bloodTank.getFluidAmount() + corruptedTank.getFluidAmount() + viscousTank.getFluidAmount() + visceralTank.getFluidAmount();
        boolean hasFluid = totalFluid > 0;

        if (this.isActive) {
            boolean startedStabilizing = false;

            if (hasFluid && !isStabilized) {
                isHeating = true;
                heatProgress += 0.002f;

                if (heatProgress >= 1.0f) {
                    heatProgress = 1.0f;

                    if (stabilizationLevel == 0.0f) {
                        startedStabilizing = true;
                    }

                    stabilizationLevel += 0.01f;
                    if (stabilizationLevel >= 1.0f) {
                        stabilizationLevel = 1.0f;
                        isStabilized = true;
                    }
                }
            } else if (!hasFluid) {
                isHeating = false;
                isStabilized = false;
                heatProgress = Math.max(0.0f, heatProgress - 0.005f);
                stabilizationLevel = Math.max(0.0f, stabilizationLevel - 0.01f);
                this.hasPlayedFullyCharged = false;
            }

            if (hasFluid) {
                updateColors(totalFluid);
            }

            if (this.isHeating && this.heatProgress < 1.0f) {
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
            } else {
                this.soundTimer = 0;
            }

            if (startedStabilizing && !this.hasPlayedFullyCharged) {
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

                    serverLevel.sendParticles(ModParticles.BLOOD_DROP_PARTICLE.get(),
                            center.x, center.y - 0.5D, center.z, 25, 0.2D, 0.1D, 0.2D, 0.3D);
                }
            }

            if (this.isStabilized) {
                if (this.ambientSoundTimer <= 0) {
                    level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.4f, 0.7f);
                    this.ambientSoundTimer = 80;
                } else {
                    this.ambientSoundTimer--;
                }

                if (this.heartbeatTimer <= 0) {
                    level.playSound(null, pos, SoundEvents.WARDEN_HEARTBEAT, SoundSource.BLOCKS, 0.7f, 1.2f);

                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.phys.Vec3 center = new net.minecraft.world.phys.Vec3(pos.getX() + 0.5D, pos.getY() + orbYOffset, pos.getZ() + 0.5D);

                        serverLevel.sendParticles(ModParticles.BLOOD_DROP_PARTICLE.get(),
                                center.x, center.y - 0.6D, center.z,
                                4, 0.3D, 0.0D, 0.3D, 0.05D);

                        serverLevel.sendParticles(new net.agusdropout.bloodyhell.particle.ParticleOptions.SmallGlitterParticleOptions(this.currentGlowColor, 0.8f, false, 20, false),
                                center.x, center.y, center.z, 4, 0.6D, 0.6D, 0.6D, 0.02D);
                    }
                    this.heartbeatTimer = 10;
                } else {
                    this.heartbeatTimer--;
                }
            }
        }

        setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    private void updateColors(int totalFluid) {
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
            if (isStabilized) {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("active"));
                state.getController().setAnimationSpeed(1.0);
            } else if (isHeating) {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("charging"));
                state.getController().setAnimationSpeed(Math.max(0.1, heatProgress));
            } else {
                state.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("activation"));
                state.getController().setAnimationSpeed(1.0);
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
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                if (tank == 0 && stack.getFluid() == ModFluids.BLOOD_SOURCE.get()) return true;
                if (tank == 1 && stack.getFluid() == ModFluids.CORRUPTED_BLOOD_SOURCE.get()) return true;
                if (tank == 2 && stack.getFluid() == ModFluids.VISCOUS_BLASPHEMY_SOURCE.get()) return true;
                if (tank == 3 && stack.getFluid() == ModFluids.VISCERAL_BLOOD_SOURCE.get()) return true;
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (resource.getFluid() == ModFluids.BLOOD_SOURCE.get()) return bloodTank.fill(resource, action);
                if (resource.getFluid() == ModFluids.CORRUPTED_BLOOD_SOURCE.get()) return corruptedTank.fill(resource, action);
                if (resource.getFluid() == ModFluids.VISCOUS_BLASPHEMY_SOURCE.get()) return viscousTank.fill(resource, action);
                if (resource.getFluid() == ModFluids.VISCERAL_BLOOD_SOURCE.get()) return visceralTank.fill(resource, action);
                return 0;
            }

            @Override
            public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }

            @Override
            public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && side != null && side.getAxis().isHorizontal()) {
            return lateralFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lateralFluidHandler.invalidate();
    }

    @Override
    public Vector3f getBlobCenter() {
        return new Vector3f(worldPosition.getX() + 0.5f, (float)(worldPosition.getY() + orbYOffset), worldPosition.getZ() + 0.5f);
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
        tag.put("BloodTank", bloodTank.writeToNBT(new CompoundTag()));
        tag.put("CorruptedTank", corruptedTank.writeToNBT(new CompoundTag()));
        tag.put("ViscousTank", viscousTank.writeToNBT(new CompoundTag()));
        tag.put("VisceralTank", visceralTank.writeToNBT(new CompoundTag()));
        tag.putFloat("HeatProgress", heatProgress);
        tag.putFloat("StabilizationLevel", stabilizationLevel);
        tag.putBoolean("IsActive", isActive);
        tag.putBoolean("IsHeating", isHeating);
        tag.putBoolean("IsStabilized", isStabilized);
        tag.putInt("SoundTimer", soundTimer);
        tag.putInt("ActivationSequenceTimer", activationSequenceTimer);
        tag.putBoolean("HasPlayedFullyCharged", hasPlayedFullyCharged);
        tag.putInt("HeartbeatTimer", heartbeatTimer);
        tag.putInt("AmbientSoundTimer", ambientSoundTimer);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        bloodTank.readFromNBT(tag.getCompound("BloodTank"));
        corruptedTank.readFromNBT(tag.getCompound("CorruptedTank"));
        viscousTank.readFromNBT(tag.getCompound("ViscousTank"));
        visceralTank.readFromNBT(tag.getCompound("VisceralTank"));
        heatProgress = tag.getFloat("HeatProgress");
        stabilizationLevel = tag.getFloat("StabilizationLevel");
        isActive = tag.getBoolean("IsActive");
        isHeating = tag.getBoolean("IsHeating");
        isStabilized = tag.getBoolean("IsStabilized");
        soundTimer = tag.getInt("SoundTimer");
        activationSequenceTimer = tag.getInt("ActivationSequenceTimer");
        hasPlayedFullyCharged = tag.getBoolean("HasPlayedFullyCharged");
        heartbeatTimer = tag.getInt("HeartbeatTimer");
        ambientSoundTimer = tag.getInt("AmbientSoundTimer");
        updateColors(bloodTank.getFluidAmount() + corruptedTank.getFluidAmount() + viscousTank.getFluidAmount() + visceralTank.getFluidAmount());
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