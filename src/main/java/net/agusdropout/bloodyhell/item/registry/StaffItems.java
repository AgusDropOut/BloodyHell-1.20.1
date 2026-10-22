package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.item.custom.staffs.IncompleteStaffOfThePureBloodedItem;
import net.agusdropout.bloodyhell.item.custom.staffs.StaffOfScarletCinderItem;
import net.agusdropout.bloodyhell.item.custom.staffs.StaffOfTheGoldenSorrowItem;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class StaffItems {
    public static final RegistryObject<Item> INCOMPLETE_STAFF_OF_THE_PURE_BLOODED = ITEMS.register("incomplete_staff_of_the_pure_blooded", () -> new IncompleteStaffOfThePureBloodedItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STAFF_OF_THE_GOLDEN_SORROW = ITEMS.register("staff_of_the_golden_sorrow",
            () -> new StaffOfTheGoldenSorrowItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));
    public static final RegistryObject<Item> STAFF_OF_SCARLET_CINDER = ITEMS.register("staff_of_scarlet_cinder",
            () -> new StaffOfScarletCinderItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));

    private StaffItems() {}

    public static void init() {}
}
