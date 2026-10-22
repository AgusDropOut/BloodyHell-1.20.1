package net.agusdropout.bloodyhell.datagen;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagGenerator extends EntityTypeTagsProvider {

    public ModEntityTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, future, BloodyHell.MODID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "bloodyhell Entity Type Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(ModTags.Entities.INMUNE_TO_BLEEDING_BLOCK)
                .add(ModEntityTypes.EYESHELL_SNAIL.get())
                .add(ModEntityTypes.BLOOD_SEEKER.get())
                .add(ModEntityTypes.BLOODTHIRSTYBEAST.get())
                .add(ModEntityTypes.ONI.get())
                .add(ModEntityTypes.BLOODPIG.get())
                .add(ModEntityTypes.CRIMSON_RAVEN.get())
                .add(ModEntityTypes.OMEN_GAZER_ENTITY.get())
                .add(ModEntityTypes.VEINRAVER_ENTITY.get());

        tag(ModTags.Entities.INMUNE_TO_VISCERAL_EFFECT)
                .add(ModEntityTypes.ONI.get())
                .add(ModEntityTypes.OMEN_GAZER_ENTITY.get())
                .add(ModEntityTypes.VEINRAVER_ENTITY.get());

        tag(ModTags.Entities.SACRIFICEABLE_ENTITY)
                .add(EntityType.COW)
                .add(EntityType.SHEEP)
                .add(EntityType.CHICKEN)
                .add(EntityType.HORSE)
                .add(EntityType.PIG)
                .add(EntityType.RABBIT)
                .add(EntityType.WOLF)
                .add(EntityType.CAT)
                .add(EntityType.OCELOT)
                .add(EntityType.PARROT)
                .add(EntityType.PANDA)
                .add(EntityType.POLAR_BEAR);

        tag(ModTags.Entities.CORRUPTED_SACRIFICEABLE_ENTITY)

                .add(ModEntityTypes.BLOODTHIRSTYBEAST.get())
                .add(ModEntityTypes.BLOOD_SEEKER.get())
                .add(ModEntityTypes.CRIMSON_RAVEN.get())
                .add(ModEntityTypes.EYESHELL_SNAIL.get())
                .add(ModEntityTypes.BLOODPIG.get())

                .add(EntityType.ZOMBIE)
                .add(EntityType.SKELETON)
                .add(EntityType.SPIDER)
                .add(EntityType.CAVE_SPIDER)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.STRAY)
                .add(EntityType.PHANTOM)
                .add(EntityType.PILLAGER)
                .add(EntityType.CREEPER)
                .add(EntityType.ENDERMAN);

    }
}