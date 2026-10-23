package net.agusdropout.bloodyhell;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

class ProgressionSourcesTest {
    private static final Path SOURCES = Path.of("src/devtools/resources/progression-sources.txt");
    private static final Path JAVA = Path.of("src/main/java");
    private static final Path DATA = Path.of("src/main/resources/data/bloodyhell");
    private static final Pattern ITEM_CONSTANT = Pattern.compile("(\\w+)\\s*=\\s*(?:ITEMS\\.register|registerGeckoBlockItem|registerBlock\\w*)\\(\\s*\"([a-z0-9_]+)\"");

    private static final Map<String, String> constantOfItem = new HashMap<>();
    private static final Map<String, String> sourceOfClass = new HashMap<>();
    private static List<String[]> entries;

    @BeforeAll
    static void load() throws IOException {
        try (Stream<Path> files = Files.walk(JAVA)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList())) {
                String source = Files.readString(file, StandardCharsets.UTF_8);
                sourceOfClass.put(file.getFileName().toString().replace(".java", ""), source);
                Matcher matcher = ITEM_CONSTANT.matcher(source);
                while (matcher.find()) constantOfItem.putIfAbsent(matcher.group(2), matcher.group(1));
            }
        }
        entries = Files.readAllLines(SOURCES, StandardCharsets.UTF_8).stream()
                .filter(line -> !line.isBlank())
                .map(line -> line.split("\t"))
                .collect(Collectors.toList());
    }

    @Test
    void everyDeclaredItemIsRegisteredAndEveryLineHasItsParts() {
        List<String> problems = new ArrayList<>();
        for (String[] entry : entries) {
            if (entry.length != 3) {
                problems.add("malformed line: " + String.join("|", entry));
                continue;
            }
            String kind = entry[0];
            String name = entry[1];
            if (!List.of("creative", "code", "gap", "mob").contains(kind)) problems.add("unknown kind " + kind + " for " + name);
            if (!kind.equals("mob") && !constantOfItem.containsKey(name) && !constantOfItem.containsKey(name + "_item")) {
                problems.add(kind + " " + name + " is not an item registered in the code");
            }
        }
        report(problems);
    }

    @Test
    void everyCodeSourceNamesAClassThatReallyUsesTheItem() {
        List<String> problems = new ArrayList<>();
        for (String[] entry : entries) {
            if (!entry[0].equals("code")) continue;
            String constant = constantOfItem.getOrDefault(entry[1], constantOfItem.get(entry[1] + "_item"));
            String source = sourceOfClass.get(entry[2]);
            if (source == null) {
                problems.add(entry[1] + ": the class " + entry[2] + " does not exist");
            } else if (constant != null && !Pattern.compile("\\b" + constant + "\\b").matcher(source).find()) {
                problems.add(entry[1] + ": the class " + entry[2] + " never mentions " + constant);
            }
        }
        report(problems);
    }

    @Test
    void everyDeclaredMobIsInTheStructureAndTemplateItNames() throws IOException {
        Pattern evidence = Pattern.compile("structure (\\w+), template (\\w+)");
        List<String> problems = new ArrayList<>();
        for (String[] entry : entries) {
            if (!entry[0].equals("mob")) continue;
            Matcher matcher = evidence.matcher(entry[2]);
            if (!matcher.matches()) {
                problems.add(entry[1] + ": the evidence must read 'structure <name>, template <name>'");
                continue;
            }
            String structure = matcher.group(1);
            String template = matcher.group(2);
            if (!Files.exists(DATA.resolve("worldgen/structure/" + structure + ".json"))) problems.add(entry[1] + ": no structure definition " + structure);
            if (!structureSetNames(structure)) problems.add(entry[1] + ": no structure set generates " + structure);
            if (!templateHasEntity(template, entry[1])) problems.add(entry[1] + ": the template " + template + " does not contain the entity");
        }
        report(problems);
    }

    private static boolean structureSetNames(String structure) throws IOException {
        Path sets = DATA.resolve("worldgen/structure_set");
        if (!Files.isDirectory(sets)) return false;
        try (Stream<Path> files = Files.walk(sets)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                if (Files.readString(file, StandardCharsets.UTF_8).contains("bloodyhell:" + structure + "\"")) return true;
            }
        }
        return false;
    }

    private static boolean templateHasEntity(String template, String entity) throws IOException {
        try (Stream<Path> files = Files.walk(DATA.resolve("structures"))) {
            for (Path file : files.filter(p -> p.getFileName().toString().equals(template + ".nbt")).collect(Collectors.toList())) {
                byte[] raw = Files.readAllBytes(file);
                byte[] data;
                try (GZIPInputStream stream = new GZIPInputStream(new ByteArrayInputStream(raw))) {
                    data = stream.readAllBytes();
                } catch (IOException notCompressed) {
                    data = raw;
                }
                if (new String(data, StandardCharsets.ISO_8859_1).contains("bloodyhell:" + entity)) return true;
            }
        }
        return false;
    }

    private static void report(List<String> problems) {
        if (!problems.isEmpty()) {
            throw new AssertionError("src/devtools/resources/progression-sources.txt disagrees with the code:\n  " + String.join("\n  ", problems));
        }
    }
}
