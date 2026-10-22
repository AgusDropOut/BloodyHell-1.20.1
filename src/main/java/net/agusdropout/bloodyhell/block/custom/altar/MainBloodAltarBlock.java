package net.agusdropout.bloodyhell.block.custom.altar;

import net.agusdropout.bloodyhell.item.registry.FlaskItems;
import net.agusdropout.bloodyhell.block.base.AbstractMainAltarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.recipe.BloodAltarRecipe;
import net.agusdropout.bloodyhell.util.VanillaPacketDispatcher;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class MainBloodAltarBlock extends AbstractMainAltarBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty HAS_RESULT = BooleanProperty.create("has_result");

    private static final VoxelShape LOWER_SHAPE = Block.box(0, 0, 0, 16, 32, 16);
    private static final VoxelShape UPPER_SHAPE = Block.box(0, -16, 0, 16, 16, 16);

    public MainBloodAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(ACTIVE, false)
                .setValue(HAS_RESULT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE, HAS_RESULT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new MainBloodAltarBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MainBloodAltarBlockEntity altar) {
                if (altar.hasResultItem()) {
                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, altar.getResultItem());
                    level.addFreshEntity(itemEntity);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) return;

        if (state.getValue(HAS_RESULT)) {
            if (random.nextFloat() < 0.8f) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
                double y = pos.getY() + 2.0 + (random.nextDouble() - 0.5) * 0.4;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;

                MagicParticleOptions options = new MagicParticleOptions(SpellPalette.RHNULL.getRandomColor(), 0.3f, false, 25, true);
                level.addParticle(options, x, y, z, 0, 0.02, 0);
            }
        }
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = blockPos.below();
            return level.getBlockState(below).use(level, player, interactionHand, blockHitResult.withPosition(below));
        }

        if (!(level.getBlockEntity(blockPos) instanceof MainBloodAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (interactionHand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandItem();

            if (altar.hasResultItem() && !heldItem.is(FlaskItems.CORRUPTED_BLOOD_FLASK.get())) {
                if (!level.isClientSide()) {
                    ItemStack result = altar.getResultItem();
                    if (!player.getInventory().add(result)) {
                        player.drop(result, false);
                    }
                    altar.clearResultItem();
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);

                    level.setBlock(blockPos, blockState.setValue(HAS_RESULT, false), 3);
                    level.setBlock(blockPos.above(), level.getBlockState(blockPos.above()).setValue(HAS_RESULT, false), 3);

                    level.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, 1.2F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            if (heldItem.is(FlaskItems.CORRUPTED_BLOOD_FLASK.get())) {
                if (!level.isClientSide()) {
                    altar.setActive(true);
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                        ItemStack emptyFlask = new ItemStack(FlaskItems.BLOOD_FLASK.get());
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(InteractionHand.MAIN_HAND, emptyFlask);
                        } else if (!player.getInventory().add(emptyFlask)) {
                            player.drop(emptyFlask, false);
                        }
                    }
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);

                    level.setBlock(blockPos, blockState.setValue(ACTIVE, true), 3);
                    level.setBlock(blockPos.above(), level.getBlockState(blockPos.above()).setValue(ACTIVE, true), 3);

                    level.playSound(null, blockPos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    super.handleVisualEffects(level, blockPos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());

            } else if (altar.isActive() && isAltarSetupReady(level, blockPos, BloodAltarBlock.class)) {
                if (level.isClientSide()) return InteractionResult.SUCCESS;

                List<List<Item>> itemsFromAltars = getItemsFromAltars(level, blockPos);

                if (itemsFromAltars.size() != 4) return InteractionResult.FAIL;

                SimpleContainer inventory = new SimpleContainer(4);
                for (int i = 0; i < 4; i++) {
                    List<Item> pedestalItems = itemsFromAltars.get(i);
                    if (pedestalItems.isEmpty()) return InteractionResult.FAIL;
                    inventory.setItem(i, new ItemStack(pedestalItems.get(0)));
                }

                Optional<BloodAltarRecipe> recipe = level.getRecipeManager()
                        .getRecipeFor(BloodAltarRecipe.Type.INSTANCE, inventory, level);

                if (recipe.isPresent()) {
                    ItemStack result = recipe.get().getResultItem(level.registryAccess());

                    dischargeAltars(level, blockPos);

                    consumeItemsFromAltars(level, blockPos);

                    altar.setResultItem(result.copy());
                    altar.setActive(false);

                    level.setBlock(blockPos, blockState.setValue(ACTIVE, false).setValue(HAS_RESULT, true), 3);
                    level.setBlock(blockPos.above(), level.getBlockState(blockPos.above()).setValue(ACTIVE, false).setValue(HAS_RESULT, true), 3);
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);

                    level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5, 15, 0.2, 0.2, 0.2, 0.1);
                    }

                    return InteractionResult.CONSUME;

                } else {
                    level.playSound(null, blockPos, SoundEvents.BASALT_BREAK, SoundSource.BLOCKS, 0.5F, 0.5F);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SMOKE, blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.05);
                    }
                    player.sendSystemMessage(Component.literal("§cThe corrupted blood rejects these offerings..."));
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.PASS;
    }

}