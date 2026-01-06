package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> BLOOD_LOGS = tag("blood_logs");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(BloodyHell.MODID, name));
        }
    }

    public static class Blocks {


        public static final TagKey<Block> BLOOD_SCRAPPER_PLANT_PLACEABLE_ON = tag("blood_scrapper_plant_placeable_on");
        public static final TagKey<Block> NEEDS_SANGUINITE_TOOL = tag("needs_sanguinite_tool");
        public static final TagKey<Block> NEEDS_BLASPHEMITE_TOOL = tag("needs_blasphemite_tool");
        public static final TagKey<Block> NEEDS_RHNULL_TOOL = tag("needs_rhnull_tool");
        public static final TagKey<Block> BLOOD_ORE_REPLACEABLES = tag("blood_ore_replaceables");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(BloodyHell.MODID, name));
        }

    }

    public static class Entities {
        public static final TagKey<EntityType<?>> INMUNE_TO_BLEEDING_BLOCK = tag("inmune_to_bleeding_block");
        public static final TagKey<EntityType<?>> INMUNE_TO_VISCERAL_EFFECT = tag("inmune_to_visceral_effect");
        public static final TagKey<EntityType<?>> SACRIFICEABLE_ENTITY = tag("sacrificeable_entity");
        public static final TagKey<EntityType<?>> CORRUPTED_SACRIFICEABLE_ENTITY = tag("corrupted_sacrificeable_entity");
        private static TagKey<EntityType<?>> tag(String name) {
            return EntityTypeTags.create(new ResourceLocation(BloodyHell.MODID, name).toString());
        }
    }

    public static class Fluids {


        public static final TagKey<Fluid> BLOODY_LIQUID = tag("bloody_liquid");
        public static final TagKey<Fluid> RHNULL_LIQUID = tag("rhnull_liquid");
        private static TagKey<Fluid> tag(String name) {
            return FluidTags.create(new ResourceLocation(BloodyHell.MODID, name));
        }
    }

    public static class Biomes {

        private static TagKey<Biome> tag(String name) {
            return BiomeTags.create(new ResourceLocation(BloodyHell.MODID, name).toString());
        }
    }
    public static class Structures {

        public static final TagKey<Structure> MAUSOLEUM = tag("mausoleum");

        private static TagKey<Structure> tag(String name) {
            return StructureTags.create(new ResourceLocation(BloodyHell.MODID, name).toString());
        }
    }


}