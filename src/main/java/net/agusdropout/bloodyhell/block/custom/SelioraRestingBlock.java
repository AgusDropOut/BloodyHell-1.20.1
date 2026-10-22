package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.entity.custom.SelioraRestingBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SelioraRestingBlock extends BaseEntityBlock {

    public SelioraRestingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SelioraRestingBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide) {

            // 1. Invocar a Seliora

            var bossEntity = ModEntityTypes.SELIORA.get().create(level);

            if (bossEntity != null) {

                bossEntity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
                level.addFreshEntity(bossEntity);
            }



            level.playSound(null, pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 1.0f, 1.0f);
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0f, 0.5f);


            if (level instanceof ServerLevel serverLevel) {

                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                        1, 0.0, 0.0, 0.0, 0.0);


                serverLevel.sendParticles(ParticleTypes.WITCH,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        20, 0.5, 0.5, 0.5, 0.1);
            }



            level.removeBlock(pos, false);

            return InteractionResult.CONSUME;
        }


        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // --- EFECTOS VISUALES PASIVOS (Lectura) ---


        if (random.nextInt(2) == 0) {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 2.0D;
            double d2 = (double)pos.getZ() + 0.5D;




            double spreadX = (random.nextDouble() - 0.5D) * 1.2D;
            double spreadY = (random.nextDouble() * 1.0D);
            double spreadZ = (random.nextDouble() - 0.5D) * 1.2D;


            double velocityX = (random.nextDouble() - 0.5D) * 0.4D;
            double velocityY = (random.nextDouble() - 0.5D) * 0.4D;
            double velocityZ = (random.nextDouble() - 0.5D) * 0.4D;

            level.addParticle(ParticleTypes.ENCHANT,
                    d0 + spreadX,
                    d1 + spreadY,
                    d2 + spreadZ,
                    velocityX,
                    velocityY,
                    velocityZ);
        }

        if (random.nextInt(100) < 10) {



            level.playLocalSound(pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 0.8F, false);
        }
    }
}