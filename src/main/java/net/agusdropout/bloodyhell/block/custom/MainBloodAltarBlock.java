package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.BloodAltarBlockEntity;
import net.agusdropout.bloodyhell.block.entity.MainBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.TentacleEntity;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ModParticles;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainBloodAltarBlock extends BaseEntityBlock {
    private MainBloodAltarBlockEntity mainBloodAltarEntity;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public MainBloodAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 35, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        mainBloodAltarEntity = new MainBloodAltarBlockEntity(pos, state);
        return mainBloodAltarEntity;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MainBloodAltarBlockEntity) {
                ((MainBloodAltarBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    // --- ESTO ES IMPORTANTE PARA EL HACK DEL BLOCK EVENT ---
    @Override
    public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
        super.triggerEvent(pState, pLevel, pPos, pId, pParam);
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity != null && blockentity.triggerEvent(pId, pParam);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!(level.getBlockEntity(blockPos) instanceof MainBloodAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (interactionHand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandItem();

            // 1. ACTIVACIÓN
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
                    level.playSound(null, blockPos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());

                // 2. RITUAL
            } else if (altar.isActive() && isAltarSetupReady(level, blockPos)) {

                // Lado Cliente: Solo predecir éxito si la estructura está bien. NO verificar receta.
                if (level.isClientSide()) return InteractionResult.SUCCESS;

                // Lado Servidor:
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

                    if (resultItem == Items.LEATHER) {
                        performSummonCow(level, blockPos);
                        return finalizeRitualServer(level, blockPos, player, blockState, altar, ItemStack.EMPTY);
                    }
                    if (resultItem == Items.RECOVERY_COMPASS) {
                        performFindMausoleum(level, blockPos, player);
                        return finalizeRitualServer(level, blockPos, player, blockState, altar, ItemStack.EMPTY);
                    }
                    if (resultItem == Items.RED_DYE) {
                        performBloodTransformation(level, blockPos);
                        return finalizeRitualServer(level, blockPos, player, blockState, altar, ItemStack.EMPTY);
                    }

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

    private InteractionResult finalizeRitualServer(Level level, BlockPos blockPos, Player player, BlockState blockState, MainBloodAltarBlockEntity altar, ItemStack rewardStack) {
        altar.setActive(false);
        level.setBlock(blockPos, blockState.setValue(ACTIVE, false), 3);
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);

        // HACK: ENVIAR SEÑAL AL CLIENTE PARA ACTIVAR EL AMBIENCE
        // ID 1 = Ejecutar efectos de ritual
        level.blockEvent(blockPos, this, 1, 0);

        if (level instanceof ServerLevel serverLevel) {
            BlockPos center = blockPos;
            BlockPos[] targets = {
                    center.north(4), center.east(4), center.south(4), center.west(4)
            };

            serverLevel.sendParticles(ModParticles.BLACK_HOLE_PARTICLE.get(),
                    center.getX() + 0.5, center.getY() + 6, center.getZ() + 0.5,
                    1, 0, 0, 0, 0.1);
            level.playSound(null, center, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.BLOCKS, 2.0f, 0.5f);

            for (BlockPos targetAltar : targets) {
                if (level.getBlockState(targetAltar).getBlock() instanceof BloodAltarBlock) {
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

    private void performBloodTransformation(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockPos[] area = {
                below.north(), below.east(), below.south(), below.west(),
                below.north().east(), below.north().west(), below.south().east(), below.south().west()
        };
        for (BlockPos p : area) {
            if (level.getBlockState(p).is(ModBlocks.BLOOD_FLUID_BLOCK.get())) {
                level.setBlockAndUpdate(p, ModBlocks.RHNULL_BLOOD_FLUID_BLOCK.get().defaultBlockState());
                if(level instanceof ServerLevel sl) {
                    sl.sendParticles(ModParticles.BLOOD_PARTICLES.get(), p.getX()+0.5, p.getY()+1, p.getZ()+0.5, 5, 0.2, 0.2, 0.2, 0.05);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0 && state.getValue(ACTIVE)) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WARDEN_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }
    }

    public boolean isAltarSetupReady(Level level, BlockPos pos) {
        BlockPos[] pillars = {pos.north(4), pos.east(4), pos.south(4), pos.west(4)};
        for(BlockPos p : pillars) {
            if (!(level.getBlockState(p).getBlock() instanceof BloodAltarBlock)) return false;
        }
        return true;
    }

    public List<List<Item>> getItemsFromAltars(Level level, BlockPos pos) {
        List<List<Item>> items = new ArrayList<>();
        BlockPos[] altarPositions = {pos.north(4), pos.east(4), pos.south(4), pos.west(4)};
        for (BlockPos altarPos : altarPositions) {
            if (level.getBlockEntity(altarPos) instanceof BloodAltarBlockEntity entity) {
                if (!entity.getItemsInside().isEmpty()) {
                    items.add(entity.getItemsInside());
                } else {
                    items.add(new ArrayList<>());
                }
            }
        }
        return items;
    }

    public void consumeItemsFromAltars(Level level, BlockPos pos) {
        BlockPos[] posArr = {pos.north(4), pos.east(4), pos.south(4), pos.west(4)};
        for(BlockPos p : posArr) {
            if (level.getBlockEntity(p) instanceof BloodAltarBlockEntity entity) {
                entity.clearItemsInside();
            }
        }
    }
}