package net.agusdropout.bloodyhell.block.registry;

import net.agusdropout.bloodyhell.block.base.*;
import net.agusdropout.bloodyhell.block.custom.*;
import net.agusdropout.bloodyhell.block.custom.mushroom.CrimsonLureMushroomBlock;
import net.agusdropout.bloodyhell.block.custom.mushroom.VoraciousMushroomBlock;
import net.agusdropout.bloodyhell.worldgen.tree.BloodTreeGrower;
import net.agusdropout.bloodyhell.worldgen.tree.GiantBloodTreeGrower;
import net.agusdropout.bloodyhell.worldgen.tree.SmallBloodTreeGrower;
import net.agusdropout.bloodyhell.worldgen.tree.SoulTreeGrower;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class PlantBlocks {
    public static final RegistryObject<Block> EYEBALL_CROP = BLOCKS.register("eyeball_crop", ()-> new EyeballCropBlock(
            BlockBehaviour.Properties.copy(Blocks.WHEAT)));
    public static final RegistryObject<Block> BLOOD_SAPLING = registerBlock("blood_sapling", ()-> new SaplingBlock(new BloodTreeGrower(),
            BlockBehaviour.Properties.copy(Blocks.CHERRY_SAPLING)));
    public static final RegistryObject<Block> SMALL_BLOOD_SAPLING = registerBlock("small_blood_sapling", ()-> new SaplingBlock(new SmallBloodTreeGrower(),
            BlockBehaviour.Properties.copy(Blocks.CHERRY_SAPLING)));
    public static final RegistryObject<Block> HANGING_SOUL_TREE_LEAVES = registerBlock("hanging_soultree_leaves", ()-> new HangingSoulTreeLeavesBlock(
            BlockBehaviour.Properties.copy(Blocks.VINE)));
    public static final RegistryObject<Block> HANGING_BLOOD_TREE_LEAVES = registerBlock("hanging_bloodtree_leaves", ()-> new HangingSoulTreeLeavesBlock(
            BlockBehaviour.Properties.copy(Blocks.VINE).lightLevel((state)->15)) );
    public static final RegistryObject<Block> SOUL_SAPLING = registerBlock("soul_sapling", ()-> new SaplingBlock(new SoulTreeGrower(),
            BlockBehaviour.Properties.copy(Blocks.CHERRY_SAPLING)));
    public static final RegistryObject<Block> BLOOD_FLOWER = registerBlock("blood_flower", ()-> new FlowerBlock(()-> MobEffects.POISON,5,
            BlockBehaviour.Properties.copy(Blocks.ALLIUM).noCollission().noOcclusion().lightLevel((state)->6)));
    public static final RegistryObject<Block> BLOOD_GRASS = registerBlock("blood_grass", ()-> new FlowerBlock(()-> MobEffects.POISON,5,
            BlockBehaviour.Properties.copy(Blocks.GRASS).noCollission()));
    public static final RegistryObject<Block> POTTED_BLOOD_FLOWER = BLOCKS.register("potted_blood_flower", ()-> new FlowerPotBlock(()-> ((FlowerPotBlock) Blocks.FLOWER_POT) ,PlantBlocks.BLOOD_FLOWER,
            BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM).noOcclusion()));
    public static final RegistryObject<Block> BLOOD_BUSH = registerBlock("blood_bush", ()-> new DoublePlantBlock(
            BlockBehaviour.Properties.copy(Blocks.ROSE_BUSH)));
    public static final RegistryObject<Block> BLOOD_LILY_BLOCK = registerBlockWithoutBlockItem("blood_lily_block", ()-> new BloodLilyBlock(
            BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).mapColor(MapColor.COLOR_PINK).noOcclusion().noCollission().lightLevel((state)->20).instabreak().sound(SoundType.LILY_PAD).offsetType(BlockBehaviour.OffsetType.XZ)));
    public static final RegistryObject<Block> BLOOD_PETALS = registerBlock("blood_petals", ()-> new FlowerBlock(()-> MobEffects.HARM,5,
            BlockBehaviour.Properties.copy(Blocks.ALLIUM).noCollission().noOcclusion()));
    public static final RegistryObject<Block> LIGHT_MUSHROOM_BLOCK = registerBlock("light_mushroom_block", ()-> new FlowerBlock(()-> MobEffects.POISON,5,
            BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM_BLOCK).noCollission().noOcclusion().lightLevel((state)->15)));
    public static final RegistryObject<Block> BLOOD_WALL_MUSHROOM_BLOCK = registerBlock("blood_wall_mushroom_block", ()-> new BloodWallMushroomBlock(
            BlockBehaviour.Properties.copy(Blocks.COCOA).noOcclusion().dynamicShape().lightLevel( (state)->15 )));
    public static final RegistryObject<Block> DROOPVINE = registerBlock("droopvine", () -> new DroopvineBlock(
            BlockBehaviour.Properties.copy(Blocks.CAVE_VINES).strength(0.1F).lightLevel(Droopvine.light())));
    public static final RegistryObject<Block> DROOPVINE_PLANT = registerBlock("droopvine_plant", () -> new DroopvinePlantBlock(
            BlockBehaviour.Properties.copy(Blocks.CAVE_VINES_PLANT).strength(0.1F).lightLevel(Droopvine.light())));
    public static final RegistryObject<Block> BLOOD_SCRAPPER_PLANT = registerBlock("blood_scrapper_plant", () -> new TallGrowingPlant(
            BlockBehaviour.Properties.copy(Blocks.BAMBOO).strength(0.1F).dynamicShape().noOcclusion().noCollission().randomTicks().forceSolidOn().lightLevel((state)->20)));
    public static final RegistryObject<Block> BLOOD_SCRAPPER_PLANT_SAPLING = registerBlock("blood_scrapper_plant_sapling", () -> new BloodScrapperPlantSapling(BlockBehaviour.Properties.copy(Blocks.BAMBOO).strength(0.1F)));
    public static final RegistryObject<Block> GIANT_BLOOD_SAPLING = registerBlock("giant_blood_sapling", ()-> new SaplingBlock(new GiantBloodTreeGrower(),
            BlockBehaviour.Properties.copy(Blocks.CHERRY_SAPLING)));
    public static final RegistryObject<Block> VORACIOUS_MUSHROOM_BLOCK = registerBlock("voracious_mushroom_block", ()-> new VoraciousMushroomBlock(
            BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM_BLOCK).strength(0.5f).noOcclusion().lightLevel(state -> state.getValue(AbstractMushroomBlock.ACTIVE) ? 8 : 0)));
    public static final RegistryObject<Block> CRIMSON_LURE_MUSHROOM_BLOCK = registerBlock("crimson_lure_mushroom_block", () -> new CrimsonLureMushroomBlock(
            BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM).lightLevel(state -> state.getValue(AbstractMushroomBlock.ACTIVE) ? 8 : 0).noOcclusion()));
    public static final RegistryObject<Block> CINDER_BLOOM_CACTUS_ROOT = registerBlock("cinder_bloom_cactus_root", ()-> new CinderBloomCactusRoot(
            BlockBehaviour.Properties.copy(Blocks.CACTUS).strength(0.5f).noOcclusion()));
    public static final RegistryObject<Block> CINDER_BLOOM_CACTUS_CON = registerBlock("cinder_bloom_cactus_con", ()-> new CinderBloomCactusCon(
                    BlockBehaviour.Properties.copy(Blocks.CACTUS).strength(0.5f).noOcclusion()));
    public static final RegistryObject<Block> CINDER_BLOOM_CACTUS_CENTER = registerBlock("cinder_bloom_cactus_center", ()-> new CinderBloomCactusCenter(
            BlockBehaviour.Properties.copy(Blocks.CACTUS).strength(0.5f).noOcclusion()));
    public static final RegistryObject<Block> CINDER_BLOOM_CACTUS_FLOWER = registerBlock("cinder_bloom_cactus_flower", ()-> new CinderBloomCactusFlower(
            BlockBehaviour.Properties.copy(Blocks.SUNFLOWER).strength(0.5f).lightLevel(state -> state.getValue(CinderBloomCactusFlower.OPEN) ? 15 : 0).noOcclusion()));
    public static final RegistryObject<Block> SPIKY_GRASS = registerBlock("spiky_grass", ()-> new BlasphemousPlant(
            BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(0.1f).lightLevel((state)->15).noCollission().noOcclusion()));
    public static final RegistryObject<Block> ROUNDED_GRASS = registerBlock("rounded_grass", ()-> new BlasphemousPlant(
            BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(0.1f).noCollission().noOcclusion()));
    public static final RegistryObject<Block> STING_FLOWER = registerBlock("sting_flower", () -> new TallPlantBlock(
                    BlockBehaviour.Properties.copy(Blocks.ROSE_BUSH)
                            .lightLevel(state -> state.getValue(TallPlantBlock.PART) == TallPlantPart.TOP ? 15 : 0)
            ));

    private PlantBlocks() {}

    public static void init() {}
}
