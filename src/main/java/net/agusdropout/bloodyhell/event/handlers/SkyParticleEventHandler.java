package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.particle.custom.FrenziedSunSkyParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkyParticleEventHandler {

    private static FrenziedSunSkyParticle activeSunParticle = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;

            if (level != null && !mc.isPaused() && mc.player != null) {
                boolean shouldExist = ClientInsightData.getPlayerInsight() > 50.0F;

                if (shouldExist) {
                    if (activeSunParticle == null || !activeSunParticle.isAlive()) {
                        activeSunParticle = new FrenziedSunSkyParticle(level, mc.player.getX(), mc.player.getY(), mc.player.getZ());
                        mc.particleEngine.add(activeSunParticle);
                    }
                } else {
                    if (activeSunParticle != null) {
                        activeSunParticle.remove();
                        activeSunParticle = null;
                    }
                }
            } else {
                activeSunParticle = null;
            }
        }
    }
}