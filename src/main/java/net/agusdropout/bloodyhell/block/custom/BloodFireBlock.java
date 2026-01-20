package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncBloodFireEffectPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class BloodFireBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public BloodFireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
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

    // --- WATER LOGGING LOGIC (Standard) ---

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        // Just handle the fluid tick, do not destroy water
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

            // Damage is slightly reduced underwater (optional choice)
            // or keep it full damage if it's "magical" fire.
            living.hurt(level.damageSources().inFire(), 2.0F);

            MobEffectInstance currentEffect = living.getEffect(ModEffects.BLOOD_FIRE_EFFECT.get());

            if (currentEffect == null || currentEffect.getDuration() < 40) {
                living.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 100, 0));

                // Sync Packets
                ModMessages.sendToPlayersTrackingEntity(new SyncBloodFireEffectPacket(living.getId(), 100, 0), living);
                if (living instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    ModMessages.sendToPlayer(new SyncBloodFireEffectPacket(living.getId(), 100, 0), serverPlayer);
                }
            }
        }
    }

    // --- VISUALS (ANIMATION TICK) ---

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;



        if (state.getValue(WATERLOGGED)) {
            // --- UNDERWATER BEHAVIOR ---

            // 1. Sizzling Sound (Frequent)
            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            // 2. Rising Bubbles (Boiling Effect)
            // Spawn 2-4 bubbles per tick that shoot upwards
            for (int i = 0; i < 4; ++i) {
                level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 0.5D,
                        pos.getZ() + random.nextDouble(),
                        0.0D, 0.1D + random.nextDouble() * 0.1D, 0.0D); // Upward velocity
            }

            // 3. Steam/Cloud (Resisting extinction)
            if (random.nextInt(3) == 0) {
                level.addParticle(ParticleTypes.CLOUD,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 0.8D,
                        pos.getZ() + random.nextDouble(),
                        0.0D, 0.05D, 0.0D);
            }

            // 4. Occasional Blood Flame (Still visible but rare)
            if (random.nextInt(10) == 0) {
                level.addParticle(ModParticles.BLOOD_FLAME.get(),
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 0.2D,
                        pos.getZ() + random.nextDouble(),
                        0.0D, 0.05D, 0.0D);
            }

        } else {
            // --- NORMAL SURFACE BEHAVIOR ---

            // Standard Fire Sound
            if (random.nextInt(24) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }

            // Standard Blood Flames
            for (int i = 0; i < 3; ++i) {
                level.addParticle(ModParticles.BLOOD_FLAME.get(),
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 0.2D + random.nextDouble() * 0.5D,
                        pos.getZ() + random.nextDouble(),
                        0.0D, 0.05D, 0.0D);
            }

            // Smoke
            if (random.nextInt(5) == 0) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, x, y + 0.5D, z, 0.0D, 0.05D, 0.0D);
            }
        }
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        // Return FALSE if the block below is also Blood Fire
        if (belowState.is(this) || belowState.is(Blocks.AIR) ) {
            return false;
        }
        return super.canSurvive(state, level, pos);
    }
}