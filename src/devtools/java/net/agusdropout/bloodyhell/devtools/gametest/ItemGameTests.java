package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class ItemGameTests {
    private static final BlockPos TARGET = new BlockPos(4, 1, 4);

    @GameTestGenerator
    public static Collection<TestFunction> everyItemCanBeUsedOnTheServer() {
        List<TestFunction> tests = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (!id.getNamespace().equals(BloodyHell.MODID)) continue;
            tests.add(new TestFunction("items", "item_" + id.getPath(), BloodyHell.MODID + ":empty", 60, 0, true, helper -> useItem(helper, item)));
        }
        return tests;
    }

    private static void useItem(GameTestHelper helper, Item item) {
        Player player = helper.makeMockPlayer();
        BlockPos absolute = helper.absolutePos(TARGET);
        player.moveTo(Vec3.atBottomCenterOf(absolute.offset(2, 0, 0)));
        ItemStack stack = new ItemStack(item, Math.max(1, Math.min(2, item.getMaxStackSize(new ItemStack(item)))));
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.getHoverName();
        stack.getDescriptionId();
        stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolute.below()), Direction.UP, absolute.below(), false);
        stack.useOn(new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, stack, hit));
        if (player.isUsingItem()) {
            player.releaseUsingItem();
        }
        helper.runAfterDelay(10, helper::succeed);
    }
}
