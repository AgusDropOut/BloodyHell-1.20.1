package net.agusdropout.bloodyhell.block.entity.custom.mushroom;

import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.custom.mushroom.VoraciousMushroomBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncVisceralEffectPacket;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class VoraciousMushroomBlockEntity extends BlockEntity {

    // 1. FLUID TANK (With Filter)
    private final FluidTank tank = new FluidTank(3000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }

        // --- RESTRICTION LOGIC ---
        // This ensures pipes/hoppers can ONLY push Blood into this block.
        // Any other fluid will be rejected.
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.BLOOD_SOURCE.get()
                    || stack.getFluid() == ModFluids.BLOOD_FLOWING.get();
        }
    };

    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> tank);

    private int timer = 0;
    private static final int BLOOD_COST_PER_TICK_CYCLE = 5; // Renamed for clarity
    private static final int EFFECT_RADIUS = 2;

    public VoraciousMushroomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VORACIOUS_MUSHROOM_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VoraciousMushroomBlockEntity pEntity) {
        if (level.isClientSide) return;

        // 2. STATE MANAGEMENT (Already handles "Stop Infecting")
        // logic: If fluid < Cost, hasBlood becomes false.
        boolean hasBlood = pEntity.tank.getFluidAmount() >= BLOOD_COST_PER_TICK_CYCLE;
        boolean isActiveState = state.getValue(VoraciousMushroomBlock.ACTIVE);

        if (hasBlood != isActiveState) {
            level.setBlock(pos, state.setValue(VoraciousMushroomBlock.ACTIVE, hasBlood), 3);
            isActiveState = hasBlood;
        }

        // If no blood, we return HERE.
        // This stops particles and effects immediately.
        if (!isActiveState) return;

        pEntity.timer++;

        // 3. APPLY EFFECT (Every 20 ticks / 1 second)
        if (pEntity.timer % 20 == 0) {
            // Check if we actually successfully drained (double safety)
            if (pEntity.drainTank(BLOOD_COST_PER_TICK_CYCLE)) {
                pEntity.applyVisceralEffect(level, pos);

                if (level instanceof ServerLevel serverLevel) {
                    pEntity.spawnExpandingParticles(serverLevel, pos);
                }
            }
        }
    }

    private void applyVisceralEffect(Level level, BlockPos pos) {
        // Define the area box
        AABB area = new AABB(pos).inflate(EFFECT_RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity entity : entities) {
            // Logic: Apply effect if missing OR if duration is low (refreshes it)
            // 100 ticks = 5 seconds.
            if (!entity.hasEffect(ModEffects.VISCERAL_EFFECT.get()) || entity.getEffect(ModEffects.VISCERAL_EFFECT.get()).getDuration() < 20) {

                // 1. Server Side Application
                entity.addEffect(new MobEffectInstance(ModEffects.VISCERAL_EFFECT.get(), 100, 0));

                // 2. FORCE SYNC PACKET
                if (level instanceof ServerLevel serverLevel) {
                    // Assuming you have a 'ModMessages.sendToClients' or similar helper
                    // If not, you usually use: ModMessages.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new SyncVisceralEffectPacket(...));

                    ModMessages.sendToClients(new SyncVisceralEffectPacket(entity.getId(), 100, 0));
                }
            }
        }
    }

    private void spawnExpandingParticles(ServerLevel level, BlockPos pos) {
        // Gold/Yellow Magic Particles
        ParticleOptions particle = new MagicParticleOptions(new Vector3f(1.0f, 0.9f, 0.2f), 1.0f, false, 40);

        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 16; i++) {
            double angle = i * (Math.PI * 2) / 16;
            double speed = 0.15;
            double velX = Math.cos(angle) * speed;
            double velZ = Math.sin(angle) * speed;

            level.sendParticles(particle,
                    centerX, centerY, centerZ,
                    0,
                    velX, 0.0, velZ,
                    1.0);
        }
    }

    // --- CAPABILITIES & UTILS ---

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt = tank.writeToNBT(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        tank.readFromNBT(nbt);
    }

    public void fillTank(int amount) {
        // This bypasses isFluidValid check because we create the stack manually with valid blood
        tank.fill(new FluidStack(ModFluids.BLOOD_SOURCE.get(), amount), IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    /**
     * Attempts to drain the tank. Returns true if successful.
     */
    public boolean drainTank(int amount) {
        if (tank.getFluidAmount() >= amount) {
            tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
            setChanged();
            return true;
        }
        return false;
    }
}