package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.base.BaseGemSproutBlock;
import net.agusdropout.bloodyhell.block.entity.base.BaseGemSproutBlockEntity;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class SproutGameTests {
    private static final BlockPos HERE = new BlockPos(4, 1, 4);
    private static final int MAX_TICKS = 200000;

    private static BaseGemSproutBlockEntity sprout(GameTestHelper helper, int age) {
        helper.setBlock(HERE, MachineBlocks.BLOOD_GEM_SPROUT.get().defaultBlockState().setValue(BaseGemSproutBlock.AGE, age));
        return (BaseGemSproutBlockEntity) helper.getBlockEntity(HERE);
    }

    private static void tick(GameTestHelper helper, BaseGemSproutBlockEntity sprout, int times) {
        BlockPos absolute = helper.absolutePos(HERE);
        for (int i = 0; i < times; i++) {
            sprout.tick(helper.getLevel(), absolute, helper.getLevel().getBlockState(absolute));
        }
    }

    private static int stored(BaseGemSproutBlockEntity sprout) {
        return sprout.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().orElseThrow().getFluidInTank(0).getAmount();
    }

    private static int age(GameTestHelper helper) {
        return helper.getBlockState(HERE).getValue(BaseGemSproutBlock.AGE);
    }

    private static void use(GameTestHelper helper, Player player) {
        BlockPos absolute = helper.absolutePos(HERE);
        BlockState state = helper.getLevel().getBlockState(absolute);
        state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false));
    }

    private static void clearItems(GameTestHelper helper) {
        List<ItemEntity> items = helper.getLevel().getEntities(EntityType.ITEM, new AABB(helper.absolutePos(HERE)).inflate(4), item -> true);
        items.forEach(ItemEntity::discard);
    }

    @GameTest(template = "empty", batch = "plants", timeoutTicks = 60)
    public static void gemSproutDrinksABloodFlask(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.BLOOD_GEM_SPROUT.get());
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(FlaskItems.FILLED_BLOOD_FLASK.get()));
        use(helper, player);
        helper.assertTrue(helper.getBlockState(HERE).getValue(BaseGemSproutBlock.FILLED), "the sprout did not take the blood of the flask");
        helper.assertTrue(stored((BaseGemSproutBlockEntity) helper.getBlockEntity(HERE)) > 0, "the sprout has no blood stored after the flask");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "plants", timeoutTicks = 100)
    public static void gemSproutGrowsToTheGemStageUsingBlood(GameTestHelper helper) {
        BaseGemSproutBlockEntity sprout = sprout(helper, 0);
        sprout.fillBlood(new FluidStack(ModFluids.BLOOD_SOURCE.get(), 1000), false);
        for (int i = 0; i < MAX_TICKS && age(helper) < BaseGemSproutBlockEntity.INSERT_GEM_AGE; i++) {
            tick(helper, sprout, 1);
        }
        helper.assertTrue(age(helper) == BaseGemSproutBlockEntity.INSERT_GEM_AGE, "the sprout did not reach the gem stage, it is at age " + age(helper));
        helper.assertTrue(stored(sprout) == 500, "two growth stages should cost 500 mB, " + stored(sprout) + " mB are left");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "plants", timeoutTicks = 100)
    public static void gemSproutDoesNotGrowWithoutBlood(GameTestHelper helper) {
        BaseGemSproutBlockEntity sprout = sprout(helper, 0);
        tick(helper, sprout, 5000);
        helper.assertTrue(age(helper) == 0, "the sprout grew without blood, it is at age " + age(helper));
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "plants", timeoutTicks = 100)
    public static void gemSproutWaitsForAGemAtTheGemStage(GameTestHelper helper) {
        BaseGemSproutBlockEntity sprout = sprout(helper, BaseGemSproutBlockEntity.INSERT_GEM_AGE);
        sprout.fillBlood(new FluidStack(ModFluids.BLOOD_SOURCE.get(), 1000), false);
        tick(helper, sprout, 5000);
        helper.assertTrue(age(helper) == BaseGemSproutBlockEntity.INSERT_GEM_AGE, "the sprout left the gem stage without a gem, it is at age " + age(helper));
        helper.assertTrue(stored(sprout) == 1000, "the sprout used blood while waiting for a gem");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "plants", timeoutTicks = 100)
    public static void gemSproutTakesItsIngredientAndMovesOn(GameTestHelper helper) {
        BaseGemSproutBlockEntity sprout = sprout(helper, BaseGemSproutBlockEntity.INSERT_GEM_AGE);
        Player player = helper.makeMockPlayer();
        ItemStack nugget = new ItemStack(GemType.CITRINE.getIngredient());
        player.setItemInHand(InteractionHand.MAIN_HAND, nugget);
        use(helper, player);
        helper.assertTrue(helper.getBlockState(HERE).getValue(BaseGemSproutBlock.ITEM_INSIDE), "the sprout did not take the ingredient");
        tick(helper, sprout, 60);
        helper.assertTrue(age(helper) == 3, "the sprout did not move on after taking the ingredient, it is at age " + age(helper));
        helper.assertFalse(helper.getBlockState(HERE).getValue(BaseGemSproutBlock.ITEM_INSIDE), "the ingredient stays shown after the sprout used it");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "plants", timeoutTicks = 100)
    public static void gemSproutRejectsAnUnrelatedItemAtTheGemStage(GameTestHelper helper) {
        sprout(helper, BaseGemSproutBlockEntity.INSERT_GEM_AGE);
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
        use(helper, player);
        helper.assertFalse(helper.getBlockState(HERE).getValue(BaseGemSproutBlock.ITEM_INSIDE), "the sprout took a stick as a gem ingredient");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "plants", timeoutTicks = 100)
    public static void gemSproutGivesItsGemWhenGrownAndStartsOver(GameTestHelper helper) {
        BaseGemSproutBlockEntity sprout = sprout(helper, BaseGemSproutBlockEntity.INSERT_GEM_AGE);
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(GemType.PURE.getIngredient()));
        use(helper, player);
        helper.getLevel().setBlock(helper.absolutePos(HERE), helper.getBlockState(HERE).setValue(BaseGemSproutBlock.AGE, BaseGemSproutBlock.MAX_AGE), 3);
        use(helper, player);
        helper.assertTrue(age(helper) == 0, "the sprout did not start over after giving its gem");
        List<ItemEntity> gems = helper.getLevel().getEntities(EntityType.ITEM, new AABB(helper.absolutePos(HERE)).inflate(4), item -> true);
        helper.assertTrue(gems.size() == 1, "the sprout should drop exactly one gem, it dropped " + gems.size());
        ItemStack gem = gems.get(0).getItem();
        helper.assertTrue(gem.is(GemType.PURE.getResultGem()), "the sprout dropped " + gem + " instead of the pure gem");
        helper.assertTrue(gem.getOrCreateTag().getDouble("damage") > 0, "the dropped gem has no damage value");
        clearItems(helper);
        helper.succeed();
    }
}
