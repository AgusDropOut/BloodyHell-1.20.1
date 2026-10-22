package net.agusdropout.bloodyhell.block;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.block.registry.FluidBlocks;
import net.agusdropout.bloodyhell.block.registry.LightBlocks;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.block.registry.MetalBlocks;
import net.agusdropout.bloodyhell.block.registry.MiscBlocks;
import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.block.registry.SandstoneBlocks;
import net.agusdropout.bloodyhell.block.registry.SoilBlocks;
import net.agusdropout.bloodyhell.block.registry.StoneBlocks;
import net.agusdropout.bloodyhell.block.registry.WoodBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BloodyHell.MODID);

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> registered = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }

    public static <T extends Block> RegistryObject<T> registerBlockWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus eventBus) {
        FluidBlocks.init();
        LightBlocks.init();
        MachineBlocks.init();
        MetalBlocks.init();
        MiscBlocks.init();
        PlantBlocks.init();
        SandstoneBlocks.init();
        SoilBlocks.init();
        StoneBlocks.init();
        WoodBlocks.init();
        BLOCKS.register(eventBus);
    }
}
