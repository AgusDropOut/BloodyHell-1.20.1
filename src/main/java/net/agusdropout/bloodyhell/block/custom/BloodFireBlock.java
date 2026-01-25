package net.agusdropout.bloodyhell.block.custom;
import net.agusdropout.bloodyhell.block.entity.BloodFireBlockEntity;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncBloodFireEffectPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock; // Import EntityBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

// Implements EntityBlock now
public class BloodFireBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public BloodFireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    // --- BLOCK ENTITY LOGIC ---
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodFireBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DOWN_AABB;
    }

    // --- WATER LOGGING LOGIC ---
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return this.canSurvive(state, level, currentPos) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // --- ENTITY & DAMAGE LOGIC ---
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            level.levelEvent((Player)null, 1009, pos, 0);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && !entity.fireImmune() && entity instanceof LivingEntity living) {

            // --- NEW OWNER CHECK LOGIC ---
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BloodFireBlockEntity fireEntity) {
                // If it's safe (Owner or Ally), STOP here.
                if (fireEntity.isSafe(living)) {
                    return;
                }
            }
            // -----------------------------

            living.hurt(level.damageSources().inFire(), 2.0F);
            MobEffectInstance currentEffect = living.getEffect(ModEffects.BLOOD_FIRE_EFFECT.get());

            if (currentEffect == null || currentEffect.getDuration() < 40) {
                living.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 500, 0));
                ModMessages.sendToPlayersTrackingEntity(new SyncBloodFireEffectPacket(living.getId(), 500, 0), living);
                if (living instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    ModMessages.sendToPlayer(new SyncBloodFireEffectPacket(living.getId(), 500, 0), serverPlayer);
                }
            }
        }
    }

    // --- VISUALS (ANIMATION TICK) ---
    // (Kept exactly the same as your code)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        //rarely remove block
        if(random.nextInt(0,1000)< 10){
            level.removeBlock(pos, false);
            return;
        }

        if (state.getValue(WATERLOGGED)) {
            // --- UNDERWATER ---
            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
            for (int i = 0; i < 4; ++i) {
                level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP,
                        pos.getX() + random.nextDouble(), pos.getY() + 0.5D, pos.getZ() + random.nextDouble(),
                        0.0D, 0.1D + random.nextDouble() * 0.1D, 0.0D);
            }
            if (random.nextInt(3) == 0) {
                level.addParticle(ParticleTypes.CLOUD,
                        pos.getX() + random.nextDouble(), pos.getY() + 0.8D, pos.getZ() + random.nextDouble(),
                        0.0D, 0.05D, 0.0D);
            }
            if (random.nextInt(10) == 0) {
                level.addParticle(ModParticles.BLOOD_FLAME.get(),
                        pos.getX() + random.nextDouble(), pos.getY() + 0.2D, pos.getZ() + random.nextDouble(),
                        0.0D, 0.02D, 0.0D);
            }

        } else {
            // --- SURFACE ---
            if (random.nextInt(24) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 3; ++i) {
                level.addParticle(ModParticles.BLOOD_FLAME.get(),
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 0.1D + random.nextDouble() * 0.3D,
                        pos.getZ() + random.nextDouble(),
                        0.0D, 0.03D + random.nextDouble() * 0.02D, 0.0D);
            }

            for (int i = 0; i < 3; ++i) {
                level.addParticle(ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 0.1D + random.nextDouble() * 0.3D,
                        pos.getZ() + random.nextDouble(),
                        0.0D, 0, 0.0D);
            }

            if (random.nextInt(5) == 0) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, x, y + 0.5D, z, 0.0D, 0.05D, 0.0D);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState p_49921_) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (belowState.is(this) || belowState.is(Blocks.AIR)) {
            return false;
        }
        return super.canSurvive(state, level, pos);
    }
}