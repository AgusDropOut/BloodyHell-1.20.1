package net.agusdropout.bloodyhell.devtools;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.BlockToolRules;
import net.agusdropout.bloodyhell.item.registry.BlasphemiteItems;
import net.agusdropout.bloodyhell.item.registry.RhnullItems;
import net.agusdropout.bloodyhell.item.registry.SanguiniteItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class BlockAudit {
    private static final Path OUTPUT = Path.of("bh-block-audit.csv");
    private static final Set<String> UNHARVESTABLE_ON_PURPOSE = Set.of("seliora_resting_block");
    private static final Set<String> HARVEST_EXEMPT = Set.of("seliora_resting_block", "blood_leaves", "soul_leaves");

    private BlockAudit() {}

    public static void write(ServerLevel level) {
        List<String> lines = new ArrayList<>();
        lines.add("id;class;hardness;blast_resistance;requires_tool;loot_table;tool_tags;tier_tags");
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            if (!id.getNamespace().equals(BloodyHell.MODID)) continue;
            BlockState state = block.defaultBlockState();
            float hardness = state.getDestroySpeed(level, BlockPos.ZERO);
            ResourceLocation lootId = block.getLootTable();
            boolean hasLoot = level.getServer().getLootData().getLootTable(lootId) != LootTable.EMPTY;
            check(id, state, hasLoot);
            if (hasLoot) checkHarvest(level, id, state);
            lines.add(String.join(";",
                    id.getPath(),
                    block.getClass().getSimpleName(),
                    String.valueOf(hardness),
                    String.valueOf(block.getExplosionResistance()),
                    String.valueOf(state.requiresCorrectToolForDrops()),
                    hasLoot ? "yes" : "no",
                    tags(state, "mineable/"),
                    tags(state, "needs_")));
        }
        try {
            Files.write(OUTPUT, lines, StandardCharsets.UTF_8);
            BloodyHell.LOGGER.info("[BHTEST] block audit written to " + OUTPUT.toAbsolutePath() + " rows=" + (lines.size() - 1));
        } catch (IOException e) {
            BloodyHell.LOGGER.error("[BHTEST] block audit failed", e);
        }
    }

    private static void check(ResourceLocation id, BlockState state, boolean hasLoot) {
        String name = id.getPath();
        BlockToolRules.Rule rule = BlockToolRules.ruleFor(id);
        if (rule == null) {
            violation(name, "has no rule in BlockToolRules");
            return;
        }
        boolean requiresTool = state.requiresCorrectToolForDrops();
        if (rule.requiresTool() && !requiresTool) {
            violation(name, "rule needs tier " + rule.tier() + " but the block does not require a correct tool");
        }
        if (rule.requiresTool() && rule.tool() == BlockToolRules.Tool.NONE) {
            violation(name, "rule has a tier but no tool");
        }
        if (requiresTool && hasLoot && rule.tool() == BlockToolRules.Tool.NONE && !UNHARVESTABLE_ON_PURPOSE.contains(name)) {
            violation(name, "requires a correct tool and has a loot table, but no tool can harvest it");
        }
        boolean hasToolTag = rule.tool().tag() != null && state.is(rule.tool().tag());
        if (rule.tool() != BlockToolRules.Tool.NONE && !hasToolTag) {
            violation(name, "missing the " + rule.tool() + " tool tag");
        }
        String actualTools = tags(state, "mineable/");
        String expectedTools = rule.tool().tag() == null ? "" : rule.tool().tag().location().toString();
        if (!actualTools.equals(expectedTools)) {
            violation(name, "tool tags are [" + actualTools + "], the rule says [" + expectedTools + "]");
        }
        String actualTier = tags(state, "needs_");
        String expectedTier = rule.tier().tag() == null ? "" : rule.tier().tag().location().toString();
        if (!actualTier.equals(expectedTier)) {
            violation(name, "tier tags are [" + actualTier + "], the rule says [" + expectedTier + "]");
        }
    }

    private static List<Supplier<Item>> chain(BlockToolRules.Tool tool) {
        List<Supplier<Item>> vanilla = switch (tool) {
            case PICKAXE -> List.of(() -> Items.WOODEN_PICKAXE, () -> Items.STONE_PICKAXE, () -> Items.IRON_PICKAXE, () -> Items.DIAMOND_PICKAXE, () -> Items.NETHERITE_PICKAXE);
            case AXE -> List.of(() -> Items.WOODEN_AXE, () -> Items.STONE_AXE, () -> Items.IRON_AXE, () -> Items.DIAMOND_AXE, () -> Items.NETHERITE_AXE);
            case SHOVEL -> List.of(() -> Items.WOODEN_SHOVEL, () -> Items.STONE_SHOVEL, () -> Items.IRON_SHOVEL, () -> Items.DIAMOND_SHOVEL, () -> Items.NETHERITE_SHOVEL);
            case HOE -> List.of(() -> Items.WOODEN_HOE, () -> Items.STONE_HOE, () -> Items.IRON_HOE, () -> Items.DIAMOND_HOE, () -> Items.NETHERITE_HOE);
            default -> List.of();
        };
        List<Supplier<Item>> all = new ArrayList<>(vanilla);
        switch (tool) {
            case PICKAXE -> all.addAll(List.of(SanguiniteItems.SANGUINITE_PICKAXE, BlasphemiteItems.BLASPHEMITE_PICKAXE, RhnullItems.RHNULL_PICKAXE));
            case AXE -> all.addAll(List.of(SanguiniteItems.SANGUINITE_AXE, BlasphemiteItems.BLASPHEMITE_AXE, RhnullItems.RHNULL_AXE));
            case SHOVEL -> all.addAll(List.of(SanguiniteItems.SANGUINITE_SHOVEL, BlasphemiteItems.BLASPHEMITE_SHOVEL, RhnullItems.RHNULL_SHOVEL));
            case HOE -> all.addAll(List.of(SanguiniteItems.SANGUINITE_HOE, BlasphemiteItems.BLASPHEMITE_HOE, RhnullItems.RHNULL_HOE));
            default -> { }
        }
        return all;
    }

    private static int levelIndex(BlockToolRules.Tier tier) {
        return switch (tier) {
            case ANY -> 0;
            case STONE -> 1;
            case IRON -> 2;
            case SANGUINITE -> 5;
            case BLASPHEMITE -> 6;
            case RHNULL -> 7;
        };
    }

    private static boolean harvests(ServerLevel level, BlockState state, ItemStack tool) {
        boolean correct = !state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state);
        if (!correct) return false;
        LootParams.Builder params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                .withParameter(LootContextParams.TOOL, tool);
        return !state.getDrops(params).isEmpty();
    }

    private static void checkHarvest(ServerLevel level, ResourceLocation id, BlockState state) {
        String name = id.getPath();
        BlockToolRules.Rule rule = BlockToolRules.ruleFor(id);
        if (rule == null || HARVEST_EXEMPT.contains(name)) return;
        if (rule.tool() == BlockToolRules.Tool.NONE) {
            ItemStack silk = new ItemStack(Items.WOODEN_PICKAXE);
            silk.enchant(Enchantments.SILK_TOUCH, 1);
            boolean any = harvests(level, state, ItemStack.EMPTY) || harvests(level, state, new ItemStack(Items.SHEARS)) || harvests(level, state, silk);
            if (!any) violation(name, "drops nothing with the hand, shears or silk touch");
            return;
        }
        List<Supplier<Item>> tools = chain(rule.tool());
        int required = levelIndex(rule.tier());
        if (!harvests(level, state, new ItemStack(tools.get(required).get()))) {
            violation(name, "drops nothing with the minimum " + rule.tool() + " " + rule.tier());
        }
        if (rule.requiresTool()) {
            int below = required == 5 ? 4 : required - 1;
            if (harvests(level, state, new ItemStack(tools.get(below).get()))) {
                violation(name, "drops with a " + rule.tool() + " below the required tier " + rule.tier());
            }
            if (harvests(level, state, ItemStack.EMPTY)) {
                violation(name, "drops with the bare hand but requires tier " + rule.tier());
            }
        }
    }

    private static void violation(String name, String message) {
        BloodyHell.LOGGER.error("[BHTEST] block audit violation: " + name + " " + message);
    }

    private static String tags(BlockState state, String marker) {
        return state.getTags()
                .map(tag -> tag.location())
                .filter(location -> location.getPath().contains(marker))
                .map(ResourceLocation::toString)
                .sorted()
                .collect(Collectors.joining(","));
    }
}
