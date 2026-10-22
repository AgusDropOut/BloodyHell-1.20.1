package net.agusdropout.bloodyhell.devtools;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AutoTestHandler {
    private static final boolean ENABLED = Boolean.getBoolean("bloodyhell.autotest");
    private static final int EXIT_DELAY_TICKS = Integer.getInteger("bloodyhell.autotest.exitTicks", 0);
    private static final int WARMUP_TICKS = 100;
    private static final int SHADER_START_DELAY_TICKS = 100;
    private static final List<String> SHADER_PACKS = Arrays.stream(System.getProperty("bloodyhell.autotest.shaders", "").split(","))
            .map(String::trim).filter(name -> !name.isEmpty()).toList();

    private static int ticksInWorld = 0;
    private static boolean built = false;
    private static int ticksSinceBuild = 0;
    private static ShaderCycleTester shaderCycle;

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
        if (built && !SHADER_PACKS.isEmpty()) {
            if (++ticksSinceBuild < SHADER_START_DELAY_TICKS) return;
            if (shaderCycle == null) {
                shaderCycle = new ShaderCycleTester(SHADER_PACKS);
                if (!shaderCycle.isAvailable()) {
                    BloodyHell.LOGGER.error("[BHTEST] shader cycle requested but Oculus or Iris was not found, run with -PbhShaders");
                    minecraft.stop();
                    return;
                }
            }
            shaderCycle.tick(minecraft);
            if (shaderCycle.isFinished()) {
                BloodyHell.LOGGER.info("[BHTEST] autotest finished, closing client");
                minecraft.stop();
            }
            return;
        }
        if (built && EXIT_DELAY_TICKS > 0 && ++ticksSinceBuild >= EXIT_DELAY_TICKS) {
            BloodyHell.LOGGER.info("[BHTEST] autotest finished, closing client");
            minecraft.stop();
        }
    }
}
