package net.agusdropout.bloodyhell.devtools;

import net.agusdropout.bloodyhell.BloodyHell;
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
import java.util.stream.Collectors;

public final class BlockAudit {
    private static final Path OUTPUT = Path.of("bh-block-audit.csv");

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

    private static String tags(BlockState state, String marker) {
        return state.getTags()
                .map(tag -> tag.location())
                .filter(location -> location.getPath().contains(marker))
                .map(ResourceLocation::toString)
                .sorted()
                .collect(Collectors.joining(","));
    }
}
