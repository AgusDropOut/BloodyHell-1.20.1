package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonVeil;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.StaffItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class SpellCastGameTests {
    private static final int USE_DURATION = 72000;
    private static final int TICKS = 30;

    @GameTest(template = "empty", batch = "spellcast", required = false)
    public static void sphereThroughTheStaffCastsOncePerCycle(GameTestHelper helper) {
        castsOncePerCycle(helper, (BaseSpellBookItem<?>) SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get(), true);
    }

    @GameTest(template = "empty", batch = "spellcast", required = false)
    public static void sphereInHandCastsOncePerCycle(GameTestHelper helper) {
        castsOncePerCycle(helper, (BaseSpellBookItem<?>) SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get(), false);
    }

    @GameTest(template = "empty", batch = "spellcast", required = false)
    public static void scratchThroughTheStaffCastsOncePerCycle(GameTestHelper helper) {
        castsOncePerCycle(helper, (BaseSpellBookItem<?>) SpellBookItems.BLOOD_SCRATCH_SPELLBOOK.get(), true);
    }

    @GameTest(template = "empty", batch = "spellcast", required = false)
    public static void scratchInHandCastsOncePerCycle(GameTestHelper helper) {
        castsOncePerCycle(helper, (BaseSpellBookItem<?>) SpellBookItems.BLOOD_SCRATCH_SPELLBOOK.get(), false);
    }

    private static void castsOncePerCycle(GameTestHelper helper, BaseSpellBookItem<?> book, boolean throughStaff) {
        Player player = FakePlayerFactory.getMinecraft(helper.getLevel());
        player.moveTo(Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(4, 1, 4))));
        PlayerCrimsonVeil veil = player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).orElseThrow(() -> new IllegalStateException("the mock player has no crimson veil"));
        veil.subCrimsomveil(veil.getCrimsonVeil());
        veil.addCrimsomveil(PlayerCrimsonVeil.MAX_CRIMSOMVEIL);
        player.stopUsingItem();
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        int before = veil.getCrimsonVeil();

        ItemStack bookStack = new ItemStack(book);
        Item staff = StaffItems.STAFF_OF_SCARLET_CINDER.get();
        ItemStack staffStack = new ItemStack(staff);
        if (throughStaff) {
            player.setItemInHand(InteractionHand.MAIN_HAND, staffStack);
            player.setItemInHand(InteractionHand.OFF_HAND, bookStack);
        } else {
            player.setItemInHand(InteractionHand.MAIN_HAND, bookStack);
        }

        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        held.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        for (int used = 1; used <= TICKS; used++) {
            held.getItem().onUseTick(helper.getLevel(), player, held, USE_DURATION - used);
        }

        int cost = book.getCrimsonCost();
        int cycle = Math.max(1, book.getMinChargeTime()) + Math.max(0, book.getCooldown());
        int expectedCasts = 0;
        for (int tick = book.getMinChargeTime(); tick <= TICKS; tick += cycle) {
            if (tick > 0) expectedCasts++;
        }
        int spent = before - veil.getCrimsonVeil();
        helper.assertTrue(spent == expectedCasts * cost, book.getSpellBookId() + (throughStaff ? " through the staff" : " in hand") + " spent " + spent + " crimson veil in " + TICKS + " ticks but " + expectedCasts + " casts of " + cost + " cost " + expectedCasts * cost);
        helper.succeed();
    }
}
