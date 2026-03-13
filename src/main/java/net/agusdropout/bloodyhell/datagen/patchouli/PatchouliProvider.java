package net.agusdropout.bloodyhell.datagen.patchouli;

import com.google.gson.JsonObject;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.agusdropout.bloodyhell.datagen.patchouli.PatchouliUtils.*;

public class PatchouliProvider implements DataProvider {
    private final PackOutput output;
    private final List<CompletableFuture<?>> futures = new ArrayList<>();
    private final String bookId = "into_the_unknown_guide";

    public PatchouliProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        writeBookBase(cache);
        generateTheUnknown(cache);
        generateBloodDimension(cache);
        generateBloodMechanisms(cache);
        generateBloodFluids(cache);
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void generateBloodMechanisms(CachedOutput cache) {
        PatchouliCategoryBuilder category = PatchouliCategoryBuilder.create(
                "blood_mechanisms", "Blood Mechanisms",
                "Advanced machinery and structures powered by the essence of life.", "bloodyhell:sanguinite_pipe_item"
        );
        saveCategory(cache, category);

        // --- ENTRY: SACRIFICIAL DAGGER ---
        PatchouliEntryBuilder daggerEntry = PatchouliEntryBuilder.create("sacrificial_dagger", category.getId(), "The Sacrificial Dagger", "bloodyhell:sacrificial_dagger")
                .addSpotlightPage("bloodyhell:sacrificial_dagger", "A sacrificial blade used to extract life and manipulate blood machinery.")
                .addTextPage("The " + dagger() + " is more than just a weapon. It serves as a wrench and a catalyst for your mechanisms." + br() + br() +
                        "Right-clicking certain machines or pipes with this blade allows you to alter their configuration, such as applying fluid filters.");
        saveEntry(cache, daggerEntry);

        // --- ENTRY: BLOOD ALTARS ---
        PatchouliEntryBuilder altars = PatchouliEntryBuilder.create("blood_altars", category.getId(), "The Altars", "bloodyhell:main_blasphemous_blood_altar_item")
                .addTextPage("The " + blood("Blood Altar") + " is the centerpiece of your dark arts. It allows you to perform rituals and infuse items with raw essence.")
                .addSpotlightPage("bloodyhell:main_blood_altar", "The standard Altar used for basic blood manipulation.")
                .addSpotlightPage("bloodyhell:main_blasphemous_blood_altar_item", "An advanced, corrupted version required for the most " + madness("Blasphemous") + " of rituals.");
        saveEntry(cache, altars);

        // --- ENTRY: HARVESTING ---
        PatchouliEntryBuilder harvesters = PatchouliEntryBuilder.create("blood_harvesters", category.getId(), "Blood Harvesting", "bloodyhell:sanguinite_blood_harvester_item")
                .addSpotlightPage("bloodyhell:sanguinite_blood_harvester_item", "Extracts life force from the recently deceased.")
                .addTextPage("Place this machine near your killing floors. It will automatically collect the spilled essence of nearby dead entities." + br() + br() +
                        "Note that the type of fluid collected varies based on the victim." + br() + br() +
                        "For more details on fluid types, refer to the " + entryLink("blood_fluids", "blood_variants", "Blood Variants") + " entry.");
        saveEntry(cache, harvesters);

        // --- ENTRY: FLUID TRANSPORT (PIPES) ---
        PatchouliEntryBuilder pipes = PatchouliEntryBuilder.create("blood_pipes", category.getId(), "Fluid Transport", "bloodyhell:sanguinite_pipe_item")
                .addTextPage("To automate your systems, you must use piping."  + br() + br() +
                        "Pipes have specific flow states. By " + link("Right-Clicking") + " the connection point on a pipe, you can toggle it between " + blood("Push") + " and " + green("Pull") + " modes.")
                .addSpotlightPage("bloodyhell:sanguinite_pipe_item", "Standard piping for basic fluid transport." + br() + br() + "Sanguinite pipes can " + link("only") + " transport " + blood("Normal Blood") + " and " + infected("Infected Blood") )
                .addTextPage("Furthermore, you can restrict what flows through a pipe. By " + link("Right-Clicking") + " a pipe connection with a " + entryLink("blood_mechanisms", "sacrificial_dagger", "Sacrificial Dagger") + ", you can set a specific fluid filter." + br() + br() +
                        "To read more about the fluids you can filter, see the " + entryLink("blood_fluids", "blood_variants", "Blood Variants") + " section.")
                .addSpotlightPage("bloodyhell:rhnull_pipe_item", "High-efficiency piping reinforced with " + gold("Rhnull") + ", capable of handling extreme pressures." + br() + br() +
                        "Note: This pipe can handle all fluid types");
        saveEntry(cache, pipes);

        // --- ENTRY: STORAGE (TANKS) ---


        JsonObject tankMultiblock = new JsonObject();
        com.google.gson.JsonArray pattern = new com.google.gson.JsonArray();

        com.google.gson.JsonArray layer1 = new com.google.gson.JsonArray();
        layer1.add("TT");
        layer1.add("TT");
        pattern.add(layer1);

        com.google.gson.JsonArray layer2 = new com.google.gson.JsonArray();
        layer2.add("TT");
        layer2.add("T0");
        pattern.add(layer2);

        tankMultiblock.add("pattern", pattern);
        JsonObject mapping = new JsonObject();
        mapping.addProperty("T", "bloodyhell:sanguinite_tank");
        tankMultiblock.add("mapping", mapping);

        // 2. Build the Entry
        PatchouliEntryBuilder tanks = PatchouliEntryBuilder.create("blood_tanks", category.getId(), "Essence Storage", "bloodyhell:sanguinite_tank")
                .addTextPage("Tanks allow you to safely store vast amounts of collected fluids for later use." + br() + br() +
                        "The " + link("only") + " ways to extract fluids from a tank is with buckets, flasks, or by connecting pipes in " + green("pull") + " mode.")
                .addTextPage("Tanks are highly modular. They can be constructed with a square base of " + link("1x1, 2x2, or 3x3") + "." + br() + br() +
                        "The height (Y-level) is virtually " + link("unlimited") + ", allowing you to build massive vertical storage silos.")
                .addSpotlightPage("bloodyhell:sanguinite_tank", "A sturdy sanguinite tank for standard blood storage." + br() + br() +
                        "This tank can " + link("only") + " store " + blood("Normal Blood") + " and " + infected("Infected Blood") + ".")
                .addSpotlightPage("bloodyhell:rhnull_tank", "An advanced tank built with " + gold("Rhnull") + ", offering higher resistance." + br() + br() +
                        "This tank can store " + link("all") + " fluid types, including the volatile " + blasphemous("Viscous Blasphemy") + ".")
                .addMultiblockPage("2x2 Tank Example", "A demonstration of a 2x2x2 tank. All blocks in the structure must be of the same tier to form properly.", tankMultiblock);

        saveEntry(cache, tanks);

        // --- ENTRY: PROCESSING (INFUSION & CONDENSING) ---
        PatchouliEntryBuilder processing = PatchouliEntryBuilder.create("blood_processing", category.getId(), "Infusion & Condensing", "bloodyhell:sanguinite_infusor")
                .addSpotlightPage("bloodyhell:sanguinite_infusor", "Uses stored blood to imbue items with powerful, dark properties.")
                .addSpotlightPage("bloodyhell:sanguinite_condenser", "Turns liquid blood into solid, crystallized fragments over time.")
                .addSpotlightPage("bloodyhell:rhnull_condenser", "A faster, more efficient condenser made of " + gold("Rhnull") + ".");
        saveEntry(cache, processing);

        // --- ENTRY: GEM CRAFTING (LAPIDARY) ---
        PatchouliEntryBuilder lapidary = PatchouliEntryBuilder.create("sanguine_lapidary", category.getId(), "The Lapidary", "bloodyhell:sanguine_lapidary")
                .addTextPage("Crystallized blood is fragile. To harness its true power, it must be refined and grown.")
                .addSpotlightPage("bloodyhell:sanguine_lapidary", "A specialized workstation for refining and polishing Blood Gems.")
                .addSpotlightPage("bloodyhell:blood_gem_sprout", "A delicate sprout that, when nourished with blood, will slowly grow into a usable Blood Gem.");
        saveEntry(cache, lapidary);
    }



