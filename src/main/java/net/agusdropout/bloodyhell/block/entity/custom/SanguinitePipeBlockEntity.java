package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguinitePipeBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.CorruptedBloodySoulEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SanguinitePipeBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 0 = Healthy. >0 = Hemorrhaging. 60 = Explode.
    private int hemorrhageTimer = 0;
    private static final int MAX_HEMORRHAGE_TIME = 60; // 3 seconds to explosion

    private final FluidTank fluidTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if(level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    public SanguinitePipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_PIPE_BE.get(), pos, state);
    }

    public FluidStack getFluidStack() {
        return fluidTank.getFluid();
    }

    // --- TICK LOGIC ---
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        FluidStack myFluid = this.getFluidStack();

        // 1. HEMORRHAGE LOGIC (Corruption Check)
        if (!myFluid.isEmpty() && isCorrupted(myFluid)) {
            hemorrhageTimer++;

            // Sync animation state to client every 10 ticks so "shaking" starts
            if (hemorrhageTimer % 10 == 0) {
                level.sendBlockUpdated(pos, state, state, 3);
            }

            // Collapse!
            if (hemorrhageTimer >= MAX_HEMORRHAGE_TIME) {
                triggerCollapse(level, pos);
                return; // Stop processing, block is gone
            }
        } else {
            // Reset timer if fluid is removed or cleaned
            if (hemorrhageTimer > 0) {
                hemorrhageTimer = 0;
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }

        if (myFluid.isEmpty()) return;

        // 2. DISTRIBUTION LOGIC
        for (Direction dir : Direction.values()) {
            // Check Visual Connection
            BooleanProperty property = getPropertyByDirection(dir);
            if (!state.getValue(property)) continue;

            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);

            if (neighborBE != null) {
                neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {

                    // CASE A: Neighbor is another Pipe (Balance Pressure)
                    if (neighborBE instanceof SanguinitePipeBlockEntity otherPipe) {
                        FluidStack neighborFluid = otherPipe.getFluidStack();
                        boolean pressureDiff = myFluid.getAmount() > neighborFluid.getAmount();

                        if (pressureDiff) {
                            if (neighborFluid.isEmpty() || neighborFluid.isFluidEqual(myFluid)) {
                                int diff = myFluid.getAmount() - neighborFluid.getAmount();
                                int toTransfer = Math.min(diff / 2, 200);
                                moveFluid(this.fluidTank, handler, toTransfer);
                            }
                        }
                    }
                    // CASE B: Neighbor is a Machine/Tank
                    else {
                        // FIX: Do NOT push back into the Harvester!
                        if (neighborBE instanceof SanguiniteBloodHarvesterBlockEntity) {
                            return;
                        }
                        // Dump into Tanks
                        moveFluid(this.fluidTank, handler, 1000);
                    }
                });
            }
        }
    }

    private boolean isCorrupted(FluidStack stack) {
        return stack.getFluid() == ModFluids.CORRUPTED_BLOOD_SOURCE.get()
                || stack.getFluid() == ModFluids.CORRUPTED_BLOOD_FLOWING.get();
    }

    private void triggerCollapse(Level level, BlockPos pos) {
        // 1. Play "Aggressive" Glass Break Sound
        // Volume 2.0F makes it audible from distance.
        // Pitch 0.7F makes it sound deeper, like heavy reinforced glass shattering.
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GLASS_BREAK,
                net.minecraft.sounds.SoundSource.BLOCKS, 2.0F, 0.7F);

        // Optional: Add a "splashing" sound for extra grossness
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.HONEY_BLOCK_BREAK,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.5F);

        // 2. Spawn the Evil Soul Entity
        CorruptedBloodySoulEntity soul = new CorruptedBloodySoulEntity(ModEntityTypes.CORRUPTED_BLOODY_SOUL_ENTITY.get(), level);
        soul.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        level.addFreshEntity(soul);

        // 3. Replace Pipe with Corrupted Blood Source
        // Instead of removing the block, we overwrite it with the liquid block.
        // This simulates the pipe bursting and the contents spilling out instantly.
        level.setBlockAndUpdate(pos, ModBlocks.CORRUPTED_BLOOD_BLOCK.get().defaultBlockState());
    }

    private void moveFluid(IFluidHandler source, IFluidHandler dest, int maxTransfer) {
        FluidStack simulatedDrain = source.drain(maxTransfer, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.getAmount() > 0) {
            int filledAmount = dest.fill(simulatedDrain, IFluidHandler.FluidAction.EXECUTE);
            if (filledAmount > 0) {
                source.drain(filledAmount, IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    // --- GECKOLIB ANIMATION ---

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // Play "shaking" if we are dying
            if (this.hemorrhageTimer > 0) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("shaking"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    // --- BOILERPLATE ---

    private BooleanProperty getPropertyByDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> SanguinitePipeBlock.NORTH;
            case SOUTH -> SanguinitePipeBlock.SOUTH;
            case EAST -> SanguinitePipeBlock.EAST;
            case WEST -> SanguinitePipeBlock.WEST;
            case UP -> SanguinitePipeBlock.UP;
            case DOWN -> SanguinitePipeBlock.DOWN;
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() { super.onLoad(); lazyFluidHandler = LazyOptional.of(() -> fluidTank); }

    @Override
    public void invalidateCaps() { super.invalidateCaps(); lazyFluidHandler.invalidate(); }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt = fluidTank.writeToNBT(nbt);
        nbt.putInt("Hemorrhage", hemorrhageTimer);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        fluidTank.readFromNBT(nbt);
        hemorrhageTimer = nbt.getInt("Hemorrhage");
    }

    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }
}