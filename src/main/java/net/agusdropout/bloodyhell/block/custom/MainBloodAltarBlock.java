package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.entity.BloodAltarBlockEntity;
import net.agusdropout.bloodyhell.block.entity.MainBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.TentacleEntity;
import net.agusdropout.bloodyhell.event.handlers.RitualAmbienceHandler;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.VanillaPacketDispatcher;
import net.agusdropout.bloodyhell.util.rituals.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        super.createBlockStateDefinition(builder);
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 35, 16);

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
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

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!(level.getBlockEntity(blockPos) instanceof MainBloodAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (interactionHand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.is(ModItems.FILLED_BLOOD_FLASK.get())) {
                altar.setActive(true);
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.BLOOD_FLASK.get()));
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
                level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                level.setBlock(blockPos, blockState.setValue(ACTIVE, true), 3);
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else if (altar.isActive() && isAltarSetupReady(level, blockPos)) {

                // --- RITUALES ---
                // Aquí pasamos el Item que queremos que el brazo entregue.
                // Si pasamos ItemStack.EMPTY, solo salen los brazos destructores (ej. invocaciones de mobs).

                SummonCowRitual summonCowRitual = new SummonCowRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (summonCowRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, ItemStack.EMPTY); // No da item, spawnea vaca

                TurnBloodIntoRhnullRitual turnBloodIntoRhnullRitual = new TurnBloodIntoRhnullRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (turnBloodIntoRhnullRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, ItemStack.EMPTY); // Asumo este item

                FindMausoleumRitual findMausoleumRitual = new FindMausoleumRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (findMausoleumRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, ItemStack.EMPTY); // Efecto, no item

                BloodAncientGemRitual bloodAncientGemRitual = new BloodAncientGemRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (bloodAncientGemRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, new ItemStack(ModItems.ANCIENT_GEM.get()));

                // Para los libros de hechizos, si el ritual da un libro físico, ponlo aquí. Si solo desbloquea, usa EMPTY.
                SpellBookScratchRitual spellBookScratchRitual = new SpellBookScratchRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (spellBookScratchRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, new ItemStack(ModItems.BLOOD_SPELL_BOOK_SCRATCH.get()));

                SpellBookBloodBallRitual spellBookBloodBallRitual = new SpellBookBloodBallRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (spellBookBloodBallRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, new ItemStack(ModItems.BLOOD_SPELL_BOOK_BLOODBALL.get()));

                SpellBookBloodNovaRitual spellBookBloodNovaRitual = new SpellBookBloodNovaRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (spellBookBloodNovaRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, new ItemStack(ModItems.BLOOD_SPELL_BOOK_BLOODNOVA.get()));

                SpellBookDaggersRainRitual spellBookDaggersRainRitual = new SpellBookDaggersRainRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (spellBookDaggersRainRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, new ItemStack(ModItems.BLOOD_SPELL_BOOK_DAGGERSRAIN.get()));

                GreatBloodAncientGemRitual greatBloodAncientGemRitual = new GreatBloodAncientGemRitual(blockState, level, blockPos, player, interactionHand, blockHitResult, getItemsFromAltars(level, blockPos));
                if (greatBloodAncientGemRitual.performRitual())
                    return success(level, blockPos, player, blockState, altar, new ItemStack(ModItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD.get())); // Asumo este item

            } else {

                level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BASALT_BREAK, SoundSource.BLOCKS, 0.5F, 0.5F, false);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    // AHORA ACEPTA UN ITEMSTACK DE RECOMPENSA
    private InteractionResult success(Level level, BlockPos blockPos, Player player, BlockState blockState, MainBloodAltarBlockEntity altar, ItemStack rewardStack) {
        altar.setActive(false);
        level.setBlock(blockPos, blockState.setValue(ACTIVE, false), 3);


        if (level.isClientSide) {
            RitualAmbienceHandler.triggerRitual(160);
        }

        if (!level.isClientSide) {
            BlockPos center = blockPos;
            BlockPos[] targets = {
                    center.north(4),
                    center.east(4),
                    center.south(4),
                    center.west(4)
            };

            // 1. VFX: Agujero Negro (Efecto Eldritch)
            if(level instanceof ServerLevel serverLevel ) {
                // Aparece arriba del altar (Y + 6.0 parece alto, ajústalo si queda muy lejos)
                serverLevel.sendParticles(ModParticles.BLACK_HOLE_PARTICLE.get(),
                        center.getX() + 0.5, center.getY() + 6, center.getZ() + 0.5,
                        1, 0, 0, 0, 0.1);
                level.playSound(null, center, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.BLOCKS, 2.0f, 0.5f); // Pitch bajo para que suene enorme
            }

            // 2. TENTÁCULOS DE SACRIFICIO (Los 4 destructores)
            for (BlockPos targetAltar : targets) {
                if (level.getBlockState(targetAltar).getBlock() instanceof BloodAltarBlock) {
                    TentacleEntity tentacle = new TentacleEntity(ModEntityTypes.TENTACLE_ENTITY.get(), level);
                    tentacle.setPos(center.getX() + 0.5, center.getY() + 1.5, center.getZ() + 0.5);
                    tentacle.setTargetAltar(targetAltar);
                    tentacle.setSummoner(player);

                    // Delay aleatorio (0 - 2.5 seg)
                    tentacle.setInitialDelay(level.random.nextInt(50));

                    level.addFreshEntity(tentacle);
                }
            }

            // 3. TENTÁCULO DE RECOMPENSA (El 5to brazo)
            // Solo sale si hay una recompensa válida que entregar
            if (rewardStack != null && !rewardStack.isEmpty()) {
                TentacleEntity rewardTentacle = new TentacleEntity(ModEntityTypes.TENTACLE_ENTITY.get(), level);

                // Nace del mismo centro
                rewardTentacle.setPos(center.getX() + 0.5, center.getY() + 1.5, center.getZ() + 0.5);
                rewardTentacle.setSummoner(player);

                // Configuración especial: Es Dador y tiene el item visual
                rewardTentacle.setRewardItem(rewardStack.copy());

                // Objetivo inicial: El jugador (para que el modelo se oriente bien al nacer)
                rewardTentacle.setTargetAltar(player.blockPosition().above());

                // --- TIMING ELDRITCH ---
                // Los brazos destructores mueren en tick 120.
                // Queremos que este salga cuando el caos está terminando para "entregar" el resultado.
                // Tick 100 = 5 segundos.
                rewardTentacle.setInitialDelay(100);

                level.addFreshEntity(rewardTentacle);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0 && state.getValue(ACTIVE)) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WARDEN_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }
        super.animateTick(state, level, pos, random);
    }

    // Helpers
    public boolean isAltarSetupReady(Level level, BlockPos pos) {
        BlockPos smallAltarPos = pos.north(4);
        if (!(level.getBlockState(smallAltarPos).getBlock() instanceof BloodAltarBlock)) return false;
        smallAltarPos = pos.east(4);
        if (!(level.getBlockState(smallAltarPos).getBlock() instanceof BloodAltarBlock)) return false;
        smallAltarPos = pos.south(4);
        if (!(level.getBlockState(smallAltarPos).getBlock() instanceof BloodAltarBlock)) return false;
        smallAltarPos = pos.west(4);
        if (!(level.getBlockState(smallAltarPos).getBlock() instanceof BloodAltarBlock)) return false;
        return true;
    }

    public List<List<Item>> getItemsFromAltars(Level level, BlockPos pos) {
        List<List<Item>> items = new ArrayList<>();
        BlockPos[] altarPositions = {pos.north(4), pos.east(4), pos.south(4), pos.west(4)};
        for (BlockPos altarPos : altarPositions) {
            if (level.getBlockState(altarPos).getBlock() instanceof BloodAltarBlock) {
                BloodAltarBlockEntity entity = (BloodAltarBlockEntity) level.getBlockEntity(altarPos);
                if (entity != null && !entity.getItemsInside().isEmpty()) items.add(entity.getItemsInside());
            }
        }
        return items;
    }

    public void consumeItemsFromAltars(Level level, BlockPos pos) {
        BlockPos[] posArr = {pos.north(4), pos.east(4), pos.south(4), pos.west(4)};
        for(BlockPos p : posArr) {
            if (level.getBlockState(p).getBlock() instanceof BloodAltarBlock) {
                BloodAltarBlockEntity entity = (BloodAltarBlockEntity) level.getBlockEntity(p);
                if(entity != null) entity.clearItemsInside();
            }
        }
        level.destroyBlock(pos.east(), false);
    }

    public boolean rainRitual(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!(level.getBlockEntity(blockPos) instanceof MainBloodAltarBlockEntity altar)) {
            return false;
        }
        List<List<Item>> items = getItemsFromAltars(level, blockPos);
        if (items.size() == 4) {
            for (List<Item> altarContainer : items) {
                for (Item stack : altarContainer) {
                    if (stack.equals(ModItems.BLOOD_FLASK.get())) {
                        consumeItemsFromAltars(level, blockPos);
                        altar.setActive(false);
                        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
                        level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                        level.setBlock(blockPos, blockState.setValue(ACTIVE, false), 3);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}