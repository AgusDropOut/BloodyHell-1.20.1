package net.agusdropout.bloodyhell.event.handlers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.util.visuals.manager.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {



        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            SwirlRenderManager.renderAllAndClear();
            BlackHoleRenderManager.renderAllAndClear();
            RadialDistortionRenderManager.renderAllAndClear();
            EntityGlitterRenderManager.renderAllAndClear();
            ShapeGlitterRenderManager.renderAllAndClear();
            BlackHoleEntityRenderManager.renderAllAndClear();
            InsightRenderManager.renderAllAndClear();
            NoiseSphereRenderManager.renderAllAndClear();
            MagicRingRenderManager.renderAllAndClear();
            SphericalShieldRenderManager.renderAllAndClear();

            FrenziedFlameRenderManager.renderAllAndClear();

            LinearFrenziedFlameRenderManager.renderAllAndClear();
            FrenziedTrailRenderManager.renderAllAndClear();
            TinyBloomRenderManager.renderAllAndClear();
            RadiantEnergyRenderManager.renderAllAndClear();
            FrenziedExplosionRenderManager.renderAllAndClear();

        }
    }
}