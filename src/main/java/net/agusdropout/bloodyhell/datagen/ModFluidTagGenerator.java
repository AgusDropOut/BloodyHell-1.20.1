package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagGenerator extends FluidTagsProvider {
    public ModFluidTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, BloodyHell.MODID, existingFileHelper);
    }


    @Override
    protected void addTags(HolderLookup.Provider provider) {

       // this.tag(ModTags.Fluids.BLOODY_LIQUID)
       //         .add(ModFluids.FLOWING_BLOOD.get())
       //         .add(ModFluids.SOURCE_BLOOD.get());
       // this.tag(ModTags.Fluids.RHNULL_LIQUID)
       //         .add(ModFluids.FLOWING_RHNULL_BLOOD.get())
       //         .add(ModFluids.SOURCE_RHNULL_BLOOD.get());






    }
}