    private void generateBloodDimension(CachedOutput cache) {
        PatchouliCategoryBuilder category = PatchouliCategoryBuilder.create(
                "blood_dimension", "The Blood Dimension",
                "How to leave this world behind.", "bloodyhell:chalice_of_the_dammed"
        );
        saveCategory(cache, category);

        PatchouliEntryBuilder vesperHut = PatchouliEntryBuilder.create("vesper_hut", category.getId(), "Vesper's Hut", "minecraft:oak_log")
                .addTextPage("Legends speak of a mysterious hermit known as " + madness("Vesper") + ". His small hut only spawns deep within " + green("Forest Biomes") + ".")
                .addImagePage("Vesper's Abode", imagePath("vesper_hut_preview"), true)
                .addTextPage("Vesper is not easy to please. To earn his trust and the means to travel, you must bring him:" + br() + br() +
                        "- 10 Bones" + br() +
                        "- 1 Ender Pearl");
        saveEntry(cache, vesperHut);

        PatchouliEntryBuilder bloodPortal = PatchouliEntryBuilder.create("blood_portal", category.getId(), "Activating the Portal", "bloodyhell:chalice_of_the_dammed")
                .addTextPage("Once Vesper is satisfied, he will grant you the " + link("Chalice of the Damned") + ". " + br() + br() +
                        "The portal is hidden nearby within the facilities. Search for a wall that looks like this:")
                .addImagePage("The Hidden Gateway", imagePath("portal_hint"), true)
                .addTextPage("Hold the " + link("Chalice of the Damned") + " and interact with the center of the portal structure to tear open a rift to the " + blood("Blood Dimension") + ".");
        saveEntry(cache, bloodPortal);
    }

