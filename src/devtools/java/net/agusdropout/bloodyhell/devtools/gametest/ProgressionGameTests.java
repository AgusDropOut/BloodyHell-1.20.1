package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.BlockToolRules;
import net.agusdropout.bloodyhell.devtools.BlockAudit;
import net.agusdropout.bloodyhell.recipe.CondenserRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class ProgressionGameTests {
    private static final int ROLLS = 40;
    private static final int MAX_STATES = 64;
    private static final Path REPORT = Path.of("bh-progression.txt");

    @GameTest(template = "empty", batch = "progression", timeoutTicks = 400)
    public static void everyItemOfTheModCanBeObtained(GameTestHelper helper) throws IOException {
        ServerLevel level = helper.getLevel();
        Map<String, String> declared = new HashMap<>();
        Map<String, String> declaredKind = new HashMap<>();
        readDeclaredSources(declared, declaredKind);

        Set<EntityType<?>> obtainableMobs = obtainableMobs(level, declaredKind);
        reportMobs(obtainableMobs);
        Map<Item, String> reached = closure(level, declared, declaredKind, obtainableMobs, true, true);
        Map<Item, String> withoutMobs = closure(level, declared, declaredKind, obtainableMobs, false, true);
        Map<Item, String> withoutChests = closure(level, declared, declaredKind, obtainableMobs, true, false);

        List<String> problems = new ArrayList<>();
        Set<String> unreachable = new TreeSet<>();
        Map<String, String> report = new TreeMap<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (!id.getNamespace().equals(BloodyHell.MODID)) continue;
            String path = id.getPath();
            String kind = declaredKind.get(path);
            String via = reached.get(item);
            boolean byData = via != null && !via.startsWith("code:");
            if (kind != null && kind.equals("deprecated")) {
                report.put(path, (byData ? "obtainable: " + via : "DEPRECATED: " + declared.get(path)));
            } else if (kind != null && kind.equals("creative")) {
                report.put(path, "creative only: " + declared.get(path) + (byData ? " (also obtainable: " + via + ")" : ""));
                if (byData) problems.add(path + " is declared creative only but can be obtained: " + via);
            } else if (via == null) {
                unreachable.add(path);
                report.put(path, "UNREACHABLE");
            } else {
                String needs = (withoutMobs.containsKey(item) ? "" : " [NEEDS MOB DROPS]") + (withoutChests.containsKey(item) ? "" : " [NEEDS A CHEST]");
                report.put(path, via + needs);
            }
        }
        List<String> lines = new ArrayList<>();
        report.forEach((path, via) -> lines.add(path + "\t" + via));
        Files.write(REPORT, lines, StandardCharsets.UTF_8);

        helper.assertTrue(problems.isEmpty(), String.join("; ", problems));
        helper.assertTrue(unreachable.isEmpty(), unreachable.size() + " items cannot be obtained and are not declared in progression-sources.txt: " + unreachable);
        helper.succeed();
    }

    private static Map<Item, String> closure(ServerLevel level, Map<String, String> declared, Map<String, String> declaredKind, Set<EntityType<?>> obtainableMobs, boolean withEntities, boolean withChests) {
        Map<Item, String> reached = new LinkedHashMap<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (!id.getNamespace().equals(BloodyHell.MODID) && !id.getNamespace().equals("minecraft")) continue;
            if (id.getNamespace().equals("minecraft")) reached.put(item, "vanilla");
        }
        declaredKind.forEach((path, kind) -> {
            if (kind.equals("code")) reached.put(item(path), "code: " + declared.get(path));
        });

        List<Supplier<Item>> toolChoices = new ArrayList<>();
        for (BlockToolRules.Tool tool : List.of(BlockToolRules.Tool.PICKAXE, BlockToolRules.Tool.AXE, BlockToolRules.Tool.SHOVEL, BlockToolRules.Tool.HOE)) {
            toolChoices.addAll(BlockAudit.chain(tool));
        }
        Map<Block, Map<ItemStack, Set<Item>>> blockCache = new HashMap<>();
        Map<EntityType<?>, Set<Item>> entityDrops = withEntities ? entityDrops(level, obtainableMobs) : Map.of();
        Map<String, Set<Item>> chestDrops = withChests ? chestDrops(level) : Map.of();

        entityDrops.forEach((type, items) -> items.forEach(item -> reached.putIfAbsent(item, "entity " + ForgeRegistries.ENTITY_TYPES.getKey(type).getPath())));
        chestDrops.forEach((table, items) -> items.forEach(item -> reached.putIfAbsent(item, "chest " + table)));

        boolean changed = true;
        while (changed) {
            changed = false;
            for (Block block : ForgeRegistries.BLOCKS) {
                ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
                if (!blockId.getNamespace().equals(BloodyHell.MODID)) continue;
                for (ItemStack tool : toolsAvailable(toolChoices, reached)) {
                    for (Item drop : blockCache.computeIfAbsent(block, b -> new HashMap<>()).computeIfAbsent(tool, t -> dropsOf(level, block, t))) {
                        if (reached.putIfAbsent(drop, "block " + blockId.getPath() + (tool.isEmpty() ? " by hand" : " with " + ForgeRegistries.ITEMS.getKey(tool.getItem()).getPath())) == null) changed = true;
                    }
                }
            }
            for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
                Item result = recipe.getResultItem(level.registryAccess()).getItem();
                if (reached.containsKey(result)) continue;
                List<Ingredient> ingredients = ingredientsOf(recipe);
                boolean craftable = true;
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.isEmpty()) continue;
                    boolean any = false;
                    for (ItemStack stack : ingredient.getItems()) {
                        if (reached.containsKey(stack.getItem())) { any = true; break; }
                    }
                    if (!any) { craftable = false; break; }
                }
                if (craftable) {
                    reached.put(result, "recipe " + recipe.getId().getPath());
                    changed = true;
                }
            }
        }

        return reached;
    }

    private static Set<EntityType<?>> obtainableMobs(ServerLevel level, Map<String, String> declaredKind) {
        Set<EntityType<?>> mobs = new java.util.HashSet<>();
        level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).forEach(biome -> {
            for (net.minecraft.world.entity.MobCategory category : net.minecraft.world.entity.MobCategory.values()) {
                biome.getMobSettings().getMobs(category).unwrap().forEach(spawner -> mobs.add(spawner.type));
            }
        });
        declaredKind.forEach((path, kind) -> {
            if (kind.equals("mob")) mobs.add(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(BloodyHell.MODID, path)));
        });
        return mobs;
    }

    private static void reportMobs(Set<EntityType<?>> obtainable) throws IOException {
        List<String> lines = new ArrayList<>();
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
            if (id.getNamespace().equals(BloodyHell.MODID)) lines.add(id.getPath() + "	" + (obtainable.contains(type) ? "spawns in survival" : "no spawn rule, structure or declared source"));
        }
        java.util.Collections.sort(lines);
        Files.write(Path.of("bh-mob-sources.txt"), lines, StandardCharsets.UTF_8);
    }

    private static Item item(String path) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(BloodyHell.MODID, path));
    }

    private static List<ItemStack> toolsAvailable(List<Supplier<Item>> chain, Map<Item, String> reached) {
        List<ItemStack> tools = new ArrayList<>();
        tools.add(ItemStack.EMPTY);
        tools.add(new ItemStack(Items.SHEARS));
        ItemStack silk = new ItemStack(Items.WOODEN_PICKAXE);
        silk.enchant(Enchantments.SILK_TOUCH, 1);
        tools.add(silk);
        for (Supplier<Item> supplier : chain) {
            Item tool = supplier.get();
            if (reached.containsKey(tool)) tools.add(new ItemStack(tool));
        }
        return tools;
    }

    private static Set<Item> dropsOf(ServerLevel level, Block block, ItemStack tool) {
        Set<Item> found = new TreeSet<>((a, b) -> ForgeRegistries.ITEMS.getKey(a).compareTo(ForgeRegistries.ITEMS.getKey(b)));
        int states = 0;
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            if (states++ >= MAX_STATES) break;
            if (state.requiresCorrectToolForDrops() && !tool.isCorrectToolForDrops(state)) continue;
            for (int i = 0; i < ROLLS; i++) {
                LootParams.Builder params = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                        .withParameter(LootContextParams.TOOL, tool);
                state.getDrops(params).forEach(stack -> found.add(stack.getItem()));
            }
        }
        return found;
    }

    private static Map<EntityType<?>, Set<Item>> entityDrops(ServerLevel level, Set<EntityType<?>> obtainableMobs) {
        Map<EntityType<?>, Set<Item>> all = new HashMap<>();
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            if (!ForgeRegistries.ENTITY_TYPES.getKey(type).getNamespace().equals(BloodyHell.MODID) || !obtainableMobs.contains(type)) continue;
            if (!(type.create(level) instanceof LivingEntity living)) continue;
            LootTable table = level.getServer().getLootData().getLootTable(type.getDefaultLootTable());
            if (table == LootTable.EMPTY) continue;
            Set<Item> items = new TreeSet<>((a, b) -> ForgeRegistries.ITEMS.getKey(a).compareTo(ForgeRegistries.ITEMS.getKey(b)));
            EntityDropGameTests.roll(level, table, living).keySet().forEach(key -> items.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(key))));
            all.put(type, items);
        }
        return all;
    }

    private static Map<String, Set<Item>> chestDrops(ServerLevel level) {
        Map<String, Set<Item>> all = new HashMap<>();
        for (ResourceLocation key : level.getServer().getLootData().getKeys(LootDataType.TABLE)) {
            if (!key.getNamespace().equals(BloodyHell.MODID) || !key.getPath().startsWith("chests/")) continue;
            LootTable table = level.getServer().getLootData().getLootTable(key);
            Set<Item> items = new TreeSet<>((a, b) -> ForgeRegistries.ITEMS.getKey(a).compareTo(ForgeRegistries.ITEMS.getKey(b)));
            for (int i = 0; i < ROLLS * 5; i++) {
                LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.ZERO).create(LootContextParamSets.CHEST);
                table.getRandomItems(params).forEach(stack -> items.add(stack.getItem()));
            }
            all.put(key.getPath(), items);
        }
        return all;
    }

    private static List<Ingredient> ingredientsOf(Recipe<?> recipe) {
        if (recipe instanceof CondenserRecipe condenser) return List.of(condenser.getItemInput());
        return new ArrayList<>(recipe.getIngredients());
    }

    private static void readDeclaredSources(Map<String, String> evidence, Map<String, String> kinds) throws IOException {
        try (InputStream stream = ProgressionGameTests.class.getClassLoader().getResourceAsStream("progression-sources.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\t");
                kinds.put(parts[1], parts[0]);
                evidence.put(parts[1], parts[2]);
            }
        }
    }
}
