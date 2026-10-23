package net.agusdropout.bloodyhell.devtools.gametest;

import com.mojang.authlib.GameProfile;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.base.BaseGemSproutBlock;
import net.agusdropout.bloodyhell.block.entity.custom.plant.BloodGemSproutBlockEntity;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.block.registry.MiscBlocks;
import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.agusdropout.bloodyhell.item.registry.GemItems;
import net.agusdropout.bloodyhell.item.registry.SanguiniteItems;
import net.agusdropout.bloodyhell.item.registry.WeaponItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class ProgressionRootsGameTests {
    private static final BlockPos HERE = new BlockPos(4, 1, 4);
    private static final int GROWTH_STEPS = 2_000_000;
    private static final int CAPSULE_ATTEMPTS = 400;

    @GameTest(template = "empty", batch = "roots_blood_flask")
    public static void anEmptyFlaskTakesBloodFromATank(GameTestHelper helper) {
        fillsAFlask(helper, ModFluids.BLOOD_SOURCE, FlaskItems.FILLED_BLOOD_FLASK);
    }

    @GameTest(template = "empty", batch = "roots_corrupted_flask")
    public static void anEmptyFlaskTakesCorruptedBloodFromATank(GameTestHelper helper) {
        fillsAFlask(helper, ModFluids.CORRUPTED_BLOOD_SOURCE, FlaskItems.CORRUPTED_BLOOD_FLASK);
    }

    @GameTest(template = "empty", batch = "roots_viscous_flask")
    public static void anEmptyFlaskTakesViscousBlasphemyFromATank(GameTestHelper helper) {
        fillsAFlask(helper, ModFluids.VISCOUS_BLASPHEMY_SOURCE, FlaskItems.FILLED_VISCOUS_BLASPHEMY_FLASK);
    }

    @GameTest(template = "empty", batch = "roots_pure_gem", timeoutTicks = 200)
    public static void aSproutGrowsAPureBloodGemFromSanguinite(GameTestHelper helper) {
        growsAGem(helper, SanguiniteItems.SANGUINITE.get(), GemItems.PURE_BLOOD_GEM.get());
    }

    @GameTest(template = "empty", batch = "roots_aventurine_gem", timeoutTicks = 200)
    public static void aSproutGrowsAnAventurineGemFromLapis(GameTestHelper helper) {
        growsAGem(helper, Items.LAPIS_LAZULI, GemItems.AVENTURINE_BLOOD_GEM.get());
    }

    @GameTest(template = "empty", batch = "roots_citrine_gem", timeoutTicks = 200)
    public static void aSproutGrowsACitrineGemFromAGoldNugget(GameTestHelper helper) {
        growsAGem(helper, Items.GOLD_NUGGET, GemItems.CITRINE_BLOOD_GEM.get());
    }

    @GameTest(template = "empty", batch = "roots_tanzarine_gem", timeoutTicks = 200)
    public static void aSproutGrowsATanzarineGemFromAnAmethystShard(GameTestHelper helper) {
        growsAGem(helper, Items.AMETHYST_SHARD, GemItems.TANZARINE_BLOOD_GEM.get());
    }

    @GameTest(template = "empty", batch = "roots_seed", timeoutTicks = 100)
    public static void theSeedOfTheSproutPlacesASprout(GameTestHelper helper) {
        helper.setBlock(HERE, net.agusdropout.bloodyhell.block.registry.SoilBlocks.BLOOD_DIRT_BLOCK.get());
        Player player = player(helper);
        ItemStack seed = new ItemStack(net.agusdropout.bloodyhell.item.registry.PlaceableItems.BLOOD_GEM_SPROUT_SEED.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, seed);
        BlockPos soil = helper.absolutePos(HERE);
        seed.useOn(new net.minecraft.world.item.context.UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, seed,
                new BlockHitResult(Vec3.atCenterOf(soil).add(0, 0.5, 0), Direction.UP, soil, false)));
        helper.assertTrue(helper.getBlockState(HERE.above()).is(MachineBlocks.BLOOD_GEM_SPROUT.get()), "the seed did not place a sprout on blood dirt; the block above is " + helper.getBlockState(HERE.above()));
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "roots_finger", timeoutTicks = 400)
    public static void breakingAnAncientBloodCapsuleEventuallyDropsTheFinger(GameTestHelper helper) {
        AABB area = new AABB(helper.absolutePos(BlockPos.ZERO), helper.absolutePos(new BlockPos(9, 6, 9))).inflate(1.0);
        Item finger = WeaponItems.UNKNOWN_ENTITY_FINGER.get();
        for (int attempt = 1; attempt <= CAPSULE_ATTEMPTS; attempt++) {
            helper.setBlock(HERE, MiscBlocks.ANCIENT_BLOOD_CAPSULE.get());
            helper.setBlock(HERE, Blocks.AIR);
            helper.getLevel().getEntitiesOfClass(OffspringOfTheUnknownEntity.class, area).forEach(Entity::discard);
            boolean dropped = !helper.getLevel().getEntitiesOfClass(ItemEntity.class, area, entity -> entity.getItem().is(finger)).isEmpty();
            if (dropped) {
                helper.succeed();
                return;
            }
        }
        helper.fail("no unknown entity finger in " + CAPSULE_ATTEMPTS + " capsules broken");
    }

    @GameTest(template = "empty", batch = "roots_bound_flask")
    public static void theSacrificialDaggerBindsAnEmptyFlask(GameTestHelper helper) {
        Player player = player(helper);
        ItemStack dagger = new ItemStack(WeaponItems.SACRIFICIAL_DAGGER.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, dagger);
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(FlaskItems.BLOOD_FLASK.get()));
        dagger.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        boolean bound = player.getInventory().items.stream().anyMatch(stack -> stack.is(FlaskItems.BOUND_BLOOD_FLASK.get()));
        helper.assertTrue(bound, "the dagger did not give a bound blood flask");
        helper.succeed();
    }

    private static Player player(GameTestHelper helper) {
        Player player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "[bloodyhell_roots]"));
        player.moveTo(Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(2, 1, 2))));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        return player;
    }

    private static BlockHitResult hit(GameTestHelper helper) {
        BlockPos absolute = helper.absolutePos(HERE);
        return new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);
    }

    private static void fillsAFlask(GameTestHelper helper, Supplier<? extends Fluid> fluid, Supplier<Item> expected) {
        helper.setBlock(HERE, MachineBlocks.RHNULL_TANK.get());
        IFluidHandler tank = helper.getBlockEntity(HERE).getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().orElseThrow();
        tank.fill(new FluidStack(fluid.get(), 250), IFluidHandler.FluidAction.EXECUTE);
        Player player = player(helper);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(FlaskItems.BLOOD_FLASK.get()));
        BlockState state = helper.getBlockState(HERE);
        state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit(helper));
        helper.assertTrue(player.getMainHandItem().is(expected.get()), "an empty flask used on a tank with " + fluid.get() + " gave " + player.getMainHandItem());
        helper.succeed();
    }

    private static void growsAGem(GameTestHelper helper, Item ingredient, Item expectedGem) {
        helper.setBlock(HERE, MachineBlocks.BLOOD_GEM_SPROUT.get());
        BloodGemSproutBlockEntity sprout = (BloodGemSproutBlockEntity) helper.getBlockEntity(HERE);
        Player player = player(helper);
        BlockPos absolute = helper.absolutePos(HERE);
        boolean inserted = false;
        for (int step = 0; step < GROWTH_STEPS; step++) {
            BlockState state = helper.getLevel().getBlockState(absolute);
            int age = state.getValue(BaseGemSproutBlock.AGE);
            if (age == BaseGemSproutBlock.MAX_AGE) break;
            if (age == net.agusdropout.bloodyhell.block.entity.base.BaseGemSproutBlockEntity.INSERT_GEM_AGE && !inserted) {
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ingredient));
                state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit(helper));
                inserted = true;
                continue;
            }
            sprout.fillBlood(new FluidStack(ModFluids.BLOOD_SOURCE.get(), 2000), false);
            sprout.tick(helper.getLevel(), absolute, state);
        }
        BlockState grown = helper.getLevel().getBlockState(absolute);
        helper.assertTrue(grown.getValue(BaseGemSproutBlock.AGE) == BaseGemSproutBlock.MAX_AGE, "the sprout fed with " + ingredient + " stopped growing at age " + grown.getValue(BaseGemSproutBlock.AGE));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        grown.use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit(helper));
        AABB area = new AABB(absolute).inflate(2.0);
        List<ItemEntity> items = helper.getLevel().getEntitiesOfClass(ItemEntity.class, area, entity -> entity.getItem().is(expectedGem));
        helper.assertTrue(!items.isEmpty(), "harvesting the sprout fed with " + ingredient + " did not drop " + expectedGem);
        helper.succeed();
    }
}
