package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "bloodyhell", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlasphemousMutationHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide) return;

        net.minecraft.nbt.CompoundTag data = entity.getPersistentData();

        int displayTick = data.getInt("MutationDisplayTick");
        if (displayTick > 0) {
            List<String> activeMutations = new ArrayList<>();
            if (data.getBoolean("BlasphemousFireResist")) activeMutations.add("fire");
            if (data.getBoolean("BlasphemousSpeed")) activeMutations.add("speed");
            if (data.getBoolean("BlasphemousStrength")) activeMutations.add("strength");
            if (data.getBoolean("BlasphemousRegen")) activeMutations.add("regen");
            if (data.getBoolean("BlasphemousResistance")) activeMutations.add("resist");

            int index = (displayTick - 1) / 15;

            if (displayTick % 15 == 1 && index < activeMutations.size()) {
                String mut = activeMutations.get(index);
                Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);
                Component msg = null;

                switch (mut) {
                    case "fire":
                        color = new Vector3f(1.0f, 0.6f, 0.0f);
                        msg = Component.literal("✦ Igneous Immunity").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
                        break;
                    case "speed":
                        color = new Vector3f(1.0f, 1.0f, 1.0f);
                        msg = Component.literal("✦ Unholy Swiftness").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
                        break;
                    case "strength":
                        color = new Vector3f(0.6f, 0.0f, 0.8f);
                        msg = Component.literal("✦ Vein Wrath").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD);
                        break;
                    case "regen":
                        color = new Vector3f(1.0f, 0.2f, 0.2f);
                        msg = Component.literal("✦ Sanguine Vitality").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
                        break;
                    case "resist":
                        color = new Vector3f(0.3f, 0.3f, 0.3f);
                        msg = Component.literal("✦ Crimson Carapace").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD);
                        break;
                }

                if (msg != null) {
                    if (data.hasUUID("MutationViewer")) {
                        Player viewer = entity.level().getPlayerByUUID(data.getUUID("MutationViewer"));
                        if (viewer != null) {
                            viewer.displayClientMessage(msg, true);
                        }
                    }

                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F + (index * 0.15F));

                    if (entity.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(new MagicParticleOptions(color, 1.5f, false, 40, true),
                                entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0), entity.getZ(),
                                20, 0.4D, 0.4D, 0.4D, 0.05D);
                    }
                }
            }

            if (index >= activeMutations.size()) {
                data.putInt("MutationDisplayTick", 0);
            } else {
                data.putInt("MutationDisplayTick", displayTick + 1);
            }
        }

        boolean isMutated = data.getBoolean("BlasphemousFireResist") ||
                data.getBoolean("BlasphemousSpeed") ||
                data.getBoolean("BlasphemousStrength") ||
                data.getBoolean("BlasphemousRegen") ||
                data.getBoolean("BlasphemousResistance");

        if (isMutated) {
            if (entity.tickCount % 40 == 0) {
                if (data.getBoolean("BlasphemousFireResist")) {
                    entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false, true));
                    entity.clearFire();
                }
                if (data.getBoolean("BlasphemousSpeed")) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, false, false, false));
                }
                if (data.getBoolean("BlasphemousStrength")) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0, false, false, false));
                }
                if (data.getBoolean("BlasphemousRegen")) {
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, false, false));
                }
                if (data.getBoolean("BlasphemousResistance")) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, false, false, false));
                }
            }
        }
    }
}