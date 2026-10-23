package net.agusdropout.bloodyhell;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class GeckoBaseRulesTest {
    private static final Path JAVA = Path.of("src/main/java");
    private static final Pattern DECLARATION = Pattern.compile("class\\s+(\\w+)[^{;]*?\\bimplements\\s+([^{]*)\\{");

    private static final Set<String> ITEM_BASES = Set.of("BaseGeckoItem", "BaseGeckoBlockItem", "BaseGeckoArmorItem", "BaseMeleeComboWeapon");
    private static final Set<String> BLOCK_ENTITY_BASES = Set.of("BaseGeckoBlockEntity", "AbstractPipeBlockEntity",
            "BlasphemousBloodAltarBlockEntity", "MainBlasphemousBloodAltarBlockEntity", "SelioraRestingBlockEntity");

    @Test
    void geckoItemsExtendOneOfTheBases() throws IOException {
        assertOnlyAllowed("GeoItem", ITEM_BASES, "Extend BaseGeckoItem, BaseGeckoArmorItem or BaseMeleeComboWeapon instead of implementing GeoItem (docs/CONTRIBUTING.md)");
    }

    @Test
    void geckoBlockEntitiesExtendTheBaseOrAreKnownExceptions() throws IOException {
        assertOnlyAllowed("GeoBlockEntity", BLOCK_ENTITY_BASES, "Extend BaseGeckoBlockEntity instead of implementing GeoBlockEntity (docs/CONTRIBUTING.md)");
    }

    private static void assertOnlyAllowed(String geckoInterface, Set<String> allowed, String advice) throws IOException {
        Pattern usesInterface = Pattern.compile("\\b" + geckoInterface + "\\b");
        Set<String> found = new TreeSet<>();
        try (Stream<Path> files = Files.walk(JAVA)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList())) {
                String source = Files.readString(file, StandardCharsets.UTF_8);
                Matcher declaration = DECLARATION.matcher(source);
                while (declaration.find()) {
                    if (usesInterface.matcher(declaration.group(2)).find()) {
                        found.add(declaration.group(1));
                    }
                }
            }
        }
        Set<String> unexpected = new TreeSet<>(found);
        unexpected.removeAll(allowed);
        if (!unexpected.isEmpty()) {
            throw new AssertionError("Classes implementing " + geckoInterface + " outside the bases: " + List.copyOf(unexpected) + ". " + advice);
        }
    }
}
