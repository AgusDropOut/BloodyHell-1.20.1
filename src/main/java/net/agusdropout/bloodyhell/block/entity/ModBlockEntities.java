package net.agusdropout.bloodyhell.block.entity;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.custom.*;
import net.agusdropout.bloodyhell.block.entity.custom.altar.BlasphemousBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBlasphemousBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.*;
import net.agusdropout.bloodyhell.block.entity.custom.mushroom.CrimsonLureMushroomBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.mushroom.VoraciousMushroomBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.plant.BloodGemSproutBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BloodyHell.MODID);

    public static final RegistryObject<BlockEntityType<BloodWorkbenchBlockEntity>> SANGUINE_CRUCIBLE =
            BLOCK_ENTITIES.register("sanguine_crucible", () ->
                    BlockEntityType.Builder.of(BloodWorkbenchBlockEntity::new,
                            ModBlocks.SANGUINE_CRUCIBLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BHChestBlockEntity>> BH_CHEST = BLOCK_ENTITIES.register("bh_chest", () ->
            BlockEntityType.Builder.of(BHChestBlockEntity::new,
                    ModBlocks.BLOOD_WOOD_CHEST.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlasphemousBloodAltarBlockEntity>> BLASPHEMOUS_BLOOD_ALTAR =
            BLOCK_ENTITIES.register("blasphemous_blood_altar_entity", () ->
                    BlockEntityType.Builder.of(BlasphemousBloodAltarBlockEntity::new,
                            ModBlocks.BLASPHEMOUS_BLOOD_ALTAR.get()).build(null));
    public static final RegistryObject<BlockEntityType<MainBlasphemousBloodAltarBlockEntity>> MAIN_BLASPHEMOUS_BLOOD_ALTAR =
            BLOCK_ENTITIES.register("main_blasphemous_blood_altar_entity", () ->
                    BlockEntityType.Builder.of(MainBlasphemousBloodAltarBlockEntity::new,
                            ModBlocks.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get()).build(null));
    public static final RegistryObject<BlockEntityType<SelioraRestingBlockEntity>> SELIORA_RESTING =
            BLOCK_ENTITIES.register("seliora_resting_block_entity", () ->
                    BlockEntityType.Builder.of(SelioraRestingBlockEntity::new,
                            ModBlocks.SELIORA_RESTING_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<StarLampBlockEntity>> STAR_LAMP =
            BLOCK_ENTITIES.register("star_lamp_block_entity", () ->
                    BlockEntityType.Builder.of(StarLampBlockEntity::new,
                            ModBlocks.STAR_LAMP_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<TombBlockEntity>> TOMB_ENTITY =
            BLOCK_ENTITIES.register("tomb_be",
                    () -> BlockEntityType.Builder.of(TombBlockEntity::new, ModBlocks.TOMB_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<BloodFireBlockEntity>> BLOOD_FIRE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("blood_fire_block_entity",
                    () -> BlockEntityType.Builder.of(BloodFireBlockEntity::new,
                            ModBlocks.BLOOD_FIRE.get()).build(null));
    public static final RegistryObject<BlockEntityType<SanguinitePipeBlockEntity>> SANGUINITE_PIPE_BE =
            BLOCK_ENTITIES.register("sanguine_pipe_be", () ->
                    BlockEntityType.Builder.of(SanguinitePipeBlockEntity::new,
                            ModBlocks.SANGUINITE_PIPE.get()).build(null));
    public static final RegistryObject<BlockEntityType<RhnullPipeBlockEntity>> RHNULL_PIPE_BE =
            BLOCK_ENTITIES.register("rhnull_pipe_be", () ->
                    BlockEntityType.Builder.of(RhnullPipeBlockEntity::new,
                            ModBlocks.RHNULL_PIPE.get()).build(null));
    public static final RegistryObject<BlockEntityType<SanguiniteTankBlockEntity>> SANGUINITE_TANK_BE =
            BLOCK_ENTITIES.register("sanguinite_tank_be", () ->
                    BlockEntityType.Builder.of(SanguiniteTankBlockEntity::new,
                            ModBlocks.SANGUINITE_TANK.get()).build(null));
    public static final RegistryObject<BlockEntityType<RhnullTankBlockEntity>> RHNULL_TANK_BE =
            BLOCK_ENTITIES.register("rhnull_tank_be", () ->
                    BlockEntityType.Builder.of(RhnullTankBlockEntity::new,
                            ModBlocks.RHNULL_TANK.get()).build(null));
    public static final RegistryObject<BlockEntityType<SanguiniteBloodHarvesterBlockEntity>> SANGUINITE_BLOOD_HARVESTER_BE =
            BLOCK_ENTITIES.register("sanguinite_blood_harvester_be", () ->
                    BlockEntityType.Builder.of(SanguiniteBloodHarvesterBlockEntity::new,
                            ModBlocks.SANGUINITE_BLOOD_HARVESTER.get()).build(null));
    public static final RegistryObject<BlockEntityType<VoraciousMushroomBlockEntity>> VORACIOUS_MUSHROOM_BE =
            BLOCK_ENTITIES.register("voracious_mushroom_be", () ->
                    BlockEntityType.Builder.of(VoraciousMushroomBlockEntity::new,
                            ModBlocks.VORACIOUS_MUSHROOM_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrimsonLureMushroomBlockEntity>> CRIMSON_LURE_MUSHROOM_BE =
            BLOCK_ENTITIES.register("crimson_lure_mushroom_be", () ->
                    BlockEntityType.Builder.of(CrimsonLureMushroomBlockEntity::new,
                            ModBlocks.CRIMSON_LURE_MUSHROOM_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<SanguiniteInfusorBlockEntity>> SANGUINITE_INFUSOR_BE =
            BLOCK_ENTITIES.register("sanguinite_infusor_be", () ->
                    BlockEntityType.Builder.of(SanguiniteInfusorBlockEntity::new,
                            ModBlocks.SANGUINITE_INFUSOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<BloodGemSproutBlockEntity>> BLOOD_GEM_SPROUT_BE =
            BLOCK_ENTITIES.register("blood_gem_sprout_be", () ->
                    BlockEntityType.Builder.of(BloodGemSproutBlockEntity::new,
                            ModBlocks.BLOOD_GEM_SPROUT.get()).build(null));
    public static final RegistryObject<BlockEntityType<SanguineLapidaryBlockEntity>> SANGUINE_LAPIDARY_BE =
            BLOCK_ENTITIES.register("sanguine_lapidary_be", () ->
                    BlockEntityType.Builder.of(SanguineLapidaryBlockEntity::new,
                            ModBlocks.SANGUINE_LAPIDARY.get()).build(null));
    public static final RegistryObject<BlockEntityType<UnknownPortalBlockEntity>> UNKNOWN_PORTAL_BE =
            BLOCK_ENTITIES.register("unknown_portal_be", () ->
                    BlockEntityType.Builder.of(UnknownPortalBlockEntity::new,
                            ModBlocks.UNKNOWN_PORTAL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<SanguiniteCondenserBlockEntity>> SANGUINITE_CONDENSER_BE =
            BLOCK_ENTITIES.register("sanguinite_condenser_be", () ->
                    BlockEntityType.Builder.of(SanguiniteCondenserBlockEntity::new,
                            ModBlocks.SANGUINITE_CONDENSER.get()).build(null));
    public static final RegistryObject<BlockEntityType<RhnullCondenserBlockEntity>> RHNULL_CONDENSER_BE =
            BLOCK_ENTITIES.register("rhnull_condenser_be", () ->
                    BlockEntityType.Builder.of(RhnullCondenserBlockEntity::new,
                            ModBlocks.RHNULL_CONDENSER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}