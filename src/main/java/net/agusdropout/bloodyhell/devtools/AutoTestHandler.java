package net.agusdropout.bloodyhell.devtools;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AutoTestHandler {
    private static final boolean ENABLED = Boolean.getBoolean("bloodyhell.autotest");
    private static final int EXIT_DELAY_TICKS = Integer.getInteger("bloodyhell.autotest.exitTicks", 0);
    private static final int WARMUP_TICKS = 100;

    private static int ticksInWorld = 0;
    private static boolean built = false;
    private static int ticksSinceBuild = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (!ENABLED || event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;

        ticksInWorld++;
        if (!built && ticksInWorld >= WARMUP_TICKS) {
            minecraft.player.connection.sendCommand("bhtest build");
            built = true;
            return;
        }
        if (built && EXIT_DELAY_TICKS > 0 && ++ticksSinceBuild >= EXIT_DELAY_TICKS) {
            BloodyHell.LOGGER.info("[BHTEST] autotest finished, closing client");
            minecraft.stop();
        }
    }
}
