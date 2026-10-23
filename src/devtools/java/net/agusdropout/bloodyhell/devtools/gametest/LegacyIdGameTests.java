package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.event.handlers.LegacyIdRemapper;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class LegacyIdGameTests {

    @GameTestGenerator
    public static Collection<TestFunction> everyRenamedIdPointsToARegisteredReplacement() {
        List<TestFunction> tests = new ArrayList<>();
        tests.addAll(tests("item", ForgeRegistries.ITEMS, LegacyIdRemapper.ITEMS));
        tests.addAll(tests("entity", ForgeRegistries.ENTITY_TYPES, LegacyIdRemapper.ENTITY_TYPES));
        return tests;
    }

    private static <T> List<TestFunction> tests(String kind, IForgeRegistry<T> registry, Map<String, String> renames) {
        List<TestFunction> tests = new ArrayList<>();
        renames.forEach((oldPath, newPath) -> tests.add(new TestFunction("legacyids", "legacy_" + kind + "_" + oldPath, BloodyHell.MODID + ":empty", 20, 0, true, helper -> {
            ResourceLocation replacement = new ResourceLocation(BloodyHell.MODID, newPath);
            ResourceLocation legacy = new ResourceLocation(BloodyHell.MODID, oldPath);
            helper.assertTrue(registry.containsKey(replacement), kind + " " + newPath + " is not registered");
            boolean legacyIsAnAliasOrGone = !registry.containsKey(legacy) || registry.getValue(legacy) == registry.getValue(replacement);
            helper.assertTrue(legacyIsAnAliasOrGone, kind + " " + oldPath + " is registered as a separate object next to " + newPath);
            helper.succeed();
        })));
        return tests;
    }
}
