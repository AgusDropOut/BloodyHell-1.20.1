package net.agusdropout.bloodyhell.event.handlers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
public class RitualAmbienceHandler {

    private static float currentIntensity = 0.0f;
    private static int activeTicks = 0;
    private static final float TRANSITION_SPEED = 0.015f;


    public static void triggerRitual(int durationTicks) {
        activeTicks = durationTicks;
    }



    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (activeTicks > 0) {
            activeTicks--;
        }


        if (activeTicks > 0) {
            if (currentIntensity < 1.0f) currentIntensity += TRANSITION_SPEED;
        } else {
            if (currentIntensity > 0.0f) currentIntensity -= TRANSITION_SPEED;
        }
        currentIntensity = Mth.clamp(currentIntensity, 0.0f, 1.0f);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (currentIntensity <= 0) return;

        float targetR = 0.0f;
        float targetG = 0.0f;
        float targetB = 0.00f;

        float r = Mth.lerp(currentIntensity, event.getRed(), targetR);
        float g = Mth.lerp(currentIntensity, event.getGreen(), targetG);
        float b = Mth.lerp(currentIntensity, event.getBlue(), targetB);

        event.setRed(r);
        event.setGreen(g);
        event.setBlue(b);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (currentIntensity <= 0) return;

        float originalStart = event.getNearPlaneDistance();
        float originalEnd = event.getFarPlaneDistance();

        float targetStart = -5.0f;
        float targetEnd = 25.0f;

        RenderSystem.setShaderFogStart(Mth.lerp(currentIntensity, originalStart, targetStart));
        RenderSystem.setShaderFogEnd(Mth.lerp(currentIntensity, originalEnd, targetEnd));
    }

    public static float getFovModifier(Player player) {
        if (currentIntensity <= 0) return 0.0f;

        float baseDistortion = 15.0f * currentIntensity;
        float time = player.tickCount + Minecraft.getInstance().getFrameTime();
        float heartbeat = (float) Math.sin(time * 0.2f) * (2.0f * currentIntensity);
        float jitter = (player.getRandom().nextFloat() - 0.5f) * (0.5f * currentIntensity);

        return baseDistortion + heartbeat + jitter;
    }
}