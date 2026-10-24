package net.agusdropout.bloodyhell.devtools.gametest;

import com.mojang.authlib.GameProfile;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.staffs.StaffOfTheGoldenSorrowItem;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.StaffItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class StaffKillStreakGameTests {
    private static final String TEMPLATE = "empty";
    private static final String BATCH = "kill-streak";

    private static Player freshPlayer(GameTestHelper helper) {
        return FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "streak"));
    }

    private static StaffOfTheGoldenSorrowItem golden() {
        return (StaffOfTheGoldenSorrowItem) StaffItems.STAFF_OF_THE_GOLDEN_SORROW.get();
    }

    private static void kill(GameTestHelper helper, Player player, int index) {
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1 + index % 6, 1, 1));
        pig.hurt(helper.getLevel().damageSources().playerAttack(player), 1000.0f);
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 40)
    public static void aKillWithTheStaffInHandAddsOneCharge(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        ItemStack staff = new ItemStack(golden());
        player.setItemInHand(InteractionHand.MAIN_HAND, staff);
        kill(helper, player, 0);
        helper.assertTrue(golden().getCharge(staff) == 1, "the kill gave " + golden().getCharge(staff) + " charge instead of 1");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 40)
    public static void aKillWithTheStaffInTheOffHandCounts(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        ItemStack staff = new ItemStack(golden());
        player.setItemInHand(InteractionHand.OFF_HAND, staff);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        kill(helper, player, 0);
        helper.assertTrue(golden().getCharge(staff) == 1, "a kill with the staff in the off hand did not count");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 40)
    public static void aKillWithoutTheStaffInHandDoesNotCount(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        ItemStack staff = new ItemStack(golden());
        player.getInventory().setItem(5, staff);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        kill(helper, player, 0);
        helper.assertTrue(golden().getCharge(staff) == 0, "a kill with the staff in the inventory counted");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 40)
    public static void theChargeStopsAtFull(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        ItemStack staff = new ItemStack(golden());
        player.setItemInHand(InteractionHand.MAIN_HAND, staff);
        for (int i = 0; i < 8; i++) kill(helper, player, i);
        helper.assertTrue(golden().getCharge(staff) == 5, "eight kills gave " + golden().getCharge(staff) + " charge instead of 5");
        helper.assertTrue(golden().getBonusFill(staff) == 1.0f, "the fill is not full");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 40)
    public static void aFullStreakGivesTheStaffBonus(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        ItemStack staff = new ItemStack(golden());
        player.setItemInHand(InteractionHand.MAIN_HAND, staff);
        for (int i = 0; i < 5; i++) kill(helper, player, i);
        BaseSpellBookItem book = (BaseSpellBookItem) SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get();
        float damage = golden().modifiersFor(staff, book).damage();
        helper.assertTrue(damage > 1.0f, "a full streak gave no damage bonus: " + damage);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 40)
    public static void theStreakFadesWithTimeInTheInventory(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        ItemStack staff = new ItemStack(golden());
        long now = helper.getLevel().getGameTime();
        for (int i = 0; i < 5; i++) golden().registerKill(staff, now - 200);
        helper.assertTrue(golden().getCharge(staff) == 5, "the setup did not reach a full streak");
        golden().inventoryTick(staff, helper.getLevel(), player, 0, false);
        helper.assertTrue(golden().getCharge(staff) == 3, "100 ticks after the grace period ended the charge should be 3, it is " + golden().getCharge(staff));
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 40)
    public static void aLongAbsenceEmptiesTheStreak(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        ItemStack staff = new ItemStack(golden());
        long now = helper.getLevel().getGameTime();
        for (int i = 0; i < 5; i++) golden().registerKill(staff, now - 100_000);
        golden().inventoryTick(staff, helper.getLevel(), player, 0, false);
        helper.assertTrue(golden().getCharge(staff) == 0, "a streak from a long time ago stayed at " + golden().getCharge(staff));
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 40)
    public static void theStreakStaysWhenTheStaffIsNotSelected(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        ItemStack staff = new ItemStack(golden());
        long now = helper.getLevel().getGameTime();
        for (int i = 0; i < 3; i++) golden().registerKill(staff, now);
        golden().inventoryTick(staff, helper.getLevel(), player, 4, false);
        helper.assertTrue(golden().getCharge(staff) == 3, "the streak was lost when the staff was not selected: " + golden().getCharge(staff));
        helper.succeed();
    }
}
