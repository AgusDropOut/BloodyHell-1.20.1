package net.agusdropout.bloodyhell.block.entity.custom.engine;

import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class ResurrectionEngineMode implements IEngineMode {

    private int processTick = 0;
    private static final int MAX_PROCESS_TIME = 200;

    private static final int REQUIRED_VISCERAL_BLOOD = 2000;
    private static final int REQUIRED_VISCOUS_BLASPHEMY = 1000;

    @Override
    public boolean canProcess(RhnullBloodEngineBlockEntity engine) {
        ItemStack coreItem = engine.getRenderStack();
        if (coreItem.getItem() != ModItems.BOUND_BLOOD_FLASK.get()) return false;

        CompoundTag nbt = coreItem.getTag();
        if (nbt == null || !nbt.contains("FallenAllyData")) return false;

        if (engine.getVisceralTank().getFluidAmount() < REQUIRED_VISCERAL_BLOOD) return false;
        if (engine.getViscousTank().getFluidAmount() < REQUIRED_VISCOUS_BLASPHEMY) return false;

        return true;
    }

    @Override
    public void tickProcess(Level level, BlockPos pos, BlockState state, RhnullBloodEngineBlockEntity engine) {
        processTick++;
        if (processTick >= MAX_PROCESS_TIME) {
            finishProcess(level, pos, engine);
            resetProcess();
            engine.setCraftingFinished(true);
        }
    }

    @Override
    public void finishProcess(Level level, BlockPos pos, RhnullBloodEngineBlockEntity engine) {
        if (level.isClientSide) return;

        if (!canProcess(engine)) return;

        ItemStack coreItem = engine.getRenderStack();
        CompoundTag nbt = coreItem.getTag();
        if (nbt == null || !nbt.contains("FallenAllyData")) return;

        engine.getVisceralTank().drain(REQUIRED_VISCERAL_BLOOD, IFluidHandler.FluidAction.EXECUTE);
        engine.getViscousTank().drain(REQUIRED_VISCOUS_BLASPHEMY, IFluidHandler.FluidAction.EXECUTE);

        CompoundTag entityData = nbt.getCompound("FallenAllyData");

        entityData.remove("Health");
        entityData.putShort("Fire", (short) -1);
        entityData.putShort("DeathTime", (short) 0);
        entityData.putShort("HurtTime", (short) 0);

        List<ItemStack> pillarItems = getPillarItems(level, pos);
        applyMutations(entityData, pillarItems);

        EntityType.create(entityData, level).ifPresent(entity -> {
            entity.setPos(pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5);

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }

            level.addFreshEntity(entity);
            level.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1.0F, 0.5F);

            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {

                serverLevel.sendParticles(
                        new net.agusdropout.bloodyhell.particle.ParticleOptions.BloodDropParticleOption(engine.getBloodBaseColor()),
                        pos.getX() + 0.5D, pos.getY() + 2.5D, pos.getZ() + 0.5D,
                        60, 0.4D, 0.4D, 0.4D, 0.35D
                );
            }
        });

        engine.clearInventory();
        consumePillarItems(level, pos);
    }

    @Override
    public void resetProcess() {
        this.processTick = 0;
    }

    private void applyMutations(CompoundTag entityData, List<ItemStack> modifiers) {
        CompoundTag forgeData = entityData.contains("ForgeData") ? entityData.getCompound("ForgeData") : new CompoundTag();

        for (ItemStack item : modifiers) {
            if (item.isEmpty()) continue;

            if (item.is(Items.MAGMA_BLOCK)) {
                forgeData.putBoolean("BlasphemousFireResist", true);
            } else if (item.is(Items.RABBIT_FOOT)) {
                forgeData.putBoolean("BlasphemousSpeed", true);
            }
        }
        entityData.put("ForgeData", forgeData);
    }

    private List<ItemStack> getPillarItems(Level level, BlockPos center) {
        List<ItemStack> items = new ArrayList<>();

        for (BlockPos offset : RhnullBloodEngineBlockEntity.PILLAR_OFFSETS) {
            BlockPos targetPos = center.offset(offset);
            if (level.getBlockEntity(targetPos) instanceof RhnullBloodEnginePillarBlockEntity pillar) {
                items.add(pillar.getRenderStack().copy());
            }
        }
        return items;
    }

    private void consumePillarItems(Level level, BlockPos center) {
        for (BlockPos offset : RhnullBloodEngineBlockEntity.PILLAR_OFFSETS) {
            BlockPos targetPos = center.offset(offset);
            if (level.getBlockEntity(targetPos) instanceof RhnullBloodEnginePillarBlockEntity pillar) {
                pillar.clearInventory();
            }
        }
    }
}