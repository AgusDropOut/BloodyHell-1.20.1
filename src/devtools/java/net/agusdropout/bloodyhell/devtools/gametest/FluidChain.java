package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.block.base.AbstractPipeBlock;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class FluidChain {
    private static final BlockPos ORIGIN = new BlockPos(0, 1, 4);

    private final GameTestHelper helper;
    private final List<BlockPos> positions = new ArrayList<>();
    private final Set<Integer> pulling = new HashSet<>();

    private FluidChain(GameTestHelper helper) {
        this.helper = helper;
    }

    static FluidChain on(GameTestHelper helper) {
        return new FluidChain(helper);
    }

    FluidChain then(RegistryObject<Block> block) {
        BlockPos pos = ORIGIN.east(positions.size());
        helper.setBlock(pos, block.get());
        positions.add(pos);
        return this;
    }

    FluidChain pull(RegistryObject<Block> pipe) {
        pulling.add(positions.size());
        return then(pipe);
    }

    FluidChain connect() {
        for (int i = 0; i < positions.size(); i++) {
            BlockPos pos = positions.get(i);
            BlockState state = helper.getBlockState(pos);
            if (!(state.getBlock() instanceof AbstractPipeBlock)) continue;
            helper.setBlock(pos, state
                    .setValue(AbstractPipeBlock.EAST, i < positions.size() - 1)
                    .setValue(AbstractPipeBlock.WEST, i > 0));
            if (pulling.contains(i)) {
                ((AbstractPipeBlockEntity) helper.getBlockEntity(pos)).togglePullMode();
            }
        }
        return this;
    }

    BlockPos at(int index) {
        return positions.get(index);
    }

    BlockPos last() {
        return positions.get(positions.size() - 1);
    }

    <T extends BlockEntity> T entity(int index, Class<T> type) {
        BlockEntity entity = helper.getBlockEntity(positions.get(index));
        if (!type.isInstance(entity)) {
            helper.fail("the block " + index + " of the chain is not a " + type.getSimpleName(), positions.get(index));
            throw new IllegalStateException("wrong block entity in the chain");
        }
        return type.cast(entity);
    }

    String describe() {
        StringBuilder text = new StringBuilder("chain:");
        for (int i = 0; i < positions.size(); i++) {
            BlockEntity entity = helper.getBlockEntity(positions.get(i));
            if (entity == null) {
                text.append(" [").append(i).append(" gone]");
                continue;
            }
            text.append(" [").append(i).append(' ').append(entity.getClass().getSimpleName());
            entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
                for (int tank = 0; tank < handler.getTanks(); tank++) {
                    text.append(' ').append(handler.getFluidInTank(tank).getAmount());
                }
            });
            text.append(']');
        }
        return text.toString();
    }

    IFluidHandler fluids(int index) {
        BlockEntity entity = entity(index, BlockEntity.class);
        return entity.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().orElseThrow();
    }
}
