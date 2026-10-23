package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.base.AbstractPipeBlock;
import net.agusdropout.bloodyhell.block.entity.base.AbstractPipeBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.SanguiniteBloodHarvesterBlockEntity;
import net.agusdropout.bloodyhell.block.registry.FluidBlocks;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulType;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
public class FluidMachineGameTests {
    private static final String TEMPLATE = "empty";
    private static final BlockPos HERE = new BlockPos(4, 1, 4);
    private static final BlockPos NEXT = HERE.east();

    private static IFluidHandler fluids(GameTestHelper helper, BlockPos pos) {
        BlockEntity entity = helper.getBlockEntity(pos);
        if (entity == null) {
            helper.fail("there is no block entity", pos);
            throw new IllegalStateException("no block entity");
        }
        return entity.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().orElseThrow();
    }

    private static FluidStack stack(Supplier<? extends Fluid> fluid, int amount) {
        return new FluidStack(fluid.get(), amount);
    }

    private static void harvesterGets(GameTestHelper helper, BloodSoulType type, Supplier<? extends Fluid> expected) {
        helper.setBlock(HERE, MachineBlocks.SANGUINITE_BLOOD_HARVESTER.get());
        SanguiniteBloodHarvesterBlockEntity harvester = (SanguiniteBloodHarvesterBlockEntity) helper.getBlockEntity(HERE);
        harvester.receiveSoul(type, 250);
        FluidStack held = harvester.getInputTank().getFluid();
        helper.assertTrue(held.getFluid() == expected.get() && held.getAmount() == 250,
                "a " + type + " soul filled " + held.getAmount() + " of " + held.getFluid() + " instead of 250 of the expected fluid");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 40)
    public static void harvesterTurnsABloodSoulIntoBlood(GameTestHelper helper) {
        harvesterGets(helper, BloodSoulType.BLOOD, ModFluids.BLOOD_SOURCE);
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 40)
    public static void harvesterTurnsACorruptedSoulIntoCorruptedBlood(GameTestHelper helper) {
        harvesterGets(helper, BloodSoulType.CORRUPTED, ModFluids.CORRUPTED_BLOOD_SOURCE);
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 40)
    public static void harvesterTurnsAnInfectedSoulIntoVisceralBlood(GameTestHelper helper) {
        harvesterGets(helper, BloodSoulType.INFECTED, ModFluids.VISCERAL_BLOOD_SOURCE);
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 100)
    public static void harvesterFeedsAnAdjacentTank(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.SANGUINITE_BLOOD_HARVESTER.get());
        helper.setBlock(NEXT, MachineBlocks.SANGUINITE_TANK.get());
        ((SanguiniteBloodHarvesterBlockEntity) helper.getBlockEntity(HERE)).receiveSoul(BloodSoulType.BLOOD, 1000);
        helper.succeedWhen(() -> helper.assertTrue(fluids(helper, NEXT).getFluidInTank(0).getAmount() > 0, "the tank next to the harvester stayed empty"));
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 40)
    public static void sanguiniteTankRejectsCorruptedBlood(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.SANGUINITE_TANK.get());
        helper.assertTrue(fluids(helper, HERE).fill(stack(ModFluids.CORRUPTED_BLOOD_SOURCE, 500), IFluidHandler.FluidAction.EXECUTE) == 0,
                "the sanguinite tank accepted corrupted blood");
        helper.assertTrue(fluids(helper, HERE).fill(stack(ModFluids.BLOOD_SOURCE, 500), IFluidHandler.FluidAction.EXECUTE) == 500,
                "the sanguinite tank rejected plain blood");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 40)
    public static void rhnullTankAcceptsEveryFluid(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.RHNULL_TANK.get());
        IFluidHandler tank = fluids(helper, HERE);
        for (Supplier<? extends Fluid> fluid : new Supplier[]{ModFluids.BLOOD_SOURCE, ModFluids.CORRUPTED_BLOOD_SOURCE, ModFluids.VISCOUS_BLASPHEMY_SOURCE, ModFluids.VISCERAL_BLOOD_SOURCE}) {
            tank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            helper.assertTrue(tank.fill(stack(fluid, 500), IFluidHandler.FluidAction.EXECUTE) == 500, "the rhnull tank rejected " + fluid.get());
        }
        helper.succeed();
    }

    private static void pipeTo(GameTestHelper helper, RegistryObject<Block> pipe) {
        BlockState connected = pipe.get().defaultBlockState().setValue(AbstractPipeBlock.EAST, true);
        helper.setBlock(HERE, connected);
        helper.setBlock(NEXT, MachineBlocks.RHNULL_TANK.get());
        AbstractPipeBlockEntity entity = (AbstractPipeBlockEntity) helper.getBlockEntity(HERE);
        entity.getFluidTank().fill(stack(ModFluids.BLOOD_SOURCE, 1000), IFluidHandler.FluidAction.EXECUTE);
        helper.succeedWhen(() -> helper.assertTrue(fluids(helper, NEXT).getFluidInTank(0).getAmount() > 0, "the fluid did not cross the pipe into the tank"));
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 200)
    public static void sanguinitePipeMovesBloodIntoATank(GameTestHelper helper) {
        pipeTo(helper, MachineBlocks.SANGUINITE_PIPE);
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 200)
    public static void rhnullPipeMovesBloodIntoATank(GameTestHelper helper) {
        pipeTo(helper, MachineBlocks.RHNULL_PIPE);
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 200)
    public static void sanguinitePipeBurstsWithCorruptedBlood(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.SANGUINITE_PIPE.get());
        AbstractPipeBlockEntity entity = (AbstractPipeBlockEntity) helper.getBlockEntity(HERE);
        entity.getFluidTank().setFluid(stack(ModFluids.CORRUPTED_BLOOD_SOURCE, 500));
        helper.succeedWhen(() -> helper.assertBlockPresent(FluidBlocks.CORRUPTED_BLOOD_BLOCK.get(), HERE));
    }

    @GameTest(template = TEMPLATE, batch = "fluids", timeoutTicks = 200)
    public static void rhnullPipeHoldsCorruptedBlood(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.RHNULL_PIPE.get());
        AbstractPipeBlockEntity entity = (AbstractPipeBlockEntity) helper.getBlockEntity(HERE);
        entity.getFluidTank().setFluid(stack(ModFluids.CORRUPTED_BLOOD_SOURCE, 500));
        helper.runAfterDelay(120, () -> {
            helper.assertBlockPresent(MachineBlocks.RHNULL_PIPE.get(), HERE);
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "fluids-design", timeoutTicks = 40, required = false)
    public static void sanguiniteTankRejectsViscousBlasphemy(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.SANGUINITE_TANK.get());
        helper.assertTrue(fluids(helper, HERE).fill(stack(ModFluids.VISCOUS_BLASPHEMY_SOURCE, 500), IFluidHandler.FluidAction.EXECUTE) == 0,
                "the sanguinite tank accepted viscous blasphemy (docs/DESIGN.md says sanguinite cannot hold it)");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "fluids-design", timeoutTicks = 200, required = false)
    public static void sanguinitePipeBurstsWithViscousBlasphemy(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.SANGUINITE_PIPE.get());
        AbstractPipeBlockEntity entity = (AbstractPipeBlockEntity) helper.getBlockEntity(HERE);
        entity.getFluidTank().setFluid(stack(ModFluids.VISCOUS_BLASPHEMY_SOURCE, 500));
        helper.succeedWhen(() -> helper.assertTrue(!helper.getBlockState(HERE).is(MachineBlocks.SANGUINITE_PIPE.get()),
                "the sanguinite pipe survived viscous blasphemy (docs/DESIGN.md says sanguinite cannot hold it)"));
    }
}
