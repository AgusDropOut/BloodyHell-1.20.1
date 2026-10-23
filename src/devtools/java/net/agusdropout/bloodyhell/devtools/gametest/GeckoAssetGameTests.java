package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoArmorItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.GeckoAsset;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@net.minecraftforge.gametest.GameTestHolder(BloodyHell.MODID)
@net.minecraftforge.gametest.PrefixGameTestTemplate(false)
public class GeckoAssetGameTests {

    @GameTestGenerator
    public static Collection<TestFunction> everyGeckoItemHasTheAssetsOfItsId() {
        List<TestFunction> tests = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            if (!(item instanceof GeckoAsset asset) || item instanceof OmnitomeItem) continue;
            String id = asset.getId();
            tests.add(new TestFunction("geckoassets", "gecko_assets_" + ForgeRegistries.ITEMS.getKey(item).getPath(), BloodyHell.MODID + ":empty", 20, 0, true, helper -> {
                require(helper, "geo/" + id + ".geo.json", id);
                require(helper, (item instanceof BaseGeckoArmorItem ? "textures/armor/" : "textures/item/") + id + ".png", id);
                boolean unanimatedArmor = item instanceof BaseGeckoArmorItem armor && !armor.getLook().animated();
                if (!(item instanceof BaseSpellBookItem) && !unanimatedArmor) {
                    require(helper, "animations/" + id + ".animation.json", id);
                }
                helper.succeed();
            }));
        }
        return tests;
    }

    private static void require(net.minecraft.gametest.framework.GameTestHelper helper, String path, String id) {
        helper.assertTrue(GeckoAssetGameTests.class.getClassLoader().getResource("assets/" + BloodyHell.MODID + "/" + path) != null,
                "the gecko item " + id + " has no asset " + path);
    }
}
