package net.agusdropout.bloodyhell.entity.interfaces;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncBloodFireEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface BloodFlammable {

    default void setOnBloodFire(LivingEntity target, int duration, int amplifier) {
        if(!getLevel().isClientSide) {
            target.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), duration, amplifier));
            ModMessages.sendToPlayersTrackingEntity(new SyncBloodFireEffectPacket(target.getId(), duration, amplifier), target);
            if (target instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToPlayer(new SyncBloodFireEffectPacket(target.getId(), duration, amplifier), serverPlayer);
            }
        }
    }


    Level getLevel();
}
