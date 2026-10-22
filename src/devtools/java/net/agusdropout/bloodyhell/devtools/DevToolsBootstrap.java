package net.agusdropout.bloodyhell.devtools;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredRenderRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DevToolsBootstrap {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (Boolean.getBoolean("bloodyhell.glprobe")) {
            DeferredRenderRegistry.setObserver(new GlStateProbe());
        }
    }
}
