package net.agusdropout.bloodyhell;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ResourceIntegrityTest {
    private static final String MODID = "bloodyhell";
    private static final Path JAVA = Path.of("src/main/java");
    private static final List<Path> ASSET_ROOTS = List.of(
            Path.of("src/main/resources/assets/" + MODID),
            Path.of("src/generated/resources/assets/" + MODID));
    private static final List<Path> DATA_ROOTS = List.of(
            Path.of("src/main/resources/data/" + MODID),
            Path.of("src/generated/resources/data/" + MODID));
    private static final Path KNOWN_GAPS = Path.of("src/test/resources/known-resource-gaps.txt");

    private static final Pattern ITEM_REGISTER = Pattern.compile("(?:ITEMS\\.register|registerGeckoBlockItem|\\bbook)\\(\\s*\"([a-z0-9_]+)\"");
    private static final Pattern SPELL_BOOK = Pattern.compile("\\bbook\\(\\s*\"([a-z0-9_]+)\",\\s*\"([a-z0-9_]+)\"");
    private static final Pattern BLOCK_REGISTER = Pattern.compile("BLOCKS\\.register\\(\\s*\"([a-z0-9_]+)\"");
    private static final Pattern BLOCK_WITH_ITEM = Pattern.compile("registerBlock\\(\\s*\"([a-z0-9_]+)\"");
    private static final Pattern BLOCK_WITHOUT_ITEM = Pattern.compile("registerBlockWithoutBlockItem\\(\\s*\"([a-z0-9_]+)\"");
    private static final Pattern RESOURCE_LITERAL = Pattern.compile("MODID\\s*,\\s*\"((?:geo|animations|textures|models|particles)/[a-z0-9_/]+\\.(?:json|png))\"");

    private static final Set<String> blocks = new TreeSet<>();
    private static final Set<String> items = new TreeSet<>();
    private static final Map<String, Path> javaSources = new HashMap<>();
    private static Set<String> knownGaps = new HashSet<>();
    private static final Set<String> usedGaps = new HashSet<>();

    @BeforeAll
    static void scanRegistrations() throws IOException {
        try (Stream<Path> files = Files.walk(JAVA)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList())) {
                String source = Files.readString(file, StandardCharsets.UTF_8);
                javaSources.put(JAVA.relativize(file).toString().replace('\\', '/'), file);
                collect(ITEM_REGISTER, source, items);
                collect(BLOCK_REGISTER, source, blocks);
                collect(BLOCK_WITHOUT_ITEM, source, blocks);
                Set<String> withItem = new TreeSet<>();
                collect(BLOCK_WITH_ITEM, source, withItem);
                blocks.addAll(withItem);
                items.addAll(withItem);
            }
        }
        if (Files.exists(KNOWN_GAPS)) {
            knownGaps = Files.readAllLines(KNOWN_GAPS, StandardCharsets.UTF_8).stream()
                    .map(String::trim).filter(l -> !l.isEmpty() && !l.startsWith("#")).collect(Collectors.toSet());
        }
    }

    @AfterAll
    static void reportStaleGaps() {
        Set<String> stale = new TreeSet<>(knownGaps);
        stale.removeAll(usedGaps);
        if (!stale.isEmpty()) {
            System.out.println("Known gaps no longer hit (remove them from " + KNOWN_GAPS + "):\n  " + String.join("\n  ", stale));
        }
    }

    private static void collect(Pattern pattern, String source, Set<String> into) {
        Matcher m = pattern.matcher(source);
        while (m.find()) into.add(m.group(1));
    }

    private static boolean assetExists(String relative) {
        return ASSET_ROOTS.stream().anyMatch(root -> Files.exists(root.resolve(relative)));
    }

    private static boolean resolves(String id, String folder, String extension) {
        if (id.startsWith("#")) return true;
        int colon = id.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : id.substring(0, colon);
        if (!namespace.equals(MODID)) return true;
        String path = id.substring(colon + 1);
        return assetExists((folder.isEmpty() ? "" : folder + "/") + path + extension);
    }

    private static List<Path> files(List<Path> roots, String folder, String extension) throws IOException {
        List<Path> result = new ArrayList<>();
        for (Path root : roots) {
            Path dir = root.resolve(folder);
            if (!Files.exists(dir)) continue;
            try (Stream<Path> walk = Files.walk(dir)) {
                walk.filter(p -> p.toString().endsWith(extension)).forEach(result::add);
            }
        }
        return result;
    }

    private static JsonElement json(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader);
        }
    }

    private static String name(Path file) {
        String fileName = file.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private static String relativeToAssets(Path file) {
        return ASSET_ROOTS.stream().filter(file::startsWith).findFirst()
                .map(root -> root.relativize(file).toString().replace('\\', '/')).orElse(file.toString());
    }

    private static void report(String title, List<String> problems) {
        Set<String> unexpected = new TreeSet<>();
        for (String problem : problems) {
            if (knownGaps.contains(problem)) {
                usedGaps.add(problem);
            } else {
                unexpected.add(problem);
            }
        }
        if (!unexpected.isEmpty()) {
            throw new AssertionError(title + " (" + unexpected.size() + "):\n  " + String.join("\n  ", unexpected)
                    + "\nFix them, or add the exact line to " + KNOWN_GAPS + " if the gap is accepted.");
        }
    }

    @Test
    void everySpellBookHasItsAssets() throws IOException {
        String registry = Files.readString(JAVA.resolve("net/agusdropout/bloodyhell/item/registry/SpellBookItems.java"), StandardCharsets.UTF_8);
        Set<String> lang = new HashSet<>();
        for (Path file : files(ASSET_ROOTS, "lang", "en_us.json")) {
            json(file).getAsJsonObject().keySet().forEach(lang::add);
        }
        List<String> problems = new ArrayList<>();
        Matcher m = SPELL_BOOK.matcher(registry);
        int books = 0;
        while (m.find()) {
            books++;
            String id = m.group(1);
            String icon = m.group(2);
            if (!assetExists("geo/" + id + ".geo.json")) problems.add("spell book model missing: " + id);
            if (!assetExists("textures/item/" + id + ".png")) problems.add("spell book texture missing: " + id);
            if (!assetExists("textures/gui/" + icon + ".png")) problems.add("spell book icon missing: " + icon + " of " + id);
            if (!lang.contains("item." + MODID + "." + id)) problems.add("spell book name missing: " + id);
        }
        if (books == 0) problems.add("no spell book found in SpellBookItems");
        report("Spell books with missing assets", problems);
    }

    @Test
    void everyBlockHasABlockstate() {
        List<String> problems = new ArrayList<>();
        for (String block : blocks) {
            if (!assetExists("blockstates/" + block + ".json")) problems.add("blockstate missing: " + block);
        }
        report("Blocks without blockstate", problems);
    }

    @Test
    void everyBlockHasAToolRule() throws IOException {
        StringBuilder registrySources = new StringBuilder();
        try (Stream<Path> groups = Files.list(JAVA.resolve("net/agusdropout/bloodyhell/block/registry"))) {
            for (Path group : groups.collect(Collectors.toList())) {
                registrySources.append(Files.readString(group, StandardCharsets.UTF_8)).append('\n');
            }
        }
        String blocksSource = registrySources.toString();
        String rulesSource = Files.readString(JAVA.resolve("net/agusdropout/bloodyhell/block/BlockToolRules.java"), StandardCharsets.UTF_8);
        Pattern constant = Pattern.compile("public static final RegistryObject<[^>]*>\\s+(\\w+)\\s*=\\s*(?:registerBlock|BLOCKS\\.register|registerBlockWithoutBlockItem)\\(");
        Matcher m = constant.matcher(blocksSource);
        List<String> problems = new ArrayList<>();
        while (m.find()) {
            if (!Pattern.compile("\\w+Blocks\\." + m.group(1) + "\\b").matcher(rulesSource).find()) {
                problems.add("block without tool rule: " + m.group(1));
            }
        }
        report("Blocks missing from BlockToolRules", problems);
    }

    @Test
    void everyItemHasAModel() {
        List<String> problems = new ArrayList<>();
        for (String item : items) {
            if (!assetExists("models/item/" + item + ".json")) problems.add("item model missing: " + item);
        }
        report("Items without model", problems);
    }

    @Test
    void everyBlockAndItemHasALangEntry() throws IOException {
        Set<String> keys = new HashSet<>();
        for (Path lang : files(ASSET_ROOTS, "lang", ".json")) {
            keys.addAll(json(lang).getAsJsonObject().keySet());
        }
        List<String> problems = new ArrayList<>();
        for (String block : blocks) {
            if (!keys.contains("block." + MODID + "." + block) && !keys.contains("item." + MODID + "." + block)) {
                problems.add("lang missing: block." + MODID + "." + block);
            }
        }
        for (String item : items) {
            String prefix = "item." + MODID + "." + item;
            boolean named = keys.contains(prefix) || keys.contains("block." + MODID + "." + item)
                    || keys.stream().anyMatch(k -> k.startsWith(prefix + "."));
            if (!named) {
                problems.add("lang missing: item." + MODID + "." + item);
            }
        }
        report("Missing lang entries", problems);
    }

    @Test
    void langKeysAreUnique() throws IOException {
        Pattern key = Pattern.compile("^\\s*\"([^\"]+)\"\\s*:");
        List<String> problems = new ArrayList<>();
        for (Path lang : files(ASSET_ROOTS, "lang", ".json")) {
            Set<String> seen = new HashSet<>();
            for (String line : Files.readAllLines(lang, StandardCharsets.UTF_8)) {
                Matcher m = key.matcher(line);
                if (m.find() && !m.group(1).equals("_comment") && !seen.add(m.group(1))) problems.add("duplicate lang key: " + m.group(1));
            }
        }
        report("Duplicate lang keys", problems);
    }

    @Test
    void modelParentsAndTexturesResolve() throws IOException {
        List<String> problems = new ArrayList<>();
        for (Path model : files(ASSET_ROOTS, "models", ".json")) {
            JsonElement root = json(model);
            if (!root.isJsonObject()) continue;
            JsonObject object = root.getAsJsonObject();
            String where = relativeToAssets(model);
            if (object.has("parent") && !resolves(object.get("parent").getAsString(), "models", ".json")) {
                problems.add("model parent missing: " + where + " -> " + object.get("parent").getAsString());
            }
            if (object.has("textures") && object.get("textures").isJsonObject()) {
                for (Map.Entry<String, JsonElement> texture : object.getAsJsonObject("textures").entrySet()) {
                    if (texture.getValue().isJsonPrimitive() && !resolves(texture.getValue().getAsString(), "textures", ".png")) {
                        problems.add("model texture missing: " + where + " -> " + texture.getValue().getAsString());
                    }
                }
            }
        }
        report("Models with unresolved parent or texture", problems);
    }

    @Test
    void blockstateModelsResolve() throws IOException {
        List<String> problems = new ArrayList<>();
        for (Path state : files(ASSET_ROOTS, "blockstates", ".json")) {
            Set<String> models = new TreeSet<>();
            collectModels(json(state), models);
            for (String model : models) {
                if (!resolves(model, "models", ".json")) problems.add("blockstate model missing: " + name(state) + " -> " + model);
            }
        }
        report("Blockstates pointing to missing models", problems);
    }

    private static void collectModels(JsonElement element, Set<String> into) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                if (entry.getKey().equals("model") && entry.getValue().isJsonPrimitive()) {
                    into.add(entry.getValue().getAsString());
                } else {
                    collectModels(entry.getValue(), into);
                }
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) collectModels(child, into);
        }
    }

    @Test
    void javaResourceLiteralsResolve() throws IOException {
        List<String> problems = new ArrayList<>();
        for (Map.Entry<String, Path> source : javaSources.entrySet()) {
            Matcher m = RESOURCE_LITERAL.matcher(Files.readString(source.getValue(), StandardCharsets.UTF_8));
            while (m.find()) {
                if (!assetExists(m.group(1))) problems.add("resource literal missing: " + m.group(1) + " (in " + source.getKey() + ")");
            }
        }
        report("Java code points to missing resources", problems);
    }

    @Test
    void shaderProgramsReferenceExistingSources() throws IOException {
        List<String> problems = new ArrayList<>();
        for (Path program : files(ASSET_ROOTS, "shaders/core", ".json")) {
            JsonObject object = json(program).getAsJsonObject();
            for (String stage : List.of("vertex", "fragment")) {
                if (!object.has(stage)) continue;
                String id = object.get(stage).getAsString();
                String extension = stage.equals("vertex") ? ".vsh" : ".fsh";
                if (!resolves(id, "shaders/core", extension)) problems.add("shader " + stage + " missing: " + name(program) + " -> " + id);
            }
        }
        report("Shader programs with missing sources", problems);
    }

    @Test
    void soundEventsReferenceExistingFiles() throws IOException {
        List<String> problems = new ArrayList<>();
        for (Path file : ASSET_ROOTS.stream().map(r -> r.resolve("sounds.json")).filter(Files::exists).collect(Collectors.toList())) {
            for (Map.Entry<String, JsonElement> event : json(file).getAsJsonObject().entrySet()) {
                JsonElement sounds = event.getValue().getAsJsonObject().get("sounds");
                if (sounds == null) continue;
                for (JsonElement sound : sounds.getAsJsonArray()) {
                    String id = sound.isJsonObject() ? sound.getAsJsonObject().get("name").getAsString() : sound.getAsString();
                    if (!resolves(id, "sounds", ".ogg")) problems.add("sound file missing: " + event.getKey() + " -> " + id);
                }
            }
        }
        report("Sound events with missing files", problems);
    }

    @Test
    void particleDefinitionsReferenceExistingTextures() throws IOException {
        List<String> problems = new ArrayList<>();
        for (Path particle : files(ASSET_ROOTS, "particles", ".json")) {
            JsonElement textures = json(particle).getAsJsonObject().get("textures");
            if (textures == null) continue;
            for (JsonElement texture : textures.getAsJsonArray()) {
                if (!resolves(texture.getAsString(), "textures/particle", ".png")) {
                    problems.add("particle texture missing: " + name(particle) + " -> " + texture.getAsString());
                }
            }
        }
        report("Particles with missing textures", problems);
    }

    @Test
    void lootTablesAndRecipesReferenceRegisteredItems() throws IOException {
        List<String> problems = new ArrayList<>();
        for (String folder : List.of("loot_tables", "recipes")) {
            for (Path file : files(DATA_ROOTS, folder, ".json")) {
                Set<String> ids = new TreeSet<>();
                collectItemIds(json(file), ids);
                for (String id : ids) {
                    if (id.startsWith(MODID + ":") && !items.contains(id.substring(MODID.length() + 1))) {
                        problems.add("unregistered item: " + folder + "/" + name(file) + " -> " + id);
                    }
                }
            }
        }
        report("Data referencing items that are not registered", problems);
    }

    private static void collectItemIds(JsonElement element, Set<String> into) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            boolean itemEntry = object.has("type") && object.get("type").isJsonPrimitive()
                    && object.get("type").getAsString().equals("minecraft:item");
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                boolean idKey = entry.getKey().equals("item") || (entry.getKey().equals("name") && itemEntry);
                if (idKey && entry.getValue().isJsonPrimitive()) {
                    into.add(entry.getValue().getAsString());
                } else {
                    collectItemIds(entry.getValue(), into);
                }
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) collectItemIds(child, into);
        }
    }

    private static String source(String relative) throws IOException {
        return Files.readString(JAVA.resolve("net/agusdropout/bloodyhell/" + relative), StandardCharsets.UTF_8);
    }

    private static Set<String> langKeys() throws IOException {
        Set<String> keys = new HashSet<>();
        for (Path lang : files(ASSET_ROOTS, "lang", ".json")) {
            keys.addAll(json(lang).getAsJsonObject().keySet());
        }
        return keys;
    }

    @Test
    void everyEntityHasLangARendererAndAttributes() throws IOException {
        String types = source("entity/ModEntityTypes.java");
        String renderers = source("client/render/ModRenderers.java");
        String events = source("event/ModEvents.java");
        Set<String> keys = langKeys();
        Pattern entity = Pattern.compile("RegistryObject<EntityType<[^>]*>>\\s+(\\w+)\\s*=\\s*ENTITY_TYPES\\.register\\(\\s*\"([a-z0-9_]+)\"(.*?);", Pattern.DOTALL);
        Matcher m = entity.matcher(types);
        List<String> problems = new ArrayList<>();
        while (m.find()) {
            String constant = m.group(1);
            String id = m.group(2);
            String definition = m.group(3);
            Pattern used = Pattern.compile("\\b" + constant + "\\b");
            if (!keys.contains("entity." + MODID + "." + id)) problems.add("entity without lang: " + id);
            if (!used.matcher(renderers).find()) problems.add("entity without renderer: " + id);
            boolean living = definition.matches("(?s).*MobCategory\\.(MONSTER|CREATURE|AMBIENT|WATER_CREATURE|WATER_AMBIENT|UNDERGROUND_WATER_CREATURE|AXOLOTLS).*");
            if (living && !used.matcher(events).find()) problems.add("living entity without attributes: " + id);
        }
        report("Entities with missing registration pieces", problems);
    }

    @Test
    void everySoundEventIsDeclaredAndSubtitled() throws IOException {
        String sounds = source("sound/ModSounds.java");
        Set<String> keys = langKeys();
        JsonObject declared = json(ASSET_ROOTS.get(0).resolve("sounds.json")).getAsJsonObject();
        Matcher m = Pattern.compile("registerSoundEvents\\(\\s*\"([a-z0-9_.]+)\"\\)").matcher(sounds);
        List<String> problems = new ArrayList<>();
        while (m.find()) {
            String id = m.group(1);
            if (!declared.has(id)) {
                problems.add("sound not in sounds.json: " + id);
                continue;
            }
            JsonElement subtitle = declared.getAsJsonObject(id).get("subtitle");
            if (subtitle != null && !keys.contains(subtitle.getAsString())) problems.add("sound subtitle without lang: " + id + " -> " + subtitle.getAsString());
        }
        for (String key : declared.keySet()) {
            if (!sounds.contains("\"" + key + "\"")) problems.add("sounds.json entry without a registered event: " + key);
        }
        report("Sound events with missing pieces", problems);
    }

    @Test
    void everyParticleHasAProviderAndItsDefinition() throws IOException {
        String particles = source("particle/ModParticles.java");
        String client = source("event/ClientEvents.java");
        Matcher m = Pattern.compile("RegistryObject<[^>]*>\\s+(\\w+)\\s*=\\s*PARTICLE_TYPES\\.register\\(\\s*\"([a-z0-9_]+)\"").matcher(particles);
        List<String> problems = new ArrayList<>();
        while (m.find()) {
            String constant = m.group(1);
            String id = m.group(2);
            Pattern provider = Pattern.compile("(registerSpriteSet|registerSpecial)\\(\\s*ModParticles\\." + constant + "\\b");
            Matcher p = provider.matcher(client);
            if (!p.find()) {
                problems.add("particle without provider: " + id);
            } else if (p.group(1).equals("registerSpriteSet") && !assetExists("particles/" + id + ".json")) {
                problems.add("particle definition missing: " + id);
            }
        }
        report("Particles with missing pieces", problems);
    }
}
