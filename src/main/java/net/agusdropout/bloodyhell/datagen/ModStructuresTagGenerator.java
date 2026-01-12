package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.worldgen.structure.ModStructures;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModStructuresTagGenerator extends StructureTagsProvider {
    public ModStructuresTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, BloodyHell.MODID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(ModTags.Structures.MAUSOLEUM)
                .add(ModStructures.MAUSOLEUM);






        this.tag(ModTags.Structures.POOL)
                .add(ModStructures.POOL_KEY);
    }
}
