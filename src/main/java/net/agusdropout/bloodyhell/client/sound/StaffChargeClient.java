package net.agusdropout.bloodyhell.client.sound;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.effect.data.OrbCharge;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.item.custom.base.SpellAffinity;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StaffChargeClient {
    private record Beat(Supplier<SoundEvent> lub, Supplier<SoundEvent> dub) {}

    private record Cues(Supplier<SoundEvent> note, Supplier<SoundEvent> full, Supplier<SoundEvent> fade, @Nullable Supplier<SoundEvent> loop, @Nullable Beat beat, float clockBoost, float notePitchScale, float pulsePeak, float pulseSeconds) {}

    private static final Map<SpellAffinity, Cues> CUES = Map.of(
            SpellAffinity.BLOOD_FIRE, new Cues(ModSounds.STAFF_CHARGE_CINDER_NOTE, ModSounds.STAFF_CHARGE_CINDER_FULL, ModSounds.STAFF_CHARGE_CINDER_FADE, ModSounds.STAFF_CHARGE_CINDER_LOOP, null, 1.5f, 1.0f, 0.75f, 0.7f),
            SpellAffinity.BLOOD, new Cues(ModSounds.STAFF_CHARGE_BLOOD_NOTE, ModSounds.STAFF_CHARGE_BLOOD_FULL, ModSounds.STAFF_CHARGE_BLOOD_FADE, null, new Beat(ModSounds.STAFF_CHARGE_BLOOD_BEAT_LUB, ModSounds.STAFF_CHARGE_BLOOD_BEAT_DUB), 0.6f, 0.65f, 1.0f, 0.45f),
            SpellAffinity.RHNULL, new Cues(ModSounds.STAFF_CHARGE_GOLDEN_NOTE, ModSounds.STAFF_CHARGE_GOLDEN_FULL, ModSounds.STAFF_CHARGE_GOLDEN_FADE, null, null, 1.5f, 1.0f, 0.75f, 0.7f));

    private static final float NOTE_VOLUME = 0.6f;
    private static final float FULL_VOLUME = 0.8f;
    private static final float FADE_VOLUME = 0.6f;
    private static final float SMOOTHING_RATE = 6.0f;
    private static final float DEFAULT_CLOCK_BOOST = 1.5f;
    private static final float LUB_VOLUME = 0.5f;
    private static final float DUB_VOLUME = 0.35f;
    private static final float FLASH_SECONDS = 0.7f;
    private static final float MAX_FRAME_SECONDS = 0.1f;

    private static final HandCharge MAIN_HAND = new HandCharge();
    private static final HandCharge OFF_HAND = new HandCharge();

    private StaffChargeClient() {}

    public static OrbCharge view(ItemStack stack, ItemDisplayContext context) {
        HandCharge hand = handShowing(stack, context);
        if (hand != null) return hand.view();
        if (stack.getItem() instanceof BaseStaffItem staff) {
            return new OrbCharge(staff.getBonusFill(stack), ShaderTime.seconds(), 0.0f, 0.0f);
        }
        return OrbCharge.idle();
    }

    @Nullable
    private static HandCharge handShowing(ItemStack stack, ItemDisplayContext context) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return null;
        InteractionHand hand;
        if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            hand = player.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        } else if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            hand = player.getMainArm() == HumanoidArm.LEFT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        } else {
            return null;
        }
        if (player.getItemInHand(hand).getItem() != stack.getItem()) return null;
        return hand == InteractionHand.MAIN_HAND ? MAIN_HAND : OFF_HAND;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = Minecraft.getInstance().player;
        MAIN_HAND.tick(player == null ? ItemStack.EMPTY : player.getMainHandItem());
        OFF_HAND.tick(player == null ? ItemStack.EMPTY : player.getOffhandItem());
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        MAIN_HAND.reset();
        OFF_HAND.reset();
    }

    private static final class HandCharge {
        private BaseStaffItem staff;
        private int lastCount;
        private float target;
        private float smoothed;
        private float clock = ShaderTime.seconds();
        private float flash;
        private float pulse;
        private float pulseSeconds = 0.7f;
        private float clockBoost = DEFAULT_CLOCK_BOOST;
        private Beat heartbeat;
        private long lastFrameMillis = Util.getMillis();
        private StaffEmberLoopSound loop;

        OrbCharge view() {
            long now = Util.getMillis();
            float seconds = Math.min((now - lastFrameMillis) / 1000.0f, MAX_FRAME_SECONDS);
            lastFrameMillis = now;
            smoothed += (target - smoothed) * (1.0f - (float) Math.exp(-seconds * SMOOTHING_RATE));
            float previousClock = clock;
            clock += seconds * (1.0f + clockBoost * smoothed);
            if (heartbeat != null) beat(previousClock, clock);
            flash = Math.max(0.0f, flash - seconds / FLASH_SECONDS);
            pulse = Math.max(0.0f, pulse - seconds / pulseSeconds);
            return new OrbCharge(smoothed, clock, flash * flash, pulse * pulse);
        }

        void tick(ItemStack stack) {
            if (!(stack.getItem() instanceof BaseStaffItem held)) {
                release();
                return;
            }
            int count = held.getCharge(stack);
            if (held != staff) {
                staff = held;
                lastCount = count;
                smoothed = held.getBonusFill(stack);
                flash = 0.0f;
                pulse = 0.0f;
            } else if (count != lastCount) {
                react(held, lastCount, count);
                lastCount = count;
            }
            target = held.getBonusFill(stack);
            Cues cues = CUES.get(held.getAffinity());
            clockBoost = cues == null ? DEFAULT_CLOCK_BOOST : cues.clockBoost();
            heartbeat = cues != null && count >= BaseStaffItem.MARKED_FOR_FULL_BONUS ? cues.beat() : null;
            keepLoop(cues, count);
        }

        void reset() {
            release();
            smoothed = 0.0f;
            flash = 0.0f;
            pulse = 0.0f;
        }

        private void release() {
            staff = null;
            lastCount = 0;
            target = 0.0f;
            heartbeat = null;
            clockBoost = DEFAULT_CLOCK_BOOST;
        }

        private void beat(float previousClock, float currentClock) {
            if (OrbHeartbeat.smallBeats(previousClock, currentClock) > 0) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(heartbeat.dub().get(), 1.0f, DUB_VOLUME));
            }
            if (OrbHeartbeat.bigBeats(previousClock, currentClock) > 0) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(heartbeat.lub().get(), 1.0f, LUB_VOLUME));
            }
        }

        private void react(BaseStaffItem held, int previous, int current) {
            int full = BaseStaffItem.MARKED_FOR_FULL_BONUS;
            Cues cues = CUES.get(held.getAffinity());
            if (current >= full && previous < full) {
                flash = 1.0f;
            } else if (current > previous && cues != null) {
                pulse = cues.pulsePeak();
                pulseSeconds = cues.pulseSeconds();
            }
            if (cues == null) return;
            StaffChargeSoundPlan.between(previous, current, full).ifPresent(step -> {
                SoundEvent sound = switch (step.cue()) {
                    case NOTE -> cues.note().get();
                    case FULL -> cues.full().get();
                    case FADE -> cues.fade().get();
                };
                float volume = switch (step.cue()) {
                    case NOTE -> NOTE_VOLUME;
                    case FULL -> FULL_VOLUME;
                    case FADE -> FADE_VOLUME;
                };
                float pitch = step.cue() == StaffChargeSoundPlan.Cue.NOTE ? step.pitch() * cues.notePitchScale() : step.pitch();
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
            });
        }

        private void keepLoop(@Nullable Cues cues, int count) {
            boolean wanted = cues != null && cues.loop() != null && count >= BaseStaffItem.MARKED_FOR_FULL_BONUS;
            if (wanted && (loop == null || loop.isStopped())) {
                loop = new StaffEmberLoopSound(cues.loop().get(), this::isFullyChargedAndHeld);
                Minecraft.getInstance().getSoundManager().play(loop);
            }
        }

        private boolean isFullyChargedAndHeld() {
            return staff != null && lastCount >= BaseStaffItem.MARKED_FOR_FULL_BONUS;
        }
    }
}
