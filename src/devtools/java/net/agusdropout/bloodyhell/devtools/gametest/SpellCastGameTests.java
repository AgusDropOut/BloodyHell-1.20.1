package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonVeil;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.StaffCastType;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.StaffItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class SpellCastGameTests {
    private static final int USE_DURATION = 72000;
    private static final int SEQUENTIAL_TICKS = 30;
    private static final int TICKS_PAST_CHARGE = 5;

    private enum Route {
        STAFF_WITH_BOOK("staff_book", true),
        STAFF_WITH_OMNITOME("staff_omnitome", true),
        OMNITOME_IN_HAND("omnitome", false),
        BOOK_IN_HAND("book", false);

        private final String label;
        private final boolean viaStaff;

        Route(String label, boolean viaStaff) {
            this.label = label;
            this.viaStaff = viaStaff;
        }
    }

    @GameTestGenerator
    public static Collection<TestFunction> everySpellBookIsCastTheExpectedNumberOfTimes() {
        List<TestFunction> tests = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            if (!(item instanceof BaseSpellBookItem<?> book)) continue;
            for (Route route : Route.values()) {
                String name = "spellcast_" + route.label + "_" + book.getSpellBookId();
                tests.add(new TestFunction("spellcast", name, BloodyHell.MODID + ":empty", 60, 0, true, helper -> cast(helper, book, route)));
            }
        }
        return tests;
    }

    private static void cast(GameTestHelper helper, BaseSpellBookItem<?> book, Route route) {
        Player player = FakePlayerFactory.getMinecraft(helper.getLevel());
        player.moveTo(Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(4, 1, 4))));
        player.stopUsingItem();
        PlayerCrimsonVeil veil = player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).orElseThrow(() -> new IllegalStateException("the player has no crimson veil"));
        veil.subCrimsomveil(veil.getCrimsonVeil());
        veil.addCrimsomveil(PlayerCrimsonVeil.MAX_CRIMSOMVEIL);
        int before = veil.getCrimsonVeil();

        ItemStack held = hold(player, book, route);
        int heldTicks = book.getStaffCastType() == StaffCastType.SEQUENTIAL ? SEQUENTIAL_TICKS : book.getMinChargeTime() + TICKS_PAST_CHARGE;
        held.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        for (int used = 1; used <= heldTicks; used++) {
            held.getItem().onUseTick(helper.getLevel(), player, held, USE_DURATION - used);
        }
        held.releaseUsing(helper.getLevel(), player, USE_DURATION - heldTicks);

        int expected = expectedCasts(book, route, heldTicks);
        int spent = before - veil.getCrimsonVeil();
        helper.assertTrue(spent == expected * book.getCrimsonCost(), book.getSpellBookId() + " cast " + route.label + " for " + heldTicks + " ticks spent " + spent
                + " crimson veil but " + expected + " casts of " + book.getCrimsonCost() + " cost " + expected * book.getCrimsonCost());
        player.stopUsingItem();
        helper.succeed();
    }

    private static ItemStack hold(Player player, BaseSpellBookItem<?> book, Route route) {
        ItemStack bookStack = new ItemStack(book);
        ItemStack omnitome = new ItemStack(SpellBookItems.THE_OMNITOME.get());
        omnitome.getOrCreateTag().put(OmnitomeItem.NBT_ACTIVE_SPELL, book.serializeBookData(bookStack));
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        switch (route) {
            case STAFF_WITH_BOOK -> {
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(StaffItems.STAFF_OF_SCARLET_CINDER.get()));
                player.setItemInHand(InteractionHand.OFF_HAND, bookStack);
            }
            case STAFF_WITH_OMNITOME -> {
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(StaffItems.STAFF_OF_SCARLET_CINDER.get()));
                player.setItemInHand(InteractionHand.OFF_HAND, omnitome);
            }
            case OMNITOME_IN_HAND -> player.setItemInHand(InteractionHand.MAIN_HAND, omnitome);
            case BOOK_IN_HAND -> player.setItemInHand(InteractionHand.MAIN_HAND, bookStack);
        }
        return player.getItemInHand(InteractionHand.MAIN_HAND);
    }

    private static int expectedCasts(BaseSpellBookItem<?> book, Route route, int heldTicks) {
        StaffCastType castType = book.getStaffCastType();
        if (castType == StaffCastType.SEQUENTIAL) {
            int casts = 0;
            for (int tick = book.getMinChargeTime(); tick <= heldTicks; tick += book.getCycleTicks()) {
                casts++;
            }
            return casts;
        }
        return 1;
    }
}
