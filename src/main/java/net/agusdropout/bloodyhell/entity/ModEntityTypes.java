package net.agusdropout.bloodyhell.entity;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.custom.*;


import net.agusdropout.bloodyhell.entity.effects.*;
import net.agusdropout.bloodyhell.entity.projectile.*;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousImpalerEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.*;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BloodyHell.MODID);

    public static final RegistryObject<EntityType<BloodThirstyBeastEntity>> BLOODTHIRSTYBEAST = ENTITY_TYPES.register("bloodthirstybeast",
            () -> EntityType.Builder.of(BloodThirstyBeastEntity::new, MobCategory.MONSTER).sized(1.5f,1.5f).build(new ResourceLocation(BloodyHell.MODID,
                    "bloodthirstybeast").toString()));
    public static final RegistryObject<EntityType<BloodSeekerEntity>> BLOOD_SEEKER = ENTITY_TYPES.register("bloodseeker",
            () -> EntityType.Builder.of(BloodSeekerEntity::new, MobCategory.CREATURE).sized(1f,1f).build(new ResourceLocation(BloodyHell.MODID,
                    "bloodseeker").toString()));
    public static final RegistryObject<EntityType<OmenGazerEntity>> OMEN_GAZER_ENTITY = ENTITY_TYPES.register("omen_gazer_entity",
            () -> EntityType.Builder.of(OmenGazerEntity::new, MobCategory.MONSTER).sized(1f,4f).build(new ResourceLocation(BloodyHell.MODID,
                    "omen_gazer_entity").toString()));
    public static final RegistryObject<EntityType<VeinraverEntity>> VEINRAVER_ENTITY = ENTITY_TYPES.register("veinraver_entity",
            () -> EntityType.Builder.of(VeinraverEntity::new, MobCategory.MONSTER).sized(1f,4f).build(new ResourceLocation(BloodyHell.MODID,
                    "veinraver_entity").toString()));
    public static final RegistryObject<EntityType<BloodySoulEntity>> BLOODY_SOUL_ENTITY = ENTITY_TYPES.register("bloody_soul_entity",
            () -> EntityType.Builder.of(BloodySoulEntity::new, MobCategory.CREATURE).sized(1f,1.5f).build(new ResourceLocation(BloodyHell.MODID,
                    "bloody_soul_entity").toString()));
    public static final RegistryObject<EntityType<CorruptedBloodySoulEntity>> CORRUPTED_BLOODY_SOUL_ENTITY = ENTITY_TYPES.register("corrupted_bloody_soul_entity",
            () -> EntityType.Builder.of(CorruptedBloodySoulEntity::new, MobCategory.CREATURE).sized(1f,1.5f).build(new ResourceLocation(BloodyHell.MODID,
                    "corrupted_bloody_soul_entity").toString()));
    public static final RegistryObject<EntityType<CrimsonRavenEntity>> CRIMSON_RAVEN = ENTITY_TYPES.register("crimsonraven",
            () -> EntityType.Builder.of(CrimsonRavenEntity::new,MobCategory.CREATURE).sized(1f,1f).build(new ResourceLocation(BloodyHell.MODID,
                    "crimsonraven").toString()));
    public static final RegistryObject<EntityType<EyeshellSnailEntity>> EYESHELL_SNAIL = ENTITY_TYPES.register("eyeshellsnail",
            () -> EntityType.Builder.of(EyeshellSnailEntity::new,MobCategory.CREATURE).sized(1f,1f).build(new ResourceLocation(BloodyHell.MODID,
                    "eyeshellsnail").toString()));
    public static final RegistryObject<EntityType<ScarletSpeckledFishEntity>> SCARLETSPECKLED_FISH = ENTITY_TYPES.register("scarletspeckledfish",
            () -> EntityType.Builder.of(ScarletSpeckledFishEntity::new,MobCategory.AMBIENT).sized(0.2f,0.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "scarletspeckledfish").toString()));
    public static final RegistryObject<EntityType<BloodPigEntity>> BLOODPIG = ENTITY_TYPES.register("bloodpig",
            () -> EntityType.Builder.of(BloodPigEntity::new,MobCategory.AMBIENT).sized(0.9f,0.9f).build(new ResourceLocation(BloodyHell.MODID,
                    "bloodpig").toString()));
    public static final RegistryObject<EntityType<OniEntity>> ONI = ENTITY_TYPES.register("oni",
            () -> EntityType.Builder.of(OniEntity::new,MobCategory.MONSTER).sized(1.5f,1.5f).build(new ResourceLocation(BloodyHell.MODID,
                    "oni").toString()));
    public static final RegistryObject<EntityType<BloodArrowEntity>> BLOOD_ARROW = ENTITY_TYPES.register("blood_arrow_entity",
            () -> EntityType.Builder.<BloodArrowEntity>of(BloodArrowEntity::new,MobCategory.AMBIENT).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_arrow_entity").toString()));
    public static final RegistryObject<EntityType<CrystalPillar>> CRYSTAL_PILLAR = ENTITY_TYPES.register("crystal_pillar",
            () -> EntityType.Builder.<CrystalPillar>of(CrystalPillar::new,MobCategory.AMBIENT).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "crystal_pillar").toString()));
    public static final RegistryObject<EntityType<EntityFallingBlock>> ENTITY_FALLING_BLOCK = ENTITY_TYPES.register("entity_falling_block",
            () -> EntityType.Builder.<EntityFallingBlock>of(EntityFallingBlock::new,MobCategory.MISC).sized(1f,1f).build(new ResourceLocation(BloodyHell.MODID,
                    "entity_falling_block").toString()));
    public static final RegistryObject<EntityType<BloodNovaDebrisEntity>> BLOOD_NOVA_DEBRIS_ENTITY = ENTITY_TYPES.register("blood_nova_debris_entity",
            () -> EntityType.Builder.<BloodNovaDebrisEntity>of(BloodNovaDebrisEntity::new,MobCategory.MISC).sized(1f,1f).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_nova_debris_entity").toString()));
    public static final RegistryObject<EntityType<EntityCameraShake>> ENTITY_CAMERA_SHAKE = ENTITY_TYPES.register("entity_camera_shake",
            () -> EntityType.Builder.<EntityCameraShake>of(EntityCameraShake::new,MobCategory.MISC).sized(1f,1f).setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(BloodyHell.MODID,
                    "entity_camera_shake").toString()));
    public static final RegistryObject<EntityType<UnknownEyeEntity>> UNKNOWN_EYE_ENTITY = ENTITY_TYPES.register("unknown_eye_entity",
            () -> EntityType.Builder.<UnknownEyeEntity>of(UnknownEyeEntity::new,MobCategory.AMBIENT).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "unknown_eye_entity").toString()));
    public static final RegistryObject<EntityType<CyclopsEntity>> CYCLOPS_ENTITY = ENTITY_TYPES.register("cyclops_entity",
            () -> EntityType.Builder.<CyclopsEntity>of(CyclopsEntity::new,MobCategory.AMBIENT).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "cyclops_entity").toString()));
    public static final RegistryObject<EntityType<BlasphemousArmEntity>> BLASPHEMOUS_ARM_ENTITY = ENTITY_TYPES.register("blasphemous_arm_entity",
            () -> EntityType.Builder.<BlasphemousArmEntity>of(BlasphemousArmEntity::new,MobCategory.AMBIENT).sized(1.3f,4f).build(new ResourceLocation(BloodyHell.MODID,
                    "blasphemous_arm_entity").toString()));
    public static final RegistryObject<EntityType<UnknownEntityArms>> UNKNOWN_ENTITY_ARMS = ENTITY_TYPES.register("unknown_entity_arms",
            () -> EntityType.Builder.<UnknownEntityArms>of(UnknownEntityArms::new,MobCategory.AMBIENT).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "unknown_entity_arms").toString()));
    public static final RegistryObject<EntityType<OffspringOfTheUnknownEntity>> OFFSPRING_OF_THE_UNKNOWN = ENTITY_TYPES.register("offspring_of_the_unknown",
            () -> EntityType.Builder.<OffspringOfTheUnknownEntity>of(OffspringOfTheUnknownEntity::new,MobCategory.MONSTER).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "offspring_of_the_unknown").toString()));
    public static final RegistryObject<EntityType<GraveWalkerEntity>> GRAVE_WALKER_ENTITY = ENTITY_TYPES.register("grave_walker_entity",
            () -> EntityType.Builder.<GraveWalkerEntity>of(GraveWalkerEntity::new,MobCategory.MONSTER).sized(1.5f,2.0f).build(new ResourceLocation(BloodyHell.MODID,
                    "grave_walker_entity").toString()));
    public static final RegistryObject<EntityType<HornedWormEntity>> HORNED_WORM = ENTITY_TYPES.register("horned_worm",
            () -> EntityType.Builder.<HornedWormEntity>of(HornedWormEntity::new,MobCategory.MONSTER).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "horned_worm").toString()));
    public static final RegistryObject<EntityType<VeilStalkerEntity>> VEIL_STALKER = ENTITY_TYPES.register("veil_stalker",
            () -> EntityType.Builder.<VeilStalkerEntity>of(VeilStalkerEntity::new,MobCategory.MONSTER).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "veil_stalker").toString()));
    public static final RegistryObject<EntityType<BlasphemousMalformationEntity>> BLASPHEMOUS_MALFORMATION = ENTITY_TYPES.register("blasphemous_malformation",
            () -> EntityType.Builder.<BlasphemousMalformationEntity>of(BlasphemousMalformationEntity::new,MobCategory.MONSTER).sized(1.2f,2f).build(new ResourceLocation(BloodyHell.MODID,
                    "blasphemous_malformation").toString()));
    public static final RegistryObject<EntityType<SelioraEntity>> SELIORA = ENTITY_TYPES.register("seliora",
            () -> EntityType.Builder.<SelioraEntity>of(SelioraEntity::new,MobCategory.MONSTER).sized(1.2f,2f).build(new ResourceLocation(BloodyHell.MODID,
                    "seliora").toString()));
    public static final RegistryObject<EntityType<SanguineSacrificeEntity>> SANGUINE_SACRIFICE_ENTITY = ENTITY_TYPES.register("sanguine_sacrifice_entity",
            () -> EntityType.Builder.<SanguineSacrificeEntity>of(SanguineSacrificeEntity::new,MobCategory.AMBIENT).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "sanguine_sacrifice_entity").toString()));
    public static final RegistryObject<EntityType<BloodSlashEntity>> BLOOD_SLASH_ENTITY = ENTITY_TYPES.register("blood_slash_entity",
            () -> EntityType.Builder.<BloodSlashEntity>of(BloodSlashEntity::new,MobCategory.AMBIENT).sized(2f,5f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_slash_entity").toString()));
    public static final RegistryObject<EntityType<BloodSlashDecalEntity>> BLOOD_SLASH_DECAL = ENTITY_TYPES.register("blood_slash_decal",
            () -> EntityType.Builder.<BloodSlashDecalEntity>of(BloodSlashDecalEntity::new, MobCategory.MISC)
                    .sized(3.0f, 0.1f) // 3 blocks wide (X/Z), very thin height (Y)
                    .clientTrackingRange(10) // Visible from 160 blocks away (10 chunks)
                    .updateInterval(Integer.MAX_VALUE) // Optimization: Never needs position updates after spawn
                    .setShouldReceiveVelocityUpdates(false) // Optimization: It is static
                    .build(new ResourceLocation(BloodyHell.MODID, "blood_slash_decal").toString()));
    public static final RegistryObject<EntityType<BloodSphereEntity>> BLOOD_PROJECTILE = ENTITY_TYPES.register("blood_projectile",
            () -> EntityType.Builder.<BloodSphereEntity>of(BloodSphereEntity::new,MobCategory.AMBIENT).sized(1.2f,1.2f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_projectile").toString()));
    public static final RegistryObject<EntityType<VirulentAnchorProjectileEntity>> VIRULENT_ANCHOR_PROJECTILE = ENTITY_TYPES.register("virulent_anchor_projectile",
            () -> EntityType.Builder.<VirulentAnchorProjectileEntity>of(VirulentAnchorProjectileEntity::new,MobCategory.AMBIENT).sized(1.2f,1.2f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "virulent_anchor_projectile").toString()));
    public static final RegistryObject<EntityType<BloodNovaEntity>> BLOOD_NOVA_ENTITY = ENTITY_TYPES.register("blood_nova_entity",
            () -> EntityType.Builder.<BloodNovaEntity>of(BloodNovaEntity::new,MobCategory.AMBIENT).sized(1.2f,1.2f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_nova_entity").toString()));
    public static final RegistryObject<EntityType<SmallCrimsonDagger>> SMALL_CRIMSON_DAGGER = ENTITY_TYPES.register("small_crimson_dagger",
            () -> EntityType.Builder.<SmallCrimsonDagger>of(SmallCrimsonDagger::new,MobCategory.AMBIENT).sized(1.0f,1.0f).build(new ResourceLocation(BloodyHell.MODID,
                    "small_crimson_dagger").toString()));
    public static final RegistryObject<EntityType<VisceralProjectile>> VISCERAL_PROJECTILE = ENTITY_TYPES.register("visceral_projectile",
            () -> EntityType.Builder.<VisceralProjectile>of(VisceralProjectile::new,MobCategory.AMBIENT).sized(1.0f,1.0f).build(new ResourceLocation(BloodyHell.MODID,
                    "visceral_projectile").toString()));
    public static final RegistryObject<EntityType<StarfallProjectile>> STARFALL_PROJECTILE = ENTITY_TYPES.register("starfall_projectile",
            () -> EntityType.Builder.<StarfallProjectile>of(StarfallProjectile::new,MobCategory.AMBIENT).sized(1.0f,1.0f).updateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "starfall_projectile").toString()));
    public static final RegistryObject<EntityType<BlasphemousWhirlwindEntity>> BLASPHEMOUS_WHIRLWIND_ENTITY = ENTITY_TYPES.register("blasphemous_whirlwind_entity",
            () -> EntityType.Builder.<BlasphemousWhirlwindEntity>of(BlasphemousWhirlwindEntity::new,MobCategory.AMBIENT).sized(1.0f,1.0f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blasphemous_whirlwind_entity").toString()));
    public static final RegistryObject<EntityType<BlasphemousSmallWhirlwindEntity>> BLASPHEMOUS_SMALL_WHIRLWIND_ENTITY = ENTITY_TYPES.register("blasphemous_small_whirlwind_entity",
            () -> EntityType.Builder.<BlasphemousSmallWhirlwindEntity>of(BlasphemousSmallWhirlwindEntity::new,MobCategory.AMBIENT).sized(1.5f,2.0f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blasphemous_small_whirlwind_entity").toString()));
    public static final RegistryObject<EntityType<BloodPortalEntity>> BLOOD_PORTAL_ENTITY = ENTITY_TYPES.register("blood_portal_entity",
            () -> EntityType.Builder.<BloodPortalEntity>of(BloodPortalEntity::new,MobCategory.AMBIENT).sized(1.0f,1.0f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_portal_entity").toString()));
    public static final RegistryObject<EntityType<BloodFireSoulEntity>> BLOOD_FIRE_SOUL = ENTITY_TYPES.register("blood_fire_soul",
            () -> EntityType.Builder.<BloodFireSoulEntity>of(BloodFireSoulEntity::new,MobCategory.AMBIENT).sized(1.0f,1.0f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_fire_soul").toString()));
    public static final RegistryObject<EntityType<BloodFireColumnEntity>> BLOOD_FIRE_COLUMN_PROJECTILE = ENTITY_TYPES.register("blood_fire_column_entity",
            () -> EntityType.Builder.<BloodFireColumnEntity>of(BloodFireColumnEntity::new,MobCategory.AMBIENT).sized(1.0f,1.0f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_fire_column_entity").toString()));
    public static final RegistryObject<EntityType<BloodFireMeteorEntity>> BLOOD_FIRE_METEOR_PROJECTILE = ENTITY_TYPES.register("blood_fire_meteor_projectile",
            () -> EntityType.Builder.<BloodFireMeteorEntity>of(BloodFireMeteorEntity::new,MobCategory.AMBIENT).sized(1.0f,1.0f).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build(new ResourceLocation(BloodyHell.MODID,
                    "blood_fire_meteor_projectile").toString()));

    public static final RegistryObject<EntityType<BlasphemousTwinDaggersCloneEntity>> BLASPHEMOUS_TWIN_DAGGERS_CLONE =
            ENTITY_TYPES.register("blasphemous_twin_daggers_clone",
                    () -> EntityType.Builder.of(BlasphemousTwinDaggersCloneEntity::new, MobCategory.MISC)
                            .sized(0.6f, 1.8f)
                            .build(new ResourceLocation(BloodyHell.MODID, "blasphemous_twin_daggers_clone").toString()));

    public static final RegistryObject<EntityType<VesperEntity>> VESPER = ENTITY_TYPES.register("vesper",
            () -> EntityType.Builder.<VesperEntity>of(VesperEntity::new,MobCategory.MONSTER).sized(1.2f,1.2f).build(new ResourceLocation(BloodyHell.MODID,
                    "vesper").toString()));

    public static final RegistryObject<EntityType<SpecialSlashEntity>> SPECIAL_SLASH =
            ENTITY_TYPES.register("special_slash",
                    () -> EntityType.Builder.<SpecialSlashEntity>of(SpecialSlashEntity::new, MobCategory.MISC)
                            .sized(5.0f, 5.0f) // Tamaño del hitbox lógico (aunque no tenga física)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(new ResourceLocation(BloodyHell.MODID, "special_slash").toString()));

    public static final RegistryObject<EntityType<BlasphemousSpinesEntity>> BLASPHEMOUS_SPINES =
            ENTITY_TYPES.register("blasphemous_spines",
                    () -> EntityType.Builder.<BlasphemousSpinesEntity>of(BlasphemousSpinesEntity::new, MobCategory.MISC)
                            .sized(0.8f, 1.5f)
                            .build(new ResourceLocation(BloodyHell.MODID, "blasphemous_spines").toString()));

    public static final RegistryObject<EntityType<BlasphemousSpearEntity>> BLASPHEMOUS_SPEAR =
            ENTITY_TYPES.register("blasphemous_spear",
                    () -> EntityType.Builder.<BlasphemousSpearEntity>of(BlasphemousSpearEntity::new, MobCategory.MISC)
                            .sized(0.8f, 2.0f) // Hitbox más alta para una lanza
                            .build(new ResourceLocation(BloodyHell.MODID, "blasphemous_spear").toString()));

    public static final RegistryObject<EntityType<BlasphemousImpalerEntity>> BLASPHEMOUS_IMPALER_ENTITY = ENTITY_TYPES.register("blasphemous_impaler_entity",
            () -> EntityType.Builder.<BlasphemousImpalerEntity>of(BlasphemousImpalerEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.8f)// Tamaño de la hitbox
                    .clientTrackingRange(4) // Rango de visión (chunks)
                    .updateInterval(20) // Actualización
                    .build("blasphemous_impaler_entity"));
    public static final RegistryObject<EntityType<TentacleEntity>> TENTACLE_ENTITY = ENTITY_TYPES.register("tentacle_entity",
            () -> EntityType.Builder.<TentacleEntity>of(TentacleEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.8f)// Tamaño de la hitbox
                    .clientTrackingRange(4) // Rango de visión (chunks)
                    .updateInterval(20) // Actualización
                    .build("tentacle_entity"));
    public static final RegistryObject<EntityType<BloodStainEntity>> BLOOD_STAIN_ENTITY = ENTITY_TYPES.register("blood_stain",
            () -> EntityType.Builder.<BloodStainEntity>of(BloodStainEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f) // Small logical hitbox
                    .clientTrackingRange(10) // Visible from 160 blocks
                    .updateInterval(Integer.MAX_VALUE) // Optimization: Never sends position updates (it's static)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(new ResourceLocation(BloodyHell.MODID, "blood_stain").toString()));

    public static final RegistryObject<EntityType<BloodClotProjectile>> BLOOD_CLOT_PROJECTILE = ENTITY_TYPES.register("blood_clot_projectile",
            () -> EntityType.Builder.<BloodClotProjectile>of(BloodClotProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(new ResourceLocation(BloodyHell.MODID, "blood_clot_projectile").toString()));
    public static final RegistryObject<EntityType<RitekeeperEntity>> RITEKEEPER = ENTITY_TYPES.register("ritekeeper",
            () -> EntityType.Builder.of(RitekeeperEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 2.0f) // Adjust size based on model
                    .build(new ResourceLocation(BloodyHell.MODID, "ritekeeper").toString()));

    public static final RegistryObject<EntityType<CinderAcolyteEntity>> CINDER_ACOLYTE =
            ENTITY_TYPES.register("cinder_acolyte",
                    () -> EntityType.Builder.of(CinderAcolyteEntity::new, MobCategory.MONSTER)
                            .sized(0.8f, 2.1f) // Tall and thin
                            .build(new ResourceLocation(BloodyHell.MODID, "cinder_acolyte").toString()));
    public static final RegistryObject<EntityType<FailedRemnantEntity>> FAILED_REMNANT =
            ENTITY_TYPES.register("failed_remnant",
                    () -> EntityType.Builder.of(FailedRemnantEntity::new, MobCategory.MONSTER)
                            .sized(1.5f, 2f) // Wide but flat (Crawling)
                            .build(new ResourceLocation(BloodyHell.MODID, "failed_remnant").toString()));
    public static final RegistryObject<EntityType<InfestationDecalEntity>> INFESTATION_DECAL =
            ENTITY_TYPES.register("infestation_decal",
                    () -> EntityType.Builder.<InfestationDecalEntity>of(InfestationDecalEntity::new, MobCategory.AMBIENT)
                            .sized(1.0f, 1.0f) // Wide but flat (Crawling)
                            .build(new ResourceLocation(BloodyHell.MODID, "infestation_decal").toString()));
    public static final RegistryObject<EntityType<BloodSoulEntity>> BLOOD_SOUL =
            ENTITY_TYPES.register("blood_soul_entity",
                    () -> EntityType.Builder.<BloodSoulEntity>of(BloodSoulEntity::new, MobCategory.AMBIENT)
                            .sized(0.5f, 0.5f)
                            .build(new ResourceLocation(BloodyHell.MODID, "blood_soul_entity").toString()));
    public static final RegistryObject<EntityType<RhnullImpalerEntity>> RHNULL_IMPALER_PROJECTILE =
            ENTITY_TYPES.register("rhnull_impaler_projectile",
                    () -> EntityType.Builder.<RhnullImpalerEntity>of(RhnullImpalerEntity::new, MobCategory.AMBIENT)
                            .sized(0.5f, 0.5f)
                            .build(new ResourceLocation(BloodyHell.MODID, "rhnull_heavy_sword_projectile").toString()));
    public static final RegistryObject<EntityType<RhnullHeavySwordEntity>> RHNULL_HEAVY_SWORD_PROJECTILE =
            ENTITY_TYPES.register("rhnull_heavy_sword_projectile",
                    () -> EntityType.Builder.<RhnullHeavySwordEntity>of(RhnullHeavySwordEntity::new, MobCategory.AMBIENT)
                            .sized(0.5f, 0.5f)
                            .build(new ResourceLocation(BloodyHell.MODID, "rhnull_heavy_sword_projectile").toString()));
    public static final RegistryObject<EntityType<RhnullPainThroneEntity>> RHNULL_PAIN_THRONE =
            ENTITY_TYPES.register("rhnull_pain_throne",
                    () -> EntityType.Builder.<RhnullPainThroneEntity>of(RhnullPainThroneEntity::new, MobCategory.MISC)
                            .sized(2f, 5.0f)
                            .clientTrackingRange(10)
                            .build("rhnull_pain_throne"));
    public static final RegistryObject<EntityType<RhnullDropletEntity>> RHNULL_DROPLET_PROJECTILE =
            ENTITY_TYPES.register("rhnull_droplet_projectile",
                    () -> EntityType.Builder.<RhnullDropletEntity>of(RhnullDropletEntity::new, MobCategory.MISC)
                            .sized(2f, 5.0f)
                            .clientTrackingRange(10)
                            .build("rhnull_droplet_projectile"));
    public static final RegistryObject<EntityType<RhnullOrbEmitter>> RHNULL_ORB_EMITTER_ENTITY =
            ENTITY_TYPES.register("rhnull_orb_emitter_entity",
                    () -> EntityType.Builder.<RhnullOrbEmitter>of(RhnullOrbEmitter::new, MobCategory.MISC)
                            .sized(2f, 5.0f)
                            .clientTrackingRange(10)
                            .build("rhnull_orb_emitter_entity"));




    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }

}
