package net.agusdropout.bloodyhell.client.render;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.client.render.block.custom.renderer.*;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.*;
import net.agusdropout.bloodyhell.client.render.entity.generic.ConfigurableGeoEntityRenderer;
import net.agusdropout.bloodyhell.client.render.entity.generic.ConfigurableGeoModel;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.client.render.entity.generic.ConfigurableInsightEntityRenderer;
import net.agusdropout.bloodyhell.client.render.entity.layer.BloodFireGeoLayer;
import net.agusdropout.bloodyhell.entity.custom.BlasphemousArmEntity;
import net.agusdropout.bloodyhell.entity.custom.BlasphemousMalformationEntity;
import net.agusdropout.bloodyhell.entity.custom.BloodPigEntity;
import net.agusdropout.bloodyhell.entity.custom.BloodSeekerEntity;
import net.agusdropout.bloodyhell.entity.custom.BloodThirstyBeastEntity;
import net.agusdropout.bloodyhell.entity.custom.BloodySoulEntity;
import net.agusdropout.bloodyhell.entity.custom.CorruptedBloodySoulEntity;
import net.agusdropout.bloodyhell.entity.custom.CrimsonRavenEntity;
import net.agusdropout.bloodyhell.entity.custom.EyeshellSnailEntity;
import net.agusdropout.bloodyhell.entity.custom.GraveWalkerEntity;
import net.agusdropout.bloodyhell.entity.custom.HornedWormEntity;
import net.agusdropout.bloodyhell.entity.custom.OffspringOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.custom.OmenGazerEntity;
import net.agusdropout.bloodyhell.entity.custom.OniEntity;
import net.agusdropout.bloodyhell.entity.custom.ScarletSpeckledFishEntity;
import net.agusdropout.bloodyhell.entity.custom.UnknownEyeEntity;
import net.agusdropout.bloodyhell.entity.custom.VeilStalkerEntity;
import net.agusdropout.bloodyhell.entity.custom.VeinraverEntity;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.BastionOfTheUnknownModel;
import net.agusdropout.bloodyhell.client.render.entity.custom.model.BurdenOfTheUnknownModel;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.BastionOfTheUnknownRenderer;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.BurdenOfTheUnknownRenderer;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.FailedSonOfTheUnknownRenderer;
import net.agusdropout.bloodyhell.client.render.entity.custom.renderer.WeepingOcularRenderer;
import net.agusdropout.bloodyhell.entity.custom.SanguineSacrificeEntity;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSmallWhirlwindEntity;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSpearEntity;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSpinesEntity;
import net.agusdropout.bloodyhell.entity.projectile.StarfallProjectile;
import net.agusdropout.bloodyhell.entity.projectile.VirulentAnchorProjectileEntity;
import net.agusdropout.bloodyhell.screen.ModMenuTypes;
import net.agusdropout.bloodyhell.screen.custom.screen.*;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModRenderers {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        registerCreatureRenderers(event);
        registerProjectileRenderers(event);
        registerEffectAndDecalRenderers(event);
        registerMinionAndBossRenderers(event);
        registerBlockEntityRenderers(event);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        registerScreenMenus();
        registerBlockRenderLayers();
    }


    private static <T extends LivingEntity & GeoAnimatable> Consumer<ConfigurableGeoEntityRenderer<T>> bloodFire() {
        return renderer -> renderer.addRenderLayer(new BloodFireGeoLayer<>(renderer));
    }

    private static void registerCreatureRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(ModEntityTypes.BLOOD_SEEKER.get(), context ->
                ConfigurableGeoEntityRenderer.<BloodSeekerEntity>builder(
                                context, new ConfigurableGeoModel<>("bloodseeker", "head", -1.0f, -1.0f))
                        .shadow(0.8f).glowing().scale(2.0f)
                        .layer(bloodFire())
                        .build());
        event.registerEntityRenderer(ModEntityTypes.OMEN_GAZER_ENTITY.get(), context ->
                ConfigurableGeoEntityRenderer.<OmenGazerEntity>builder(context,
                        new ConfigurableGeoModel<>("omen_gazer_entity", "head", -1.0f, -1.0f))
                        .shadow(0.8f).glowing().scale(0.9f)
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.VEINRAVER_ENTITY.get(), context ->
                ConfigurableGeoEntityRenderer.<VeinraverEntity>builder(
                                context, new ConfigurableGeoModel<>("veinraver_entity", "head", 1.0f, 1.0f))
                        .shadow(0.8f).glowing().scale(0.9f)
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.BLOODY_SOUL_ENTITY.get(), context ->
                ConfigurableGeoEntityRenderer.<BloodySoulEntity>builder(
                                context, new ConfigurableGeoModel<>("bloody_soul_entity", "head", -1.0f, -1.0f))
                        .shadow(0.0f).glowing()
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.CORRUPTED_BLOODY_SOUL_ENTITY.get(), context ->
                ConfigurableGeoEntityRenderer.<CorruptedBloodySoulEntity>builder(
                                context, new ConfigurableGeoModel<>("corrupted_bloody_soul_entity", "head", -1.0f, -1.0f))
                        .shadow(0.0f).glowing()
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.BLOODTHIRSTYBEAST.get(), context ->
                ConfigurableGeoEntityRenderer.<BloodThirstyBeastEntity>builder(context,
                        new ConfigurableGeoModel<>("bloodthirstybeast"))
                        .shadow(0.1f)
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.ONI.get(), context ->
                ConfigurableGeoEntityRenderer.<OniEntity>builder(context,
                                new ConfigurableGeoModel<>("oni", "head", -1.0f, -1.0f))
                        .shadow(0.8f).glowing().scale(2.0f)
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.SCARLETSPECKLED_FISH.get(), context ->
                ConfigurableGeoEntityRenderer.<ScarletSpeckledFishEntity>builder(context,
                                new ConfigurableGeoModel<>("scarletspeckledfish"))
                        .shadow(0.2f)
                        .scaleBy(entity -> entity.isBaby() ? 0.075f : 0.15f)
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.CRIMSON_RAVEN.get(), context ->
                ConfigurableGeoEntityRenderer.<CrimsonRavenEntity>builder(context,
                        new ConfigurableGeoModel<>("crimsonraven", "head", 1.0f, 1.0f))
                        .shadow(0.8f).glowing()
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.EYESHELL_SNAIL.get(), context ->
                ConfigurableGeoEntityRenderer.<EyeshellSnailEntity>builder(context,
                        new ConfigurableGeoModel<>("eyeshellsnail", "head", -1.0f, -1.0f))
                        .shadow(0.8f).glowing()
                        .scaleBy(entity -> entity.isBaby() ? 0.2f : 0.4f)
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.BLOODPIG.get(), context ->
                ConfigurableGeoEntityRenderer.<BloodPigEntity>builder(context,
                        new ConfigurableGeoModel<>("bloodpig", "head", -0.5f, -0.5f))
                        .shadow(0.8f).scale(1.5f)
                        .layer(bloodFire()).build());


        event.registerEntityRenderer(ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN.get(), context ->
                new ConfigurableInsightEntityRenderer<
                        OffspringOfTheUnknownEntity>(
                        context, new ConfigurableGeoModel<>("offspring_of_the_unknown", "head", -1.0f, -1.0f),
                        0.0001f, entity -> 1.0f));
        event.registerEntityRenderer(ModEntityTypes.SELIORA.get(), SelioraRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLASPHEMOUS_MALFORMATION.get(), context ->
                new ConfigurableInsightEntityRenderer<
                        BlasphemousMalformationEntity>(context,
                        new ConfigurableGeoModel<>("blasphemous_malformation", "head", -1.0f, -1.0f),
                        0.00008f, entity -> 1.0f));


        event.registerEntityRenderer(ModEntityTypes.HORNED_WORM.get(), context ->
                ConfigurableGeoEntityRenderer.<HornedWormEntity>builder(
                                context, new ConfigurableGeoModel<HornedWormEntity>("horned_worm", "horned_worm",
                                        "head", -1.0f, -1.0f,
                                        (model, entity, animationState) -> {
                                            var body = model.getAnimationProcessor().getBone("horned_worm");
                                            if (body != null) {
                                                body.setHidden(entity.isBurrowed());
                                            }
                                        }))
                        .shadow(0.001f).glowing()
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.VEIL_STALKER.get(), context ->
                ConfigurableGeoEntityRenderer.<VeilStalkerEntity>builder(context,
                        new ConfigurableGeoModel<>("veil_stalker", "head", -1.0f, -1.0f))
                        .shadow(0.001f).glowing()
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.GRAVE_WALKER_ENTITY.get(), context ->
                ConfigurableGeoEntityRenderer.<GraveWalkerEntity>builder(context,
                        new ConfigurableGeoModel<>("grave_walker", "head", -1.0f, -1.0f))
                        .shadow(0.8f).glowing().scale(0.6f)
                        .layer(bloodFire()).build());


        event.registerEntityRenderer(ModEntityTypes.RITEKEEPER.get(), RitekeeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CINDER_ACOLYTE.get(), CinderAcolyteRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.FAILED_REMNANT.get(), FailedRemnantRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CYCLOPS_ENTITY.get(), CyclopsRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.VESPER.get(), VesperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_SOUL.get(), BloodSoulRenderer::new);
    }


    private static void registerProjectileRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BLOOD_ARROW.get(), BloodArrowRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_PROJECTILE.get(), BloodSphereRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.VIRULENT_ANCHOR_PROJECTILE.get(), context ->
                ConfigurableGeoEntityRenderer.<VirulentAnchorProjectileEntity>builder(context,
                                new ConfigurableGeoModel<>("virulent_anchor_projectile", "virulent_anchor_projectile",
                                        null, 0.0f, 0.0f,
                                        ConfigurableGeoModel.alignBoneToRotation("virulent_anchor_projectile")))
                        .shadow(0.01f).glowing().build());
        event.registerEntityRenderer(ModEntityTypes.SMALL_CRIMSON_DAGGER.get(), SmallCrimsonDaggerRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.VISCERAL_PROJECTILE.get(), VisceralProjectileEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.STARFALL_PROJECTILE.get(), context ->
                ConfigurableGeoEntityRenderer.<StarfallProjectile>builder(context,
                                new ConfigurableGeoModel<>("starfall_projectile", "starfall_projectile",
                                        null, 0.0f, 0.0f,
                                        ConfigurableGeoModel.alignBoneToRotation("starfall_projectile")))
                        .shadow(0.01f).glowing()
                        .scaleBy(entity -> 1.0f + (1.0f - entity.getLifeTicks() / 200.0f) * 1.5f)
                        .build());
        event.registerEntityRenderer(ModEntityTypes.BLASPHEMOUS_SPINES.get(), context ->
                ConfigurableGeoEntityRenderer.<BlasphemousSpinesEntity>builder(context,
                                new ConfigurableGeoModel<>("blasphemous_spines"))
                        .scaleBy(entity -> 0.6f + (entity.getId() % 5) * 0.05f,
                                entity -> 0.7f + (entity.getId() % 3) * 0.1f)
                        .build());
        event.registerEntityRenderer(ModEntityTypes.BLASPHEMOUS_SPEAR.get(), context ->
                ConfigurableGeoEntityRenderer.<BlasphemousSpearEntity>builder(context,
                                new ConfigurableGeoModel<>("blasphemous_spear"))
                        .shadow(0.3f).glowing()
                        .scaleBy(entity -> 0.8f + (entity.getId() % 5) * 0.05f,
                                entity -> 0.8f + (entity.getId() % 3) * 0.15f)
                        .build());
        event.registerEntityRenderer(ModEntityTypes.BLASPHEMOUS_IMPALER_ENTITY.get(), BlasphemousImpalerEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_CLOT_PROJECTILE.get(), BloodClotRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_FIRE_COLUMN_PROJECTILE.get(), BloodFireColumnRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_FIRE_METEOR_PROJECTILE.get(), BloodFireMeteorRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.RHNULL_IMPALER_PROJECTILE.get(), RhnullImpalerRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.RHNULL_HEAVY_SWORD_PROJECTILE.get(), RhnullIHeavySwordRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.RHNULL_DROPLET_PROJECTILE.get(), SimpleColoredLineProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.WEEPING_TEAR_PROJECTILE.get(), SimpleColoredLineProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.VISCOUS_PROJECTILE.get(), ViscousProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.ORBITAL_FRENZIED_PROJECTILE.get(), OrbitalFrenziedProjectileRenderer::new);
    }


    private static void registerEffectAndDecalRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.CRYSTAL_PILLAR.get(), CrystalPillarRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SANGUINE_SACRIFICE_ENTITY.get(), context -> {
            float verticalScale = (float) (0.8 + 0.6 * Math.random());
            return ConfigurableGeoEntityRenderer.<SanguineSacrificeEntity>builder(context,
                            new ConfigurableGeoModel<>("sanguine_sacrifice_entity"))
                    .shadow(0.8f).glowing()
                    .scaleBy(entity -> 1.0f, entity -> verticalScale)
                    .build();
        });
        event.registerEntityRenderer(ModEntityTypes.BLOOD_SLASH_ENTITY.get(), BloodSlashEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SPECIAL_SLASH.get(), SpecialSlashRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), EntityFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_NOVA_DEBRIS_ENTITY.get(), EntityFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.ENTITY_CAMERA_SHAKE.get(), RenderNothing::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_NOVA_ENTITY.get(), BloodNovaRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_PORTAL_ENTITY.get(), BloodPortalRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.TENTACLE_ENTITY.get(), TentacleEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_SLASH_DECAL.get(), BloodSlashDecalRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_STAIN_ENTITY.get(), BloodStainRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLOOD_FIRE_SOUL.get(), BloodFireSoulRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.INFESTATION_DECAL.get(), InfestationDecalRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.RHNULL_PAIN_THRONE.get(), RhnullPainThroneRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.RHNULL_ORB_EMITTER_ENTITY.get(), RhnullOrbEmitterRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLACK_HOLE.get(), BlackHoleRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.UNKNOWN_LANTERN_RIFT.get(), BlackHoleRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.NAMELESS_TRIAL_RIFT.get(), BlackHoleRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.FRENZIED_FIRE.get(), FrenziedFireRenderer::new);
    }


    private static void registerMinionAndBossRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BLASPHEMOUS_ARM_ENTITY.get(), context ->
                ConfigurableGeoEntityRenderer.<BlasphemousArmEntity>builder(context,
                                new ConfigurableGeoModel<>("blasphemous_arm_entity"))
                        .shadow(0.8f).glowing()
                        .layer(bloodFire()).build());
        event.registerEntityRenderer(ModEntityTypes.UNKNOWN_EYE_ENTITY.get(), context ->
                ConfigurableGeoEntityRenderer.<UnknownEyeEntity>builder(
                                context, new ConfigurableGeoModel<>("unknown_eye_entity", "unknown_eye_entity", "eye",
                                        (entity, bone) -> {
                                            bone.setRotX(-entity.getLookPitch() * Mth.DEG_TO_RAD);
                                            bone.setRotY(-entity.getLookYaw() * Mth.DEG_TO_RAD);
                                        }))
                        .shadow(0.8f).build());
        event.registerEntityRenderer(ModEntityTypes.UNKNOWN_ENTITY_ARMS.get(), context ->
                ConfigurableGeoEntityRenderer.builder(context,
                                new ConfigurableGeoModel<>("unknown_entity_arms"))
                        .shadow(0.8f).glowing().build());
        event.registerEntityRenderer(ModEntityTypes.BLASPHEMOUS_SMALL_WHIRLWIND_ENTITY.get(), context ->
                ConfigurableGeoEntityRenderer.<BlasphemousSmallWhirlwindEntity>builder(
                                context, new ConfigurableGeoModel<>("blasphemous_small_whirlwind_entity",
                                        "blasphemous_small_whirlwind_entity",
                                        (model, entity, animationState) -> {
                                            float time = entity.tickCount + animationState.getPartialTick();
                                            var mainRing = model.getAnimationProcessor().getBone("mainring");
                                            if (mainRing != null) {
                                                mainRing.setRotY(time * 0.15f);
                                            }
                                        }))
                        .shadow(0.8f).glowing().build());
        event.registerEntityRenderer(ModEntityTypes.BLASPHEMOUS_WHIRLWIND_ENTITY.get(), BlasphemousWhirlwindEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BLASPHEMOUS_TWIN_DAGGERS_CLONE.get(), BlasphemousTwinDaggersCloneRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.WEEPING_OCULAR.get(), WeepingOcularRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.FAILED_SON_OF_THE_UNKNOWN.get(), FailedSonOfTheUnknownRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.UNKNOWN_LANTERN.get(), UnknownLanternRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.HOSTILE_UNKNOWN_ENTITY_ARMS.get(), HostileUnknownEntityArmsRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BURDEN_OF_THE_UNKNOWN.get(), context -> new BurdenOfTheUnknownRenderer(context, new BurdenOfTheUnknownModel()));
        event.registerEntityRenderer(ModEntityTypes.BASTION_OF_THE_UNKNOWN.get(), context -> new BastionOfTheUnknownRenderer(context, new BastionOfTheUnknownModel()));
        event.registerEntityRenderer(ModEntityTypes.CRAWLING_DELUSION.get(),
                context -> new CrawlingDelusionRenderer(context));
        event.registerEntityRenderer(ModEntityTypes.ECHO_OF_THE_NAMELESS.get(),
                context -> new EchoOfTheNamelessRenderer(context));
    }


    private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BH_CHEST.get(), BHChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SANGUINITE_CONDENSER_BE.get(), CondenserRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RHNULL_CONDENSER_BE.get(), CondenserRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SANGUINITE_INFUSOR_BE.get(), SanguiniteInfusorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SANGUINE_LAPIDARY_BE.get(), SanguineLapidaryRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.UNKNOWN_PORTAL_BE.get(), UnknownPortalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SANGUINITE_PIPE_BE.get(), PipeRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RHNULL_PIPE_BE.get(), PipeRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SANGUINITE_TANK_BE.get(), TankRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RHNULL_TANK_BE.get(), TankRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SANGUINITE_BLOOD_HARVESTER_BE.get(), SanguiniteHarvesterRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.STAR_LAMP.get(), StarLampRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SELIORA_RESTING.get(), SelioraRestingBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLASPHEMOUS_BLOOD_ALTAR.get(), BlasphemousBloodAltarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get(), MainBlasphemousBloodAltarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RHNULL_BLOOD_ENGINE.get(), RhnullBloodEngineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RHNULL_BLOOD_ENGINE_PILLAR.get(), RhnullBloodEnginePillarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLOOD_GEM_SPROUT_BE.get(), BloodGemSproutRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLOOD_ALTAR_BE.get(), BloodAltarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAIN_BLOOD_ALTAR_BE.get(), MainBloodAltarRenderer::new);
    }

    private static void registerScreenMenus() {
        MenuScreens.register(ModMenuTypes.BLOOD_WORKBENCH_MENU.get(), BloodWorkBenchScreen::new);
        MenuScreens.register(ModMenuTypes.VESPER_MENU.get(), VesperScreen::new);
        MenuScreens.register(ModMenuTypes.SANGUINE_LAPIDARY_MENU.get(), SanguineLapidaryScreen::new);
        MenuScreens.register(ModMenuTypes.RELIQUARY_MENU.get(), ReliquaryScreen::new);
        MenuScreens.register(ModMenuTypes.OMNITOME_MENU.get(), OmnitomeScreen::new);
    }


    private static void registerBlockRenderLayers() {

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SANGUINITE_BLOOD_HARVESTER.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANCIENT_BLOOD_CAPSULE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_PETALS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SPIKY_GRASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SANGUINE_CRUCIBLE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.VORACIOUS_MUSHROOM_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRIMSON_LURE_MUSHROOM_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.LIGHT_MUSHROOM_BLOCK.get(), RenderType.translucent());


        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SANGUINE_LAPIDARY.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CINDER_BLOOM_CACTUS_ROOT.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CINDER_BLOOM_CACTUS_CON.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CINDER_BLOOM_CACTUS_FLOWER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_GEM_SPROUT.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.VISCERAL_INFECTED_VEIN.get(), RenderType.cutout());


        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_BUSH.get(), RenderType::canConsolidateConsecutiveGeometry);
    }



}
