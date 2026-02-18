package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicFloorParticleOptions;

import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BloodCapsuleBlock extends Block {
    // Using Horizontal Facing to prevent crashing on placement
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BloodCapsuleBlock(Properties properties) {
        super(properties
                .noOcclusion()
                .isValidSpawn((state, level, pos, entityType) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // --- DESTRUCTION LOGIC (Spawning Mobs/Items) ---
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // Only run if the block is actually changing to a DIFFERENT block (i.e., destroyed)
        if (!state.is(newState.getBlock())) {

            // Run on Server Side only
            if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
                float chance = level.random.nextFloat();

                // Logic: Exclusively spawn one OR the other OR neither

                // 1. 25% Chance to spawn the Mob
                if (chance < 0.25f) {
                    Entity entity = ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN.get().create(level);
                    if (entity != null) {
                        entity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
                        level.addFreshEntity(entity);
                    }
                }
                // 2. 25% Chance to drop the Finger (Only if mob didn't spawn)
                else if (chance < 0.50f) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                            new ItemStack(ModItems.UNKNOWN_ENTITY_FINGER.get()));
                }

                // Remaining 50%: Normal behavior (just breaks, drops block loot if configured)
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    // --- PARTICLES (Magic Red Gradient) ---
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // 30% Chance per tick
        if (random.nextFloat() < 0.3f) {

            // 1. Calculate Random Position INSIDE the block (Safety padding of 0.2)
            double x = pos.getX() + 0.2 + (random.nextDouble() * 0.6);
            double y = pos.getY() + 0.2 + (random.nextDouble() * 0.6);
            double z = pos.getZ() + 0.2 + (random.nextDouble() * 0.6);

            // 2. Colors: Red -> Dark Red Gradient
            // We use a simple randomizer for the gradient factor
            float ratio = random.nextFloat();
            Vector3f color = ParticleHelper.gradient3(ratio,
                    new Vector3f(1.0f, 0.0f, 0.0f),  // Pure Red
                    new Vector3f(0.6f, 0.0f, 0.0f),  // Darker Red
                    new Vector3f(0.3f, 0.0f, 0.0f)); // Very Dark Red

            float size = 0.15f + random.nextFloat() * 0.1f;

            // 3. Spawn Particle using your helper
            ParticleHelper.spawn(level, new MagicFloorParticleOptions(
                    color,
                    size,
                    true, // Emissive (glowing) matches the theme well
                    40    // Lifetime
            ), x, y, z, 0.0, 0.005, 0.0); // Very slow upward float
        }

        // Ambient Sound
        if (random.nextFloat() < 0.02f) {
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.3f, 0.1f, false);
        }
    }

    // --- PLACEMENT & VISUALS (Standard) ---
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // IMPORTANT: Fixes the visibility issue
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) { return 1.0F; }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) { return true; }
}