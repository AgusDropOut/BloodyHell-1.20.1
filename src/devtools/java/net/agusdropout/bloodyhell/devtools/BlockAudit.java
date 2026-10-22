package net.agusdropout.bloodyhell.devtools;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.BlockToolRules;
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
import java.util.stream.Collectors;

public final class BlockAudit {
    private static final Path OUTPUT = Path.of("bh-block-audit.csv");
    private static final Set<String> UNHARVESTABLE_ON_PURPOSE = Set.of("seliora_resting_block");

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
