package net.agusdropout.bloodyhell.block;

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
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BlockToolRules {
    public enum Tool {
        PICKAXE(BlockTags.MINEABLE_WITH_PICKAXE),
        AXE(BlockTags.MINEABLE_WITH_AXE),
        SHOVEL(BlockTags.MINEABLE_WITH_SHOVEL),
        HOE(BlockTags.MINEABLE_WITH_HOE),
        NONE(null);

        private final TagKey<Block> tag;

        Tool(TagKey<Block> tag) {
            this.tag = tag;
        }

        public TagKey<Block> tag() {
            return tag;
        }
    }

    public enum Tier {
        ANY(null),
        STONE(BlockTags.NEEDS_STONE_TOOL),
        IRON(BlockTags.NEEDS_IRON_TOOL),
        SANGUINITE(ModTags.Blocks.NEEDS_SANGUINITE_TOOL),
        BLASPHEMITE(ModTags.Blocks.NEEDS_BLASPHEMITE_TOOL),
        RHNULL(ModTags.Blocks.NEEDS_RHNULL_TOOL);

        private final TagKey<Block> tag;

        Tier(TagKey<Block> tag) {
            this.tag = tag;
        }

        public TagKey<Block> tag() {
            return tag;
        }
    }

    public record Rule(Tool tool, Tier tier) {
        public boolean requiresTool() {
            return tier != Tier.ANY;
        }
    }

    private static final Map<ResourceLocation, Rule> RULES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, RegistryObject<? extends Block>> BLOCKS = new LinkedHashMap<>();

    private BlockToolRules() {}

    @SafeVarargs
    private static void add(Tool tool, Tier tier, RegistryObject<? extends Block>... blocks) {
        Rule rule = new Rule(tool, tier);
        for (RegistryObject<? extends Block> block : blocks) {
            if (RULES.put(block.getId(), rule) != null) {
                throw new IllegalStateException("Block classified twice: " + block.getId());
            }
            BLOCKS.put(block.getId(), block);
        }
    }

    public static Rule ruleFor(ResourceLocation id) {
        return RULES.get(id);
    }

    public static Map<ResourceLocation, RegistryObject<? extends Block>> blocks() {
        return BLOCKS;
    }

    static {
        add(Tool.PICKAXE, Tier.STONE,
                StoneBlocks.BLOODY_STONE_BLOCK,
                StoneBlocks.BLOODY_STONE_STAIRS,
                StoneBlocks.BLOODY_STONE_SLAB,
                StoneBlocks.BLOODY_STONE_FENCE,
                StoneBlocks.BLOODY_STONE_FENCE_GATE,
                StoneBlocks.BLOODY_STONE_WALL,
                StoneBlocks.POLISHED_BLOODY_STONE_BLOCK,
                StoneBlocks.POLISHED_BLOODY_STONE_STAIRS,
                StoneBlocks.POLISHED_BLOODY_STONE_SLAB,
                StoneBlocks.POLISHED_BLOODY_STONE_FENCE,
                StoneBlocks.POLISHED_BLOODY_STONE_FENCE_GATE,
                StoneBlocks.POLISHED_BLOODY_STONE_WALL,
                StoneBlocks.BLOODY_STONE_TILES_BLOCK,
                StoneBlocks.BLOODY_STONE_TILES_STAIRS,
                StoneBlocks.BLOODY_STONE_TILES_SLAB,
                StoneBlocks.BLOODY_STONE_TILES_FENCE,
                StoneBlocks.BLOODY_STONE_FENCE_TILES_GATE,
                StoneBlocks.BLOODY_STONE_TILES_WALL,
                StoneBlocks.BLOODY_STONE_BRICKS,
                StoneBlocks.BLOODY_STONE_BRICKS_STAIRS,
                StoneBlocks.BLOODY_STONE_BRICKS_SLAB,
                StoneBlocks.BLOODY_STONE_BRICKS_FENCE,
                StoneBlocks.BLOODY_STONE_FENCE_BRICKS_GATE,
                StoneBlocks.BLOODY_STONE_BRICKS_WALL,
                SandstoneBlocks.BLASPHEMOUS_SANDSTONE_BLOCK,
                SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK,
                SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS,
                SandstoneBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB,
                SandstoneBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK,
                SandstoneBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK,
                SandstoneBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK,
                SandstoneBlocks.ERODED_BLASPHEMOUS_SANDSTONE,
                SandstoneBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE,
                SandstoneBlocks.CRACKED_BLASPHEMOUS_SANDSTONE,
                MachineBlocks.SANGUINITE_PIPE,
                MachineBlocks.RHNULL_PIPE,
                MachineBlocks.SANGUINITE_TANK,
                MachineBlocks.RHNULL_TANK,
                LightBlocks.ANCIENT_BLOODY_LAMP);

        add(Tool.PICKAXE, Tier.ANY,
                MiscBlocks.SMALL_ROCKS,
                LightBlocks.BLOOD_GLOWING_CHAINS_BLOCK,
                LightBlocks.ANCIENT_TORCH_BLOCK,
                MiscBlocks.ANCIENT_BLOOD_CAPSULE,
                LightBlocks.GLOWING_CRYSTAL_LANTERN);

        add(Tool.PICKAXE, Tier.IRON,
                MetalBlocks.SANGUINITE_BLOCK,
                MetalBlocks.SANGUINITE_ORE,
                MiscBlocks.BLEEDING_BLOCK,
                MachineBlocks.SANGUINE_CRUCIBLE,
                LightBlocks.GLOWING_CRYSTAL,
                StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS,
                StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB,
                StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS,
                StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL,
                StoneBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN,
                StoneBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS,
                StoneBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS,
                MachineBlocks.BLASPHEMOUS_BLOOD_ALTAR,
                MachineBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR,
                MachineBlocks.BLOOD_ALTAR,
                MachineBlocks.MAIN_BLOOD_ALTAR,
                MachineBlocks.UNKNOWN_PORTAL_BLOCK,
                MachineBlocks.SANGUINITE_CONDENSER,
                MachineBlocks.RHNULL_CONDENSER,
                MachineBlocks.SANGUINITE_BLOOD_HARVESTER,
                MachineBlocks.SANGUINITE_INFUSOR,
                MachineBlocks.SANGUINE_LAPIDARY,
                MachineBlocks.RHNULL_BLOOD_ENGINE_BLOCK,
                MachineBlocks.RHNULL_BLOOD_ENGINE_PILLAR_BLOCK,
                LightBlocks.STAR_LAMP_BLOCK,
                LightBlocks.SOUL_LAMP);

        add(Tool.PICKAXE, Tier.SANGUINITE,
                MetalBlocks.BLASPHEMITE_BLOCK,
                MetalBlocks.BLASPHEMITE_ORE);

        add(Tool.PICKAXE, Tier.BLASPHEMITE,
                MetalBlocks.RHNULL_BLOCK);

        add(Tool.SHOVEL, Tier.ANY,
                SoilBlocks.BLOOD_GRASS_BLOCK,
                SoilBlocks.BLOOD_DIRT_BLOCK,
                SoilBlocks.BLASPHEMOUS_SAND_BLOCK,
                SoilBlocks.BLOODDIRT_FARMLAND);

        add(Tool.AXE, Tier.ANY,
                WoodBlocks.BLOOD_LOG,
                WoodBlocks.BLOOD_WOOD,
                WoodBlocks.STRIPPED_BLOOD_LOG,
                WoodBlocks.STRIPPED_BLOOD_WOOD,
                WoodBlocks.SOUL_LOG,
                WoodBlocks.SOUL_WOOD,
                WoodBlocks.STRIPPED_SOUL_LOG,
                WoodBlocks.STRIPPED_SOUL_WOOD,
                WoodBlocks.BLOOD_PLANKS,
                WoodBlocks.BLOOD_PLANKS_STAIRS,
                WoodBlocks.BLOOD_PLANKS_SLAB,
                WoodBlocks.BLOOD_PLANKS_FENCE,
                WoodBlocks.BLOOD_PLANKS_FENCE_GATE,
                WoodBlocks.SOUL_PLANKS,
                WoodBlocks.BLOOD_WOOD_CHEST,
                MiscBlocks.FORBIDDEN_BOOKSHELF_BLOCK);

        add(Tool.HOE, Tier.ANY,
                WoodBlocks.BLOOD_LEAVES,
                WoodBlocks.SOUL_LEAVES,
                PlantBlocks.HANGING_SOUL_TREE_LEAVES,
                PlantBlocks.HANGING_BLOOD_TREE_LEAVES);

        add(Tool.NONE, Tier.ANY,
                MiscBlocks.EYEBALLSHELL_SNAIL_GOO,
                MiscBlocks.EYEBALLSHELL_SNAIL_GOO_BLOCK,
                PlantBlocks.EYEBALL_CROP,
                PlantBlocks.BLOOD_SAPLING,
                PlantBlocks.SMALL_BLOOD_SAPLING,
                PlantBlocks.SOUL_SAPLING,
                PlantBlocks.BLOOD_FLOWER,
                PlantBlocks.BLOOD_GRASS,
                PlantBlocks.POTTED_BLOOD_FLOWER,
                PlantBlocks.BLOOD_BUSH,
                PlantBlocks.BLOOD_LILY_BLOCK,
                PlantBlocks.BLOOD_PETALS,
                PlantBlocks.LIGHT_MUSHROOM_BLOCK,
                PlantBlocks.BLOOD_WALL_MUSHROOM_BLOCK,
                PlantBlocks.DROOPVINE,
                PlantBlocks.DROOPVINE_PLANT,
                PlantBlocks.BLOOD_SCRAPPER_PLANT,
                PlantBlocks.BLOOD_SCRAPPER_PLANT_SAPLING,
                PlantBlocks.GIANT_BLOOD_SAPLING,
                PlantBlocks.VORACIOUS_MUSHROOM_BLOCK,
                PlantBlocks.CRIMSON_LURE_MUSHROOM_BLOCK,
                PlantBlocks.VISCERAL_INFECTED_VEIN,
                FluidBlocks.BLOOD_FLUID_BLOCK,
                FluidBlocks.CORRUPTED_BLOOD_BLOCK,
                FluidBlocks.VISCOUS_BLASPHEMY_BLOCK,
                FluidBlocks.VISCERAL_BLOOD_BLOCK,
                LightBlocks.BLOOD_GLOW_STONE,
                LightBlocks.GLOWING_CRYSTAL_GLASS_BLOCK,
                PlantBlocks.CINDER_BLOOM_CACTUS_ROOT,
                PlantBlocks.CINDER_BLOOM_CACTUS_CON,
                PlantBlocks.CINDER_BLOOM_CACTUS_CENTER,
                PlantBlocks.CINDER_BLOOM_CACTUS_FLOWER,
                PlantBlocks.SPIKY_GRASS,
                PlantBlocks.ROUNDED_GRASS,
                PlantBlocks.STING_FLOWER,
                MiscBlocks.BLOOD_PORTAL,
                MiscBlocks.ONI_STATUE,
                MiscBlocks.SELIORA_RESTING_BLOCK,
                MiscBlocks.DECORATED_POT_BLOCK,
                MiscBlocks.TOMB_BLOCK,
                MiscBlocks.BLOOD_FIRE,
                MiscBlocks.FRENZIED_FIRE_BLOCK,
                MachineBlocks.BLOOD_GEM_SPROUT);
    }
}
