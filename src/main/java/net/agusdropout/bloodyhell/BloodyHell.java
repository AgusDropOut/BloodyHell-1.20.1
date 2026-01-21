package net.agusdropout.bloodyhell;

import com.mojang.logging.LogUtils;

import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.capability.IBloodPortal;
import net.agusdropout.bloodyhell.client.render.BloodDimensionRenderInfo;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.client.*;

import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.fluid.ModFluidTypes;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModCreativeModeTab;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.painting.ModPaintings;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.custom.CylinderParticle;
import net.agusdropout.bloodyhell.potion.ModPotions;
import net.agusdropout.bloodyhell.recipe.ModRecipes;
import net.agusdropout.bloodyhell.registry.BloodCapabilities;
import net.agusdropout.bloodyhell.screen.BloodWorkBenchScreen;
import net.agusdropout.bloodyhell.screen.ModCreativeModeTabs;
import net.agusdropout.bloodyhell.screen.ModMenuTypes;
import net.agusdropout.bloodyhell.screen.VesperScreen;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.ModItemProperties;
import net.agusdropout.bloodyhell.villager.ModPOIs;
import net.agusdropout.bloodyhell.worldgen.dimension.ModDimensions;
import net.agusdropout.bloodyhell.worldgen.feature.ModFeatures;
import net.agusdropout.bloodyhell.worldgen.structure.ModStructures;

import net.agusdropout.bloodyhell.worldgen.tree.ModTreeDecoratorTypes;
import net.agusdropout.bloodyhell.worldgen.tree.ModTrunkPlacerTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.DimensionSpecialEffects;
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
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Locale;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(BloodyHell.MODID)
public class BloodyHell
{
    public static final String MODID = "bloodyhell";

