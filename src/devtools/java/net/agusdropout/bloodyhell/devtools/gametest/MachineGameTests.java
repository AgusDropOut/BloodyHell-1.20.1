package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.base.AbstractCondenserBlockEntity;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.recipe.CondenserRecipe;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class MachineGameTests {
    private static final String TEMPLATE = "empty";
    private static final BlockPos MACHINE = new BlockPos(4, 1, 4);

    @GameTest(template = TEMPLATE, batch = "machines", timeoutTicks = 300)
    public static void sanguiniteCondenserMakesItsRecipe(GameTestHelper helper) {
        condenserMakesItsRecipe(helper, MachineBlocks.SANGUINITE_CONDENSER);
    }

    @GameTest(template = TEMPLATE, batch = "machines", timeoutTicks = 300)
    public static void rhnullCondenserMakesItsRecipe(GameTestHelper helper) {
        condenserMakesItsRecipe(helper, MachineBlocks.RHNULL_CONDENSER);
    }

    private static void condenserMakesItsRecipe(GameTestHelper helper, RegistryObject<Block> condenserBlock) {
        List<CondenserRecipe> recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(CondenserRecipe.Type.INSTANCE);
        if (recipes.isEmpty()) {
            helper.fail("there are no condensing recipes");
            return;
        }
        CondenserRecipe recipe = recipes.get(0);
        helper.setBlock(MACHINE, condenserBlock.get());
        if (!(helper.getBlockEntity(MACHINE) instanceof AbstractCondenserBlockEntity condenser)) {
            helper.fail("the condenser has no block entity", MACHINE);
            return;
        }
        int filled = condenser.getInputTank().fill(recipe.getFluidInput().copy(), IFluidHandler.FluidAction.EXECUTE);
        helper.assertTrue(filled == recipe.getFluidInput().getAmount(), "the tank did not accept the recipe fluid");
        ItemStack input = recipe.getItemInput().getItems()[0].copy();
        condenser.getItemHandler().setStackInSlot(0, input);
        ItemStack result = recipe.getResultItem(helper.getLevel().registryAccess());
        helper.succeedWhen(() -> {
            helper.assertItemEntityPresent(result.getItem(), MACHINE.above(), 3.0);
            helper.assertTrue(condenser.getInputTank().isEmpty(), "the fluid was not consumed");
            helper.assertTrue(condenser.getItemHandler().getStackInSlot(0).isEmpty(), "the input item was not consumed");
        });
    }

    @GameTestGenerator
    public static Collection<TestFunction> everyBlockSurvivesPlacementAndTicks() {
        List<TestFunction> tests = new ArrayList<>();
        for (RegistryObject<Block> entry : ModBlocks.BLOCKS.getEntries()) {
            tests.add(new TestFunction("blocks", "block_" + entry.getId().getPath(), BloodyHell.MODID + ":" + TEMPLATE, 60, 0, true,
                    helper -> placeAndTick(helper, entry.get())));
        }
        return tests;
    }

    private static void placeAndTick(GameTestHelper helper, Block block) {
        BlockState state = block.defaultBlockState();
        helper.setBlock(MACHINE, state);
        helper.runAfterDelay(30, () -> {
            BlockState placed = helper.getBlockState(MACHINE);
            if (placed.is(block) && state.hasBlockEntity()) {
                BlockEntity entity = helper.getBlockEntity(MACHINE);
                helper.assertTrue(entity != null, "the block has no block entity after ticking");
            }
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "machines", timeoutTicks = 800)
    public static void infusorMakesEveryRecipe(GameTestHelper helper) {
        List<SanguiniteInfusorRecipe> recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(SanguiniteInfusorRecipe.Type.INSTANCE);
        if (recipes.isEmpty()) {
            helper.fail("there are no infusing recipes");
            return;
        }
        List<Runnable> checks = new ArrayList<>();
        for (int i = 0; i < recipes.size(); i++) {
            SanguiniteInfusorRecipe recipe = recipes.get(i);
            BlockPos pos = new BlockPos(1 + (i % 4) * 2, 1, 1 + (i / 4) * 2);
            helper.setBlock(pos, MachineBlocks.SANGUINITE_INFUSOR.get());
            var entity = helper.getBlockEntity(pos);
            var fluids = entity.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().orElseThrow();
            var items = entity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();
            fluids.fill(recipe.getFluid1().copy(), IFluidHandler.FluidAction.EXECUTE);
            fluids.fill(recipe.getFluid2().copy(), IFluidHandler.FluidAction.EXECUTE);
            ItemStack rest = items.insertItem(0, recipe.getInputItem().getItems()[0].copy(), false);
            helper.assertTrue(rest.isEmpty(), "the infusor did not accept the input of " + recipe.getId());
            ItemStack result = recipe.getResultItem(helper.getLevel().registryAccess());
            checks.add(() -> helper.assertTrue(items.getStackInSlot(1).is(result.getItem()), "the infusor did not produce " + result.getItem()));
        }
        helper.succeedWhen(() -> checks.forEach(Runnable::run));
    }

    @GameTestGenerator
    public static Collection<TestFunction> everyEntitySpawnsAndTicks() {
        List<TestFunction> tests = new ArrayList<>();
        for (RegistryObject<EntityType<?>> entry : ModEntityTypes.ENTITY_TYPES.getEntries().stream().map(e -> (RegistryObject<EntityType<?>>) (RegistryObject<?>) e).toList()) {
            tests.add(new TestFunction("entities", "entity_" + entry.getId().getPath(), BloodyHell.MODID + ":" + TEMPLATE, 60, 0, true,
                    helper -> spawnAndTick(helper, entry.get())));
        }
        return tests;
    }

    private static void spawnAndTick(GameTestHelper helper, EntityType<?> type) {
        Entity entity = type.create(helper.getLevel());
        if (entity == null) {
            helper.fail("the entity type could not create an entity");
            return;
        }
        BlockPos pos = helper.absolutePos(MACHINE.above());
        entity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        helper.getLevel().addFreshEntity(entity);
        helper.runAfterDelay(30, () -> {
            entity.discard();
            helper.succeed();
        });
    }
}
