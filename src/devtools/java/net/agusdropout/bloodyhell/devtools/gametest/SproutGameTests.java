package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.base.BaseGemSproutBlock;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class SproutGameTests {
    private static final BlockPos HERE = new BlockPos(4, 1, 4);

    @GameTest(template = "empty", batch = "plants", timeoutTicks = 60)
    public static void gemSproutDrinksABloodFlask(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.BLOOD_GEM_SPROUT.get());
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(FlaskItems.FILLED_BLOOD_FLASK.get()));
        BlockPos absolute = helper.absolutePos(HERE);
        BlockState state = helper.getLevel().getBlockState(absolute);
        state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false));
        helper.assertTrue(helper.getBlockState(HERE).getValue(BaseGemSproutBlock.FILLED), "the sprout did not take the blood of the flask");
        int stored = helper.getBlockEntity(HERE).getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().orElseThrow().getFluidInTank(0).getAmount();
        helper.assertTrue(stored > 0, "the sprout has no blood stored after the flask");
        helper.succeed();
    }
}
