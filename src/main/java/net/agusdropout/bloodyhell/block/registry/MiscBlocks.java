package net.agusdropout.bloodyhell.block.registry;

import net.agusdropout.bloodyhell.block.ModSoundTypes;
import net.agusdropout.bloodyhell.block.custom.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import static net.agusdropout.bloodyhell.block.ModBlocks.*;

public final class MiscBlocks {
    public static final RegistryObject<Block> EYEBALLSHELL_SNAIL_GOO = registerBlock("eyeballshell_snail_goo", () -> new GooLayerBlock(
            BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK).strength(0.2f).randomTicks().replaceable().noCollission().noOcclusion()));
    public static final RegistryObject<Block> EYEBALLSHELL_SNAIL_GOO_BLOCK = registerBlock("eyeballshell_snail_goo_block", () -> new GooBlock(
            BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK).strength(0.2f) ));
    public static final RegistryObject<Block> BLEEDING_BLOCK = registerBlock("bleeding_block", () -> new BleedingBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(6f).requiresCorrectToolForDrops() ));
    public static final RegistryObject<Block> BLOOD_PORTAL = registerBlockWithoutBlockItem("blood_portal", ModBloodPortalBlock::new);
    public static final RegistryObject<Block> SMALL_ROCKS = registerBlock("small_rocks", ()-> new SmallRocks(
            BlockBehaviour.Properties.copy(Blocks.STONE).noCollission().noOcclusion().strength(1)));
    public static final RegistryObject<Block> ONI_STATUE = registerBlock("oni_statue", () -> new OniStatueBlock(
            BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK).strength(1F).noOcclusion().randomTicks().noLootTable()));
    public static final RegistryObject<Block> SELIORA_RESTING_BLOCK = registerBlockWithoutBlockItem("seliora_resting_block", () -> new SelioraRestingBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion().strength(2000f)));
    public static final RegistryObject<Block> DECORATED_POT_BLOCK = registerBlock("decorated_pot_block", () -> new DetailedPotBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(0.2f)
                            .sound(SoundType.DECORATED_POT)
                            .noOcclusion()
                            .noCollission()
            ));
    public static final RegistryObject<Block> FORBIDDEN_BOOKSHELF_BLOCK = registerBlock("forbidden_bookshelf_block", () -> new Block(
            BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> TOMB_BLOCK = registerBlock("tomb_block", () -> new TombBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion().noLootTable()));
    public static final RegistryObject<Block> BLOOD_FIRE = BLOCKS.register("blood_fire",
            () -> new BloodFireBlock(
                    BlockBehaviour.Properties.copy(Blocks.FIRE)
                            .noCollission()
                            .instabreak()
                            .sound(ModSoundTypes.SILENT)
                            .lightLevel(state -> 15)
                            .replaceable()
                            .noLootTable()
            ));
    public static final RegistryObject<Block> FRENZIED_FIRE_BLOCK = BLOCKS.register("frenzied_fire_block",
            () -> new FrenziedFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .noLootTable()));
    public static final RegistryObject<Block> ANCIENT_BLOOD_CAPSULE = registerBlock("ancient_blood_capsule",
            () -> new BloodCapsuleBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .strength(0.3f)
                    .sound(SoundType.GLASS)
                    .noOcclusion()));

    private MiscBlocks() {}

    public static void init() {}
}
