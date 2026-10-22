package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.block.registry.PlantBlocks;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;

public class TallGrowingPlant extends Block implements BonemealableBlock, IPlantable {
    protected static final float SMALL_LEAVES_AABB_OFFSET = 3.0F;
    protected static final float LARGE_LEAVES_AABB_OFFSET = 5.0F;
    protected static final float COLLISION_AABB_OFFSET = 1.5F;
    protected static final VoxelShape SMALL_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape LARGE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    public static final IntegerProperty AGE;
    public static final EnumProperty<BloodScrapperPlantLeaves> LEAVES;
    public static final IntegerProperty STAGE;
    public static final int MAX_HEIGHT = 16;
    public static final int STAGE_GROWING = 0;
    public static final int STAGE_DONE_GROWING = 1;
    public static final int AGE_THIN_BAMBOO = 0;
    public static final int AGE_THICK_BAMBOO = 1;


    public TallGrowingPlant(BlockBehaviour.Properties p_261753_) {
        super(p_261753_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(LEAVES, BloodScrapperPlantLeaves.NONE)).setValue(STAGE, 0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_261641_) {
        p_261641_.add(new Property[]{AGE, LEAVES, STAGE});
    }

    public boolean propagatesSkylightDown(BlockState p_261479_, BlockGetter p_261942_, BlockPos p_261844_) {
        return true;
    }

    public VoxelShape getShape(BlockState p_261515_, BlockGetter p_261586_, BlockPos p_261526_, CollisionContext p_261930_) {
        VoxelShape voxelshape = p_261515_.getValue(LEAVES) == BloodScrapperPlantLeaves.LARGE ? LARGE_SHAPE : SMALL_SHAPE;
        Vec3 vec3 = p_261515_.getOffset(p_261586_, p_261526_);
        return voxelshape.move(vec3.x, vec3.y, vec3.z);
    }

    public boolean isPathfindable(BlockState p_262166_, BlockGetter p_262143_, BlockPos p_261968_, PathComputationType p_261513_) {
        return false;
    }

    public VoxelShape getCollisionShape(BlockState p_261560_, BlockGetter p_261965_, BlockPos p_261950_, CollisionContext p_261571_) {
        Vec3 vec3 = p_261560_.getOffset(p_261965_, p_261950_);
        return COLLISION_SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    public boolean isCollisionShapeFullBlock(BlockState p_262062_, BlockGetter p_261848_, BlockPos p_261466_) {
        return false;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_261764_) {
        FluidState fluidstate = p_261764_.getLevel().getFluidState(p_261764_.getClickedPos());
        if (!fluidstate.isEmpty()) {
            return null;
        } else {
            BlockState blockstate = p_261764_.getLevel().getBlockState(p_261764_.getClickedPos().below());
            if (blockstate.is(ModTags.Blocks.BLOOD_SCRAPPER_PLANT_PLACEABLE_ON)) {
                if (blockstate.is(PlantBlocks.BLOOD_SCRAPPER_PLANT_SAPLING.get())) {
                    return (BlockState)this.defaultBlockState().setValue(AGE, 0);
                } else if (blockstate.is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get())) {
                    int i = (Integer)blockstate.getValue(AGE) > 0 ? 1 : 0;
                    return (BlockState)this.defaultBlockState().setValue(AGE, i);
                } else {
                    BlockState blockstate1 = p_261764_.getLevel().getBlockState(p_261764_.getClickedPos().above());
                    return blockstate1.is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get()) ? (BlockState)this.defaultBlockState().setValue(AGE, (Integer)blockstate1.getValue(AGE)) : PlantBlocks.BLOOD_SCRAPPER_PLANT_SAPLING.get().defaultBlockState();
                }
            } else {
                return null;
            }
        }
    }

    public void tick(BlockState p_261612_, ServerLevel p_261527_, BlockPos p_261846_, RandomSource p_261638_) {
        if (!p_261612_.canSurvive(p_261527_, p_261846_)) {
            p_261527_.destroyBlock(p_261846_, true);
        }

    }

    public boolean isRandomlyTicking(BlockState p_262083_) {
        return (Integer)p_262083_.getValue(STAGE) == 0;
    }

    public void randomTick(BlockState p_261931_, ServerLevel p_261751_, BlockPos p_261616_, RandomSource p_261766_) {
        if ((Integer)p_261931_.getValue(STAGE) == 0 && p_261751_.isEmptyBlock(p_261616_.above()) && p_261751_.getRawBrightness(p_261616_.above(), 0) >= 9) {
            int i = this.getHeightBelowUpToMax(p_261751_, p_261616_) + 1;
            if (i < 16 && ForgeHooks.onCropsGrowPre(p_261751_, p_261616_, p_261931_, p_261766_.nextInt(3) == 0)) {
                this.growBamboo(p_261931_, p_261751_, p_261616_, p_261766_, i);
                ForgeHooks.onCropsGrowPost(p_261751_, p_261616_, p_261931_);
            }
        }

    }

    public boolean canSurvive(BlockState p_261860_, LevelReader p_262154_, BlockPos p_261493_) {
        return p_262154_.getBlockState(p_261493_.below()).is(ModTags.Blocks.BLOOD_SCRAPPER_PLANT_PLACEABLE_ON);
    }

