package net.agusdropout.bloodyhell.block.custom.altar;


import net.agusdropout.bloodyhell.block.base.AbstractMainAltarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBlasphemousBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.TentacleEntity;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.BlackHoleParticleOptions;
import net.agusdropout.bloodyhell.recipe.BloodAltarRecipe;
import net.agusdropout.bloodyhell.util.VanillaPacketDispatcher;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainBlasphemousBloodAltarBlock extends AbstractMainAltarBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape LOWER_SHAPE = Block.box(0, 0, 0, 16, 32, 16);
    private static final VoxelShape UPPER_SHAPE = Block.box(0, -16, 0, 16, 16, 16);

    public MainBlasphemousBloodAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new MainBlasphemousBloodAltarBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
        super.triggerEvent(pState, pLevel, pPos, pId, pParam);
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity != null && blockentity.triggerEvent(pId, pParam);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        // Redirect upper half clicks to the lower half
        if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = blockPos.below();
            BlockState belowState = level.getBlockState(below);
            return belowState.use(level, player, interactionHand, blockHitResult.withPosition(below));
        }

        if (!(level.getBlockEntity(blockPos) instanceof MainBlasphemousBloodAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (interactionHand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.is(ModItems.FILLED_BLOOD_FLASK.get())) {
                if(!level.isClientSide()) {
                    altar.setActive(true);
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                        ItemStack emptyFlask = new ItemStack(ModItems.BLOOD_FLASK.get());
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
                }
                return InteractionResult.sidedSuccess(level.isClientSide());

            } else if (altar.isActive() && isAltarSetupReady(level, blockPos, BlasphemousBloodAltarBlock.class)) {

                if (level.isClientSide()) return InteractionResult.SUCCESS;

                List<List<Item>> itemsFromAltars = getItemsFromAltars(level, blockPos);
                if (itemsFromAltars.size() != 4) return InteractionResult.FAIL;

                List<Item> referenceSet = itemsFromAltars.get(0);
                if (!isSetEqual(referenceSet, itemsFromAltars.get(1)) ||
                        !isSetEqual(referenceSet, itemsFromAltars.get(2)) ||
                        !isSetEqual(referenceSet, itemsFromAltars.get(3))) {
                    player.sendSystemMessage(Component.literal("§cFailed ritual: All altars must contain the same set of items."));
                    return InteractionResult.FAIL;
                }

                SimpleContainer inventory = new SimpleContainer(3);
                for (int i = 0; i < referenceSet.size(); i++) {
                    inventory.setItem(i, new ItemStack(referenceSet.get(i)));
                }

                Optional<BloodAltarRecipe> recipe = level.getRecipeManager()
                        .getRecipeFor(BloodAltarRecipe.Type.INSTANCE, inventory, level);

                if (recipe.isPresent()) {
                    ItemStack result = recipe.get().getResultItem(level.registryAccess());
                    Item resultItem = result.getItem();
                    consumeItemsFromAltars(level, blockPos);



                    return finalizeRitualServer(level, blockPos, player, blockState, altar, result);

                } else {
                    level.playSound(null, blockPos, SoundEvents.BASALT_BREAK, SoundSource.BLOCKS, 0.5F, 0.5F);
                    ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.05);
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.PASS;
    }

    private InteractionResult finalizeRitualServer(Level level, BlockPos blockPos, Player player, BlockState blockState, MainBlasphemousBloodAltarBlockEntity altar, ItemStack rewardStack) {
        altar.setActive(false);
        level.setBlock(blockPos, blockState.setValue(ACTIVE, false), 3);
        level.setBlock(blockPos.above(), level.getBlockState(blockPos.above()).setValue(ACTIVE, false), 3);
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);

        level.blockEvent(blockPos, this, 1, 0);

        if (level instanceof ServerLevel serverLevel) {
            BlockPos center = blockPos;
            BlockPos[] targets = {
                    center.north(4), center.east(4), center.south(4), center.west(4)
            };

            serverLevel.sendParticles(new BlackHoleParticleOptions(2.0f, 1.0f, 0.871f, 0f, false),
                    center.getX() + 0.5, center.getY() + 6, center.getZ() + 0.5,
                    1, 0, 0, 0, 0.1);
            level.playSound(null, center, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.BLOCKS, 2.0f, 0.5f);

            for (BlockPos targetAltar : targets) {
                if (level.getBlockState(targetAltar).getBlock() instanceof BlasphemousBloodAltarBlock) {
                    TentacleEntity tentacle = new TentacleEntity(ModEntityTypes.TENTACLE_ENTITY.get(), level);
                    tentacle.setPos(center.getX() + 0.5, center.getY() + 1.5, center.getZ() + 0.5);
                    tentacle.setTargetAltar(targetAltar);
                    tentacle.setSummoner(player);
                    tentacle.setInitialDelay(level.random.nextInt(40));
                    level.addFreshEntity(tentacle);
                }
            }

            if (rewardStack != null && !rewardStack.isEmpty()) {
                TentacleEntity rewardTentacle = new TentacleEntity(ModEntityTypes.TENTACLE_ENTITY.get(), level);
                rewardTentacle.setPos(center.getX() + 0.5, center.getY() + 1.5, center.getZ() + 0.5);
                rewardTentacle.setSummoner(player);
                rewardTentacle.setRewardItem(rewardStack.copy());
                rewardTentacle.setTargetAltar(player.blockPosition().above());
                rewardTentacle.setInitialDelay(80);
                level.addFreshEntity(rewardTentacle);
            }
        }
        return InteractionResult.CONSUME;
    }

    private boolean isSetEqual(List<Item> setA, List<Item> setB) {
        if (setA.size() != setB.size()) return false;
        List<Item> copyB = new ArrayList<>(setB);
        for (Item item : setA) {
            if (!copyB.remove(item)) return false;
        }
        return copyB.isEmpty();
    }

    private void performSummonCow(Level level, BlockPos pos) {
        Cow cow = new Cow(EntityType.COW, level);
        cow.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(cow);
    }

    private void performFindMausoleum(Level level, BlockPos pos, Player player) {
        if (level instanceof ServerLevel serverLevel) {
            BlockPos location = serverLevel.findNearestMapStructure(ModTags.Structures.MAUSOLEUM, pos, 100, false);
            if (location != null) {
                BlockPos safePos = serverLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, location);
                player.teleportTo(safePos.getX(), safePos.getY() + 1, safePos.getZ());
                serverLevel.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY(), player.getZ(), 50, 0.5, 1, 0.5, 0.1);
                player.sendSystemMessage(Component.literal("§cYou have been summoned to the mausoleum..."));
            }
        }
    }



    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0 && state.getValue(ACTIVE)) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WARDEN_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }
    }
}