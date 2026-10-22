package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.devtools.GlStateProbe;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.agusdropout.bloodyhell.util.visuals.manager.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {


        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            GlStateProbe.begin();
            SwirlRenderManager.renderAllAndClear();
            GlStateProbe.mark("Swirl");
            BlackHoleRenderManager.renderAllAndClear();
            GlStateProbe.mark("BlackHole");
            RadialDistortionRenderManager.renderAllAndClear();
            GlStateProbe.mark("RadialDistortion");
            EntityGlitterRenderManager.renderAllAndClear();
            GlStateProbe.mark("EntityGlitter");
            ShapeGlitterRenderManager.renderAllAndClear();
            GlStateProbe.mark("ShapeGlitter");
            BlackHoleEntityRenderManager.renderAllAndClear();
            GlStateProbe.mark("BlackHoleEntity");
            InsightRenderManager.renderAllAndClear();
            GlStateProbe.mark("Insight");
            NoiseSphereRenderManager.renderAllAndClear();
            GlStateProbe.mark("NoiseSphere");
            MagicRingRenderManager.renderAllAndClear();
            GlStateProbe.mark("MagicRing");
            SphericalShieldRenderManager.renderAllAndClear();
            GlStateProbe.mark("SphericalShield");
            FrenziedFlameRenderManager.renderAllAndClear();
            GlStateProbe.mark("FrenziedFlame");
            LinearFrenziedFlameRenderManager.renderAllAndClear();
            GlStateProbe.mark("LinearFrenziedFlame");
            FrenziedTrailRenderManager.renderAllAndClear();
            GlStateProbe.mark("FrenziedTrail");
            BloodDropRenderManager.renderAllAndClear();
            GlStateProbe.mark("BloodDrop");
            TinyBloomRenderManager.renderAllAndClear();
            GlStateProbe.mark("TinyBloom");
            RadiantEnergyRenderManager.renderAllAndClear();
            GlStateProbe.mark("RadiantEnergy");
            FrenziedExplosionRenderManager.renderAllAndClear();
            GlStateProbe.mark("FrenziedExplosion");
            BloodBlobRenderManager.renderAll(event);
            GlStateProbe.mark("BloodBlob");
            BloodOrbRenderManager.renderAll(event);
            GlStateProbe.mark("BloodOrb");
            MiniBlobRenderManager.renderAll(event);
            GlStateProbe.mark("MiniBlob");
            GoldenSorrowOrbRenderManager.renderAll(event);
            GlStateProbe.mark("GoldenSorrowOrb");
            ScarletCinderOrbRenderManager.renderAll(event);
            GlStateProbe.mark("ScarletCinderOrb");
            BloodStainDecalRenderManager.renderAllAndClear(event);
            GlStateProbe.mark("BloodStainDecal");

            OmnitomeBookRenderManager.INSTANCE.renderAllAndClear();
            GlStateProbe.mark("OmnitomeBook");
            OmnitomeClipRenderManager.INSTANCE.renderAllAndClear();
            GlStateProbe.mark("OmnitomeClip");
            OmnitomeVoidRenderManager.INSTANCE.renderAllAndClear();
            GlStateProbe.mark("OmnitomeVoid");






        }









    }
}