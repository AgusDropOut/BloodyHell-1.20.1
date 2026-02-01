package net.agusdropout.bloodyhell.block.entity;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.custom.*;
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
    public static final RegistryObject<BlockEntityType<BloodAltarBlockEntity>> BLOOD_ALTAR =
            BLOCK_ENTITIES.register("blood_altar_entity", () ->
                    BlockEntityType.Builder.of(BloodAltarBlockEntity::new,
                            ModBlocks.BLOOD_ALTAR.get()).build(null));
    public static final RegistryObject<BlockEntityType<MainBloodAltarBlockEntity>> MAIN_BLOOD_ALTAR =
            BLOCK_ENTITIES.register("main_blood_altar_entity", () ->
                    BlockEntityType.Builder.of(MainBloodAltarBlockEntity::new,
                            ModBlocks.MAIN_BLOOD_ALTAR.get()).build(null));
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
    public static final RegistryObject<BlockEntityType<SanguiniteTankBlockEntity>> SANGUINITE_TANK_BE =
            BLOCK_ENTITIES.register("sanguinite_tank_be", () ->
                    BlockEntityType.Builder.of(SanguiniteTankBlockEntity::new,
                            ModBlocks.SANGUINITE_TANK.get()).build(null));
    public static final RegistryObject<BlockEntityType<SanguiniteBloodHarvesterBlockEntity>> SANGUINITE_BLOOD_HARVESTER_BE =
            BLOCK_ENTITIES.register("sanguinite_blood_harvester_be", () ->
                    BlockEntityType.Builder.of(SanguiniteBloodHarvesterBlockEntity::new,
                            ModBlocks.SANGUINITE_BLOOD_HARVESTER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}