package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGemSproutBlockEntity;
import net.agusdropout.bloodyhell.block.entity.custom.plant.BloodGemSproutBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class BaseGemSproutBlock  extends BaseEntityBlock {

    public static final int MAX_AGE = 5;


    // 0 = Empty/Just Planted, 3 = Fully Grown
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final BooleanProperty ITEM_INSIDE = BooleanProperty.create("item_inside");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;


    public BaseGemSproutBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(FILLED, false)
                .setValue(ITEM_INSIDE, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BloodGemSproutBlockEntity sprout) {
                if (state.getValue(AGE) == MAX_AGE) {
                    popGem(pos, level, level.random);
                    sprout.setGemType("empty");
                    sprout.setTempStoredItem(ItemStack.EMPTY);
                    level.setBlock(pos, state.setValue(ITEM_INSIDE, false), 3);
                    sprout.setGemColor(0x3cff00); // Reset color to Default Green
                    level.setBlock(pos, state.setValue(AGE, 0), 3);
                    level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                } else {
                    if(player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.FILLED_BLOOD_FLASK.get())){
                        ItemStack flask = player.getItemInHand(InteractionHand.MAIN_HAND);
                        if(!player.isCreative()) {
                            flask.shrink(1);
                        }
                        if( sprout.fillBlood(new FluidStack(ModFluids.BLOOD_SOURCE.get(),250), false)) {
                            level.setBlock(pos, state.setValue(FILLED, true), 3);
                            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

                if(state.getValue(AGE) == BaseGemSproutBlockEntity.INSERT_GEM_AGE ){
                    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                    GemType gem = GemType.fromItem(stack);
                    if(gem != GemType.EMPTY){
                        sprout.setGemColor(gem.getColor());
                        sprout.setGemType(gem.name());
                        level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                        level.setBlock(pos, state.setValue(ITEM_INSIDE, true), 3);
                        sprout.setTempStoredItem(stack);
                        if(!player.isCreative()) {
                            stack.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE).add(FILLED).add(FACING).add(ITEM_INSIDE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BloodGemSproutBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(level.isClientSide) return null ;
        return createTickerHelper(type, ModBlockEntities.BLOOD_GEM_SPROUT_BE.get(),
                (l, p, s, be) -> be.tick(l, p, s));
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    public void popGem(BlockPos pos, Level level, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BloodGemSproutBlockEntity sprout) {
            ItemStack gemStack = sprout.getResultGem(random);
            popResource(level, pos, gemStack);
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource) {
        super.animateTick(blockState, level, pos, randomSource);
        if(blockState.getValue(AGE) == BaseGemSproutBlockEntity.INSERT_GEM_AGE) {
            spawnAskingGemItemParticles(level, pos);
        }
    }

    private void spawnAskingGemItemParticles(Level level, BlockPos pos) {

        if (level.getGameTime() % 5 != 0) return;


        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.6;
        double z = pos.getZ() + 0.5;

        RandomSource r = level.random;

        double offsetX = (r.nextDouble() - 0.5) * 0.3;
        double offsetZ = (r.nextDouble() - 0.5) * 0.3;

        ParticleHelper.spawn(
                level,
                new MagicParticleOptions(
                        new Vector3f(0.6f, 0.0f, 0.05f),
                        0.18f,
                        false,
                        40
                ),
                x + offsetX, y, z + offsetZ,
                0, 0.015, 0
        );

        // --- 2. The "Pulse" (Bright Red) ---

        if (level.getGameTime() % 20 == 0) {
            Vec3 ringCenter = new Vec3(x, y + 0.2, z);
            ParticleHelper.spawnRing(
                    level,
                    new MagicParticleOptions(
                            new Vector3f(1.0f, 0.2f, 0.2f),
                            0.15f,
                            false,
                            30
                    ),
                    ringCenter,
                    0.35,
                    5,
                    0.0
            );
        }
    }
}