    private void generateTheUnknown(CachedOutput cache) {
        PatchouliCategoryBuilder category = PatchouliCategoryBuilder.create(
                "the_unknown", "The Unknown",
                "Creatures born of " + blood("Blood") + " and " + madness("Madness") + ".",
                "bloodyhell:gaze_of_the_unknown"
        );
        saveCategory(cache, category);

        PatchouliEntryBuilder lantern = PatchouliEntryBuilder.create("unknown_lantern", category.getId(), "The Unknown Lantern", "bloodyhell:gaze_of_the_unknown")
                .addEntityPage("bloodyhell:unknown_lantern", "Unknown Lantern", "A manifestation of cosmic dread.", 0.6f, 0.0f)
                .addTextPage("To survive, you must locate the " + madness("Rift") + " (shown on next page) and close it." + br() + br() +
                        "Failure: " + insight("-5") + br() +
                        "Success: " + insight("+10"))
                .addImagePage("The Rift", imagePath("rift_preview"), true);
        saveEntry(cache, lantern);

        PatchouliEntryBuilder echoShard = PatchouliEntryBuilder.create("blood_echo_shard", category.getId(), "Blood Echo Shard", "bloodyhell:blood_echo_shard")
                .addSpotlightPage("bloodyhell:blood_echo_shard", "A crystalline resonance.")
                .addTextPage("This shard will resonate when near a " + madness("Rift") + "." + br() + br() +
                        "Highly recommended for those with low " + insight("") + ".");
        saveEntry(cache, echoShard);
    }

    private void generateBloodFluids(CachedOutput cache) {
        PatchouliCategoryBuilder category = PatchouliCategoryBuilder.create(
                "blood_fluids", "Vitals & Essences",
                "Not all blood is created equal. The source dictates the power.", "bloodyhell:blood_bucket"
        );
        saveCategory(cache, category);

        PatchouliEntryBuilder variants = PatchouliEntryBuilder.create("blood_variants", category.getId(), "Blood Variants", "bloodyhell:blood_bucket")
                .addSpotlightPage("bloodyhell:blood_bucket", link(blood("Normal Blood")) + br() +
                        "Harvested from the slaughter of innocent, friendly creatures. Used for basic infusions. (pigs, cows, sheep, etc.)")
                .addSpotlightPage("bloodyhell:corrupted_blood_bucket", link(madness("Corrupted Blood")) + br() +
                        "Extracted from the corpses of hostile foes, both mundane and otherworldly. (zombies, skeletons, endermen, etc. and mod mobs alike)")
                .addSpotlightPage("bloodyhell:visceral_blood_bucket", link(infected("Infected Blood")) + br() +
                        "A highly infectious substance, harvested from friend or foes infected with an otherworldy illness." + br() + br() + "(Further research required.)")
                .addSpotlightPage("bloodyhell:viscous_blasphemy_bucket", link(blasphemous("Viscous Blasphemy")) + br() +
                        "A highly dangerous and complex substance. The exact nature of this fluid is yet to be fully understood..." + br() + br() + "(Further research required.)");
        saveEntry(cache, variants);
    }

    private void writeBookBase(CachedOutput cache) {
        JsonObject bookJson = PatchouliBookBuilder.create("The Unknown Guide", "A record of horrors.")
                .setModel(new ResourceLocation("patchouli", "book_brown"))
                .setI18n(false)
                .build();

        Path path = output.getOutputFolder().resolve("data/" + BloodyHell.MODID + "/patchouli_books/" + bookId + "/book.json");
        futures.add(DataProvider.saveStable(cache, bookJson, path));
    }

    private void saveCategory(CachedOutput cache, PatchouliCategoryBuilder builder) {
        Path path = output.getOutputFolder().resolve("assets/" + BloodyHell.MODID + "/patchouli_books/" + bookId + "/en_us/categories/" + builder.getId() + ".json");
        futures.add(DataProvider.saveStable(cache, builder.build(), path));
    }

    private void saveEntry(CachedOutput cache, PatchouliEntryBuilder builder) {
        Path path = output.getOutputFolder().resolve("assets/" + BloodyHell.MODID + "/patchouli_books/" + bookId + "/en_us/entries/" + builder.getCategoryId() + "/" + builder.getId() + ".json");
        futures.add(DataProvider.saveStable(cache, builder.build(), path));
    }

    @Override public String getName() { return "Bloody Hell Patchouli Datagen"; }
}