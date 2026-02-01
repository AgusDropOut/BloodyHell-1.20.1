package net.agusdropout.bloodyhell.event.handlers;



import net.agusdropout.bloodyhell.block.entity.custom.SanguiniteBloodHarvesterBlockEntity;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;

import net.agusdropout.bloodyhell.entity.soul.BloodSoulEntity;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulSize;
import net.agusdropout.bloodyhell.entity.soul.BloodSoulType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class BloodHarvestHandler {

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide) return;

        // Check if entity is valid for sacrifice first
        if (!isValidSacrifice(entity)) return;

        // Configuration: Radius
        int range = 10;
        BlockPos entityPos = entity.blockPosition();

        // Find Harvester
        BlockPos harvesterPos = BlockPos.findClosestMatch(entityPos, range, range, pos ->
                level.getBlockEntity(pos) instanceof SanguiniteBloodHarvesterBlockEntity
        ).orElse(null);

        if (harvesterPos != null) {
            BloodSoulType type = determineBloodType(entity);
            BloodSoulSize size = determineBloodSize(entity);

            BloodSoulEntity soul = new BloodSoulEntity(level, harvesterPos, type, size);
            soul.setPos(entity.getX(), entity.getY() + 0.5, entity.getZ());
            level.addFreshEntity(soul);
        }
    }

    private static boolean isValidSacrifice(LivingEntity entity) {
        return entity.getType().is(ModTags.Entities.SACRIFICEABLE_ENTITY) ||
                entity.getType().is(ModTags.Entities.CORRUPTED_SACRIFICEABLE_ENTITY) ||
                entity instanceof Monster; // Allow generic monsters even if not tagged
    }

    private static BloodSoulType determineBloodType(LivingEntity entity) {
        if (entity.getType().is(ModTags.Entities.CORRUPTED_SACRIFICEABLE_ENTITY)) {
            return BloodSoulType.CORRUPTED;
        }
        if (entity.getType().is(ModTags.Entities.SACRIFICEABLE_ENTITY)) {
            return BloodSoulType.BLOOD;
        }
        // Fallback
        if (entity instanceof Monster) return BloodSoulType.CORRUPTED;
        return BloodSoulType.BLOOD;
    }

    private static BloodSoulSize determineBloodSize(LivingEntity entity) {
        float maxHp = entity.getMaxHealth();
        if (maxHp > 50) return BloodSoulSize.LARGE;
        if (maxHp >= 10) return BloodSoulSize.MEDIUM;
        return BloodSoulSize.SMALL;
    }
}