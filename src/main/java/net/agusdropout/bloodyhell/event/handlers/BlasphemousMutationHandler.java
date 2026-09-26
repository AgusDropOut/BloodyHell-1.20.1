package net.agusdropout.bloodyhell.event.handlers;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bloodyhell", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlasphemousMutationHandler {

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();


        if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            if (entity.getPersistentData().getBoolean("BlasphemousFireResist")) {
                event.setCanceled(true);
                entity.clearFire();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide) return;

        if (entity.getPersistentData().getBoolean("BlasphemousSpeed")) {
            if (entity.tickCount % 40 == 0) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, false, false, false));
            }
        }
    }
}