package net.agusdropout.bloodyhell;

import com.mojang.logging.LogUtils;

import net.agusdropout.bloodyhell.client.render.block.custom.renderer.BlasphemousBloodAltarRenderer;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.client.render.block.custom.renderer.*;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.capability.IBloodPortal;
import net.agusdropout.bloodyhell.config.ModClientConfigs;
import net.agusdropout.bloodyhell.config.ModCommonConfig;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.*;

import net.agusdropout.bloodyhell.client.render.entity.generic.GenericMinionModel;
import net.agusdropout.bloodyhell.client.render.entity.generic.GenericMinionRenderer;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.BastionOfTheUnknownModel;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.BurdenOfTheUnknownModel;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.BastionOfTheUnknownRenderer;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.BurdenOfTheUnknownRenderer;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.FailedSonOfTheUnknownRenderer;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.WeepingOcularRenderer;
import net.agusdropout.bloodyhell.entity.minions.custom.BurdenOfTheUnknownEntity;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.CrawlingDelusionRenderer;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.EchoOfTheNamelessRenderer;
import net.agusdropout.bloodyhell.fluid.ModFluidTypes;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.painting.ModPaintings;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.custom.CylinderParticle;
import net.agusdropout.bloodyhell.potion.ModPotions;
import net.agusdropout.bloodyhell.recipe.ModRecipes;
import net.agusdropout.bloodyhell.registry.BloodCapabilities;
import net.agusdropout.bloodyhell.screen.custom.screen.*;
import net.agusdropout.bloodyhell.screen.ModCreativeModeTabs;
import net.agusdropout.bloodyhell.screen.ModMenuTypes;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.ModPOIs;
import net.agusdropout.bloodyhell.worldgen.feature.ModFeatures;
import net.agusdropout.bloodyhell.worldgen.structure.ModStructures;

import net.agusdropout.bloodyhell.worldgen.tree.ModTreeDecoratorTypes;
import net.agusdropout.bloodyhell.worldgen.tree.ModTrunkPlacerTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Locale;


@Mod(BloodyHell.MODID)
public class
BloodyHell
{
    public static final String MODID = "bloodyhell";

    public static final Logger LOGGER = LogUtils.getLogger();

    
    public BloodyHell()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::portalTick);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModClientConfigs.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC);
        GeckoLib.initialize();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModSounds.register(modEventBus);
        ModPaintings.register(modEventBus);
        ModParticles.register(modEventBus);
        ModFluids.register(modEventBus);
        ModFluidTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModTrunkPlacerTypes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModTreeDecoratorTypes.register(modEventBus);
        ModStructures.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModPotions.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModPOIs.register(modEventBus);


    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

        event.enqueueWork(() -> {
            SpawnPlacements.register(net.agusdropout.bloodyhell.entity.ModEntityTypes.BLOOD_SEEKER.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.BLOODTHIRSTYBEAST.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.OMEN_GAZER_ENTITY.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.VEINRAVER_ENTITY.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.CRIMSON_RAVEN.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
                    Animal::checkMobSpawnRules);
            SpawnPlacements.register(ModEntityTypes.EYESHELL_SNAIL.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
                    Animal::checkMobSpawnRules);
            SpawnPlacements.register(ModEntityTypes.SCARLETSPECKLED_FISH.get(),
                    SpawnPlacements.Type.IN_WATER, Heightmap.Types.OCEAN_FLOOR,
                    AbstractFish::checkMobSpawnRules);
            SpawnPlacements.register(ModEntityTypes.BLOODPIG.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
                    Animal::checkMobSpawnRules);
            SpawnPlacements.register(ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkAnyLightMonsterSpawnRules);


            SpawnPlacements.register(ModEntityTypes.BLASPHEMOUS_MALFORMATION.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkAnyLightMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.HORNED_WORM.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.VEIL_STALKER.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.CINDER_ACOLYTE.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkAnyLightMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.FAILED_REMNANT.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkAnyLightMonsterSpawnRules);


            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BLOOD_FLOWER.getId(),ModBlocks.POTTED_BLOOD_FLOWER);
            ModMessages.register();

            MinecraftForge.EVENT_BUS.addListener(this::onPlayerInteract);






        });


    }
    private void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof HoeItem && (block == ModBlocks.BLOOD_GRASS_BLOCK.get() || block == ModBlocks.BLOOD_DIRT_BLOCK.get())) {
            player.swing(InteractionHand.MAIN_HAND);
            level.setBlockAndUpdate(pos, ModBlocks.BLOODDIRT_FARMLAND.get().defaultBlockState());
            heldItem.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(playerEntity.getUsedItemHand()));
            event.setCanceled(true);
        }

    }

    public void portalTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            player.getCapability(BloodCapabilities.BLOOD_PORTAL_CAPABILITY).ifPresent(IBloodPortal::handleBloodPortal);
        }
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }
}
