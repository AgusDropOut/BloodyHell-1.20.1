package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class OffspringGameTests {
    private static final float HURT_FRACTION = 0.2f;
    private static final double FLEE_DISTANCE_GAIN = 4.0;

    @GameTest(template = "empty", batch = "offspring", timeoutTicks = 160)
    public static void aBadlyHurtOffspringMovesAwayFromItsTarget(GameTestHelper helper) {
        Pig target = helper.spawn(EntityType.PIG, new BlockPos(6, 1, 4));
        target.setNoAi(true);
        OffspringOfTheUnknownEntity offspring = helper.spawn(ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN.get(), new BlockPos(3, 1, 4));
        offspring.setHealth(offspring.getMaxHealth() * HURT_FRACTION);
        offspring.setTarget(target);
        double startDistance = offspring.distanceTo(target);
        helper.succeedWhen(() -> helper.assertTrue(offspring.distanceTo(target) >= startDistance + FLEE_DISTANCE_GAIN,
                "the hurt offspring stayed at " + offspring.distanceTo(target) + " blocks from its target, it started at " + startDistance));
    }

    @GameTest(template = "empty", batch = "offspring", timeoutTicks = 160)
    public static void aHealthyOffspringStaysCloseToItsTarget(GameTestHelper helper) {
        Pig target = helper.spawn(EntityType.PIG, new BlockPos(6, 1, 4));
        target.setNoAi(true);
        OffspringOfTheUnknownEntity offspring = helper.spawn(ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN.get(), new BlockPos(3, 1, 4));
        offspring.setTarget(target);
        double startDistance = offspring.distanceTo(target);
        double[] farthest = {startDistance};
        helper.onEachTick(() -> farthest[0] = Math.max(farthest[0], offspring.distanceTo(target)));
        helper.runAfterDelay(120, () -> {
            helper.assertTrue(farthest[0] < startDistance + FLEE_DISTANCE_GAIN, "the healthy offspring went as far as " + farthest[0] + " blocks from its target, it started at " + startDistance);
            helper.succeed();
        });
    }
}
