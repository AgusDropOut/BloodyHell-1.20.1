package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguinitePipeBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.FilterableFluidBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.CorruptedBloodySoulEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SanguinitePipeBlockEntity extends FilterableFluidBlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int hemorrhageTimer = 0;
    private static final int MAX_HEMORRHAGE_TIME = 60;

    // TRUE = Extract from tanks. FALSE = Insert into tanks.
    private boolean isPullMode = false;

    public SanguinitePipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_PIPE_BE.get(), pos, state, 1000);
    }

    // --- INTERACTION ---

    public void togglePullMode() {
        this.isPullMode = !this.isPullMode;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean isPullMode() {
        return isPullMode;
    }

    // --- TICK LOGIC ---

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        FluidStack myFluid = fluidTank.getFluid();

        // 1. Corruption Check
        if (!myFluid.isEmpty() && isCorrupted(myFluid)) {
            handleCorruption(level, pos);
        } else if (hemorrhageTimer > 0) {
            hemorrhageTimer = 0;
            if (level != null) level.sendBlockUpdated(pos, state, state, 3);
        }

        // 2. Flow Logic
        for (Direction dir : Direction.values()) {
            // Optimization: Only check sides that are visually connected
            BooleanProperty property = getPropertyByDirection(dir);
            if (!state.getValue(property)) continue;

            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);

            if (neighborBE != null) {
                neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {

                    boolean isNeighborPipe = neighborBE instanceof SanguinitePipeBlockEntity;

                    // CASE A: Neighbor is a PIPE
                    // Always try to flow into other pipes to distribute fluid, regardless of push/pull mode.
                    if (isNeighborPipe && !myFluid.isEmpty()) {
                        FluidStack neighborFluid = ((SanguinitePipeBlockEntity) neighborBE).getFluidTank().getFluid();

                        // Pressure Logic: Move from High to Low
                        if (myFluid.getAmount() > neighborFluid.getAmount()) {
                            if (neighborFluid.isEmpty() || neighborFluid.isFluidEqual(myFluid)) {
                                int diff = myFluid.getAmount() - neighborFluid.getAmount();
                                int toTransfer = Math.min(diff / 2, 200);
                                moveFluid(this.fluidTank, handler, toTransfer);
                            }
                        }
                    }

                    // CASE B: Neighbor is a BLOCK (Tank/Machine)
                    // This is where the Push/Pull switch logic applies.
                    else if (!isNeighborPipe) {

                        if (isPullMode) {
                            // PULL MODE: Suck fluid FROM the tank INTO the pipe
                            if (fluidTank.getSpace() > 0) {
                                FluidStack drained = handler.drain(200, IFluidHandler.FluidAction.SIMULATE);
                                if (!drained.isEmpty() && fluidTank.isFluidValid(drained)) {
                                    int filled = fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                                    handler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        } else {
                            // PUSH MODE: Push fluid FROM the pipe INTO the tank
                            if (!myFluid.isEmpty()) {
                                // Safety: Prevent backflow into Harvester if needed, though 'isFluidValid' on harvester usually handles this.
                                moveFluid(this.fluidTank, handler, 500);
                            }
                        }
                    }
                });
            }
        }
    }

    // --- HELPER METHODS ---

    private void handleCorruption(Level level, BlockPos pos) {
        hemorrhageTimer++;
        if (hemorrhageTimer % 10 == 0) level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);

        if (hemorrhageTimer >= MAX_HEMORRHAGE_TIME) {
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GLASS_BREAK, net.minecraft.sounds.SoundSource.BLOCKS, 2.0F, 0.7F);
            CorruptedBloodySoulEntity soul = new CorruptedBloodySoulEntity(ModEntityTypes.CORRUPTED_BLOODY_SOUL_ENTITY.get(), level);
            soul.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            level.addFreshEntity(soul);
            level.setBlockAndUpdate(pos, ModBlocks.CORRUPTED_BLOOD_BLOCK.get().defaultBlockState());
        }
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

    private boolean isCorrupted(FluidStack stack) {
        return stack.getFluid() == ModFluids.CORRUPTED_BLOOD_SOURCE.get()
                || stack.getFluid() == ModFluids.CORRUPTED_BLOOD_FLOWING.get();
    }

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

    // --- SAVING & LOADING ---

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Hemorrhage", hemorrhageTimer);
        nbt.putBoolean("IsPullMode", isPullMode);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        hemorrhageTimer = nbt.getInt("Hemorrhage");
        isPullMode = nbt.getBoolean("IsPullMode");
    }

    // --- ANIMATION ---

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (this.hemorrhageTimer > 0) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("shaking"));
            }
            // Visual feedback for Pull Mode
            if (isPullMode) {
                // return state.setAndContinue(RawAnimation.begin().thenLoop("pumping"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}