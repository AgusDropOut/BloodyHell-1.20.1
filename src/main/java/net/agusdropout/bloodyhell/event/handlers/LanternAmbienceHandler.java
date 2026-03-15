package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.config.ModClientConfigs;
import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
public class LanternAmbienceHandler {

    private static float currentIntensity = 0.0f;
    private static final float TRANSITION_SPEED = 0.015f;

    private static float previousGazeIntensity = 0.0f;

    private static SimpleSoundInstance activeGazeSound = null;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;


        boolean isBeingHunted = false;
        var lanterns = mc.level.getEntitiesOfClass(UnknownLanternEntity.class, mc.player.getBoundingBox().inflate(64.0D));

        for (UnknownLanternEntity lantern : lanterns) {
            if (lantern.isAlive() && mc.player.getUUID().equals(lantern.getTargetPlayer())) {
                isBeingHunted = true;
                break;
            }
        }

        if (isBeingHunted) {
            if (currentIntensity < 1.0f) currentIntensity += TRANSITION_SPEED;
        } else {
            if (currentIntensity > 0.0f) currentIntensity -= TRANSITION_SPEED;
        }

        currentIntensity = Mth.clamp(currentIntensity, 0.0f, 1.0f);

        if (currentIntensity > 0.2f && mc.level.random.nextFloat() < (currentIntensity * 0.4f)) {
            double offsetX = (mc.level.random.nextDouble() - 0.5) * 16.0;
            double offsetY = (mc.level.random.nextDouble() - 0.5) * 10.0 + 2.0;
            double offsetZ = (mc.level.random.nextDouble() - 0.5) * 16.0;

            mc.level.addParticle(ModParticles.EYE_PARTICLE.get(),
                    mc.player.getX() + offsetX,
                    mc.player.getY() + offsetY,
                    mc.player.getZ() + offsetZ,
                    0.0, 0.0, 0.0);
        }


        float actualGaze = getCurrentIntensity();

        if (actualGaze > 0.0f && previousGazeIntensity == 0.0f) {
            activeGazeSound = SimpleSoundInstance.forUI(ModSounds.UNKNOWN_LANTERN_GAZE.get(), 1.0F, 1.0F);
            mc.getSoundManager().play(activeGazeSound);
        }

        else if (actualGaze == 0.0f && activeGazeSound != null) {
            mc.getSoundManager().stop(activeGazeSound);
            activeGazeSound = null;
        }

        previousGazeIntensity = actualGaze;
    }

    public static float getCurrentIntensity() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return 0f;

        var lanterns = mc.level.getEntitiesOfClass(UnknownLanternEntity.class, mc.player.getBoundingBox().inflate(32.0D));
        for (UnknownLanternEntity lantern : lanterns) {
            if (mc.player.getUUID().equals(lantern.getTargetPlayer())) {
                return lantern.getGazeIntensity();
            }
        }
        return 0f;
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        if (currentIntensity <= 0) return;

        double fov = event.getFOV();
        float time = Minecraft.getInstance().player.tickCount + (float) event.getPartialTick();

        float heartbeat = (float) Math.sin(time * 0.2f) * (15.0f * currentIntensity);
        float jitter = (Minecraft.getInstance().player.getRandom().nextFloat() - 0.5f) * (2.0f * currentIntensity);



        if (ModClientConfigs.ENABLE_FOV_EFFECTS.get()) {
            event.setFOV(fov - (heartbeat + jitter));
        }
    }
}