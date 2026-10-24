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
import net.minecraft.world.entity.player.Player;
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
    private record Cues(Supplier<SoundEvent> note, Supplier<SoundEvent> full, Supplier<SoundEvent> fade, @Nullable Supplier<SoundEvent> loop) {}

    private static final Map<SpellAffinity, Cues> CUES = Map.of(
            SpellAffinity.BLOOD_FIRE, new Cues(ModSounds.STAFF_CHARGE_CINDER_NOTE, ModSounds.STAFF_CHARGE_CINDER_FULL, ModSounds.STAFF_CHARGE_CINDER_FADE, ModSounds.STAFF_CHARGE_CINDER_LOOP),
            SpellAffinity.BLOOD, new Cues(ModSounds.STAFF_CHARGE_BLOOD_NOTE, ModSounds.STAFF_CHARGE_BLOOD_FULL, ModSounds.STAFF_CHARGE_BLOOD_FADE, null),
            SpellAffinity.RHNULL, new Cues(ModSounds.STAFF_CHARGE_GOLDEN_NOTE, ModSounds.STAFF_CHARGE_GOLDEN_FULL, ModSounds.STAFF_CHARGE_GOLDEN_FADE, null));

    private static final float NOTE_VOLUME = 0.6f;
    private static final float FULL_VOLUME = 0.8f;
    private static final float FADE_VOLUME = 0.6f;
    private static final float SMOOTHING_RATE = 6.0f;
    private static final float CLOCK_BOOST = 1.5f;
    private static final float FLASH_SECONDS = 0.7f;
    private static final float MAX_FRAME_SECONDS = 0.1f;

    private static BaseStaffItem heldStaff;
    private static int lastCount;
    private static float target;
    private static float smoothed;
    private static float clock = ShaderTime.seconds();
    private static float flash;
    private static long lastFrameMillis = Util.getMillis();
    private static StaffEmberLoopSound loop;

    private StaffChargeClient() {}

    public static OrbCharge view() {
        long now = Util.getMillis();
        float seconds = Math.min((now - lastFrameMillis) / 1000.0f, MAX_FRAME_SECONDS);
        lastFrameMillis = now;
        smoothed += (target - smoothed) * (1.0f - (float) Math.exp(-seconds * SMOOTHING_RATE));
        clock += seconds * (1.0f + CLOCK_BOOST * smoothed);
        flash = Math.max(0.0f, flash - seconds / FLASH_SECONDS);
        return new OrbCharge(smoothed, clock, flash * flash);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = Minecraft.getInstance().player;
        ItemStack stack = player == null ? ItemStack.EMPTY : heldStaffStack(player);
        if (!(stack.getItem() instanceof BaseStaffItem staff)) {
            release();
            return;
        }
        int count = staff.getMarkedTargets(stack);
        if (staff != heldStaff) {
            heldStaff = staff;
            lastCount = count;
        } else if (count != lastCount) {
            react(staff, lastCount, count);
            lastCount = count;
        }
        target = staff.getBonusFill(stack);
        keepLoop(staff, count);
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        release();
        smoothed = 0.0f;
        flash = 0.0f;
    }

    private static ItemStack heldStaffStack(Player player) {
        ItemStack main = player.getMainHandItem();
        return main.getItem() instanceof BaseStaffItem ? main : player.getOffhandItem();
    }

    private static void release() {
        heldStaff = null;
        lastCount = 0;
        target = 0.0f;
    }

    private static void react(BaseStaffItem staff, int previous, int current) {
        int full = BaseStaffItem.MARKED_FOR_FULL_BONUS;
        if (current >= full && previous < full) flash = 1.0f;
        Cues cues = CUES.get(staff.getAffinity());
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
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, step.pitch(), volume));
        });
    }

    private static void keepLoop(BaseStaffItem staff, int count) {
        Cues cues = CUES.get(staff.getAffinity());
        boolean wanted = cues != null && cues.loop() != null && count >= BaseStaffItem.MARKED_FOR_FULL_BONUS;
        if (wanted && (loop == null || loop.isStopped())) {
            loop = new StaffEmberLoopSound(cues.loop().get(), StaffChargeClient::isFullyChargedAndHeld);
            Minecraft.getInstance().getSoundManager().play(loop);
        }
    }

    private static boolean isFullyChargedAndHeld() {
        return heldStaff != null && lastCount >= BaseStaffItem.MARKED_FOR_FULL_BONUS;
    }
}
