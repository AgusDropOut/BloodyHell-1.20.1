package net.agusdropout.bloodyhell.devtools.gametest;

import com.mojang.authlib.GameProfile;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class EntityDropGameTests {
    private static final int ROLLS = 300;
    private static final java.util.concurrent.ConcurrentSkipListMap<String, String> SUMMARY = new java.util.concurrent.ConcurrentSkipListMap<>();
    private static final Map<String, String> DROPS_NOTHING_BY_DESIGN = Map.ofEntries(
            Map.entry("bloody_soul_entity", "drops nothing on purpose"),
            Map.entry("corrupted_bloody_soul_entity", "drops nothing on purpose"),
            Map.entry("blasphemous_twin_daggers_clone", "drops nothing on purpose"),
            Map.entry("offspring_of_the_unknown", "drops nothing on purpose"),
            Map.entry("grave_walker_entity", "drops nothing on purpose"),
            Map.entry("horned_worm", "drops nothing on purpose"),
            Map.entry("veil_stalker", "drops nothing on purpose"),
            Map.entry("blasphemous_malformation", "drops nothing on purpose"),
            Map.entry("vesper", "drops nothing on purpose"),
            Map.entry("failed_son_of_the_unknown", "drops nothing on purpose"),
            Map.entry("weeping_ocular", "drops nothing on purpose"),
            Map.entry("unknown_lantern", "drops nothing on purpose"),
            Map.entry("burden_of_the_unknown", "drops nothing on purpose"),
            Map.entry("bastion_of_the_unknown", "drops nothing on purpose"),
            Map.entry("crawling_delusion", "drops nothing on purpose"),
            Map.entry("cyclops_entity", "drops nothing on purpose"),
            Map.entry("blasphemous_arm_entity", "drops nothing on purpose"),
            Map.entry("echo_of_the_nameless", "drops nothing on purpose"));

    @GameTestGenerator
    public static Collection<TestFunction> everyLivingEntityDropsWhatItsTableSays() {
        List<TestFunction> tests = new ArrayList<>();
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
            if (!id.getNamespace().equals(BloodyHell.MODID)) continue;
            tests.add(new TestFunction("entitydrops", "entity_drops_" + id.getPath(), BloodyHell.MODID + ":empty", 20, 0, true, helper -> check(helper, type, id)));
        }
        return tests;
    }

    private static void check(GameTestHelper helper, EntityType<?> type, ResourceLocation id) {
        Entity entity = type.create(helper.getLevel());
        if (!(entity instanceof LivingEntity living)) {
            helper.succeed();
            return;
        }
        String name = id.getPath();
        ServerLevel level = helper.getLevel();
        LootTable table = level.getServer().getLootData().getLootTable(type.getDefaultLootTable());
        Map<String, Integer> dropped = table == LootTable.EMPTY ? Map.of() : roll(level, table, living);
        boolean drops = !dropped.isEmpty();
        SUMMARY.put(name, drops ? "drops " + dropped + " in " + ROLLS + " kills" : "drops nothing");
        writeSummary();
        boolean excused = DROPS_NOTHING_BY_DESIGN.containsKey(name);
        helper.assertTrue(!(drops && excused), name + " is on the list of entities that drop nothing on purpose but it dropped " + dropped.keySet() + "; remove it from the list");
        helper.assertTrue(drops || excused, name + " is a living entity that never dropped anything in " + ROLLS + " kills by a player (loot table " + type.getDefaultLootTable() + ") and is not on the list of entities that drop nothing on purpose");
        helper.succeed();
    }

    private static synchronized void writeSummary() {
        List<String> lines = new ArrayList<>();
        SUMMARY.forEach((entity, text) -> lines.add(entity + "	" + text));
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("bh-entity-drops.txt"), lines, java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map<String, Integer> roll(ServerLevel level, LootTable table, LivingEntity entity) {
        Player player = FakePlayerFactory.get(level, new GameProfile(UUID.randomUUID(), "[bloodyhell_loot]"));
        Map<String, Integer> dropped = new TreeMap<>();
        for (int i = 0; i < ROLLS; i++) {
            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, entity)
                    .withParameter(LootContextParams.ORIGIN, entity.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(player))
                    .withOptionalParameter(LootContextParams.KILLER_ENTITY, player)
                    .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                    .withLuck(player.getLuck())
                    .create(LootContextParamSets.ENTITY);
            for (ItemStack stack : table.getRandomItems(params)) {
                if (stack.isEmpty()) continue;
                Item item = stack.getItem();
                dropped.merge(ForgeRegistries.ITEMS.getKey(item).toString(), stack.getCount(), Integer::sum);
            }
        }
        return dropped;
    }
}
