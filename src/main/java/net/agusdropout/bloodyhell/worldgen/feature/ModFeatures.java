package net.agusdropout.bloodyhell.worldgen.feature;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.base.TallPlantBlock;
import net.agusdropout.bloodyhell.worldgen.feature.custom.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, BloodyHell.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> DROOPVINE = FEATURES.register("droopvine", () ->
            new DroopvineFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GLOWING_CRYSTAL = FEATURES.register("glowing_crystal", () ->
            new GlowingCrystalFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<ProbabilityFeatureConfiguration>> BLOOD_SCRAPPER_PLANT = FEATURES.register("blood_scrapper_plant", () ->
            new BloodScrapperPlantFeature(ProbabilityFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> STONE_PILLAR = FEATURES.register("stone_pillar", () ->
            new StonePillarFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CINDER_CACTUS = FEATURES.register("cinder_cactus", () ->
            new CinderBloomCactusFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CINDER_FLOWER = FEATURES.register("cinder_flower", () ->
            new CinderBloomCactusFlowerFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GIANT_SPINE = FEATURES.register("giant_spine", () ->
            new GiantSpineFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GIANT_ROOT = FEATURES.register("giant_root", () ->
            new GiantRootFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> STING_FLOWER_FEATURE = FEATURES.register("sting_flower", () ->
            new VariableHeightPlantFeature(NoneFeatureConfiguration.CODEC,(TallPlantBlock) PlantBlocks.STING_FLOWER.get()));


    public static void register(IEventBus eventBus){
        FEATURES.register(eventBus);
    }
}