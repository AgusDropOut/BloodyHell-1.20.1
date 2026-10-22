package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.agusdropout.bloodyhell.block.entity.base.IFluidBlockHolder;
import net.agusdropout.bloodyhell.block.entity.base.IGeoFluidBlock;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulType;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
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
import software.bernie.geckolib.core.animation.RawAnimation;

public class SanguiniteBloodHarvesterBlockEntity extends BaseGeckoBlockEntity implements IGeoFluidBlock, IFluidBlockHolder {

    @Override
    public FluidTank getInputTank() { return tank; }
    @Override
    public float getFluidHeight() { return 11.0f; }
    @Override
    public float getFluidRadius() { return 0.18f; }
    @Override
    public float getFluidHeightOffset() { return 0.15f; }
    @Override
    public String getFluidBoneName() { return "blood"; }

    public enum AnimationState { IDLE, ABSORBING, IDLE_ACTIVE }

    private AnimationState currentState = AnimationState.IDLE;
    private int idleActiveTimer = 0;
    private static final int IDLE_ACTIVE_DURATION = 60;

    private final FluidTank tank = new FluidTank(10000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> tank);

    public SanguiniteBloodHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_BLOOD_HARVESTER_BE.get(), pos, state);
    }

    @Override
    public String getAssetPathName() { return "sanguinite_blood_harvester"; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<SanguiniteBloodHarvesterBlockEntity> controller = new AnimationController<>(this, "controller", 5, state -> {
            switch (this.currentState) {
                case ABSORBING -> { return state.setAndContinue(RawAnimation.begin().thenLoop("Absorbing")); }
                case IDLE_ACTIVE -> { return state.setAndContinue(RawAnimation.begin().thenLoop("idleActive")); }
                default -> { return state.setAndContinue(RawAnimation.begin().thenLoop("idle")); }
            }
        });

        controller.setCustomInstructionKeyframeHandler(event -> {
            String instruction = event.getKeyframeData().getInstructions();
            Direction dir = null;

            if (instruction.equals("strikeNorth;")) dir = Direction.NORTH;
            else if (instruction.equals("strikeSouth;")) dir = Direction.SOUTH;
            else if (instruction.equals("strikeEast;")) dir = Direction.EAST;
            else if (instruction.equals("strikeWest;")) dir = Direction.WEST;

            if (dir != null) {
                spawnStrikeEffects(dir);
            }
        });

        controllers.add(controller);
    }

    private void spawnStrikeEffects(Direction dir) {
        if (this.level == null) return;

        double posX = this.worldPosition.getX() + 0.5 + (dir.getStepX() * 0.5);
        double posY = this.worldPosition.getY() + 1.0;
        double posZ = this.worldPosition.getZ() + 0.5 + (dir.getStepZ() * 0.5);
        Vec3 effectPos = new Vec3(posX, posY, posZ);

        if (this.level.isClientSide) {
            for (int i = 0; i < 8; i++) {
                double spreadX = (this.level.random.nextDouble() - 0.5) * 0.1;
                double spreadZ = (this.level.random.nextDouble() - 0.5) * 0.1;

                FluidStack currentFluid = this.tank.getFluid();
                if (!currentFluid.isEmpty()) {
                    int fluidColorInt = IClientFluidTypeExtensions.of(currentFluid.getFluid()).getTintColor(currentFluid);

                    this.level.addParticle(new ChillFallingParticleOptions(ColorHelper.hexToVector3f(fluidColorInt), 0.03f, 30, 0),
                            posX, posY + 0.2, posZ,
                            (dir.getStepX() * 0.15) + spreadX,
                            0.9 + (this.level.random.nextDouble() * 0.1),
                            (dir.getStepZ() * 0.15) + spreadZ
                    );
                }
            }
            this.level.playLocalSound(
                    this.worldPosition.getX() + 0.5,
                    this.worldPosition.getY() + 1.0,
                    this.worldPosition.getZ() + 0.5,
                    ModSounds.HARVESTER_PUMP.get(),
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f,
                    false
            );

            EntityCameraShake.clientCameraShake(this.level, effectPos, 6.0f, 0.05f, 5, 5);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SanguiniteBloodHarvesterBlockEntity pEntity) {
        if (level.isClientSide) return;
        if (pEntity.currentState == AnimationState.IDLE_ACTIVE) {
            if (pEntity.idleActiveTimer > 0) pEntity.idleActiveTimer--;
            else pEntity.updateState(AnimationState.IDLE);
        }
        if (!pEntity.tank.isEmpty()) pEntity.distributeFluid(level, pos);
    }

    public void startAbsorbing() {
        if (level == null || level.isClientSide) return;
        updateState(AnimationState.ABSORBING);
    }

    public void receiveSoul(BloodSoulType type, int amount) {
        if (level == null || level.isClientSide) return;
        FluidStack fluidToAdd = switch (type) {
            case CORRUPTED -> new FluidStack(ModFluids.CORRUPTED_BLOOD_SOURCE.get(), amount);
            case INFECTED -> new FluidStack(ModFluids.VISCERAL_BLOOD_SOURCE.get(), amount);
            default -> {
                ResourceLocation fluidLoc = new ResourceLocation(net.agusdropout.bloodyhell.config.ModCommonConfig.HARVESTER_DEFAULT_BLOOD_FLUID.get());
                net.minecraft.world.level.material.Fluid configuredFluid = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getValue(fluidLoc);

                if (configuredFluid == null || configuredFluid == net.minecraft.world.level.material.Fluids.EMPTY) {
                    yield new FluidStack(ModFluids.BLOOD_SOURCE.get(), amount);
                } else {
                    yield new FluidStack(configuredFluid, amount);
                }
            }
        };
        tank.fill(fluidToAdd, IFluidHandler.FluidAction.EXECUTE);
        this.idleActiveTimer = IDLE_ACTIVE_DURATION;
        updateState(AnimationState.IDLE_ACTIVE);
    }

    private void updateState(AnimationState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void distributeFluid(Level level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (tank.isEmpty()) return;
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())
                        .ifPresent(handler -> {
                            FluidStack drainable = tank.drain(500, IFluidHandler.FluidAction.SIMULATE);
                            int filled = handler.fill(drainable, IFluidHandler.FluidAction.EXECUTE);
                            if (filled > 0) tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        });
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        tank.writeToNBT(nbt);
        nbt.putInt("AnimState", currentState.ordinal());
        nbt.putInt("IdleActiveTimer", idleActiveTimer);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        tank.readFromNBT(nbt);
        currentState = AnimationState.values()[nbt.getInt("AnimState")];
        idleActiveTimer = nbt.getInt("IdleActiveTimer");
    }

    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }

    @Nullable
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