    public static final Logger LOGGER = LogUtils.getLogger();

    
    public BloodyHell()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::registerDimEffects);

        MinecraftForge.EVENT_BUS.addListener(this::portalTick);
        ModCreativeModeTab.register(modEventBus);
        GeckoLib.initialize();
        ModPOIs.register(modEventBus);
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
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, // O MOTION_BLOCKING si es interior con techo
                    Monster::checkAnyLightMonsterSpawnRules); // <--- AQUÍ ESTÁ EL CAMBIO

            // CAMBIO 2: Blasphemous Malformation
            SpawnPlacements.register(ModEntityTypes.BLASPHEMOUS_MALFORMATION.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkAnyLightMonsterSpawnRules); // <--- USA ESTA REGLA;
            SpawnPlacements.register(ModEntityTypes.HORNED_WORM.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
            SpawnPlacements.register(ModEntityTypes.VEIL_STALKER.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);


            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BLOOD_FLOWER.getId(),ModBlocks.POTTED_BLOOD_FLOWER);
            ModMessages.register();
            ModItemProperties.addCustomItemProperties();
           //ModVillagers.registerPOIs();
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
            // Transformar el bloque en BLOODDIRT_FARMLAND
            level.setBlockAndUpdate(pos, ModBlocks.BLOODDIRT_FARMLAND.get().defaultBlockState());

            // Dañar la azada
            heldItem.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(playerEntity.getUsedItemHand()));

            // Detener la propagación del evento para evitar que se realice la acción predeterminada de la azada
            event.setCanceled(true);
        }

    }
    private void registerDimEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ModDimensions.DIMENSION_RENDERER, new BloodDimensionRenderInfo(128.0F, false, DimensionSpecialEffects.SkyType.NONE, false, false));
    }



    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {



        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            Minecraft.getInstance().particleEngine.register(ModParticles.CYLINDER_PARTICLE.get(), new CylinderParticle.Provider());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_BLOOD.get(), RenderType.translucent());  //RENDERIZADO DE LOS LIQUIDOS PARA QUE SEAN TRANSPARNETES
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_BLOOD.get(), RenderType.translucent());
            MenuScreens.register(ModMenuTypes.BLOOD_WORKBENCH_MENU.get(), BloodWorkBenchScreen::new);
            MenuScreens.register(ModMenuTypes.VESPER_MENU.get(), VesperScreen::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_SEEKER.get(), BloodSeekerRenderer::new);
            EntityRenderers.register(ModEntityTypes.OMEN_GAZER_ENTITY.get(), OmenGazerRenderer::new);
            EntityRenderers.register(ModEntityTypes.VEINRAVER_ENTITY.get(), VeinRaverEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOODY_SOUL_ENTITY.get(), BloodySoulEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.CORRUPTED_BLOODY_SOUL_ENTITY.get(), CorruptedBloodySoulEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOODTHIRSTYBEAST.get(), BloodThirstyBeastRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLASPHEMOUS_ARM_ENTITY.get(), BlasphemousArmEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.ONI.get(), OniRenderer::new);
            EntityRenderers.register(ModEntityTypes.SCARLETSPECKLED_FISH.get(), ScarletSpeckledFishRenderer::new);
            EntityRenderers.register(ModEntityTypes.CRIMSON_RAVEN.get(), CrimsonRavenRenderer::new);
            EntityRenderers.register(ModEntityTypes.EYESHELL_SNAIL.get(), EyeshellSnailRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOODPIG.get(), BloodPigRenderer::new);
            EntityRenderers.register(ModEntityTypes.CRYSTAL_PILLAR.get(), CrystalPillarRenderer::new);
            EntityRenderers.register(ModEntityTypes.UNKNOWN_EYE_ENTITY.get(), UnknownEyeEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.UNKNOWN_ENTITY_ARMS.get(), UnknownEntityArmsRenderer::new);
            EntityRenderers.register(ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN.get(), OffspringOfTheUnknownRenderer::new);
            EntityRenderers.register(ModEntityTypes.SELIORA.get(), SelioraRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLASPHEMOUS_MALFORMATION.get(), BlasphemousMalformationRenderer::new);
            EntityRenderers.register(ModEntityTypes.HORNED_WORM.get(), HornedWormRenderer::new);
            EntityRenderers.register(ModEntityTypes.VEIL_STALKER.get(), VeilStalkerRenderer::new);
            EntityRenderers.register(ModEntityTypes.SANGUINE_SACRIFICE_ENTITY.get(), SanguineSacrificeEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_SLASH_ENTITY.get(), BloodSlashEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_ARROW.get(), BloodArrowRenderer::new);
            EntityRenderers.register(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), EntityFallingBlockRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_NOVA_DEBRIS_ENTITY.get(), EntityFallingBlockRenderer::new);
            EntityRenderers.register(ModEntityTypes.ENTITY_CAMERA_SHAKE.get(), RenderNothing::new);
            BlockEntityRenderers.register(ModBlockEntities.BH_CHEST.get(),BHChestRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_ARROW.get(), BloodArrowRenderer::new);
            EntityRenderers.register(ModEntityTypes.VESPER.get(), VesperRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_PROJECTILE.get(), BloodSphereRenderer::new);
            EntityRenderers.register(ModEntityTypes.VIRULENT_ANCHOR_PROJECTILE.get(), VirulentAnchorProjectileEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_NOVA_ENTITY.get(), BloodNovaRenderer::new);
            EntityRenderers.register(ModEntityTypes.SMALL_CRIMSON_DAGGER.get(), SmallCrimsonDaggerRenderer::new);
            EntityRenderers.register(ModEntityTypes.VISCERAL_PROJECTILE.get(), VisceralProjectileEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.STARFALL_PROJECTILE.get(), StarfallProjectileEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_PORTAL_ENTITY.get(), BloodPortalRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLASPHEMOUS_SMALL_WHIRLWIND_ENTITY.get(), BlasphemousSmallWhirlwindEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLASPHEMOUS_WHIRLWIND_ENTITY.get(), BlasphemousWhirlwindEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLASPHEMOUS_TWIN_DAGGERS_CLONE.get(), BlasphemousTwinDaggersCloneRenderer::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_BUSH.get(), RenderType::canConsolidateConsecutiveGeometry);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_PETALS.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SPIKY_GRASS.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SANGUINE_CRUCIBLE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LIGHT_MUSHROOM_BLOCK.get(), RenderType.translucent());
            BlockEntityRenderers.register(ModBlockEntities.BLOOD_ALTAR.get(), BloodAltarRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.STAR_LAMP.get(), StarLampRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.SELIORA_RESTING.get(), SelioraRestingBlockRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.MAIN_BLOOD_ALTAR.get(), MainBloodAltarRenderer::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CINDER_BLOOM_CACTUS_ROOT.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CINDER_BLOOM_CACTUS_CON.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CINDER_BLOOM_CACTUS_FLOWER.get(), RenderType.cutout());
            EntityRenderers.register(ModEntityTypes.CYCLOPS_ENTITY.get(), CyclopsRenderer::new);
            EntityRenderers.register(ModEntityTypes.SPECIAL_SLASH.get(), SpecialSlashRenderer::new);
            EntityRenderers.register(ModEntityTypes.ENTITY_CAMERA_SHAKE.get(), RenderNothing::new);
            EntityRenderers.register(ModEntityTypes.BLASPHEMOUS_SPINES.get(), BlasphemousSpinesRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLASPHEMOUS_SPEAR.get(), BlasphemousSpearRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLASPHEMOUS_IMPALER_ENTITY.get(), BlasphemousImpalerEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.GRAVE_WALKER_ENTITY.get(), GraveWalkerEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.TENTACLE_ENTITY.get(), TentacleEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_SLASH_DECAL.get(), BloodSlashDecalRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_STAIN_ENTITY.get(), BloodStainRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_CLOT_PROJECTILE.get(), BloodClotRenderer::new);
            EntityRenderers.register(ModEntityTypes.RITEKEEPER.get(), RitekeeperRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_FIRE_SOUL.get(), BloodFireSoulRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_FIRE_COLUMN_PROJECTILE.get(), BloodFireColumnRenderer::new);
            EntityRenderers.register(ModEntityTypes.BLOOD_FIRE_METEOR_PROJECTILE.get(), BloodFireMeteorRenderer::new);



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
