package net.agusdropout.bloodyhell.block;

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
                ModBlocks.BLOODY_STONE_BLOCK,
                ModBlocks.BLOODY_STONE_STAIRS,
                ModBlocks.BLOODY_STONE_SLAB,
                ModBlocks.BLOODY_STONE_FENCE,
                ModBlocks.BLOODY_STONE_FENCE_GATE,
                ModBlocks.BLOODY_STONE_WALL,
                ModBlocks.POLISHED_BLOODY_STONE_BLOCK,
                ModBlocks.POLISHED_BLOODY_STONE_STAIRS,
                ModBlocks.POLISHED_BLOODY_STONE_SLAB,
                ModBlocks.POLISHED_BLOODY_STONE_FENCE,
                ModBlocks.POLISHED_BLOODY_STONE_FENCE_GATE,
                ModBlocks.POLISHED_BLOODY_STONE_WALL,
                ModBlocks.BLOODY_STONE_TILES_BLOCK,
                ModBlocks.BLOODY_STONE_TILES_STAIRS,
                ModBlocks.BLOODY_STONE_TILES_SLAB,
                ModBlocks.BLOODY_STONE_TILES_FENCE,
                ModBlocks.BLOODY_STONE_FENCE_TILES_GATE,
                ModBlocks.BLOODY_STONE_TILES_WALL,
                ModBlocks.BLOODY_STONE_BRICKS,
                ModBlocks.BLOODY_STONE_BRICKS_STAIRS,
                ModBlocks.BLOODY_STONE_BRICKS_SLAB,
                ModBlocks.BLOODY_STONE_BRICKS_FENCE,
                ModBlocks.BLOODY_STONE_FENCE_BRICKS_GATE,
                ModBlocks.BLOODY_STONE_BRICKS_WALL,
                ModBlocks.BLASPHEMOUS_SANDSTONE_BLOCK,
                ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK,
                ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS,
                ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB,
                ModBlocks.CUT_BLASPHEMOUS_SANDSTONE_BLOCK,
                ModBlocks.CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK,
                ModBlocks.CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK,
                ModBlocks.ERODED_BLASPHEMOUS_SANDSTONE,
                ModBlocks.FULLY_ERODED_BLASPHEMOUS_SANDSTONE,
                ModBlocks.CRACKED_BLASPHEMOUS_SANDSTONE,
                ModBlocks.SANGUINITE_PIPE,
                ModBlocks.RHNULL_PIPE,
                ModBlocks.SANGUINITE_TANK,
                ModBlocks.RHNULL_TANK,
                ModBlocks.ANCIENT_BLOODY_LAMP);

        add(Tool.PICKAXE, Tier.ANY,
                ModBlocks.SMALL_ROCKS,
                ModBlocks.BLOOD_GLOWING_CHAINS_BLOCK,
                ModBlocks.ANCIENT_TORCH_BLOCK,
                ModBlocks.ANCIENT_BLOOD_CAPSULE,
                ModBlocks.GLOWING_CRYSTAL_LANTERN);

        add(Tool.PICKAXE, Tier.IRON,
                ModBlocks.SANGUINITE_BLOCK,
                ModBlocks.SANGUINITE_ORE,
                ModBlocks.BLEEDING_BLOCK,
                ModBlocks.Jumpy_Block,
                ModBlocks.SANGUINE_CRUCIBLE,
                ModBlocks.GLOWING_CRYSTAL,
                ModBlocks.ANCIENT_BLOODY_STONE_BRICKS,
                ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_SLAB,
                ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_STAIRS,
                ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_WALL,
                ModBlocks.ANCIENT_BLOODY_STONE_BRICKS_COLUMN,
                ModBlocks.ANCIENT_DETAILED_BLOODY_STONE_BRICKS,
                ModBlocks.ANCIENT_CHISELED_BLOODY_STONE_BRICKS,
                ModBlocks.BLASPHEMOUS_BLOOD_ALTAR,
                ModBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR,
                ModBlocks.BLOOD_ALTAR,
                ModBlocks.MAIN_BLOOD_ALTAR,
                ModBlocks.UNKNOWN_PORTAL_BLOCK,
                ModBlocks.SANGUINITE_CONDENSER,
                ModBlocks.RHNULL_CONDENSER,
                ModBlocks.SANGUINITE_BLOOD_HARVESTER,
                ModBlocks.SANGUINITE_INFUSOR,
                ModBlocks.SANGUINE_LAPIDARY,
                ModBlocks.RHNULL_BLOOD_ENGINE_BLOCK,
                ModBlocks.RHNULL_BLOOD_ENGINE_PILLAR_BLOCK,
                ModBlocks.STAR_LAMP_BLOCK,
                ModBlocks.SOUL_LAMP);

        add(Tool.PICKAXE, Tier.SANGUINITE,
                ModBlocks.BLASPHEMITE_BLOCK,
                ModBlocks.BLASPHEMITE_ORE);

        add(Tool.PICKAXE, Tier.BLASPHEMITE,
                ModBlocks.RHNULL_BLOCK);

        add(Tool.SHOVEL, Tier.ANY,
                ModBlocks.BLOOD_GRASS_BLOCK,
                ModBlocks.BLOOD_DIRT_BLOCK,
                ModBlocks.BLASPHEMOUS_SAND_BLOCK,
                ModBlocks.BLOODDIRT_FARMLAND);

        add(Tool.AXE, Tier.ANY,
                ModBlocks.BLOOD_LOG,
                ModBlocks.BLOOD_WOOD,
                ModBlocks.STRIPPED_BLOOD_LOG,
                ModBlocks.STRIPPED_BLOOD_WOOD,
                ModBlocks.SOUL_LOG,
                ModBlocks.SOUL_WOOD,
                ModBlocks.STRIPPED_SOUL_LOG,
                ModBlocks.STRIPPED_SOUL_WOOD,
                ModBlocks.BLOOD_PLANKS,
                ModBlocks.BLOOD_PLANKS_STAIRS,
                ModBlocks.BLOOD_PLANKS_SLAB,
                ModBlocks.BLOOD_PLANKS_FENCE,
                ModBlocks.BLOOD_PLANKS_FENCE_GATE,
                ModBlocks.SOUL_PLANKS,
                ModBlocks.BLOOD_WOOD_CHEST,
                ModBlocks.FORBIDDEN_BOOKSHELF_BLOCK);

        add(Tool.HOE, Tier.ANY,
                ModBlocks.BLOOD_LEAVES,
                ModBlocks.SOUL_LEAVES,
                ModBlocks.HANGING_SOUL_TREE_LEAVES,
                ModBlocks.HANGING_BLOOD_TREE_LEAVES);

        add(Tool.NONE, Tier.ANY,
                ModBlocks.EYEBALLSHELL_SNAIL_GOO,
                ModBlocks.EYEBALLSHELL_SNAIL_GOO_BLOCK,
                ModBlocks.EYEBALL_CROP,
                ModBlocks.BLOOD_SAPLING,
                ModBlocks.SMALL_BLOOD_SAPLING,
                ModBlocks.SOUL_SAPLING,
                ModBlocks.BLOOD_FLOWER,
                ModBlocks.BLOOD_GRASS,
                ModBlocks.POTTED_BLOOD_FLOWER,
                ModBlocks.BLOOD_BUSH,
                ModBlocks.BLOOD_LILY_BLOCK,
                ModBlocks.BLOOD_PETALS,
                ModBlocks.LIGHT_MUSHROOM_BLOCK,
                ModBlocks.BLOOD_WALL_MUSHROOM_BLOCK,
                ModBlocks.DROOPVINE,
                ModBlocks.DROOPVINE_PLANT,
                ModBlocks.BLOOD_SCRAPPER_PLANT,
                ModBlocks.BLOOD_SCRAPPER_PLANT_SAPLING,
                ModBlocks.GIANT_BLOOD_SAPLING,
                ModBlocks.VORACIOUS_MUSHROOM_BLOCK,
                ModBlocks.CRIMSON_LURE_MUSHROOM_BLOCK,
                ModBlocks.VISCERAL_INFECTED_VEIN,
                ModBlocks.BLOOD_FLUID_BLOCK,
                ModBlocks.CORRUPTED_BLOOD_BLOCK,
                ModBlocks.VISCOUS_BLASPHEMY_BLOCK,
                ModBlocks.VISCERAL_BLOOD_BLOCK,
                ModBlocks.BLOOD_GLOW_STONE,
                ModBlocks.GLOWING_CRYSTAL_GLASS_BLOCK,
                ModBlocks.CINDER_BLOOM_CACTUS_ROOT,
                ModBlocks.CINDER_BLOOM_CACTUS_CON,
                ModBlocks.CINDER_BLOOM_CACTUS_CENTER,
                ModBlocks.CINDER_BLOOM_CACTUS_FLOWER,
                ModBlocks.SPIKY_GRASS,
                ModBlocks.ROUNDED_GRASS,
                ModBlocks.STING_FLOWER,
                ModBlocks.BLOOD_PORTAL,
                ModBlocks.ONI_STATUE,
                ModBlocks.SELIORA_RESTING_BLOCK,
                ModBlocks.DECORATED_POT_BLOCK,
                ModBlocks.TOMB_BLOCK,
                ModBlocks.BLOOD_FIRE,
                ModBlocks.FRENZIED_FIRE_BLOCK,
                ModBlocks.BLOOD_GEM_SPROUT);
    }
}
