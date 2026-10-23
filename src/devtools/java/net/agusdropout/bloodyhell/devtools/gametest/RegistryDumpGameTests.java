package net.agusdropout.bloodyhell.devtools.gametest;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

@GameTestHolder(BloodyHell.MODID)
@PrefixGameTestTemplate(false)
public class RegistryDumpGameTests {
    private static final Path OUTPUT = Path.of("bh-registry-ids.txt");

    @GameTest(template = "empty", batch = "registrydump")
    public static void writeEveryRegisteredIdOfTheMod(GameTestHelper helper) throws IOException {
        Set<String> lines = new TreeSet<>();
        RegistryAccess access = helper.getLevel().registryAccess();
        access.registries().forEach(entry -> collect(entry.key().location().getPath(), entry.value(), lines));
        helper.getLevel().getRecipeManager().getRecipeIds()
                .filter(id -> id.getNamespace().equals(BloodyHell.MODID))
                .forEach(id -> lines.add("recipe\t" + id.getPath()));
        Files.write(OUTPUT, lines, StandardCharsets.UTF_8);
        helper.succeed();
    }

    private static <T> void collect(String registryName, Registry<T> registry, Set<String> lines) {
        for (ResourceLocation id : registry.keySet()) {
            if (id.getNamespace().equals(BloodyHell.MODID)) lines.add(registryName + "\t" + id.getPath());
        }
        registry.getTagNames().forEach(tag -> {
            if (tag.location().getNamespace().equals(BloodyHell.MODID)) lines.add("tag/" + registryName + "\t" + tag.location().getPath());
        });
    }
}
