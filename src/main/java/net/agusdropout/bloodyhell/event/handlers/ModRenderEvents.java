package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.agusdropout.bloodyhell.client.render.deferred.DeferredRenderRegistry;
import net.agusdropout.bloodyhell.client.render.deferred.SceneColorCopy;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModRenderEvents {

    @SubscribeEvent
    public static void onGameShuttingDown(GameShuttingDownEvent event) {
        SceneColorCopy.release();
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {


        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            DeferredRenderRegistry.renderAll(new FrameContext(event.getCamera(), event.getPoseStack(), event.getProjectionMatrix(), event.getPartialTick()));






        }









    }
}