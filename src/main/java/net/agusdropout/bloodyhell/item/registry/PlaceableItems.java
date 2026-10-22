package net.agusdropout.bloodyhell.item.registry;

import net.agusdropout.bloodyhell.block.registry.LightBlocks;
import net.agusdropout.bloodyhell.block.registry.MachineBlocks;
import net.agusdropout.bloodyhell.block.registry.MiscBlocks;
import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.item.custom.*;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.item.ModItems.*;

public final class PlaceableItems {
    public static final RegistryObject<Item> BLOOD_GEM_SPROUT_SEED = ITEMS.register("blood_gem_sprout_seed", () -> new ItemNameBlockItem(MachineBlocks.BLOOD_GEM_SPROUT.get(),
            new Item.Properties()));
    public static final RegistryObject<Item> VORACIOUS_MUSHROOM = ITEMS.register("voracious_mushroom_item", () -> new BlockItem(PlantBlocks.VORACIOUS_MUSHROOM_BLOCK.get(),
            new Item.Properties()));
    public static final RegistryObject<Item> CRIMSON_LURE_MUSHROOM = ITEMS.register("crimson_lure_mushroom_item", () -> new BlockItem(PlantBlocks.CRIMSON_LURE_MUSHROOM_BLOCK.get(),
            new Item.Properties()));
    public static final RegistryObject<Item> EYEBALL_SEED = ITEMS.register("eyeball_seed", () -> new ItemNameBlockItem(PlantBlocks.EYEBALL_CROP.get(),
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_LILY = ITEMS.register("blood_lily", () -> new PlaceOnWaterBlockItem(
            PlantBlocks.BLOOD_LILY_BLOCK.get(),new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMOUS_BLOOD_ALTAR_ITEM = registerGeckoBlockItem("blasphemous_blood_altar_item", MachineBlocks.BLASPHEMOUS_BLOOD_ALTAR, "blasphemous_blood_altar", true);
    public static final RegistryObject<Item> MAIN_BLASPHEMOUS_BLOOD_ALTAR_ITEM = registerGeckoBlockItem("main_blasphemous_blood_altar_item", MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR, "main_blasphemous_blood_altar", true);
    public static final RegistryObject<Item> RHNULL_BLOOD_ENGINE_ITEM = registerGeckoBlockItem("rhnull_blood_engine_item", MachineBlocks.RHNULL_BLOOD_ENGINE_BLOCK, "rhnull_blood_engine_block", false);
    public static final RegistryObject<Item> RHNULL_BLOOD_ENGINE_PILLAR_ITEM = registerGeckoBlockItem("rhnull_blood_engine_pillar_item", MachineBlocks.RHNULL_BLOOD_ENGINE_PILLAR_BLOCK, "rhnull_blood_engine_pillar_block", false);
    public static final RegistryObject<Item> SANGUINITE_PIPE_ITEM = registerGeckoBlockItem("sanguinite_pipe_item", MachineBlocks.SANGUINITE_PIPE, "sanguinite_pipe", false);
    public static final RegistryObject<Item> RHNULL_PIPE_ITEM = registerGeckoBlockItem("rhnull_pipe_item", MachineBlocks.RHNULL_PIPE, "rhnull_pipe", false);
    public static final RegistryObject<Item> SANGUINITE_BLOOD_HARVESTER_ITEM = registerGeckoBlockItem("sanguinite_blood_harvester_item", MachineBlocks.SANGUINITE_BLOOD_HARVESTER, "sanguinite_blood_harvester", false);
    public static final RegistryObject<Item> SANGUINITE_CONDENSER_ITEM = registerGeckoBlockItem("sanguinite_condenser_item", MachineBlocks.SANGUINITE_CONDENSER, "sanguinite_condenser", false);
    public static final RegistryObject<Item> RHNULL_CONDENSER_ITEM = registerGeckoBlockItem("rhnull_condenser_item", MachineBlocks.RHNULL_CONDENSER, "rhnull_condenser", false);
    public static final RegistryObject<Item> UNKNOWN_PORTAL_ITEM = registerGeckoBlockItem("unknown_portal_item", MachineBlocks.UNKNOWN_PORTAL_BLOCK, "unknown_portal_block", true);
    public static final RegistryObject<Item> SELIORA_RESTING_ITEM = registerGeckoBlockItem("seliora_resting_item", MiscBlocks.SELIORA_RESTING_BLOCK, "seliora_resting_block", "seliora_resting_block_texture", false);
    public static final RegistryObject<Item> ANCIENT_TORCH_ITEM = ITEMS.register("ancient_torch",
            () -> new AncientTorchItem(LightBlocks.ANCIENT_TORCH_BLOCK.get(), new Item.Properties()));

    private PlaceableItems() {}

    public static void init() {}
}
