package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.UnknownPortalBlockEntity;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.entity.custom.HostileUnknownEntityArms;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class PortalMechanismGameTests {
    private static final String TEMPLATE = "empty";
    private static final String BATCH = "portal-mechanism";
    private static final BlockPos HERE = new BlockPos(4, 1, 4);
    private static final int TICKS_TO_OPEN = 200;

    private static UnknownPortalBlockEntity portal(GameTestHelper helper) {
        helper.getLevel().getEntitiesOfClass(HostileUnknownEntityArms.class, new AABB(helper.absolutePos(HERE)).inflate(6)).forEach(HostileUnknownEntityArms::discard);
        helper.setBlock(HERE, MachineBlocks.UNKNOWN_PORTAL_BLOCK.get());
        return (UnknownPortalBlockEntity) helper.getBlockEntity(HERE);
    }

    private static void tick(GameTestHelper helper, UnknownPortalBlockEntity portal, int times) {
        BlockPos absolute = helper.absolutePos(HERE);
        for (int i = 0; i < times; i++) {
            UnknownPortalBlockEntity.serverTick(helper.getLevel(), absolute, helper.getLevel().getBlockState(absolute), portal);
        }
    }

    private static int arms(GameTestHelper helper) {
        return helper.getLevel().getEntitiesOfClass(HostileUnknownEntityArms.class, new AABB(helper.absolutePos(HERE)).inflate(3)).size();
    }

    private static void fillBlood(UnknownPortalBlockEntity portal, int amount) {
        portal.inputTank.fill(new FluidStack(ModFluids.BLOOD_SOURCE.get(), amount), IFluidHandler.FluidAction.EXECUTE);
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 100)
    public static void portalOnlyTakesBloodAndOnlyGivesBlasphemy(GameTestHelper helper) {
        UnknownPortalBlockEntity portal = portal(helper);
        helper.assertTrue(portal.inputTank.fill(new FluidStack(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 500), IFluidHandler.FluidAction.EXECUTE) == 0, "the input accepted viscous blasphemy");
        helper.assertTrue(portal.inputTank.fill(new FluidStack(ModFluids.BLOOD_SOURCE.get(), 500), IFluidHandler.FluidAction.EXECUTE) == 500, "the input rejected blood");
        helper.assertTrue(portal.outputTank.fill(new FluidStack(ModFluids.BLOOD_SOURCE.get(), 500), IFluidHandler.FluidAction.EXECUTE) == 0, "the output accepted blood");
        helper.assertTrue(portal.outputTank.fill(new FluidStack(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 500), IFluidHandler.FluidAction.EXECUTE) == 500, "the output rejected viscous blasphemy");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 100)
    public static void portalOpensSlowlyAndSummonsFourArmsWhenOpen(GameTestHelper helper) {
        UnknownPortalBlockEntity portal = portal(helper);
        fillBlood(portal, 4000);
        tick(helper, portal, TICKS_TO_OPEN / 2);
        helper.assertTrue(portal.portalProgress == 50.0f, "half the time should open half the portal, it is at " + portal.portalProgress);
        helper.assertTrue(arms(helper) == 0, "arms appeared before the portal was open");
        tick(helper, portal, TICKS_TO_OPEN / 2);
        helper.assertTrue(portal.portalProgress == 100.0f, "the portal did not open, it is at " + portal.portalProgress);
        helper.assertTrue(arms(helper) == UnknownPortalBlockEntity.MAX_SUMMONED_ARMS, "an open portal should hold " + UnknownPortalBlockEntity.MAX_SUMMONED_ARMS + " arms, it holds " + arms(helper));
        helper.assertTrue(portal.inputTank.getFluidAmount() == 4000 - 5 * TICKS_TO_OPEN, "opening should cost 5 mB per tick, " + portal.inputTank.getFluidAmount() + " mB are left");
        portal.destroySummonedArms(helper.getLevel());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 100)
    public static void portalDoesNotSummonMoreArmsWhileOpen(GameTestHelper helper) {
        UnknownPortalBlockEntity portal = portal(helper);
        fillBlood(portal, 4000);
        tick(helper, portal, TICKS_TO_OPEN);
        int opened = arms(helper);
        tick(helper, portal, 20);
        helper.assertTrue(opened == UnknownPortalBlockEntity.MAX_SUMMONED_ARMS && arms(helper) == opened, "the portal had " + opened + " arms when it opened and " + arms(helper) + " twenty ticks later");
        portal.destroySummonedArms(helper.getLevel());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 100)
    public static void portalClosesAndDropsItsArmsWhenBloodRunsOut(GameTestHelper helper) {
        UnknownPortalBlockEntity portal = portal(helper);
        fillBlood(portal, 1000);
        tick(helper, portal, TICKS_TO_OPEN);
        helper.assertTrue(arms(helper) == UnknownPortalBlockEntity.MAX_SUMMONED_ARMS, "the portal did not open with exactly the blood it needs");
        tick(helper, portal, 1);
        helper.assertTrue(portal.portalProgress < 100.0f, "the portal stayed open without blood");
        helper.assertTrue(arms(helper) == 0, "the arms stayed after the portal started closing");
        tick(helper, portal, TICKS_TO_OPEN);
        helper.assertTrue(portal.portalProgress == 0.0f, "the portal did not close completely, it is at " + portal.portalProgress);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 100)
    public static void portalKeepsItsStateThroughSaving(GameTestHelper helper) {
        UnknownPortalBlockEntity portal = portal(helper);
        fillBlood(portal, 800);
        tick(helper, portal, 30);
        portal.outputTank.fill(new FluidStack(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 300), IFluidHandler.FluidAction.EXECUTE);
        CompoundTag saved = portal.saveWithoutMetadata();

        UnknownPortalBlockEntity loaded = new UnknownPortalBlockEntity(helper.absolutePos(HERE), helper.getBlockState(HERE));
        loaded.load(saved);

        helper.assertTrue(loaded.portalProgress == portal.portalProgress, "the progress changed through saving");
        helper.assertTrue(loaded.inputTank.getFluidAmount() == portal.inputTank.getFluidAmount(), "the blood changed through saving");
        helper.assertTrue(loaded.outputTank.getFluidAmount() == 300, "the blasphemy changed through saving");
        helper.succeed();
    }
}
