package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.devtools.BlockAudit;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class BlockDropGameTests {

    @GameTestGenerator
    public static Collection<TestFunction> everyBlockDropsAndBreaksAsItsRuleSays() {
        List<TestFunction> tests = new ArrayList<>();
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            if (!id.getNamespace().equals(BloodyHell.MODID)) continue;
            tests.add(new TestFunction("blockdrops", "block_drops_" + id.getPath(), BloodyHell.MODID + ":empty", 20, 0, true, helper -> {
                List<String> violations = BlockAudit.violations(helper.getLevel(), block);
                helper.assertTrue(violations.isEmpty(), id.getPath() + " " + String.join("; ", violations));
                helper.succeed();
            }));
        }
        return tests;
    }
}
