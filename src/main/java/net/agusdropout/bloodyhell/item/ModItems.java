package net.agusdropout.bloodyhell.item;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.registry.BlasphemiteItems;
import net.agusdropout.bloodyhell.item.registry.BucketItems;
import net.agusdropout.bloodyhell.item.registry.DropItems;
import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.agusdropout.bloodyhell.item.registry.FoodItems;
import net.agusdropout.bloodyhell.item.registry.GemItems;
import net.agusdropout.bloodyhell.item.registry.MiscItems;
import net.agusdropout.bloodyhell.item.registry.PlaceableItems;
import net.agusdropout.bloodyhell.item.registry.ReliquaryItems;
import net.agusdropout.bloodyhell.item.registry.RhnullItems;
import net.agusdropout.bloodyhell.item.registry.SanguiniteItems;
import net.agusdropout.bloodyhell.item.registry.SpawnEggItems;
import net.agusdropout.bloodyhell.item.registry.SpellBookItems;
import net.agusdropout.bloodyhell.item.registry.StaffItems;
import net.agusdropout.bloodyhell.item.registry.WeaponItems;
import net.agusdropout.bloodyhell.item.custom.base.GeckoBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BloodyHell.MODID);

    public static RegistryObject<Item> registerGeckoBlockItem(String name, Supplier<? extends Block> block, String id, boolean glowingLayer) {
        return registerGeckoBlockItem(name, block, id, id, glowingLayer);
    }

    public static RegistryObject<Item> registerGeckoBlockItem(String name, Supplier<? extends Block> block, String id, String textureName, boolean glowingLayer) {
        return ITEMS.register(name, () -> new GeckoBlockItem(block.get(), new Item.Properties(), id, textureName, glowingLayer));
    }

    public static void register(IEventBus eventBus) {
        BlasphemiteItems.init();
        BucketItems.init();
        DropItems.init();
        FlaskItems.init();
        FoodItems.init();
        GemItems.init();
        MiscItems.init();
        PlaceableItems.init();
        ReliquaryItems.init();
        RhnullItems.init();
        SanguiniteItems.init();
        SpawnEggItems.init();
        SpellBookItems.init();
        StaffItems.init();
        WeaponItems.init();
        ITEMS.register(eventBus);
    }
}
