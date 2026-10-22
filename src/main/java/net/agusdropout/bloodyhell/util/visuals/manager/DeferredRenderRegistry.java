package net.agusdropout.bloodyhell.util.visuals.manager;

import net.agusdropout.bloodyhell.devtools.GlStateProbe;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.List;

public final class DeferredRenderRegistry {
    private static final List<DeferredRenderManager> ORDERED = List.of(
            DeferredRenderManager.of("Swirl", e -> SwirlRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("BlackHole", e -> BlackHoleRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("RadialDistortion", e -> RadialDistortionRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("EntityGlitter", e -> DeferredTaskQueues.ENTITY_GLITTER.renderAndClear()),
            DeferredRenderManager.of("ShapeGlitter", e -> DeferredTaskQueues.SHAPE_GLITTER.renderAndClear()),
            DeferredRenderManager.of("BlackHoleEntity", e -> BlackHoleEntityRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("Insight", e -> InsightRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("NoiseSphere", e -> NoiseSphereRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("MagicRing", e -> MagicRingRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("SphericalShield", e -> SphericalShieldRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("FrenziedFlame", e -> FrenziedFlameRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("LinearFrenziedFlame", e -> LinearFrenziedFlameRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("FrenziedTrail", e -> FrenziedTrailRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("BloodDrop", e -> BloodDropRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("TinyBloom", e -> TinyBloomRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("RadiantEnergy", e -> RadiantEnergyRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("FrenziedExplosion", e -> FrenziedExplosionRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("BloodBlob", BloodBlobRenderManager::renderAll),
            DeferredRenderManager.of("BloodOrb", BloodOrbRenderManager::renderAll),
            DeferredRenderManager.of("MiniBlob", MiniBlobRenderManager::renderAll),
            DeferredRenderManager.of("GoldenSorrowOrb", GoldenSorrowOrbRenderManager::renderAll),
            DeferredRenderManager.of("ScarletCinderOrb", ScarletCinderOrbRenderManager::renderAll),
            DeferredRenderManager.of("BloodStainDecal", BloodStainDecalRenderManager::renderAllAndClear),
            DeferredRenderManager.of("OmnitomeBook", e -> OmnitomeBookRenderManager.INSTANCE.renderAllAndClear()),
            DeferredRenderManager.of("OmnitomeClip", e -> OmnitomeClipRenderManager.INSTANCE.renderAllAndClear()),
            DeferredRenderManager.of("OmnitomeVoid", e -> OmnitomeVoidRenderManager.INSTANCE.renderAllAndClear())
    );

    private DeferredRenderRegistry() {}

    public static void renderAll(RenderLevelStageEvent event) {
        GlStateProbe.begin();
        for (DeferredRenderManager manager : ORDERED) {
            manager.renderAndClear(event);
            GlStateProbe.mark(manager.name());
        }
    }
}
