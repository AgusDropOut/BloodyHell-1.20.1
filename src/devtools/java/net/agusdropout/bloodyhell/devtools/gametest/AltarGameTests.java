package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.base.AbstractMainAltarBlock;
import net.agusdropout.bloodyhell.block.entity.base.IHoldingItemsAltar;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.agusdropout.bloodyhell.recipe.BlasphemousBloodAltarRecipe;
import net.agusdropout.bloodyhell.recipe.BloodAltarRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class AltarGameTests {
    private static final String TEMPLATE = "empty";
    private static final int RITUAL_SLOTS = 32;
    private static final BlockPos CENTER = new BlockPos(4, 1, 4);
    private static final BlockPos[] PEDESTALS = {CENTER.north(4), CENTER.east(4), CENTER.south(4), CENTER.west(4)};

    private static void placeAltars(GameTestHelper helper, Block main, Block pedestal) {
        BlockState lower = main.defaultBlockState();
        helper.setBlock(CENTER, lower);
        helper.setBlock(CENTER.above(), lower.setValue(AbstractMainAltarBlock.HALF, DoubleBlockHalf.UPPER));
        for (BlockPos pos : PEDESTALS) {
            helper.setBlock(pos, pedestal);
        }
    }

    private static ItemStackHandler handler(GameTestHelper helper, BlockPos pos) {
        BlockEntity entity = helper.getBlockEntity(pos);
        if (!(entity instanceof IHoldingItemsAltar altar)) {
            helper.fail("the pedestal has no item handler", pos);
            throw new IllegalStateException("pedestal without handler");
        }
        return altar.getItemHandler();
    }

    private static InteractionResult use(GameTestHelper helper, Player player, ItemStack held) {
        player.setItemInHand(InteractionHand.MAIN_HAND, held);
        BlockPos absolute = helper.absolutePos(CENTER);
        BlockState state = helper.getLevel().getBlockState(absolute);
        return state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false));
    }

    private static boolean pedestalsEmpty(GameTestHelper helper, int slots) {
        for (BlockPos pos : PEDESTALS) {
            ItemStackHandler handler = handler(helper, pos);
            for (int slot = 0; slot < slots; slot++) {
                if (!handler.getStackInSlot(slot).isEmpty()) return false;
            }
        }
        return true;
    }

    private static boolean has(Player player, ItemStack expected) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(expected.getItem()) && stack.getCount() >= expected.getCount()) return true;
        }
        return false;
    }

    private static void bloodAltarRitual(GameTestHelper helper, int index) {
        List<BloodAltarRecipe> recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(BloodAltarRecipe.Type.INSTANCE);
        if (index >= recipes.size()) {
            helper.succeed();
            return;
        }
        BloodAltarRecipe recipe = recipes.get(index);
        placeAltars(helper, MachineBlocks.MAIN_BLOOD_ALTAR.get(), MachineBlocks.BLOOD_ALTAR.get());
        List<Ingredient> ingredients = recipe.getIngredients();
        helper.assertTrue(ingredients.size() == 4, "the recipe " + recipe.getId() + " does not have 4 ingredients");
        for (int i = 0; i < 4; i++) {
            handler(helper, PEDESTALS[i]).setStackInSlot(0, ingredients.get(i).getItems()[0].copy());
        }
        Player player = helper.makeMockPlayer();
        ItemStack result = recipe.getResultItem(helper.getLevel().registryAccess());
        use(helper, player, new ItemStack(FlaskItems.CORRUPTED_BLOOD_FLASK.get()));
        MainBloodAltarBlockEntity altar = (MainBloodAltarBlockEntity) helper.getBlockEntity(CENTER);
        helper.assertTrue(altar.isActive(), "the altar did not activate with a corrupted blood flask");
        use(helper, player, ItemStack.EMPTY);
        helper.assertTrue(altar.hasResultItem(), "the ritual " + recipe.getId() + " produced no result");
        helper.assertTrue(altar.getResultItem().is(result.getItem()), "the ritual " + recipe.getId() + " produced the wrong item");
        helper.assertTrue(pedestalsEmpty(helper, 1), "the ritual " + recipe.getId() + " did not consume the offerings");
        use(helper, player, ItemStack.EMPTY);
        helper.assertTrue(has(player, result), "the player did not receive the result of " + recipe.getId());
        helper.assertTrue(!altar.hasResultItem(), "the altar kept its result after the player took it");
        helper.succeed();
    }

    private static void blasphemousAltarRitual(GameTestHelper helper, int index) {
        List<BlasphemousBloodAltarRecipe> recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(BlasphemousBloodAltarRecipe.Type.INSTANCE);
        if (index >= recipes.size()) {
            helper.succeed();
            return;
        }
        BlasphemousBloodAltarRecipe recipe = recipes.get(index);
        placeAltars(helper, MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get(), MachineBlocks.BLASPHEMOUS_BLOOD_ALTAR.get());
        List<Ingredient> ingredients = recipe.getIngredients();
        helper.assertTrue(ingredients.size() == 3, "the recipe " + recipe.getId() + " does not have 3 ingredients");
        for (BlockPos pos : PEDESTALS) {
            ItemStackHandler handler = handler(helper, pos);
            for (int slot = 0; slot < 3; slot++) {
                handler.setStackInSlot(slot, ingredients.get(slot).getItems()[0].copy());
            }
        }
        Player player = helper.makeMockPlayer();
        player.moveTo(helper.absoluteVec(new Vec3(2.5, 1.0, 2.5)));
        ItemStack result = recipe.getResultItem(helper.getLevel().registryAccess());
        use(helper, player, new ItemStack(FlaskItems.FILLED_BLOOD_FLASK.get()));
        use(helper, player, ItemStack.EMPTY);
        helper.assertTrue(pedestalsEmpty(helper, 3), "the ritual " + recipe.getId() + " did not consume the offerings");
        helper.succeedWhen(() -> {
            boolean delivered = !helper.getLevel().getEntitiesOfClass(ItemEntity.class, new net.minecraft.world.phys.AABB(helper.absolutePos(new BlockPos(-12, -2, -12)), helper.absolutePos(new BlockPos(20, 12, 20))),
                    entity -> entity.getItem().is(result.getItem())).isEmpty();
            helper.assertTrue(delivered, "the reward of " + recipe.getId() + " was not delivered");
        });
    }

    private static void rejectedOfferings(GameTestHelper helper, Block main, Block pedestal, int slots, ItemStack flask) {
        placeAltars(helper, main, pedestal);
        for (BlockPos pos : PEDESTALS) {
            for (int slot = 0; slot < slots; slot++) {
                handler(helper, pos).setStackInSlot(slot, new ItemStack(net.minecraft.world.item.Items.DIRT));
            }
        }
        Player player = helper.makeMockPlayer();
        use(helper, player, flask);
        use(helper, player, ItemStack.EMPTY);
        helper.assertTrue(!pedestalsEmpty(helper, slots), "the altar consumed offerings that match no recipe");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "altars", timeoutTicks = 100)
    public static void bloodAltarRejectsUnknownOfferings(GameTestHelper helper) {
        rejectedOfferings(helper, MachineBlocks.MAIN_BLOOD_ALTAR.get(), MachineBlocks.BLOOD_ALTAR.get(), 1,
                new ItemStack(FlaskItems.CORRUPTED_BLOOD_FLASK.get()));
    }

    @GameTest(template = TEMPLATE, batch = "altars", timeoutTicks = 100)
    public static void blasphemousAltarRejectsUnknownOfferings(GameTestHelper helper) {
        rejectedOfferings(helper, MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get(), MachineBlocks.BLASPHEMOUS_BLOOD_ALTAR.get(), 3,
                new ItemStack(FlaskItems.FILLED_BLOOD_FLASK.get()));
    }

    @GameTest(template = TEMPLATE, batch = "altars", timeoutTicks = 20)
    public static void everyAltarRecipeHasATest(GameTestHelper helper) {
        int blood = helper.getLevel().getRecipeManager().getAllRecipesFor(BloodAltarRecipe.Type.INSTANCE).size();
        int blasphemous = helper.getLevel().getRecipeManager().getAllRecipesFor(BlasphemousBloodAltarRecipe.Type.INSTANCE).size();
        helper.assertTrue(blood <= RITUAL_SLOTS, "the blood altar has " + blood + " recipes but only " + RITUAL_SLOTS + " have a test: raise RITUAL_SLOTS");
        helper.assertTrue(blasphemous <= RITUAL_SLOTS, "the blasphemous altar has " + blasphemous + " recipes but only " + RITUAL_SLOTS + " have a test: raise RITUAL_SLOTS");
        helper.succeed();
    }

    @GameTestGenerator
    public static Collection<TestFunction> everyRitualIsTested() {
        List<TestFunction> tests = new ArrayList<>();
        for (int index = 0; index < RITUAL_SLOTS; index++) {
            int slot = index;
            tests.add(new TestFunction("altars", "blood_ritual_" + slot, BloodyHell.MODID + ":" + TEMPLATE, 100, 0, true, helper -> bloodAltarRitual(helper, slot)));
            tests.add(new TestFunction("altars", "blasphemous_ritual_" + slot, BloodyHell.MODID + ":" + TEMPLATE, 500, 0, true, helper -> blasphemousAltarRitual(helper, slot)));
        }
        return tests;
    }
}
