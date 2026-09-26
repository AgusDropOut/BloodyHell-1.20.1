package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.entity.custom.BloodySoulEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "bloodyhell", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AllySoulHandler {

    @SubscribeEvent
    public static void onAllyDeath(LivingDeathEvent event) {
        LivingEntity deceased = event.getEntity();
        Level level = deceased.level();

        if (level.isClientSide) return;

        boolean isTamableAlly = (deceased instanceof TamableAnimal tamable && tamable.isTame());
        boolean isMountAlly = (deceased instanceof AbstractHorse horse && horse.isTamed());
        boolean isVillager = (deceased instanceof AbstractVillager);

        if (isTamableAlly || isMountAlly || isVillager) {
            BloodySoulEntity soul = new BloodySoulEntity(ModEntityTypes.BLOODY_SOUL_ENTITY.get(), level);
            soul.setPos(deceased.getX(), deceased.getY() + 0.5D, deceased.getZ());

            CompoundTag entityData = new CompoundTag();
            deceased.saveWithoutId(entityData);

            String entityId = ForgeRegistries.ENTITY_TYPES.getKey(deceased.getType()).toString();
            entityData.putString("id", entityId);

            soul.getPersistentData().putString("FallenAllyType", entityId);
            soul.getPersistentData().put("FallenAllyData", entityData);

            if (deceased.hasCustomName()) {
                soul.getPersistentData().putString("FallenAllyName", deceased.getCustomName().getString());
            }

            level.addFreshEntity(soul);
        }
    }
}