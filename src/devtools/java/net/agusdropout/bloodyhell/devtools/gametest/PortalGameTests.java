package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.registry.MetalBlocks;
import net.agusdropout.bloodyhell.block.registry.MiscBlocks;
import net.agusdropout.bloodyhell.item.registry.MiscItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class PortalGameTests {
    private static final String TEMPLATE = "empty";

    private static void buildFrame(GameTestHelper helper) {
        for (int x = 2; x <= 5; x++) {
            for (int y = 1; y <= 5; y++) {
                boolean border = x == 2 || x == 5 || y == 1 || y == 5;
                if (border) helper.setBlock(new BlockPos(x, y, 4), MetalBlocks.SANGUINITE_BLOCK.get());
            }
        }
    }

    @GameTest(template = TEMPLATE, batch = "portal", timeoutTicks = 60)
    public static void chaliceLightsAPortalFrame(GameTestHelper helper) {
        buildFrame(helper);
        Player player = helper.makeMockPlayer();
        ItemStack chalice = new ItemStack(MiscItems.CHALICE_OF_THE_DAMMED.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, chalice);
        BlockPos bottom = helper.absolutePos(new BlockPos(3, 1, 4));
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(bottom).add(0, 0.5, 0), Direction.UP, bottom, false);
        InteractionResult result = chalice.useOn(new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, chalice, hit));
        helper.assertTrue(result.consumesAction(), "the chalice did not light the frame");
        for (int x = 3; x <= 4; x++) {
            for (int y = 2; y <= 4; y++) {
                helper.assertBlockPresent(MiscBlocks.BLOOD_PORTAL.get(), new BlockPos(x, y, 4));
            }
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "portal", timeoutTicks = 60)
    public static void chaliceDoesNothingWithoutAFrame(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 1, 4), MetalBlocks.SANGUINITE_BLOCK.get());
        Player player = helper.makeMockPlayer();
        ItemStack chalice = new ItemStack(MiscItems.CHALICE_OF_THE_DAMMED.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, chalice);
        BlockPos pos = helper.absolutePos(new BlockPos(3, 1, 4));
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.5, 0), Direction.UP, pos, false);
        chalice.useOn(new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, chalice, hit));
        helper.assertBlockNotPresent(MiscBlocks.BLOOD_PORTAL.get(), new BlockPos(3, 2, 4));
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "portal", timeoutTicks = 60)
    public static void chaliceLightsAPortalFromTheTopFrameBlock(GameTestHelper helper) {
        buildFrame(helper);
        Player player = helper.makeMockPlayer();
        ItemStack chalice = new ItemStack(MiscItems.CHALICE_OF_THE_DAMMED.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, chalice);
        BlockPos top = helper.absolutePos(new BlockPos(3, 5, 4));
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(top).add(0, 0.5, 0), Direction.UP, top, false);
        chalice.useOn(new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, chalice, hit));
        helper.assertBlockPresent(MiscBlocks.BLOOD_PORTAL.get(), new BlockPos(3, 3, 4));
        helper.succeed();
    }
}
