package net.agusdropout.bloodyhell.block;


import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.base.*;
import net.agusdropout.bloodyhell.block.custom.*;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguiniteBloodHarvesterBlock;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguiniteInfusorBlock;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguinitePipeBlock;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguiniteTankBlock;
import net.agusdropout.bloodyhell.block.custom.mushroom.VoraciousMushroomBlock;
import net.agusdropout.bloodyhell.block.custom.mushroom.InfestationVeinBlock;
import net.agusdropout.bloodyhell.block.custom.plant.BloodGemSproutBlock;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.worldgen.tree.BloodTreeGrower;
import net.agusdropout.bloodyhell.worldgen.tree.GiantBloodTreeGrower;
import net.agusdropout.bloodyhell.worldgen.tree.SmallBloodTreeGrower;
import net.agusdropout.bloodyhell.worldgen.tree.SoulTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BloodyHell.MODID);
    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    // Define a custom sound type with 0.0F volume
    public static final SoundType SILENT = new SoundType(
            0.0F, // Volume (0 = Silent)
            0.0F, // Pitch
            SoundEvents.WOOL_BREAK, // These sounds won't play because volume is 0
            SoundEvents.WOOL_STEP,
            SoundEvents.WOOL_PLACE,
            SoundEvents.WOOL_HIT,
            SoundEvents.WOOL_FALL
    );
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    //Sanguinite
    public static final RegistryObject<Block> SANGUINITE_BLOCK = registerBlock("sanguinite_block", ()-> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6f).requiresCorrectToolForDrops()));

    //Rhnull
    public static final RegistryObject<Block> RHNULL_BLOCK = registerBlock("rhnull_block", ()-> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6f).requiresCorrectToolForDrops()));


    //Bloody Stone
    public static final RegistryObject<Block> BLOODY_STONE_BLOCK = registerBlock("bloody_stone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_STAIRS = registerBlock("bloody_stone_stairs", () -> new StairBlock(() -> ModBlocks.BLOODY_STONE_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS)));
    public static final RegistryObject<Block> BLOODY_STONE_SLAB = registerBlock("bloody_stone_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_FENCE)));
    public static final RegistryObject<Block> BLOODY_STONE_FENCE = registerBlock("bloody_stone_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS)));
    public static final RegistryObject<Block> BLOODY_STONE_FENCE_GATE = registerBlock("bloody_stone_fence_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_BREAK));
    public static final RegistryObject<Block> BLOODY_STONE_WALL = registerBlock("bloody_stone_wall", () -> new WallBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS)));


    //Polished Bloody Stone
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_BLOCK = registerBlock("polished_bloody_stone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.POLISHED_GRANITE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_STAIRS = registerBlock("polished_bloody_stone_stairs", () -> new StairBlock(() -> ModBlocks.POLISHED_BLOODY_STONE_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_SLAB = registerBlock("polished_bloody_stone_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_FENCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_FENCE = registerBlock("polished_bloody_stone_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_FENCE_GATE = registerBlock("polished_bloody_stone_fence_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).requiresCorrectToolForDrops(), SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_BREAK));
    public static final RegistryObject<Block> POLISHED_BLOODY_STONE_WALL = registerBlock("polished_bloody_stone_wall", () -> new WallBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).requiresCorrectToolForDrops()));

    //Bloody Stone Tiles
    public static final RegistryObject<Block> BLOODY_STONE_TILES_BLOCK = registerBlock("bloody_stone_tiles_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_STAIRS = registerBlock("bloody_stone_tiles_stairs", () -> new StairBlock(() -> ModBlocks.BLOODY_STONE_TILES_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICK_STAIRS).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_SLAB = registerBlock("bloody_stone_tiles_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_FENCE = registerBlock("bloody_stone_tiles_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_FENCE_TILES_GATE = registerBlock("bloody_stone_fence_tiles_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).requiresCorrectToolForDrops(), SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_BREAK));
    public static final RegistryObject<Block> BLOODY_STONE_TILES_WALL = registerBlock("bloody_stone_tiles_wall", () -> new WallBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).requiresCorrectToolForDrops()));

    //Bloody Stone Bricks
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS = registerBlock("bloody_stone_bricks", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS_STAIRS = registerBlock("bloody_stone_bricks_stairs", () -> new StairBlock(() -> ModBlocks.BLOODY_STONE_BRICKS.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS_SLAB = registerBlock("bloody_stone_bricks_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_FENCE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS_FENCE = registerBlock("bloody_stone_bricks_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOODY_STONE_FENCE_BRICKS_GATE = registerBlock("bloody_stone_fence_bricks_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).requiresCorrectToolForDrops(), SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_BREAK));
    public static final RegistryObject<Block> BLOODY_STONE_BRICKS_WALL = registerBlock("bloody_stone_bricks_wall", () -> new WallBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS).requiresCorrectToolForDrops()));


    //Blood Grass
    public static final RegistryObject<Block> BLOOD_GRASS_BLOCK = registerBlock("blood_grass_block", ()-> new BloodGrassBlock(
            BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(1f).randomTicks().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> BLOODDIRT_FARMLAND = registerBlock("blooddirt_farmland", () -> new BloodDirtFarmlandBlock(
            BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> BLOOD_DIRT_BLOCK = registerBlock("blood_dirt_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.DIRT).strength(1f)));



    //Ores
    public static final RegistryObject<Block> SANGUINITE_ORE = registerBlock("sanguinite_ore", ()-> new DropExperienceBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_ORE).strength(6f).requiresCorrectToolForDrops(), UniformInt.of(3,7)));




    //Mob generated
    public static final RegistryObject<Block> EYEBALLSHELL_SNAIL_GOO = registerBlock("eyeballshell_snail_goo", () -> new GooLayerBlock(
            BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK).strength(6f).randomTicks().replaceable().noCollission().noOcclusion()));
    public static final RegistryObject<Block> EYEBALLSHELL_SNAIL_GOO_BLOCK = registerBlock("eyeballshell_snail_goo_block", () -> new GooBlock(
            BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK).strength(6f) ));



    //Vegetation
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
            BlockBehaviour.Properties.copy(Blocks.GRASS).noCollission().noOcclusion()));
    public static final RegistryObject<Block> POTTED_BLOOD_FLOWER = BLOCKS.register("potted_blood_flower", ()-> new FlowerPotBlock(()-> ((FlowerPotBlock) Blocks.FLOWER_POT) ,ModBlocks.BLOOD_FLOWER,
            BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM).noOcclusion()));
    public static final RegistryObject<Block> BLOOD_BUSH = registerBlock("blood_bush", ()-> new DoublePlantBlock(
            BlockBehaviour.Properties.copy(Blocks.ROSE_BUSH)));
    public static final RegistryObject<Block> BLOOD_LILY_BLOCK = registerBlock("blood_lily_block", ()-> new BloodLilyBlock(
            BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).mapColor(MapColor.COLOR_PINK).noOcclusion().noCollission().lightLevel((state)->20).instabreak().sound(SoundType.LILY_PAD).offsetType(BlockBehaviour.OffsetType.XZ)));
    public static final RegistryObject<Block> BLOOD_PETALS = registerBlock("blood_petals", ()-> new FlowerBlock(()-> MobEffects.HARM,5,
            BlockBehaviour.Properties.copy(Blocks.ALLIUM).noCollission().noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> LIGHT_MUSHROOM_BLOCK = registerBlock("light_mushroom_block", ()-> new FlowerBlock(()-> MobEffects.POISON,5,
            BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM_BLOCK).noCollission().noOcclusion().lightLevel((state)->15)));
    public static final RegistryObject<Block> BLOOD_WALL_MUSHROOM_BLOCK = registerBlock("blood_wall_mushroom_block", ()-> new BaseWallPlantBlock(
            BlockBehaviour.Properties.copy(Blocks.COCOA).noOcclusion().dynamicShape().lightLevel( (state)->15 )) {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
                    super.createBlockStateDefinition(stateManager);
                }
            });
    public static final RegistryObject<Block> DROOPVINE = registerBlock("droopvine", () -> new DroopvineBlock(
            BlockBehaviour.Properties.copy(Blocks.CAVE_VINES).strength(0.1F).lightLevel(Droopvine.light())));
    public static final RegistryObject<Block> DROOPVINE_PLANT = registerBlock("droopvine_plant", () -> new DroopvinePlantBlock(
            BlockBehaviour.Properties.copy(Blocks.CAVE_VINES_PLANT).strength(0.1F).lightLevel(Droopvine.light())));
    public static final RegistryObject<Block> BLOOD_SCRAPPER_PLANT = registerBlock("blood_scrapper_plant", () -> new TallGrowingPlant(
            BlockBehaviour.Properties.copy(Blocks.BAMBOO).strength(0.1F).dynamicShape().noOcclusion().noCollission().randomTicks().forceSolidOn().lightLevel((state)->20)));
    public static final RegistryObject<Block> BLOOD_SCRAPPER_PLANT_SAPLING = registerBlock("blood_scrapper_plant_sapling", () -> new BloodScrapperPlantSapling(BlockBehaviour.Properties.copy(Blocks.BAMBOO).strength(0.1F)));
    public static final RegistryObject<Block> GIANT_BLOOD_SAPLING = registerBlock("giant_blood_sapling", ()-> new SaplingBlock(new GiantBloodTreeGrower(),
            BlockBehaviour.Properties.copy(Blocks.CHERRY_SAPLING)));

    //Wood
    public static final RegistryObject<Block> BLOOD_LOG = registerBlock("blood_log", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> BLOOD_WOOD = registerBlock("blood_wood", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_BLOOD_LOG = registerBlock("stripped_blood_log", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_BLOOD_WOOD = registerBlock("stripped_blood_wood", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD).strength(3f)));

    public static final RegistryObject<Block> BLOOD_PLANKS = registerBlock("blood_planks", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)){
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });
    public static final RegistryObject<Block> BLOOD_PLANKS_STAIRS = registerBlock("blood_planks_stairs", () -> new StairBlock(() -> ModBlocks.BLOOD_PLANKS.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> BLOOD_PLANKS_SLAB = registerBlock("blood_planks_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> BLOOD_PLANKS_FENCE = registerBlock("blood_planks_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> BLOOD_PLANKS_FENCE_GATE = registerBlock("blood_planks_fence_gate", () -> new FenceGateBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), SoundEvents.WOOD_PLACE, SoundEvents.WOOD_BREAK));


    public static final RegistryObject<Block> BLOOD_WOOD_CHEST = registerBlock("blood_wood_chest", () -> new BHChestBlock(
            BlockBehaviour.Properties.copy(Blocks.CHEST).strength(1F).lightLevel((state)-> 7).noOcclusion()));
    public static final RegistryObject<Block> BLOOD_LEAVES = registerBlock("blood_leaves", ()-> new LeavesBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).lightLevel((state)->15)));
    public static final RegistryObject<Block> SOUL_LOG = registerBlock("soul_log", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> SOUL_WOOD = registerBlock("soul_wood", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_SOUL_LOG = registerBlock("stripped_soul_log", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_SOUL_WOOD = registerBlock("stripped_soul_wood", ()-> new ModFlammableRotatedPillarBlock(
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> SOUL_PLANKS = registerBlock("soul_planks", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)){
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });
    public static final RegistryObject<Block> SOUL_LEAVES = registerBlock("soul_leaves", ()-> new LeavesBlock(
            BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));


    //Special mushrooms
    public static final RegistryObject<Block> VORACIOUS_MUSHROOM_BLOCK = registerBlock("voracious_mushroom_block", ()-> new VoraciousMushroomBlock(
            BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM_BLOCK).strength(0.5f).noOcclusion().lightLevel((state)->10)));
    public static final RegistryObject<Block> VISCERAL_INFECTED_VEIN = registerBlock("visceral_infected_vein", ()-> new InfestationVeinBlock(
            BlockBehaviour.Properties.copy(Blocks.SCULK_VEIN).strength(0.5f).noOcclusion().noCollission().replaceable().lightLevel((state)->10).noLootTable()));



    //Fluid
    // FLUID BLOCKS
    public static final RegistryObject<LiquidBlock> BLOOD_FLUID_BLOCK = BLOCKS.register("blood_fluid_block",
            () -> new BaseLiquidBlock(
                    ModFluids.BLOOD_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable(),
                    ModParticles.BLOOD_PARTICLES, // Lazy Particle
                    0.05f // Chance
            ));

    // 2. CORRUPTED BLOOD (Dark Red) -> Uses Vanilla Ash
    public static final RegistryObject<LiquidBlock> CORRUPTED_BLOOD_BLOCK = BLOCKS.register("corrupted_blood_block",
            () -> new BaseLiquidBlock(
                    ModFluids.CORRUPTED_BLOOD_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable(),
                    () -> net.minecraft.core.particles.ParticleTypes.ASH,
                    0.1f
            ));

    // 3. VISCOUS BLASPHEMY (Black/Yellow) -> Uses Magic Gold Particles
    public static final RegistryObject<LiquidBlock> VISCOUS_BLASPHEMY_BLOCK = BLOCKS.register("viscous_blasphemy_block",
            () -> new BaseLiquidBlock(
                    ModFluids.VISCOUS_BLASPHEMY_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.LAVA).noLootTable().lightLevel((state) -> 15),
                    () -> new net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions(
                            new org.joml.Vector3f(1.0f, 0.88f, 0.07f), // Gold Color
                            1.2f, // Size
                            true, // Jitter
                            40    // Lifetime
                    ),
                    0.02f
            ));
    // 3. VISCOUS BLASPHEMY (Black/Yellow) -> Uses Magic Gold Particles
    public static final RegistryObject<LiquidBlock> VISCERAL_BLOOD_BLOCK = BLOCKS.register("visceral_fluid_block",
            () -> new BaseLiquidBlock(
                    ModFluids.VISCERAL_BLOOD_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.LAVA).noLootTable().lightLevel((state) -> 15),
                    () -> new net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions(
                            new org.joml.Vector3f(1.0f, 0.88f, 0.07f), // Gold Color
                            1.2f, // Size
                            true, // Jitter
                            40    // Lifetime
                    ),
                    0.02f
            ));


    //Glowing
    public static final RegistryObject<Block> SOUL_LAMP = registerBlock("soul_lamp", ()-> new Soullampblock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6f).lightLevel(state -> state.getValue(Soullampblock.LIT) ? 15 : 0).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOOD_GLOW_STONE = registerBlock("blood_glow_stone",
            ()-> new EffectBlock(BlockBehaviour.Properties.copy(Blocks.GLOWSTONE).lightLevel((state)->15)));
    public static final RegistryObject<Block> GLOWING_CRYSTAL = registerBlock("glowing_crystal", () -> new GlowingCrystalBlock(
            BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK).strength(1F).lightLevel((state)-> 20).explosionResistance(20f).noOcclusion()));
    public static final RegistryObject<Block> GLOWING_CRYSTAL_GLASS_BLOCK = registerBlock("glowing_crystal_glass_block", () -> new GlassBlock(
            BlockBehaviour.Properties.copy(Blocks.GLASS).strength(1F).lightLevel((state)-> 20).noOcclusion()));
    public static final RegistryObject<Block> GLOWING_CRYSTAL_LANTERN = registerBlock("glowing_crystal_lantern", () -> new LanternBlock(
            BlockBehaviour.Properties.copy(Blocks.GLASS).strength(1F).lightLevel((state)-> 14).noOcclusion()));
    public static final RegistryObject<Block> BLOOD_GLOWING_CHAINS_BLOCK = registerBlock("blood_glowing_chains_block", () -> new ChainBlock(
            BlockBehaviour.Properties.copy(Blocks.CHAIN).strength(1F).lightLevel((state)-> 7).noOcclusion()));

    //Tranfusion Power Generating Mushrooms
    public static final RegistryObject<Block> BLOOD_ALTAR = BLOCKS.register("blood_altar", ()-> new BloodAltarBlock(
            BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES).noOcclusion().lightLevel((state)->15).noParticlesOnBreak()));
    public static final RegistryObject<Block> MAIN_BLOOD_ALTAR = BLOCKS.register("main_blood_altar", ()-> new MainBloodAltarBlock(
            BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES).noOcclusion().lightLevel((state)->15).noParticlesOnBreak()));

    //Blasphemous Biome Blocks
    public static final RegistryObject<Block> BLASPHEMOUS_SAND_BLOCK = registerBlock("blasphemous_sand_block", () -> new SandBlock(1,
            BlockBehaviour.Properties.copy(Blocks.SAND).strength(1F)));
    public static final RegistryObject<Block> BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CINDER_BLOOM_CACTUS_ROOT = registerBlock("cinder_bloom_cactus_root", ()-> new CinderBloomCactusRoot(
            BlockBehaviour.Properties.copy(Blocks.CACTUS).strength(0.5f).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CINDER_BLOOM_CACTUS_CON = registerBlock("cinder_bloom_cactus_con", ()-> new CinderBloomCactusCon(
            BlockBehaviour.Properties.copy(Blocks.CACTUS).strength(0.5f).noOcclusion().requiresCorrectToolForDrops()) {
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
            super.createBlockStateDefinition(stateManager);
        }
    });
    public static final RegistryObject<Block> CINDER_BLOOM_CACTUS_CENTER = registerBlock("cinder_bloom_cactus_center", ()-> new CinderBloomCactusCenter(
            BlockBehaviour.Properties.copy(Blocks.CACTUS).strength(0.5f).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CINDER_BLOOM_CACTUS_FLOWER = registerBlock("cinder_bloom_cactus_flower", ()-> new CinderBloomCactusFlower(
            BlockBehaviour.Properties.copy(Blocks.SUNFLOWER).strength(0.5f).lightLevel(state -> state.getValue(CinderBloomCactusFlower.OPEN) ? 15 : 0).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ERODED_BLASPHEMOUS_SANDSTONE = registerBlock("eroded_blasphemous_sandstone", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).lightLevel((state)->15).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> FULLY_ERODED_BLASPHEMOUS_SANDSTONE = registerBlock("fully_eroded_blasphemous_sandstone", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).lightLevel((state)->15).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CRACKED_BLASPHEMOUS_SANDSTONE = registerBlock("cracked_blasphemous_sandstone", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).lightLevel((state)->15).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SPIKY_GRASS = registerBlock("spiky_grass", ()-> new BlasphemousPlant(
            BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(0.1f).lightLevel((state)->15).noCollission().noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ROUNDED_GRASS = registerBlock("rounded_grass", ()-> new BlasphemousPlant(
            BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(0.1f).noCollission().noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> STING_FLOWER = registerBlock("sting_flower", () -> new TallPlantBlock(
            BlockBehaviour.Properties.copy(Blocks.ROSE_BUSH)
                    .lightLevel(state -> state.getValue(TallPlantBlock.PART) == TallPlantPart.TOP ? 15 : 0)
    ));

    //Misc
    public static final RegistryObject<Block> BLEEDING_BLOCK = registerBlock("bleeding_block", () -> new BleedingBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(6f).requiresCorrectToolForDrops() ));
    public static final RegistryObject<Block> SANGUINE_CRUCIBLE = registerBlock("sanguine_crucible", ()-> new BloodWorkbenchBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> BLOOD_PORTAL = registerBlockWithoutBlockItem("blood_portal", ModBloodPortalBlock::new);
    public static final RegistryObject<Block> SMALL_ROCKS = registerBlock("small_rocks", ()-> new SmallRocks(
                    BlockBehaviour.Properties.copy(Blocks.STONE).noCollission().noOcclusion().strength(1)));
    public static final RegistryObject<Block> ONI_STATUE = registerBlock("oni_statue", () -> new OniStatueBlock(
            BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK).strength(1F).noOcclusion().randomTicks().noLootTable()));
    public static final RegistryObject<Block> Jumpy_Block = registerBlock("jumpy_block", ()-> new JumpyBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CHISELED_BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("chiseled_blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CHISELED_DETAILED_BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("chiseled_detailed_blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CUT_BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("cut_blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK = registerBlock("smooth_blasphemous_sandstone_block", ()-> new Block(
            BlockBehaviour.Properties.copy(Blocks.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SMOOTH_BLASPHEMOUS_SANDSTONE_STAIRS = registerBlock("smooth_blasphemous_sandstone_stairs", () -> new StairBlock(() -> ModBlocks.SMOOTH_BLASPHEMOUS_SANDSTONE_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.STONE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SMOOTH_BLASPHEMOUS_SANDSTONE_SLAB = registerBlock("smooth_blasphemous_sandstone_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SELIORA_RESTING_BLOCK = registerBlock("seliora_resting_block", () -> new SelioraRestingBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion().strength(2000f)));
    public static final RegistryObject<Block> STAR_LAMP_BLOCK = registerBlock("star_lamp_block", () -> new StarLampBlock(
            BlockBehaviour.Properties.copy(Blocks.STONE).lightLevel((state)->15).noOcclusion()));
    public static final RegistryObject<Block> DECORATED_POT_BLOCK = registerBlock("decorated_pot_block", () -> new DetailedPotBlock(
            BlockBehaviour.Properties.of() // Usamos .of() para empezar de cero
                    .mapColor(MapColor.STONE)
                    .strength(0.2f) // Se rompe facil
                    .sound(SoundType.DECORATED_POT) // Sonido de vasija
                    .noOcclusion()
                    .noCollission()
            // IMPORTANTE: NO ponemos .requiresCorrectToolForDrops()
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
                            .sound(SILENT) // <--- APPLY THE FIX HERE
                            .lightLevel(state -> 15) // Ensure it still emits light if needed
                            .replaceable()
                            .noLootTable()

            ));

    public static final RegistryObject<Block> BLASPHEMITE_ORE = registerBlock("blasphemite_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(5f) // Dureza (Hierro es 3, Diamante es 3, Deepslate es 4.5)
                    .requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7))); // Experiencia que suelta (entre 3 y 7)


    //Ancient Bloody Stone Bricks


    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS = registerBlock("ancient_bloody_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(2f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS_SLAB = registerBlock("ancient_bloody_stone_bricks_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(2f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS_STAIRS = registerBlock("ancient_bloody_stone_bricks_stairs",
            () -> new StairBlock(() -> ModBlocks.ANCIENT_BLOODY_STONE_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(2f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS_WALL = registerBlock("ancient_bloody_stone_bricks_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(2f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_STONE_BRICKS_COLUMN = registerBlock("ancient_bloody_stone_bricks_column",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(2f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_DETAILED_BLOODY_STONE_BRICKS = registerBlock("ancient_detailed_bloody_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(2f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_CHISELED_BLOODY_STONE_BRICKS= registerBlock("ancient_chiseled_bloody_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(2f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ANCIENT_BLOODY_LAMP = registerBlock("ancient_bloody_lamp", () -> new AncientBloodyLamp(
            BlockBehaviour.Properties.copy(Blocks.LANTERN).strength(1F).lightLevel((state)-> 14).noOcclusion()));
    public static final RegistryObject<Block> ANCIENT_TORCH_BLOCK = registerBlock("ancient_torch_block", () -> new AncientTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.TORCH).strength(1F).lightLevel((state) -> 14).noOcclusion()) {
    });

    public static final RegistryObject<Block> ANCIENT_BLOOD_CAPSULE = registerBlock("ancient_blood_capsule",
            () -> new BloodCapsuleBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .strength(0.3f)
                    .sound(SoundType.GLASS)
                    .noOcclusion()));

    //Mechanisms
    public static final RegistryObject<Block> SANGUINITE_PIPE = registerBlock("sanguinite_pipe",
            () -> new SanguinitePipeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SANGUINITE_TANK = registerBlock("sanguinite_tank",
            () -> new SanguiniteTankBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SANGUINITE_BLOOD_HARVESTER = registerBlock("sanguinite_blood_harvester",
            () -> new SanguiniteBloodHarvesterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SANGUINITE_INFUSOR = registerBlock("sanguinite_infusor",
            () -> new SanguiniteInfusorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLOOD_GEM_SPROUT = registerBlock("blood_gem_sprout",
            () -> new BloodGemSproutBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion().strength(3f).noParticlesOnBreak().requiresCorrectToolForDrops()));

    private static <T extends Block> RegistryObject<T> registerBlockWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }



}