    public BlockState updateShape(BlockState p_261476_, Direction p_261512_, BlockState p_262167_, LevelAccessor p_261956_, BlockPos p_261876_, BlockPos p_262140_) {
        if (!p_261476_.canSurvive(p_261956_, p_261876_)) {
            p_261956_.scheduleTick(p_261876_, this, 1);
        }

        if (p_261512_ == Direction.UP && p_262167_.is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get()) && (Integer)p_262167_.getValue(AGE) > (Integer)p_261476_.getValue(AGE)) {
            p_261956_.setBlock(p_261876_, (BlockState)p_261476_.cycle(AGE), 2);
        }

        return super.updateShape(p_261476_, p_261512_, p_262167_, p_261956_, p_261876_, p_262140_);
    }

    public boolean isValidBonemealTarget(LevelReader p_262065_, BlockPos p_262033_, BlockState p_261700_, boolean p_261524_) {
        int i = this.getHeightAboveUpToMax(p_262065_, p_262033_);
        int j = this.getHeightBelowUpToMax(p_262065_, p_262033_);
        return i + j + 1 < 16 && (Integer)p_262065_.getBlockState(p_262033_.above(i)).getValue(STAGE) != 1;
    }

    public boolean isBonemealSuccess(Level p_261870_, RandomSource p_261802_, BlockPos p_262123_, BlockState p_261972_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_261845_, RandomSource p_262034_, BlockPos p_261955_, BlockState p_261685_) {
        int i = this.getHeightAboveUpToMax(p_261845_, p_261955_);
        int j = this.getHeightBelowUpToMax(p_261845_, p_261955_);
        int k = i + j + 1;
        int l = 1 + p_262034_.nextInt(2);

        for(int i1 = 0; i1 < l; ++i1) {
            BlockPos blockpos = p_261955_.above(i);
            BlockState blockstate = p_261845_.getBlockState(blockpos);
            if (k >= 16 || (Integer)blockstate.getValue(STAGE) == 1 || !p_261845_.isEmptyBlock(blockpos.above())) {
                return;
            }

            this.growBamboo(blockstate, p_261845_, blockpos, p_262034_, k);
            ++i;
            ++k;
        }

    }

    public float getDestroyProgress(BlockState p_261691_, Player p_262171_, BlockGetter p_261621_, BlockPos p_261500_) {
        return p_262171_.getMainHandItem().canPerformAction(ToolActions.SWORD_DIG) ? 1.0F : super.getDestroyProgress(p_261691_, p_262171_, p_261621_, p_261500_);
    }

    protected void growBamboo(BlockState p_261855_, Level p_262076_, BlockPos p_262109_, RandomSource p_261633_, int p_261759_) {
        BlockState blockstate = p_262076_.getBlockState(p_262109_.below());
        BlockPos blockpos = p_262109_.below(2);
        BlockState blockstate1 = p_262076_.getBlockState(blockpos);
        BloodScrapperPlantLeaves bloodbambooleaves = BloodScrapperPlantLeaves.NONE;
        if (p_261759_ >= 1) {
            if (blockstate.is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get()) && blockstate.getValue(LEAVES) != BloodScrapperPlantLeaves.NONE) {
                if (blockstate.is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get()) && blockstate.getValue(LEAVES) != BloodScrapperPlantLeaves.NONE) {
                    bloodbambooleaves = BloodScrapperPlantLeaves.LARGE;
                    if (blockstate1.is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get())) {
                        p_262076_.setBlock(p_262109_.below(), (BlockState)blockstate.setValue(LEAVES, BloodScrapperPlantLeaves.SMALL), 3);
                        p_262076_.setBlock(blockpos, (BlockState)blockstate1.setValue(LEAVES, BloodScrapperPlantLeaves.NONE), 3);
                    }
                }
            } else {
                bloodbambooleaves = BloodScrapperPlantLeaves.SMALL;
            }
        }

        int i = (Integer)p_261855_.getValue(AGE) != 1 && !blockstate1.is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get()) ? 0 : 1;
        int j = (p_261759_ < 11 || !(p_261633_.nextFloat() < 0.25F)) && p_261759_ != 15 ? 0 : 1;
        p_262076_.setBlock(p_262109_.above(), (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(AGE, i)).setValue(LEAVES, bloodbambooleaves)).setValue(STAGE, j), 3);
    }

    protected int getHeightAboveUpToMax(BlockGetter p_261541_, BlockPos p_261593_) {
        int i;
        for(i = 0; i < 16 && p_261541_.getBlockState(p_261593_.above(i + 1)).is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get()); ++i) {
        }

        return i;
    }

    protected int getHeightBelowUpToMax(BlockGetter p_261927_, BlockPos p_261481_) {
        int i;
        for(i = 0; i < 16 && p_261927_.getBlockState(p_261481_.below(i + 1)).is(PlantBlocks.BLOOD_SCRAPPER_PLANT.get()); ++i) {
        }

        return i;
    }

    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() != this ? this.defaultBlockState() : state;
    }

    static {
        AGE = BlockStateProperties.AGE_1;
        LEAVES = ModBlockStateProperties.BLOOD_LEAVES;
        STAGE = BlockStateProperties.STAGE;
    }
}
