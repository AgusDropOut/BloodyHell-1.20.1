package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.event.handlers.LegacyIdRemapper;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class LegacyIdGameTests {

    @GameTest(template = "empty", batch = "legacy-ids", timeoutTicks = 20)
    public static void everyLegacyItemIdPointsToAnItemThatExists(GameTestHelper helper) {
        for (Map.Entry<String, String> entry : LegacyIdRemapper.ITEMS.entrySet()) {
            ResourceLocation target = new ResourceLocation(BloodyHell.MODID, entry.getValue());
            helper.assertTrue(ForgeRegistries.ITEMS.containsKey(target), "the legacy id " + entry.getKey() + " points to " + target + ", which is not an item");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "legacy-ids", timeoutTicks = 20)
    public static void noLegacyItemIdIsStillRegistered(GameTestHelper helper) {
        for (String legacy : LegacyIdRemapper.ITEMS.keySet()) {
            ResourceLocation id = new ResourceLocation(BloodyHell.MODID, legacy);
            helper.assertFalse(ForgeRegistries.ITEMS.containsKey(id), "the legacy id " + id + " is registered again, so the remap would hide a real item");
        }
        helper.succeed();
    }
}
