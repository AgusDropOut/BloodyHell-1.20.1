package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulType;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class SanguiniteBloodHarvesterBlockEntity extends BaseGeckoBlockEntity {

    // Animation State
    private int harvestTimer = 0; // >0 means currently harvesting

    private final FluidTank tank = new FluidTank(10000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            // Important: Sync fluid changes to client if you want to render fluid levels later
            if(level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> tank);

    public SanguiniteBloodHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_BLOOD_HARVESTER_BE.get(), pos, state);
    }

    @Override
    public String getAssetPathName() {
        return "sanguinite_blood_harvester";
    }

    // --- ANIMATION CONTROLLER ---

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // We set transitionLength to 10 ticks (0.5 seconds) for smooth blending
        controllers.add(new AnimationController<>(this, "controller", 10, state -> {

            // IF timer > 0, we are active -> Play Harvest
            if (this.harvestTimer > 0) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("harvesting"));
            }

            // ELSE -> Play Idle
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }


    private void tryHarvest(Level level, BlockPos pos) {
        AABB area = new AABB(pos).move(0, 1, 0);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
        boolean foundVictim = false;

        for (LivingEntity entity : entities) {
            if (entity.isAlive()) {
                // Apply Damage
                boolean hurt = entity.hurt(level.damageSources().magic(), 2.0f);
                if (hurt) {
                    // Fill Tank
                    tank.fill(new FluidStack(ModFluids.BLOOD_SOURCE.get(), 100), IFluidHandler.FluidAction.EXECUTE);
                    foundVictim = true;
                }
            }
        }

        // State Change Logic
        if (foundVictim) {
            // Reset timer to 40 ticks (2 seconds) of animation per hit
            // We check against 20 to avoid sending unnecessary packets if it's already running
            if (this.harvestTimer < 20) {
                this.harvestTimer = 40;
                // SYNC TO CLIENT: This is crucial for the animation to start!
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                setChanged();
            }
        }
    }


    // --- SYNCING ---
    // We must sync 'harvestTimer' so the client knows when to switch animations

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt = tank.writeToNBT(nbt);
        nbt.putInt("HarvestTimer", harvestTimer);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        tank.readFromNBT(nbt);
        harvestTimer = nbt.getInt("HarvestTimer");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }



    public static void tick(Level level, BlockPos pos, BlockState state, SanguiniteBloodHarvesterBlockEntity pEntity) {
        if (pEntity.harvestTimer > 0) pEntity.harvestTimer--;

        if (level.isClientSide) return;

        // Push fluid to horizontal pipes
        if (!pEntity.tank.isEmpty()) {
            pEntity.distributeFluid(level, pos);
        }
    }

    // Called by Soul Entity when it arrives
    public void receiveSoul(BloodSoulType type, int amount) {
        if (level == null || level.isClientSide) return;

        FluidStack fluidToAdd;
        if (type == BloodSoulType.CORRUPTED) {
            fluidToAdd = new FluidStack(ModFluids.CORRUPTED_BLOOD_SOURCE.get(), amount);
        } else {
            fluidToAdd = new FluidStack(ModFluids.BLOOD_SOURCE.get(), amount);
        }

        tank.fill(fluidToAdd, IFluidHandler.FluidAction.EXECUTE);

        // Restart animation
        this.harvestTimer = 40;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        setChanged();
    }

    private void distributeFluid(Level level, BlockPos pos) {
        // Iterate only Horizontal sides (North, South, East, West)
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (tank.isEmpty()) return;

            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor != null) {
                // You can add a check here "instanceof PipeBlockEntity" if you want strict control,
                // but usually checking the capability is cleaner compatibility.
                neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())
                        .ifPresent(handler -> {
                            // Logic: If neighbor can take it, give it.
                            FluidStack drainable = tank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                            int filled = handler.fill(drainable, IFluidHandler.FluidAction.EXECUTE);
                            if (filled > 0) {
                                tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                            }
                        });
            }
        }
    }
}