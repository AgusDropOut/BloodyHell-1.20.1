package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.HostileUnknownEntityArms;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.particle.ParticleOptions.BlackHoleParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnknownPortalBlockEntity extends BaseGeckoBlockEntity {

    private static final int BLOOD_PER_TICK = 5;
    private static final float PROGRESS_STEP = 0.5f;
    public static final float Y_OFFSET = 4.0f;
    public static final int MAX_SUMMONED_ARMS = 4;

    public float portalProgress = 0.0f;
    private final List<UUID> summonedArmsIds = new ArrayList<>();

    public final FluidTank inputTank = new FluidTank(4000, fluid -> fluid.getFluid() == ModFluids.BLOOD_SOURCE.get()) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public final FluidTank outputTank = new FluidTank(4000, fluid -> fluid.getFluid() == ModFluids.VISCOUS_BLASPHEMY_SOURCE.get()) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() { return 2; }
        @Override
        public @NotNull FluidStack getFluidInTank(int tank) { return tank == 0 ? inputTank.getFluid() : outputTank.getFluid(); }
        @Override
        public int getTankCapacity(int tank) { return tank == 0 ? inputTank.getCapacity() : outputTank.getCapacity(); }
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return tank == 0 ? inputTank.isFluidValid(stack) : outputTank.isFluidValid(stack); }
        @Override
        public int fill(FluidStack resource, FluidAction action) { return inputTank.fill(resource, action); }
        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return outputTank.drain(resource, action); }
        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return outputTank.drain(maxDrain, action); }
    });

    public UnknownPortalBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.UNKNOWN_PORTAL_BE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, UnknownPortalBlockEntity entity) {
        boolean isDirty = false;
        boolean hasEnoughBlood = entity.inputTank.getFluidAmount() >= BLOOD_PER_TICK;

        if (hasEnoughBlood) {
            entity.inputTank.drain(BLOOD_PER_TICK, IFluidHandler.FluidAction.EXECUTE);
            if (entity.portalProgress < 100.0f) {
                entity.portalProgress = Math.min(100.0f, entity.portalProgress + PROGRESS_STEP);
                isDirty = true;
            }
        } else {
            if (entity.portalProgress > 0.0f) {
                entity.portalProgress = Math.max(0.0f, entity.portalProgress - PROGRESS_STEP);
                isDirty = true;
            }
        }

        if (entity.portalProgress >= 100.0f && entity.summonedArmsIds.size() < MAX_SUMMONED_ARMS) {
            int armsToSpawn = MAX_SUMMONED_ARMS - entity.summonedArmsIds.size();
            for (int i = 0; i < armsToSpawn; i++) {
                HostileUnknownEntityArms arms = new HostileUnknownEntityArms(ModEntityTypes.HOSTILE_UNKNOWN_ENTITY_ARMS.get(), level);
                arms.setPos(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D);
                arms.setYRot(level.random.nextFloat() * 360.0F);
                arms.setPortalPos(pos);
                level.addFreshEntity(arms);
                entity.summonedArmsIds.add(arms.getUUID());
            }
            isDirty = true;
        } else if (entity.portalProgress < 100.0f && !entity.summonedArmsIds.isEmpty()) {
            for (UUID uuid : entity.summonedArmsIds) {
                Entity arms = ((ServerLevel) level).getEntity(uuid);
                if (arms instanceof HostileUnknownEntityArms) {
                    arms.discard();
                }
            }
            entity.summonedArmsIds.clear();
            isDirty = true;
        }

        if (isDirty) {
            entity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public boolean hasActiveBlackHole = false;

    public static void clientTick(Level level, BlockPos pos, BlockState state, UnknownPortalBlockEntity entity) {
        if (entity.portalProgress > 0) {
            if (!entity.hasActiveBlackHole) {
                level.addParticle(new BlackHoleParticleOptions(0.1f, 1f, 0.9f, 0.0f, true),
                        pos.getX()+0.5D, pos.getY() + Y_OFFSET, pos.getZ()+0.5D ,
                        0.0D, 0.0D, 0.0D);

                entity.hasActiveBlackHole = true;
            }
        } else {
            entity.hasActiveBlackHole = false;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            float speedMultiplier = 1.0f + ((this.portalProgress / 100.0f) * 2.0f);
            state.getController().setAnimationSpeed(speedMultiplier);
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public String getAssetPathName() {
        return "unknown_portal_block";
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("InputTank", inputTank.writeToNBT(new CompoundTag()));
        tag.put("OutputTank", outputTank.writeToNBT(new CompoundTag()));
        tag.putFloat("PortalProgress", portalProgress);


        ListTag uuidList = new ListTag();
        for (UUID uuid : summonedArmsIds) {
            uuidList.add(NbtUtils.createUUID(uuid));
        }
        tag.put("SummonedArmsIds", uuidList);

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inputTank.readFromNBT(tag.getCompound("InputTank"));
        outputTank.readFromNBT(tag.getCompound("OutputTank"));
        portalProgress = tag.getFloat("PortalProgress");

        summonedArmsIds.clear();
        if (tag.contains("SummonedArmsIds", 9)) {
            ListTag uuidList = tag.getList("SummonedArmsIds", 11);
            for (int i = 0; i < uuidList.size(); i++) {
                summonedArmsIds.add(NbtUtils.loadUUID(uuidList.get(i)));
            }
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    public void destroySummonedArms(Level level) {
        if (!level.isClientSide() && !this.summonedArmsIds.isEmpty()) {
            for (UUID uuid : this.summonedArmsIds) {
                Entity arms = ((ServerLevel) level).getEntity(uuid);
                if (arms instanceof HostileUnknownEntityArms) {
                    arms.discard();
                }
            }
            this.summonedArmsIds.clear();
        }
    }
}