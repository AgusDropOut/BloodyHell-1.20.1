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
        generateBloodSpells(cache);
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void generateBloodSpells(CachedOutput cache) {
        PatchouliCategoryBuilder category = PatchouliCategoryBuilder.create(
                "blood_spells", "Blood Spells & Gems",
                "Harnessing crystallized blood to cast devastating magic.", "bloodyhell:pure_blood_gem"
        );
        saveCategory(cache, category);

        // --- ENTRY: SPELL BOOKS ---
        PatchouliEntryBuilder spellbooks = PatchouliEntryBuilder.create("spellbooks", category.getId(), "Spell Books", "bloodyhell:blood_scratch_spellbook")
                .addSpotlightPage("tag:bloodyhell:spellbooks", link("Spell Books") + br() + br() +
                        "Tomes of ancient power that allow you to channel raw essence into devastating magical attacks.")
                .addTextPage("Casting magic comes at a cost. Each Spell Book consumes a specific amount of " + entryLink("blood_spells", "crimson_veil", "Crimson Veil") + " power upon use. If your reserves are too low, the spell will fail to cast.")
                .addTextPage("The true potential of a Spell Book lies in its modularity. By taking a book to the " + entryLink("blood_mechanisms", "sanguine_lapidary", "Sanguinite Lapidary") + ", you can socket up to 3 " + entryLink("blood_spells", "power_gems", "Blood Gems") + " into it, vastly enhancing its capabilities.");
        saveEntry(cache, spellbooks);

        // --- ENTRY: BLOOD GEMS ---
        PatchouliEntryBuilder powerGems = PatchouliEntryBuilder.create("power_gems", category.getId(), "Blood Gems", "bloodyhell:pure_blood_gem")
                .addTextPage("Blood Gems are the crystallized manifestation of raw power, essential for upgrading your spell books." + br() + br() +
                        "These gems are exclusively obtained by nurturing a " + entryLink("blood_mechanisms", "blood_gem_sprout", "Blood Gem Sprout") + "." + br() + br() +
                        "When harvested, each gem rolls a random stat value based on a rarity curve. The type and color of the gem is dictated by the mineral fed to the sprout.")
                .addSpotlightPage("bloodyhell:pure_blood_gem", blood("Pure Blood Gem") + br() +
                        "Cultivated from " + blood("Sanguinite") + "." + br() + br() +
                        "This crimson gem significantly amplifies the destructive force of a spell." + br() + br() +
                        link("Stat Roll:") + " +2.0 to +10.0 Damage")
                .addSpotlightPage("bloodyhell:aventurine_blood_gem", "$(9)Aventurine Blood Gem$()" + br() +
                        "Cultivated from " + "$(9)Lapis Lazuli$()." + br() + br() +
                        "This azure gem expands the physical presence and area of effect of a spell." + br() + br() +
                        link("Stat Roll:") + " +10% to +50% Size")
                .addSpotlightPage("bloodyhell:citrine_blood_gem", gold("Citrine Blood Gem") + br() +
                        "Cultivated from a " + gold("Gold Nugget") + "." + br() + br() +
                        "This radiant gem fractures the spell, multiplying its output." + br() + br() +
                        link("Stat Roll:") + " +1 to +3 Projectiles")
                .addSpotlightPage("bloodyhell:tanzarine_blood_gem", madness("Tanzarine Blood Gem") + br() +
                        "Cultivated from an " + madness("Amethyst Shard") + "." + br() + br() +
                        "This violet gem bends time, extending the lifespan of a spell." + br() + br() +
                        link("Stat Roll:") + " +0.5s to +3.0s Duration");
        saveEntry(cache, powerGems);

        // --- ENTRY: MAGIC GEMS (Ancient & Rhnull) ---
        PatchouliEntryBuilder magicGems = PatchouliEntryBuilder.create("magic_gems", category.getId(), "Arcane Gems", "bloodyhell:ancient_gem")
                .addTextPage("While standard Blood Gems modify existing spells, " + link("Arcane Gems") + " are the foundational catalysts used in powerful magic recipes to forge the spell books themselves.")
                .addSpotlightPage("bloodyhell:ancient_gem,bloodyhell:great_ancient_gem", link("Ancient Gem & Great Ancient Gem") + br() + br() +
                        "A solidified chunk of raw essence. It is created by processing fluid inside a " + entryLink("blood_mechanisms", "blood_condensers", "Condenser") + ". " + br() + br() +
                        "The size of the gem (Standard or Great) depends on the " + entryLink("blood_mechanisms", "gem_frames", "Gem Frame") + " used.")
                .addSpotlightPage("bloodyhell:ancient_rhnull_gem,bloodyhell:great_ancient_rhnull_gem", gold("Rhnull Gem & Great Rhnull Gem") + br() + br() +
                        "A hyper-dense arcane core. It is obtained by taking a standard or great Ancient Gem and processing it further inside a " + entryLink("blood_mechanisms", "sanguinite_infusor", "Blood Infusor") + ".");
        saveEntry(cache, magicGems);

        // --- ENTRY: CRIMSON VEIL ---
        PatchouliEntryBuilder crimsonVeil = PatchouliEntryBuilder.create("crimson_veil", category.getId(), "The Crimson Veil", "bloodyhell:filled_blood_flask")
                .addTextPage("To wield the power of Blood Spells, you must draw upon the " + blood("Crimson Veil") + ". This acts as your internal reserve of arcane energy, a mana pool bound directly to your life force." + br() + br() +
                        "Casting spells depletes the Veil. If the Veil runs dry, your spells will fail to manifest.")
                .addTextPage("To manually replenish the Crimson Veil, you must consume fluid directly from " + link("Blood Flasks") + "." + br() + br() +
                        "Drinking a standard " + blood("Blood Flask") + " will restore a moderate amount of power, while a " + corrupted("Corrupted Blood Flask") + " will restore significantly less.")
                .addSpotlightPage("tag:bloodyhell:crimson_veil_passive_recharger", link("Amulets of Ancestral Blood") + br() + br() +
                        "If manually drinking blood becomes burdensome, you can forge these ancient amulets. " + br() + br() +
                        "When equipped, they will automatically and passively regenerate your Crimson Veil over time.");
        saveEntry(cache, crimsonVeil);
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
        JsonObject altarMultiblock = new JsonObject();
        com.google.gson.JsonArray altarPattern = new com.google.gson.JsonArray();
        altarPattern.add("____A____");
        altarPattern.add("_________");
        altarPattern.add("_________");
        altarPattern.add("_________");
        altarPattern.add("A___0___A");
        altarPattern.add("_________");
        altarPattern.add("_________");
        altarPattern.add("_________");
        altarPattern.add("____A____");

        com.google.gson.JsonArray layerArray = new com.google.gson.JsonArray();
        for(int i = 0; i < altarPattern.size(); i++) {
            layerArray.add(altarPattern.get(i).getAsString());
        }
        com.google.gson.JsonArray finalPattern = new com.google.gson.JsonArray();
        finalPattern.add(layerArray);
        altarMultiblock.add("pattern", finalPattern);

        JsonObject altarMapping = new JsonObject();
        altarMapping.addProperty("0", "bloodyhell:main_blood_altar");
        altarMapping.addProperty("A", "bloodyhell:blood_altar");
        altarMultiblock.add("mapping", altarMapping);

        PatchouliEntryBuilder altars = PatchouliEntryBuilder.create("blood_altars", category.getId(), "Blood Altars", "bloodyhell:main_blood_altar")
                .addTextPage("The " + blood("Blood Altar") + " is the centerpiece of your dark arts. By utilizing it, you are directly interacting with the " + link("Ancient Blood Gods") + ", offering sacrifices in exchange for their power.")
                .addTextPage("Through these rituals, you can summon powerful " + link("Spell Books") + " and craft specialized items necessary to resist the horrors of the Unknown.")
                .addSpotlightPage("bloodyhell:main_blood_altar", "The Main Altar where the catalyst is placed." + br() + br() + "Critically, before the Main Altar can be used for any crafting ritual, it must first be filled using a " + corrupted("Corrupted Blood Flask") + ".")
                .addSpotlightPage("bloodyhell:blood_altar", "Standard Blood Altars act as pedestals. They hold the ingredients required for the ritual.")
                .addMultiblockPage("Ritual Setup", "The Main Altar sits in the center. Four standard Blood Altars must be placed in the cardinal directions, exactly 3 empty blocks away from the center.", altarMultiblock);
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

        // --- ENTRY: INFUSORS ---
        PatchouliEntryBuilder infusor = PatchouliEntryBuilder.create("sanguinite_infusor", category.getId(), "Blood Infusion", "bloodyhell:sanguinite_infusor")
                .addSpotlightPage("bloodyhell:sanguinite_infusor", "Uses stored blood to imbue items with powerful, dark properties.")
                .addTextPage("To function, the Infusor must be supplied with fluid via pipes set to " + blood("Push") + " mode." + br() + br() +
                        "It can " + link("only") + " process " + blood("Normal Blood") + " and " + infected("Infected Blood") + ".");
        saveEntry(cache, infusor);

        // --- ENTRY: GEM FRAMES ---
        PatchouliEntryBuilder gemFrames = PatchouliEntryBuilder.create("gem_frames", category.getId(), "Gem Frames", "bloodyhell:sanguinite_gem_frame")
                .addTextPage("To crystallize blood properly within a Condenser, a frame must be used to give it shape and structure.")
                .addSpotlightPage("bloodyhell:sanguinite_gem_frame", "Standard frame for shaping blood crystals.")
                .addSpotlightPage("bloodyhell:rhnull_gem_frame", "An advanced frame capable of containing denser, more volatile energies.")
                .addTextPage("The type of frame you use directly determines both the resulting " + link("gem's size") + " and the " + link("amount of blood") + " that will be consumed during the condensation process.");
        saveEntry(cache, gemFrames);

        // --- ENTRY: CONDENSERS ---
        PatchouliEntryBuilder condensers = PatchouliEntryBuilder.create("blood_condensers", category.getId(), "Blood Condensers", "bloodyhell:sanguinite_condenser")
                .addTextPage("Condensers turn liquid blood into solid, crystallized fragments. The process requires a " + entryLink("blood_mechanisms", "gem_frames", "Gem Frame") + " to shape the crystal." + br() + br() +
                        "A condenser can " + link("only") + " be pumped with " + link("one") + " type of fluid at a time.")
                .addSpotlightPage("bloodyhell:sanguinite_condenser", "Standard condenser." + br() + br() + "Can " + link("only") + " hold " + blood("Normal Blood") + " and " + infected("Infected Blood") + ".")
                .addSpotlightPage("bloodyhell:rhnull_condenser", "A highly resilient condenser made of " + gold("Rhnull") + "." + br() + br() +
                        "Can safely hold " + link("all") + " fluid types.");
        saveEntry(cache, condensers);

        // --- ENTRY: GEM CRAFTING (LAPIDARY) ---
        PatchouliEntryBuilder lapidary = PatchouliEntryBuilder.create("sanguine_lapidary", category.getId(), "The Lapidary", "bloodyhell:sanguine_lapidary")
                .addTextPage("The Sanguinite Lapidary is a specialized workstation designed to imbue and upgrade Spell Books with crystallized power." + br() + br() +
                        "By placing a Spell Book within, you can socket up to " + link("3") + " " + entryLink("blood_spells", "power_gems", "Blood Gems") + " into it.")
                .addSpotlightPage("bloodyhell:sanguine_lapidary", "Socketing gems significantly amplifies the spell's potential, altering its damage, range, or effect.");
        saveEntry(cache, lapidary);

        // --- ENTRY: BLOOD GEM SPROUT (1x1 Multiblock Trick) ---
        JsonObject sproutMultiblock = new JsonObject();
        com.google.gson.JsonArray sproutPattern = new com.google.gson.JsonArray();
        com.google.gson.JsonArray sproutLayer = new com.google.gson.JsonArray();
        sproutLayer.add("0");
        sproutPattern.add(sproutLayer);
        sproutMultiblock.add("pattern", sproutPattern);
        JsonObject sproutMapping = new JsonObject();
        sproutMapping.addProperty("0", "bloodyhell:blood_gem_sprout[age=2]");
        sproutMultiblock.add("mapping", sproutMapping);

        PatchouliEntryBuilder sprout = PatchouliEntryBuilder.create("blood_gem_sprout", category.getId(), "Blood Gem Sprout", "bloodyhell:blood_gem_sprout_seed")
                .addSpotlightPage("bloodyhell:blood_gem_sprout_seed", "A delicate sprout that must be carefully nurtured to grow Blood Gems.")
                .addTextPage("The sprout must be constantly supplied with fluid. You must connect pipes to it and set them to " + blood("Push") + " mode." + br() + br() +
                        "As it drinks the blood, it will slowly grow through various phases.")
                .addMultiblockPage("Asking Phase", "When the sprout reaches this state, it is waiting for a mineral catalyst.", sproutMultiblock)
                .addTextPage("Eventually, the sprout will reach a critical phase where it requires a specific mineral catalyst to crystallize properly. " + br() + br() +
                        "Depending on the mineral you give it, it will produce a different " + entryLink("blood_spells", "power_gems", "Blood Gem") + ":")
                .addTextPage("- " + link("Lapis Lazuli") + " -> Aventurine" + br() +
                        "- " + link("Amethyst Shard") + " -> Tanzarine" + br() +
                        "- " + link("Sanguinite") + " -> Pure Blood Gem" + br() +
                        "- " + link("Gold Nugget") + " -> Citrine");
        saveEntry(cache, sprout);
    }

    private void generateBloodDimension(CachedOutput cache) {
        PatchouliCategoryBuilder category = PatchouliCategoryBuilder.create(
                "blood_dimension", "The Blood Dimension",
                "How to leave this world behind. Find Vesper's hut", "bloodyhell:chalice_of_the_dammed"
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

        // --- ENTRY: SELIORA (BOSS) ---
        PatchouliEntryBuilder selioraBoss = PatchouliEntryBuilder.create("seliora_boss", category.getId(), "Seliora", "bloodyhell:seliora_essence")
                .addEntityPage("bloodyhell:seliora", "Seliora", "A corrupted mage who foolishly sought to make contact with the Unknown.", 0.6f, 0.0f)
                .addTextPage("She resides within the ancient pyramids scattered across the surface of the " + madness("Blasphemous Biome") + " in the Blood Dimension." + br() + br() +
                        "Defeating her yields her essence, a vital catalyst for forging dreadful tools.")
                .addSpotlightPage("bloodyhell:seliora_essence", link("Seliora's Essence") + br() + br() + "A remnant of her corrupted soul, left behind upon her defeat.")
                .addSpotlightPage("bloodyhell:blasphemous_twin_daggers,bloodyhell:blasphemous_hulking_mass_of_iron,bloodyhell:blasphemous_impaler", link("Blasphemous Arsenal") + br() + br() +
                        "Using Seliora's Essence at a standard " + link("Crafting Table") + " allows you to craft these devastating weapons.");
        saveEntry(cache, selioraBoss);

        // --- ENTRY: RITEKEEPER (BOSS) ---
        PatchouliEntryBuilder ritekeeperBoss = PatchouliEntryBuilder.create("ritekeeper_boss", category.getId(), "The Ritekeeper", "bloodyhell:ritekeeper_heart")
                .addEntityPage("bloodyhell:ritekeeper", "Ritekeeper", "A powerful practitioner of Blood Fire magic. Though he interacted with the Unknown, he was far more careful than Seliora.", 0.6f, 0.0f)
                .addTextPage("This formidable foe can be found lurking deep underground within the " + blood("Blood Biome") + " of the Blood Dimension, specifically sealed inside the " + link("Sanctum of the Unknown") + " dungeon.")
                .addSpotlightPage("bloodyhell:ritekeeper_heart", link("Ritekeeper's Soul") + br() + br() + "The still-beating heart and soul of the Ritekeeper, obtained upon his defeat.")
                .addSpotlightPage("bloodyhell:bloodfire_meteor_spellbook,bloodyhell:bloodfire_column_spellbook,bloodyhell:bloodfire_soul_spellbook", link("Blood Fire Spell Books") + br() + br() +
                        "By sacrificing the Ritekeeper's Soul at a standard " + entryLink("blood_mechanisms", "blood_altars", "Blood Altar") + ", you can craft these incredibly destructive spell books.");
        saveEntry(cache, ritekeeperBoss);
    }

    private void generateTheUnknown(CachedOutput cache) {
        PatchouliCategoryBuilder category = PatchouliCategoryBuilder.create(
                "the_unknown", "The Unknown",
                "Creatures born of " + blood("Blood") + " and " + madness("Madness") + ".",
                "bloodyhell:gaze_of_the_unknown"
        );
        saveCategory(cache, category);

        // --- ENTRY: INSIGHT ---
        PatchouliEntryBuilder insightEntry = PatchouliEntryBuilder.create("insight", category.getId(), "Insight", "minecraft:ender_eye")
                .addTextPage("Insight represents your comprehension of the cosmic horrors that lurk around you. You will begin this dark journey with " + link("0 Insight") + ".")
                .addTextPage("The primary way to gain Insight is by consuming the " + entryLink("the_unknown", "unknown_lantern", "Gaze of the Unknown") + "." + br() + br() +
                        "However, you must be careful: gaining Insight attracts the attention of visitors from other planes.")
                .addTextPage("When you have low Insight, certain entities will remain phased out of reality. You will not be able to hit them. Sometimes they will ignore you, but other times, they will hunt you from the shadows." + br() + br() +
                        "You cannot strike what you cannot comprehend.")
                .addTextPage("Once you cross a certain Insight threshold, the veil lifts, and you will be able to see these horrors clearly and fight back.");
        saveEntry(cache, insightEntry);

        // --- ENTRY: HERETICAL DAGGER ---
        PatchouliEntryBuilder hereticalDagger = PatchouliEntryBuilder.create("heretical_dagger", category.getId(), "Heretical Sacrificial Dagger", "bloodyhell:heretic_sacrificial_dagger")
                .addSpotlightPage("bloodyhell:heretic_sacrificial_dagger", "A tainted version of the Sacrificial Dagger, bound to the cosmic void.")
                .addTextPage("Simply holding the Heretical Sacrificial Dagger allows you to see exactly how much " + entryLink("the_unknown", "insight", "Insight") + " you possess.")
                .addTextPage("Furthermore, much like its " + entryLink("blood_mechanisms", "sacrificial_dagger", "lesser counterpart") + ", this dagger is used for sacrifices. " + br() + br() +
                        "If you slay a living entity with this weapon, you will offer its life to the unknown, rewarding you with an " + link("Unknown Entity Finger") + ".");
        saveEntry(cache, hereticalDagger);

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

        PatchouliEntryBuilder reliquary = PatchouliEntryBuilder.create("reliquary", category.getId(), "The Reliquary", "bloodyhell:reliquary")
                .addSpotlightPage("bloodyhell:reliquary", "A conduit used to call forth allies from the unknown.")
                .addTextPage("The Reliquary allows you to summon entities from the Unknown to fight by your side. " + br() + br() +
                        "However, your ability to maintain these summons is strictly limited by your total " + entryLink("the_unknown", "insight", "Insight") + " capacity.")
                .addSpotlightPage("tag:bloodyhell:reliquary_rune_item", link("Summoning Runes") + br() + br() +
                        "To summon an ally, you must place a specific Rune within the Reliquary. Each Rune corresponds to a unique entity and requires a different amount of capacity to maintain.")
                .addSpotlightPage("tag:bloodyhell:reliquary_upgrade_item", link("Reliquary Lenses") + br() + br() +
                        "By default, the Reliquary can only hold a limited number of Runes. You can expand its internal slots by installing Ocular Lenses.");
        saveEntry(cache, reliquary);

        // --- ENTRY: UNKNOWN PORTAL BLOCK ---
        PatchouliEntryBuilder unknownPortal = PatchouliEntryBuilder.create("unknown_portal", category.getId(), "Unknown Portal", "bloodyhell:unknown_portal_item")
                .addSpotlightPage("bloodyhell:unknown_portal_item", "A dormant gateway to places best left untouched.")
                .addTextPage("To awaken this structure, you must force-feed it raw life. Connect pipes to the block and supply it with blood in " + blood("Push") + " mode.")
                .addTextPage("Once enough blood is supplied, the portal will tear open and hostile arms will begin to emerge from the void. " + br() + br() +
                        "To stabilize the tear, you must feed " + link("living entities") + " directly into the grasp of the portal.")
                .addTextPage("As it consumes life, the portal will begin to secrete a dark, volatile byproduct. " + br() + br() +
                        "This substance must be extracted using pipes set to " + green("Pull") + " mode." + br() + br() +
                        "For details on what this fluid is, refer to the " + entryLink("blood_fluids", "blood_variants", "Blood Variants") + " section.")
                .addMultiblockPage("Portal Automation", "A basic setup. Pump blood in from one tank to activate the portal, and pull the resulting Viscous Blasphemy out into the other.", createUnknownPortalMultiblock());
        saveEntry(cache, unknownPortal);

        // --- ENTRY: BLASPHEMOUS ALTARS ---
        JsonObject blasphemousAltarMultiblock = new JsonObject();
        com.google.gson.JsonArray bAltarPattern = new com.google.gson.JsonArray();
        bAltarPattern.add("____A____");
        bAltarPattern.add("_________");
        bAltarPattern.add("_________");
        bAltarPattern.add("_________");
        bAltarPattern.add("A___0___A");
        bAltarPattern.add("_________");
        bAltarPattern.add("_________");
        bAltarPattern.add("_________");
        bAltarPattern.add("____A____");

        com.google.gson.JsonArray bLayerArray = new com.google.gson.JsonArray();
        for(int i = 0; i < bAltarPattern.size(); i++) {
            bLayerArray.add(bAltarPattern.get(i).getAsString());
        }
        com.google.gson.JsonArray bFinalPattern = new com.google.gson.JsonArray();
        bFinalPattern.add(bLayerArray);
        blasphemousAltarMultiblock.add("pattern", bFinalPattern);

        JsonObject bAltarMapping = new JsonObject();
        bAltarMapping.addProperty("0", "bloodyhell:main_blasphemous_blood_altar");
        bAltarMapping.addProperty("A", "bloodyhell:blasphemous_blood_altar");
        blasphemousAltarMultiblock.add("mapping", bAltarMapping);

        PatchouliEntryBuilder blasphemousAltars = PatchouliEntryBuilder.create("blasphemous_altars", category.getId(), "Blasphemous Altars", "bloodyhell:main_blasphemous_blood_altar_item")
                .addTextPage("The " + madness("Blasphemous Altar") + " serves as your focal point for interacting with the forces of the " + link("Unknown") + ".")
                .addSpotlightPage("bloodyhell:main_blasphemous_blood_altar_item", "The central catalyst." + br() + br() + "Crucially, before this altar can be used for crafting, it must first be filled using a standard " + link("Blood Flask") + " (not a Corrupted one).")
                .addSpotlightPage("bloodyhell:blasphemous_blood_altar_item", "Blasphemous pedestals. These hold the ingredients required for your rituals.")
                .addMultiblockPage("Ritual Setup", "The layout is identical to the standard altars: The Main Altar sits in the center, with four pedestals placed exactly 3 empty blocks away in the cardinal directions.", blasphemousAltarMultiblock);
        saveEntry(cache, blasphemousAltars);
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
                .addSpotlightPage("bloodyhell:corrupted_blood_bucket", link(corrupted("Corrupted Blood")) + br() +
                        "Extracted from the corpses of hostile foes, both mundane and otherworldly. (zombies, skeletons, endermen, etc. and mod mobs alike)")
                .addSpotlightPage("bloodyhell:visceral_blood_bucket", link(infected("Infected Blood")) + br() +
                        "A highly infectious substance, harvested from friend or foes infected with an otherworldy illness." + br() + br() + "(Further research required.)")
                .addSpotlightPage("bloodyhell:viscous_blasphemy_bucket", link(blasphemous("Viscous Blasphemy")) + br() +
                        "A highly dangerous and complex substance. It is a byproduct generated exclusively by the " + entryLink("the_unknown", "unknown_portal", "Unknown Portal") + " when it consumes living flesh.");
        saveEntry(cache, variants);

        // --- ENTRY: SOULS ---
        PatchouliEntryBuilder souls = PatchouliEntryBuilder.create("souls", category.getId(), "Manifested Souls", "bloodyhell:blood_flask")
                .addTextPage("Fluids are not the only essence that can be extracted from the living. By slaying a creature with the " + entryLink("blood_mechanisms", "sacrificial_dagger", "Sacrificial Dagger") + ", you can force its soul to manifest upon death.")
                .addTextPage("These souls are highly volatile and will " + link("vanish after a few seconds") + ". To capture them, you must quickly interact with the floating soul while holding an " + link("empty Blood Flask") + " in your hand.")
                .addImagePage("Normal Soul", imagePath("blood_soul_preview"), true)
                .addTextPage("Much like fluids, the nature of the soul is determined by the creature it belonged to. " + blood("Normal Souls") + " come from passive animals, while " + madness("Corrupted Souls") + " are torn from aggressive monsters.")
                .addImagePage("Corrupted Soul", imagePath("corrupted_soul_preview"), true);
        saveEntry(cache, souls);
    }

    private JsonObject createUnknownPortalMultiblock() {
        JsonObject multiblock = new JsonObject();
        com.google.gson.JsonArray pattern = new com.google.gson.JsonArray();

        // Top layer (Y=1)
        com.google.gson.JsonArray layerTop = new com.google.gson.JsonArray();
        layerTop.add("T   T");
        pattern.add(layerTop);

        // Bottom layer (Y=0)
        com.google.gson.JsonArray layerBot = new com.google.gson.JsonArray();
        layerBot.add("TP0PT");
        pattern.add(layerBot);

        multiblock.add("pattern", pattern);

        JsonObject mapping = new JsonObject();
        mapping.addProperty("T", "bloodyhell:sanguinite_tank");
        mapping.addProperty("P", "bloodyhell:sanguinite_pipe");
        mapping.addProperty("0", "bloodyhell:unknown_portal_block");
        multiblock.add("mapping", mapping);

        return multiblock;
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