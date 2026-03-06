package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.CorruptedBloodySoulEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class SanguinitePipeBlockEntity extends AbstractPipeBlockEntity {

    private int hemorrhageTimer = 0;
    private static final int MAX_HEMORRHAGE_TIME = 60;

    public SanguinitePipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANGUINITE_PIPE_BE.get(), pos, state, 1000);
    }

    @Override
    protected boolean isFluidSupported(FluidStack stack) {
        return stack.getFluid() != ModFluids.CORRUPTED_BLOOD_SOURCE.get()
                && stack.getFluid() != ModFluids.CORRUPTED_BLOOD_FLOWING.get();
    }

    @Override
    protected void handleUnsupportedFluidTick(Level level, BlockPos pos, BlockState state) {
        hemorrhageTimer++;
        if (hemorrhageTimer % 10 == 0) level.sendBlockUpdated(pos, state, state, 3);

        if (hemorrhageTimer >= MAX_HEMORRHAGE_TIME) {
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GLASS_BREAK, net.minecraft.sounds.SoundSource.BLOCKS, 2.0F, 0.7F);
            CorruptedBloodySoulEntity soul = new CorruptedBloodySoulEntity(ModEntityTypes.CORRUPTED_BLOODY_SOUL_ENTITY.get(), level);
            soul.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            level.addFreshEntity(soul);
            level.setBlockAndUpdate(pos, ModBlocks.CORRUPTED_BLOOD_BLOCK.get().defaultBlockState());
        }
    }

    @Override
    protected void onSupportedFluidTick(Level level, BlockPos pos, BlockState state) {
        if (hemorrhageTimer > 0) {
            hemorrhageTimer = 0;
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Hemorrhage", hemorrhageTimer);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        hemorrhageTimer = nbt.getInt("Hemorrhage");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (this.hemorrhageTimer > 0) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("shaking"));
            }
            return PlayState.STOP;
        }));
    }
}