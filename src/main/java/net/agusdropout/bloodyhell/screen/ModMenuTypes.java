package net.agusdropout.bloodyhell.screen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.screen.custom.menu.BloodWorkBenchMenu;
import net.agusdropout.bloodyhell.screen.custom.menu.SanguineLapidaryMenu;
import net.agusdropout.bloodyhell.screen.custom.menu.VesperMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, BloodyHell.MODID);

    // --- Menu Registrations ---

    public static final RegistryObject<MenuType<BloodWorkBenchMenu>> BLOOD_WORKBENCH_MENU =
            registerMenuType(BloodWorkBenchMenu::new, "blood_workbench_menu");

    public static final RegistryObject<MenuType<VesperMenu>> VESPER_MENU =
            registerMenuType(VesperMenu::new, "vesper_menu");

    public static final RegistryObject<MenuType<SanguineLapidaryMenu>> SANGUINE_LAPIDARY_MENU =
            registerMenuType(SanguineLapidaryMenu::new, "sanguine_lapidary_menu");


    // --- Helper Methods ---
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}