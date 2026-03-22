package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.datagen.loot.ModLootTables;
import net.agusdropout.bloodyhell.datagen.patchouli.PatchouliProvider;
import net.agusdropout.bloodyhell.datagen.recipe.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModLootTables(packOutput));
        generator.addProvider(event.includeClient(), new ModRecipes(packOutput));
        generator.addProvider(event.includeServer(), new PatchouliProvider(packOutput));

        generator.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));

        ModBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(),
                new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));
        ModFluidTagGenerator fluidTagGenerator = generator.addProvider(event.includeServer(),
                new ModFluidTagGenerator(packOutput, lookupProvider, existingFileHelper));
        ModStructuresTagGenerator structureTagGenerator = generator.addProvider(event.includeServer(),
                new ModStructuresTagGenerator(packOutput, lookupProvider, existingFileHelper));
        ModEntityTagGenerator entityTagGenerator = generator.addProvider(event.includeServer(),
                new ModEntityTagGenerator(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));
    }
}
