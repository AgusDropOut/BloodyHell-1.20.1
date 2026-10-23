package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.base.AbstractCondenserBlockEntity;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteBloodHarvesterBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteInfusorBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.UnknownPortalBlockEntity;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulType;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.registry.GemItems;
import net.agusdropout.bloodyhell.item.registry.RhnullItems;
import net.agusdropout.bloodyhell.recipe.SanguiniteInfusorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class FluidChainGameTests {
    private static final String TEMPLATE = "empty";
    private static final int CONDENSING_TICKS = 80;
    private static final int SURPLUS = 50;

    private static FluidStack stack(Supplier<? extends Fluid> fluid, int amount) {
        return new FluidStack(fluid.get(), amount);
    }

    private static FluidChain tankToCondenser(GameTestHelper helper, RegistryObject<Block> tank, RegistryObject<Block> pipe, RegistryObject<Block> condenser) {
        return FluidChain.on(helper).then(tank).pull(pipe).then(pipe).then(condenser).connect();
    }

    private static void condensesThroughPipes(GameTestHelper helper, RegistryObject<Block> tank, RegistryObject<Block> pipe, RegistryObject<Block> condenser,
                                              Supplier<? extends Fluid> fluid, int amount, Supplier<Item> frame, Supplier<Item> result) {
        FluidChain chain = tankToCondenser(helper, tank, pipe, condenser);
        chain.fluids(0).fill(stack(fluid, amount + SURPLUS), IFluidHandler.FluidAction.EXECUTE);
        AbstractCondenserBlockEntity machine = chain.entity(3, AbstractCondenserBlockEntity.class);
        machine.getItemHandler().setStackInSlot(0, new ItemStack(frame.get()));
        helper.succeedWhen(() -> helper.assertItemEntityPresent(result.get(), chain.last().above(), 3.0));
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 400)
    public static void bloodTankFeedsACondenserThroughPipesIntoAnAncientGem(GameTestHelper helper) {
        condensesThroughPipes(helper, MachineBlocks.SANGUINITE_TANK, MachineBlocks.SANGUINITE_PIPE, MachineBlocks.SANGUINITE_CONDENSER,
                ModFluids.BLOOD_SOURCE, 1000, GemItems.SANGUINITE_GEM_FRAME, GemItems.ANCIENT_GEM);
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 400)
    public static void bloodTankFeedsACondenserThroughPipesIntoAGreatAncientGem(GameTestHelper helper) {
        condensesThroughPipes(helper, MachineBlocks.SANGUINITE_TANK, MachineBlocks.SANGUINITE_PIPE, MachineBlocks.SANGUINITE_CONDENSER,
                ModFluids.BLOOD_SOURCE, 2000, GemItems.SANGUINITE_GREAT_GEM_FRAME, GemItems.GREAT_ANCIENT_GEM);
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 400)
    public static void viscousTankFeedsARhnullCondenserThroughPipesIntoAnAncientBlasphemousGem(GameTestHelper helper) {
        condensesThroughPipes(helper, MachineBlocks.RHNULL_TANK, MachineBlocks.RHNULL_PIPE, MachineBlocks.RHNULL_CONDENSER,
                ModFluids.VISCOUS_BLASPHEMY_SOURCE, 1000, GemItems.RHNULL_GEM_FRAME, GemItems.ANCIENT_BLASPHEMOUS_GEM);
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 400)
    public static void viscousTankFeedsARhnullCondenserThroughPipesIntoAGreatAncientBlasphemousGem(GameTestHelper helper) {
        condensesThroughPipes(helper, MachineBlocks.RHNULL_TANK, MachineBlocks.RHNULL_PIPE, MachineBlocks.RHNULL_CONDENSER,
                ModFluids.VISCOUS_BLASPHEMY_SOURCE, 2000, GemItems.RHNULL_GREAT_GEM_FRAME, GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM);
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 600)
    public static void portalOutputReachesARhnullCondenserThroughPipesAndATank(GameTestHelper helper) {
        FluidChain chain = FluidChain.on(helper)
                .then(MachineBlocks.UNKNOWN_PORTAL_BLOCK)
                .pull(MachineBlocks.RHNULL_PIPE).then(MachineBlocks.RHNULL_PIPE)
                .then(MachineBlocks.RHNULL_TANK)
                .pull(MachineBlocks.RHNULL_PIPE).then(MachineBlocks.RHNULL_PIPE)
                .then(MachineBlocks.RHNULL_CONDENSER)
                .connect();
        chain.entity(0, UnknownPortalBlockEntity.class).outputTank.fill(stack(ModFluids.VISCOUS_BLASPHEMY_SOURCE, 2000 + SURPLUS), IFluidHandler.FluidAction.EXECUTE);
        AbstractCondenserBlockEntity condenser = chain.entity(6, AbstractCondenserBlockEntity.class);
        condenser.getItemHandler().setStackInSlot(0, new ItemStack(GemItems.RHNULL_GREAT_GEM_FRAME.get()));
        helper.succeedWhen(() -> helper.assertItemEntityPresent(GemItems.GREAT_ANCIENT_BLASPHEMOUS_GEM.get(), chain.last().above(), 3.0));
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 600)
    public static void harvesterBloodReachesThePortalThroughPipesAndATank(GameTestHelper helper) {
        FluidChain chain = FluidChain.on(helper)
                .then(MachineBlocks.SANGUINITE_BLOOD_HARVESTER)
                .pull(MachineBlocks.SANGUINITE_PIPE).then(MachineBlocks.SANGUINITE_PIPE)
                .then(MachineBlocks.SANGUINITE_TANK)
                .pull(MachineBlocks.SANGUINITE_PIPE).then(MachineBlocks.SANGUINITE_PIPE)
                .then(MachineBlocks.UNKNOWN_PORTAL_BLOCK)
                .connect();
        chain.entity(0, SanguiniteBloodHarvesterBlockEntity.class).receiveSoul(BloodSoulType.BLOOD, 1000);
        UnknownPortalBlockEntity portal = chain.entity(6, UnknownPortalBlockEntity.class);
        helper.succeedWhen(() -> helper.assertTrue(portal.portalProgress > 0.0f, "the blood of the harvester never made the portal progress; " + chain.describe()));
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 300, required = false)
    public static void pushPipeNextToTheHarvesterDoesNotSendTheBloodBack(GameTestHelper helper) {
        FluidChain chain = FluidChain.on(helper)
                .then(MachineBlocks.SANGUINITE_BLOOD_HARVESTER)
                .then(MachineBlocks.SANGUINITE_PIPE)
                .then(MachineBlocks.SANGUINITE_TANK)
                .connect();
        chain.entity(0, SanguiniteBloodHarvesterBlockEntity.class).receiveSoul(BloodSoulType.BLOOD, 1000);
        helper.succeedWhen(() -> helper.assertTrue(chain.fluids(2).getFluidInTank(0).getAmount() > 0,
                "the push pipe never delivered the harvester blood to the tank; " + chain.describe()));
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 300)
    public static void sanguinitePipeNextToThePortalBurstsWhenItPullsViscousBlasphemy(GameTestHelper helper) {
        FluidChain chain = FluidChain.on(helper)
                .then(MachineBlocks.UNKNOWN_PORTAL_BLOCK)
                .pull(MachineBlocks.SANGUINITE_PIPE)
                .connect();
        chain.entity(0, UnknownPortalBlockEntity.class).outputTank.fill(stack(ModFluids.VISCOUS_BLASPHEMY_SOURCE, 1000), IFluidHandler.FluidAction.EXECUTE);
        helper.succeedWhen(() -> helper.assertTrue(!helper.getBlockState(chain.at(1)).is(MachineBlocks.SANGUINITE_PIPE.get()),
                "the sanguinite pipe survived pulling viscous blasphemy from the portal"));
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 300)
    public static void condenserWithoutItsFrameKeepsTheFluidAndMakesNothing(GameTestHelper helper) {
        FluidChain chain = tankToCondenser(helper, MachineBlocks.SANGUINITE_TANK, MachineBlocks.SANGUINITE_PIPE, MachineBlocks.SANGUINITE_CONDENSER);
        chain.fluids(0).fill(stack(ModFluids.BLOOD_SOURCE, 1000), IFluidHandler.FluidAction.EXECUTE);
        AbstractCondenserBlockEntity condenser = chain.entity(3, AbstractCondenserBlockEntity.class);
        helper.runAfterDelay(CONDENSING_TICKS + 60, () -> {
            helper.assertTrue(condenser.getInputTank().getFluidAmount() >= 1000 - SURPLUS, "the condenser holds only " + condenser.getInputTank().getFluidAmount() + " of the 1000 that left the tank");
            helper.assertItemEntityNotPresent(GemItems.ANCIENT_GEM.get(), chain.last().above(), 3.0);
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 300)
    public static void condenserWithTooLittleFluidWaitsAndMakesNothing(GameTestHelper helper) {
        FluidChain chain = tankToCondenser(helper, MachineBlocks.SANGUINITE_TANK, MachineBlocks.SANGUINITE_PIPE, MachineBlocks.SANGUINITE_CONDENSER);
        chain.fluids(0).fill(stack(ModFluids.BLOOD_SOURCE, 500), IFluidHandler.FluidAction.EXECUTE);
        AbstractCondenserBlockEntity condenser = chain.entity(3, AbstractCondenserBlockEntity.class);
        condenser.getItemHandler().setStackInSlot(0, new ItemStack(GemItems.SANGUINITE_GEM_FRAME.get()));
        helper.runAfterDelay(CONDENSING_TICKS + 60, () -> {
            helper.assertItemEntityNotPresent(GemItems.ANCIENT_GEM.get(), chain.last().above(), 3.0);
            helper.assertTrue(condenser.getItemHandler().getStackInSlot(0).getCount() == 1, "the frame was consumed without enough fluid");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 400)
    public static void condenserKeepsTheLeftoverFluidAfterOneCraft(GameTestHelper helper) {
        FluidChain chain = tankToCondenser(helper, MachineBlocks.SANGUINITE_TANK, MachineBlocks.SANGUINITE_PIPE, MachineBlocks.SANGUINITE_CONDENSER);
        chain.fluids(0).fill(stack(ModFluids.BLOOD_SOURCE, 1500 + SURPLUS), IFluidHandler.FluidAction.EXECUTE);
        AbstractCondenserBlockEntity condenser = chain.entity(3, AbstractCondenserBlockEntity.class);
        condenser.getItemHandler().setStackInSlot(0, new ItemStack(GemItems.SANGUINITE_GEM_FRAME.get()));
        helper.runAfterDelay(CONDENSING_TICKS + 120, () -> {
            helper.assertItemEntityPresent(GemItems.ANCIENT_GEM.get(), chain.last().above(), 3.0);
            helper.assertTrue(condenser.getInputTank().getFluidAmount() >= 500 && condenser.getInputTank().getFluidAmount() < 1000, "the condenser holds " + condenser.getInputTank().getFluidAmount() + " instead of the roughly 500 left over");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 600)
    public static void condenserCraftsOnePerFrameOfAStack(GameTestHelper helper) {
        net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(helper.absolutePos(net.minecraft.core.BlockPos.ZERO), helper.absolutePos(new net.minecraft.core.BlockPos(9, 6, 9))).inflate(1.0);
        helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, area, entity -> entity.getItem().is(GemItems.ANCIENT_GEM.get()))
                .forEach(net.minecraft.world.entity.Entity::discard);
        FluidChain chain = tankToCondenser(helper, MachineBlocks.SANGUINITE_TANK, MachineBlocks.SANGUINITE_PIPE, MachineBlocks.SANGUINITE_CONDENSER);
        chain.fluids(0).fill(stack(ModFluids.BLOOD_SOURCE, 2000 + SURPLUS), IFluidHandler.FluidAction.EXECUTE);
        AbstractCondenserBlockEntity condenser = chain.entity(3, AbstractCondenserBlockEntity.class);
        condenser.getItemHandler().setStackInSlot(0, new ItemStack(GemItems.SANGUINITE_GEM_FRAME.get(), 2));
        helper.runAfterDelay(CONDENSING_TICKS * 2 + 160, () -> {
            int gems = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, area,
                            entity -> entity.getItem().is(GemItems.ANCIENT_GEM.get()))
                    .stream().mapToInt(entity -> entity.getItem().getCount()).sum();
            helper.assertTrue(gems == 2, "a stack of 2 frames with 2000 blood made " + gems + " gems instead of 2");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 400, required = false)
    public static void exactlyTheRecipeAmountCrossesTwoPipesIntoTheCondenser(GameTestHelper helper) {
        FluidChain chain = tankToCondenser(helper, MachineBlocks.SANGUINITE_TANK, MachineBlocks.SANGUINITE_PIPE, MachineBlocks.SANGUINITE_CONDENSER);
        chain.fluids(0).fill(stack(ModFluids.BLOOD_SOURCE, 1000), IFluidHandler.FluidAction.EXECUTE);
        chain.entity(3, AbstractCondenserBlockEntity.class).getItemHandler().setStackInSlot(0, new ItemStack(GemItems.SANGUINITE_GEM_FRAME.get()));
        helper.succeedWhen(() -> helper.assertItemEntityPresent(GemItems.ANCIENT_GEM.get(), chain.last().above(), 3.0));
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 300)
    public static void sanguiniteCondenserNeverReceivesViscousBlasphemyFromARhnullChain(GameTestHelper helper) {
        FluidChain chain = tankToCondenser(helper, MachineBlocks.RHNULL_TANK, MachineBlocks.RHNULL_PIPE, MachineBlocks.SANGUINITE_CONDENSER);
        chain.fluids(0).fill(stack(ModFluids.VISCOUS_BLASPHEMY_SOURCE, 1000), IFluidHandler.FluidAction.EXECUTE);
        AbstractCondenserBlockEntity condenser = chain.entity(3, AbstractCondenserBlockEntity.class);
        helper.runAfterDelay(100, () -> {
            helper.assertTrue(condenser.getInputTank().isEmpty(), "the sanguinite condenser received " + condenser.getInputTank().getFluid().getFluid());
            AbstractPipeBlockEntity pipe = chain.entity(2, AbstractPipeBlockEntity.class);
            helper.assertTrue(pipe.getFluidTank().getFluidAmount() > 0, "the rhnull pipe lost the fluid that the condenser refused");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 300)
    public static void wrongFluidIsNotCondensedIntoAGem(GameTestHelper helper) {
        FluidChain chain = tankToCondenser(helper, MachineBlocks.RHNULL_TANK, MachineBlocks.RHNULL_PIPE, MachineBlocks.RHNULL_CONDENSER);
        chain.fluids(0).fill(stack(ModFluids.BLOOD_SOURCE, 1000), IFluidHandler.FluidAction.EXECUTE);
        AbstractCondenserBlockEntity condenser = chain.entity(3, AbstractCondenserBlockEntity.class);
        condenser.getItemHandler().setStackInSlot(0, new ItemStack(GemItems.RHNULL_GEM_FRAME.get()));
        helper.runAfterDelay(CONDENSING_TICKS + 60, () -> {
            helper.assertItemEntityNotPresent(GemItems.ANCIENT_BLASPHEMOUS_GEM.get(), chain.last().above(), 3.0);
            helper.assertTrue(condenser.getItemHandler().getStackInSlot(0).getCount() == 1, "the rhnull frame was consumed by plain blood");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "chains", timeoutTicks = 800)
    public static void twoHarvestersFeedTheInfusorThroughPipesAndItMakesRhnull(GameTestHelper helper) {
        SanguiniteInfusorRecipe recipe = helper.getLevel().getRecipeManager().getAllRecipesFor(SanguiniteInfusorRecipe.Type.INSTANCE).stream()
                .filter(candidate -> candidate.getResultItem(helper.getLevel().registryAccess()).is(RhnullItems.RHNULL.get()))
                .findFirst().orElse(null);
        if (recipe == null) {
            helper.fail("there is no infusing recipe that makes rhnull");
            return;
        }
        FluidChain chain = FluidChain.on(helper)
                .then(MachineBlocks.SANGUINITE_BLOOD_HARVESTER)
                .pull(MachineBlocks.SANGUINITE_PIPE).then(MachineBlocks.SANGUINITE_PIPE)
                .then(MachineBlocks.SANGUINITE_INFUSOR)
                .then(MachineBlocks.SANGUINITE_PIPE).pull(MachineBlocks.SANGUINITE_PIPE)
                .then(MachineBlocks.SANGUINITE_BLOOD_HARVESTER)
                .connect();
        chain.entity(0, SanguiniteBloodHarvesterBlockEntity.class).receiveSoul(soulFor(recipe.getFluid1()), recipe.getFluid1().getAmount() + SURPLUS);
        chain.entity(6, SanguiniteBloodHarvesterBlockEntity.class).receiveSoul(soulFor(recipe.getFluid2()), recipe.getFluid2().getAmount() + SURPLUS);
        SanguiniteInfusorBlockEntity infusor = chain.entity(3, SanguiniteInfusorBlockEntity.class);
        var items = infusor.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElseThrow();
        helper.assertTrue(items.insertItem(0, recipe.getInputItem().getItems()[0].copy(), false).isEmpty(), "the infusor did not accept the sanguinite");
        helper.succeedWhen(() -> helper.assertTrue(items.getStackInSlot(1).is(RhnullItems.RHNULL.get()), "no rhnull came out of the infusor; " + chain.describe()));
    }

    private static BloodSoulType soulFor(FluidStack fluid) {
        return fluid.getFluid().isSame(ModFluids.VISCERAL_BLOOD_SOURCE.get()) ? BloodSoulType.INFECTED : BloodSoulType.BLOOD;
    }
}
