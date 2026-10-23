package net.agusdropout.bloodyhell.devtools.gametest;

import com.mojang.authlib.GameProfile;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.StaffItems;
import net.agusdropout.bloodyhell.util.capability.CrimsonVeilHelper;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class CrimsonVeilHudGameTests {

    @GameTestGenerator
    public static Collection<TestFunction> theHudShowsForEveryItemThatCanCast() {
        List<TestFunction> tests = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            boolean castsFromAnyHand = item instanceof BaseSpellBookItem || item instanceof OmnitomeItem;
            if (!castsFromAnyHand && !(item instanceof BaseStaffItem)) continue;
            String name = ForgeRegistries.ITEMS.getKey(item).getPath();
            tests.add(new TestFunction("crimsonveilhud", "hud_main_hand_" + name, BloodyHell.MODID + ":empty", 20, 0, true,
                    helper -> assertShown(helper, item, InteractionHand.MAIN_HAND, true)));
            if (castsFromAnyHand) {
                tests.add(new TestFunction("crimsonveilhud", "hud_off_hand_" + name, BloodyHell.MODID + ":empty", 20, 0, true,
                        helper -> assertShown(helper, item, InteractionHand.OFF_HAND, true)));
            }
        }
        return tests;
    }

    @GameTest(template = "empty", batch = "crimsonveilhud")
    public static void theHudIsHiddenWithNothingThatCasts(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        helper.assertTrue(!CrimsonVeilHelper.isHoldingConsumer(player), "the hud shows with empty hands");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_PICKAXE));
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD));
        helper.assertTrue(!CrimsonVeilHelper.isHoldingConsumer(player), "the hud shows with a pickaxe and a shield");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "crimsonveilhud")
    public static void theHudShowsWithAStaffAndABook(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(StaffItems.STAFF_OF_SCARLET_CINDER.get()));
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(SpellBookItems.BLOOD_NOVA_SPELLBOOK.get()));
        helper.assertTrue(CrimsonVeilHelper.isHoldingConsumer(player), "the hud is hidden with a staff and a book");
        helper.succeed();
    }

    private static void assertShown(GameTestHelper helper, Item item, InteractionHand hand, boolean expected) {
        Player player = freshPlayer(helper);
        player.setItemInHand(hand, new ItemStack(item));
        helper.assertTrue(CrimsonVeilHelper.isHoldingConsumer(player) == expected,
                ForgeRegistries.ITEMS.getKey(item) + " in the " + hand + " should " + (expected ? "" : "not ") + "show the crimson veil hud");
        helper.succeed();
    }

    private static Player freshPlayer(GameTestHelper helper) {
        Player player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "[bloodyhell_hud]"));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        return player;
    }
}
