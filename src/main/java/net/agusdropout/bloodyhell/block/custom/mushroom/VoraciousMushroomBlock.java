package net.agusdropout.bloodyhell.block.custom.mushroom;

import net.agusdropout.bloodyhell.block.entity.custom.mushroom.VoraciousMushroomBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class VoraciousMushroomBlock extends BaseEntityBlock {

    // 1. Define the Property
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public VoraciousMushroomBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Check for Filled Blood Flask
        if (itemStack.is(ModItems.FILLED_BLOOD_FLASK.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof VoraciousMushroomBlockEntity mushroomBE) {

                    // Access the tank via Capability
                    mushroomBE.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
                        // Assume Flask holds 250mB (1/4 bucket)
                        int fillAmount = 250;

                        // Try to fill (Simulation first)
                        // Make sure you have a FluidStack defined for your Blood
                        // FluidStack bloodStack = new FluidStack(ModFluids.BLOOD_SOURCE.get(), fillAmount);

                        // Simpler logic if you don't have FluidStack handy yet:
                        // Just manually fill the internal tank class if you are lazy,
                        // BUT correct way is via handler.fill()

                        // For now, let's assume direct tank access or valid FluidStack:
                        // handler.fill(bloodStack, IFluidHandler.FluidAction.EXECUTE);

                        // Since I don't have your Fluid definitions, here is the logic structure:
                        // 1. Fill Tank
                        // 2. Play Sound
                        // 3. Consume Item / Return Empty Bottle
                        mushroomBE.fillTank(fillAmount);
                    });

                    // TEMPORARY MANUAL FILL (Delete this when you have Fluids set up):


                    // Consume Item
                    if (!player.isCreative()) {
                        player.setItemInHand(hand, new ItemStack(ModItems.BLOOD_FLASK.get())); // Return empty flask
                    }
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            // Spawn particles occasionally (1 in 3 chance per tick)
            if (random.nextInt(3) == 0) {
                // Calculate spawn position (slightly randomized around center)
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
                double y = pos.getY() + 0.5 + random.nextDouble() * 0.4;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;

                // 1. Define the Color (Golden/Yellow)
                // RGB: 1.0, 0.9, 0.2
                Vector3f goldColor = new Vector3f(1.0f, 0.9f, 0.2f);

                // 2. Create the Options Object using your Custom Constructor
                // Color: Gold
                // Size: 1.0f (Standard)
                // Jitter: false (We want a clean upward stream, not random noise)
                // Lifetime: 40 ticks (2 seconds)
                MagicParticleOptions particleData = new MagicParticleOptions(goldColor, 1.0f, false, 40);

                // 3. Spawn the particle
                // Velocity: 0, 0.05, 0 (Slowly floating Up)
                level.addParticle(particleData, x, y, z, 0.0D, 0.05D, 0.0D);
            }
        }
    }

    // Boilerplate for EntityBlock
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new net.agusdropout.bloodyhell.block.entity.custom.mushroom.VoraciousMushroomBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, net.agusdropout.bloodyhell.block.entity.ModBlockEntities.VORACIOUS_MUSHROOM_BE.get(),
                net.agusdropout.bloodyhell.block.entity.custom.mushroom.VoraciousMushroomBlockEntity::tick);
    }
}