package net.agusdropout.bloodyhell.devtools.gametest;

import com.mojang.authlib.GameProfile;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonVeil;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.entity.projectile.spell.BloodPortalEntity;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.StaffItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.UUID;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class StaffBonusGameTests {
    private static final int USE_DURATION = 72000;
    private static final int REFRESH_TICK = BaseStaffItem.BONUS_REFRESH_TICKS;
    private static final BlockPos CENTER = new BlockPos(4, 1, 4);

    @GameTest(template = "empty", batch = "staffbonus_scarletCinderCountsTheBloodFireTargetsUpToFive")
    public static void scarletCinderCountsTheBloodFireTargetsUpToFive(GameTestHelper helper) {
        countsTargets(helper, StaffItems.STAFF_OF_SCARLET_CINDER.get(), ModEffects.BLOOD_FIRE_EFFECT.get());
    }

    @GameTest(template = "empty", batch = "staffbonus_incompleteStaffCountsTheBleedingTargetsUpToFive")
    public static void incompleteStaffCountsTheBleedingTargetsUpToFive(GameTestHelper helper) {
        countsTargets(helper, StaffItems.INCOMPLETE_STAFF_OF_THE_PURE_BLOODED.get(), ModEffects.BLEEDING.get());
    }

    @GameTest(template = "empty", batch = "staffbonus_targetsWithAnotherEffectOrOutOfRangeDoNotCount")
    public static void targetsWithAnotherEffectOrOutOfRangeDoNotCount(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        BaseStaffItem staff = (BaseStaffItem) StaffItems.INCOMPLETE_STAFF_OF_THE_PURE_BLOODED.get();
        ItemStack stack = new ItemStack(staff);
        mark(helper, new BlockPos(1, 1, 1), ModEffects.BLOOD_FIRE_EFFECT.get());
        Pig far = EntityType.PIG.create(helper.getLevel());
        far.moveTo(player.position().add(BaseStaffItem.BONUS_RADIUS + 4.0, 0, 0));
        far.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 6000));
        helper.getLevel().addFreshEntity(far);
        refresh(helper, staff, stack, player, true);
        helper.assertTrue(staff.getMarkedTargets(stack) == 0, "a target with another effect or out of range was counted: " + staff.getMarkedTargets(stack));
        far.discard();
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "staffbonus_theCountResetsWhenTheStaffIsNotSelected")
    public static void theCountResetsWhenTheStaffIsNotSelected(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        BaseStaffItem staff = (BaseStaffItem) StaffItems.INCOMPLETE_STAFF_OF_THE_PURE_BLOODED.get();
        ItemStack stack = new ItemStack(staff);
        mark(helper, new BlockPos(1, 1, 1), ModEffects.BLEEDING.get());
        refresh(helper, staff, stack, player, true);
        helper.assertTrue(staff.getMarkedTargets(stack) == 1, "the target was not counted");
        refresh(helper, staff, stack, player, false);
        helper.assertTrue(staff.getMarkedTargets(stack) == 0, "the count stayed after the staff was not selected");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "staffbonus_anAffineBookGetsTheFullBonus")
    public static void anAffineBookGetsTheFullBonus(GameTestHelper helper) {
        BaseSpellBookItem<?> book = (BaseSpellBookItem<?>) SpellBookItems.BLOOD_NOVA_SPELLBOOK.get();
        Cast plain = castThroughTheStaff(helper, book, 0);
        Cast charged = castThroughTheStaff(helper, book, 5);
        helper.assertTrue(plain.spent == book.getCrimsonCost(), "the plain cast spent " + plain.spent);
        helper.assertTrue(charged.spent == Math.round(book.getCrimsonCost() * (1.0f - BaseStaffItem.MAX_COST_REDUCTION)), "the charged cast spent " + charged.spent);
        assertRatio(helper, charged.damage / plain.damage, 1.0f + BaseStaffItem.MAX_DAMAGE_BONUS, plain, charged);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "staffbonus_aBookOfAnotherAffinityGetsAShareOfTheBonus")
    public static void aBookOfAnotherAffinityGetsAShareOfTheBonus(GameTestHelper helper) {
        BaseSpellBookItem<?> book = (BaseSpellBookItem<?>) SpellBookItems.BLOOD_FIRE_SOUL_SPELLBOOK.get();
        Cast plain = castThroughTheStaff(helper, book, 0);
        Cast charged = castThroughTheStaff(helper, book, 5);
        float share = BaseStaffItem.OFF_AFFINITY_SHARE;
        helper.assertTrue(charged.spent == Math.round(book.getCrimsonCost() * (1.0f - BaseStaffItem.MAX_COST_REDUCTION * share)), "the charged cast spent " + charged.spent);
        assertRatio(helper, charged.damage / plain.damage, 1.0f + BaseStaffItem.MAX_DAMAGE_BONUS * share, plain, charged);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "staffbonus_aCastRightAfterSelectingTheStaffCountsTheTargets")
    public static void aCastRightAfterSelectingTheStaffCountsTheTargets(GameTestHelper helper) {
        BaseSpellBookItem<?> book = (BaseSpellBookItem<?>) SpellBookItems.BLOOD_NOVA_SPELLBOOK.get();
        Cast charged = castThroughTheStaff(helper, book, 5, book.getMinChargeTime() + 5, false);
        helper.assertTrue(charged.spent == Math.round(book.getCrimsonCost() * (1.0f - BaseStaffItem.MAX_COST_REDUCTION)), "the cast started without a refresh of the count spent " + charged.spent);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "staffbonus_theDaggersOfTheRainGetTheDamageBonus")
    public static void theDaggersOfTheRainGetTheDamageBonus(GameTestHelper helper) {
        BaseSpellBookItem<?> book = (BaseSpellBookItem<?>) SpellBookItems.BLOOD_DAGGERSRAIN_SPELLBOOK.get();
        Cast plain = castThroughTheStaff(helper, book, 0);
        Cast charged = castThroughTheStaff(helper, book, 5);
        helper.assertTrue(plain.portalMultiplier == 1.0f, "the plain portal multiplied the damage by " + plain.portalMultiplier);
        assertRatio(helper, charged.portalMultiplier, 1.0f + BaseStaffItem.MAX_DAMAGE_BONUS, plain, charged);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "staffbonus_aDeadTargetDoesNotCount")
    public static void aDeadTargetDoesNotCount(GameTestHelper helper) {
        Player player = freshPlayer(helper);
        BaseStaffItem staff = (BaseStaffItem) StaffItems.INCOMPLETE_STAFF_OF_THE_PURE_BLOODED.get();
        ItemStack stack = new ItemStack(staff);
        Pig dying = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        dying.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 6000));
        dying.setHealth(0.0f);
        refresh(helper, staff, stack, player, true);
        helper.assertTrue(staff.getMarkedTargets(stack) == 0, "a dying target was counted");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "staffbonus_aSequentialBookCastsFasterAndCheaperWhenTheStaffIsCharged")
    public static void aSequentialBookCastsFasterAndCheaperWhenTheStaffIsCharged(GameTestHelper helper) {
        BaseSpellBookItem<?> book = (BaseSpellBookItem<?>) SpellBookItems.BLOOD_SCRATCH_SPELLBOOK.get();
        int ticks = 50;
        Cast plain = castThroughTheStaff(helper, book, 0, ticks);
        Cast charged = castThroughTheStaff(helper, book, 5, ticks);
        int plainCasts = plain.spent / book.getCrimsonCost();
        int chargedCost = Math.round(book.getCrimsonCost() * (1.0f - BaseStaffItem.MAX_COST_REDUCTION));
        helper.assertTrue(plain.spent == plainCasts * book.getCrimsonCost(), "the plain cast spent " + plain.spent);
        helper.assertTrue(charged.spent % chargedCost == 0, "the charged cast spent " + charged.spent + " which is not a multiple of " + chargedCost);
        helper.assertTrue(charged.spent / chargedCost > plainCasts, "the charged staff cast " + charged.spent / chargedCost + " times against " + plainCasts + " without charge");
        helper.succeed();
    }

    private record Cast(int spent, float damage, float portalMultiplier) {}

    private static void assertRatio(GameTestHelper helper, float actual, float expected, Cast plain, Cast charged) {
        helper.assertTrue(Math.abs(actual - expected) < 0.01f, "the damage changed by " + actual + " instead of " + expected + " (plain " + plain.damage + ", charged " + charged.damage + ")");
    }

    private static Player freshPlayer(GameTestHelper helper) {
        Player player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "[bloodyhell_test]"));
        player.moveTo(Vec3.atBottomCenterOf(helper.absolutePos(CENTER)));
        player.tickCount = REFRESH_TICK;
        helper.getLevel().getEntitiesOfClass(Pig.class, player.getBoundingBox().inflate(64.0)).forEach(Entity::discard);
        PlayerCrimsonVeil veil = veil(player);
        veil.subCrimsomveil(veil.getCrimsonVeil());
        veil.addCrimsomveil(PlayerCrimsonVeil.MAX_CRIMSOMVEIL);
        return player;
    }

    private static PlayerCrimsonVeil veil(Player player) {
        return player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).orElseThrow(() -> new IllegalStateException("the player has no crimson veil"));
    }

    private static void mark(GameTestHelper helper, BlockPos relative, MobEffect effect) {
        Pig pig = helper.spawn(EntityType.PIG, relative);
        pig.addEffect(new MobEffectInstance(effect, 6000));
    }

    private static void refresh(GameTestHelper helper, BaseStaffItem staff, ItemStack stack, Player player, boolean selected) {
        player.tickCount = REFRESH_TICK;
        staff.inventoryTick(stack, helper.getLevel(), player, 0, selected);
    }

    private static void countsTargets(GameTestHelper helper, Item item, MobEffect effect) {
        Player player = freshPlayer(helper);
        BaseStaffItem staff = (BaseStaffItem) item;
        ItemStack stack = new ItemStack(staff);
        int marked = 0;
        for (int target : new int[]{0, 2, 5, 7}) {
            while (marked < target) {
                mark(helper, new BlockPos(1 + marked, 1, 1), effect);
                marked++;
            }
            refresh(helper, staff, stack, player, true);
            int expected = Math.min(target, BaseStaffItem.MARKED_FOR_FULL_BONUS);
            helper.assertTrue(staff.getMarkedTargets(stack) == expected, target + " marked targets counted as " + staff.getMarkedTargets(stack) + " instead of " + expected);
        }
        helper.succeed();
    }

    private static Cast castThroughTheStaff(GameTestHelper helper, BaseSpellBookItem<?> book, int marked) {
        return castThroughTheStaff(helper, book, marked, book.getMinChargeTime() + 5);
    }

    private static Cast castThroughTheStaff(GameTestHelper helper, BaseSpellBookItem<?> book, int marked, int heldTicks) {
        return castThroughTheStaff(helper, book, marked, heldTicks, true);
    }

    private static Cast castThroughTheStaff(GameTestHelper helper, BaseSpellBookItem<?> book, int marked, int heldTicks, boolean refreshFirst) {
        Player player = freshPlayer(helper);
        Item staffItem = StaffItems.INCOMPLETE_STAFF_OF_THE_PURE_BLOODED.get();
        BaseStaffItem staff = (BaseStaffItem) staffItem;
        ItemStack staffStack = new ItemStack(staffItem);
        List<Pig> targets = new java.util.ArrayList<>();
        for (int i = 0; i < marked; i++) {
            Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1 + i, 1, 1));
            pig.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 6000));
            targets.add(pig);
        }
        if (refreshFirst) {
            refresh(helper, staff, staffStack, player, true);
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, staffStack);
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(book));
        int before = veil(player).getCrimsonVeil();

        staffStack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        for (int used = 1; used <= heldTicks; used++) {
            staffStack.getItem().onUseTick(helper.getLevel(), player, staffStack, USE_DURATION - used);
        }
        staffStack.releaseUsing(helper.getLevel(), player, USE_DURATION - heldTicks);

        float damage = 0.0f;
        float portalMultiplier = 1.0f;
        AABB area = player.getBoundingBox().inflate(30.0);
        for (Entity entity : helper.getLevel().getEntitiesOfClass(Entity.class, area, e -> e instanceof IGemSpell)) {
            damage = ((IGemSpell) entity).getBaseDamage();
            if (entity instanceof BloodPortalEntity portal) portalMultiplier = portal.getDamageMultiplier();
            entity.discard();
        }
        targets.forEach(Entity::discard);
        player.stopUsingItem();
        return new Cast(before - veil(player).getCrimsonVeil(), damage, portalMultiplier);
    }
}
