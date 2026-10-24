package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.entity.base.BaseSanguineLapidaryBlockEntity;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.item.registry.GemItems;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.SlotItemHandler;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class LapidaryGameTests {
    private static final String TEMPLATE = "empty";
    private static final BlockPos HERE = new BlockPos(4, 1, 4);
    private static final int WEAPON = 3;

    private static AbstractContainerMenu open(GameTestHelper helper) {
        helper.setBlock(HERE, MachineBlocks.SANGUINE_LAPIDARY.get());
        Player player = helper.makeMockPlayer();
        BaseSanguineLapidaryBlockEntity lapidary = (BaseSanguineLapidaryBlockEntity) helper.getBlockEntity(HERE);
        return lapidary.createMenu(1, player.getInventory(), player);
    }

    private static SlotItemHandler slot(AbstractContainerMenu menu, int index) {
        return menu.slots.stream()
                .filter(slot -> slot instanceof SlotItemHandler handler && handler.getSlotIndex() == index)
                .map(slot -> (SlotItemHandler) slot)
                .findFirst().orElseThrow();
    }

    private static ItemStack gem(GemType type, double value) {
        ItemStack stack = new ItemStack(type.getResultGem());
        stack.getOrCreateTag().putDouble(type.getBonusType(), value);
        return stack;
    }

    @GameTest(template = TEMPLATE, batch = "lapidary", timeoutTicks = 40)
    public static void bookSlotTakesOnlySpellBooks(GameTestHelper helper) {
        AbstractContainerMenu menu = open(helper);
        helper.assertTrue(slot(menu, WEAPON).mayPlace(new ItemStack(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get())), "the book slot rejected a spell book");
        helper.assertFalse(slot(menu, WEAPON).mayPlace(new ItemStack(Items.STICK)), "the book slot accepted a stick");
        helper.assertFalse(slot(menu, WEAPON).mayPlace(new ItemStack(GemItems.PURE_BLOOD_GEM.get())), "the book slot accepted a gem");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "lapidary", timeoutTicks = 40)
    public static void gemSlotsTakeOnlyPowerGems(GameTestHelper helper) {
        AbstractContainerMenu menu = open(helper);
        for (int index = 0; index < 3; index++) {
            helper.assertTrue(slot(menu, index).mayPlace(new ItemStack(GemItems.CITRINE_BLOOD_GEM.get())), "gem slot " + index + " rejected a gem");
            helper.assertFalse(slot(menu, index).mayPlace(new ItemStack(Items.GOLD_NUGGET)), "gem slot " + index + " accepted a gold nugget");
            helper.assertFalse(slot(menu, index).mayPlace(new ItemStack(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get())), "gem slot " + index + " accepted a book");
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "lapidary", timeoutTicks = 40)
    public static void takingTheBookPacksTheGemsIntoIt(GameTestHelper helper) {
        AbstractContainerMenu menu = open(helper);
        slot(menu, WEAPON).set(new ItemStack(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get()));
        slot(menu, 0).set(gem(GemType.PURE, 2.5));
        slot(menu, 1).set(gem(GemType.CITRINE, 3.0));

        ItemStack book = slot(menu, WEAPON).remove(1);

        helper.assertFalse(book.isEmpty(), "the book did not come out");
        helper.assertTrue(book.getOrCreateTag().getCompound("socket_0").getDouble("damage") == 2.5, "the pure gem did not stay in the first socket");
        helper.assertTrue(book.getOrCreateTag().getCompound("socket_1").getDouble("quantity") == 3.0, "the citrine gem did not stay in the second socket");
        helper.assertFalse(book.getOrCreateTag().contains("socket_2"), "an empty gem slot made a socket");
        for (int index = 0; index < 3; index++) {
            helper.assertTrue(slot(menu, index).getItem().isEmpty(), "gem slot " + index + " still holds a gem after packing");
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "lapidary", timeoutTicks = 40)
    public static void insertingASocketedBookReturnsItsGems(GameTestHelper helper) {
        AbstractContainerMenu menu = open(helper);
        ItemStack book = new ItemStack(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get());
        book.getOrCreateTag().put("socket_0", damageSocket(1.5));
        book.getOrCreateTag().put("socket_1", sizeSocket(0.25));

        slot(menu, WEAPON).set(book);

        ItemStack first = slot(menu, 0).getItem();
        ItemStack second = slot(menu, 1).getItem();
        helper.assertTrue(first.is(GemItems.PURE_BLOOD_GEM.get()) && first.getOrCreateTag().getDouble("damage") == 1.5, "the first socket did not come back as a pure gem with its value");
        helper.assertTrue(second.is(GemItems.AVENTURINE_BLOOD_GEM.get()) && second.getOrCreateTag().getDouble("size") == 0.25, "the second socket did not come back as an aventurine gem with its value");
        helper.assertTrue(slot(menu, 2).getItem().isEmpty(), "the third gem slot is not empty");
        helper.assertFalse(slot(menu, WEAPON).getItem().getOrCreateTag().contains("socket_0"), "the book still has its sockets after unpacking");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "lapidary", timeoutTicks = 40)
    public static void takingABookWithoutGemsLeavesItClean(GameTestHelper helper) {
        AbstractContainerMenu menu = open(helper);
        slot(menu, WEAPON).set(new ItemStack(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get()));

        ItemStack book = slot(menu, WEAPON).remove(1);

        helper.assertFalse(book.isEmpty(), "the book did not come out");
        helper.assertFalse(book.getOrCreateTag().contains("socket_0"), "a book without gems got a socket");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "lapidary", timeoutTicks = 40)
    public static void gemsSurviveAPackAndUnpackCycle(GameTestHelper helper) {
        AbstractContainerMenu menu = open(helper);
        slot(menu, WEAPON).set(new ItemStack(SpellBookItems.BLOOD_SPHERE_SPELLBOOK.get()));
        slot(menu, 0).set(gem(GemType.TANZARINE, 4.5));
        ItemStack book = slot(menu, WEAPON).remove(1);

        slot(menu, WEAPON).set(book);

        ItemStack back = slot(menu, 0).getItem();
        helper.assertTrue(back.is(GemItems.TANZARINE_BLOOD_GEM.get()), "the gem changed type through the cycle");
        helper.assertTrue(back.getOrCreateTag().getDouble("duration") == 4.5, "the gem value changed through the cycle");
        helper.succeed();
    }

    private static CompoundTag damageSocket(double value) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("damage", value);
        return tag;
    }

    private static CompoundTag sizeSocket(double value) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("size", value);
        return tag;
    }
}
