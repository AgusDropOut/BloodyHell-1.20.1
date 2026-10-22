package net.agusdropout.bloodyhell.client.render.deferred;


import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderObserver;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.effect.BlackHoleEntityRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.BlackHoleRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.BillboardEffects;
import net.agusdropout.bloodyhell.client.render.effect.BloodBlobRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.OrbEffects;
import net.agusdropout.bloodyhell.client.render.effect.TrailEffects;
import net.agusdropout.bloodyhell.client.render.effect.BloodStainDecalRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.MagicRingRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.MiniBlobRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.NoiseSphereRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.RadialDistortionRenderManager;
import net.agusdropout.bloodyhell.client.render.effect.SphericalShieldRenderManager;
import net.agusdropout.bloodyhell.client.render.item.deferred.OmnitomeBookRenderManager;
import net.agusdropout.bloodyhell.client.render.item.deferred.OmnitomeClipRenderManager;
import net.agusdropout.bloodyhell.client.render.item.deferred.OmnitomeVoidRenderManager;
import java.util.List;

public final class DeferredRenderRegistry {
    private static final List<DeferredRenderManager> ORDERED = List.of(
            BillboardEffects.SWIRL,
            DeferredRenderManager.of("BlackHole", e -> BlackHoleRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("RadialDistortion", e -> RadialDistortionRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("EntityGlitter", e -> DeferredTaskQueues.ENTITY_GLITTER.renderAndClear()),
            DeferredRenderManager.of("ShapeGlitter", e -> DeferredTaskQueues.SHAPE_GLITTER.renderAndClear()),
            DeferredRenderManager.of("BlackHoleEntity", e -> BlackHoleEntityRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("Insight", e -> DeferredTaskQueues.INSIGHT.renderAndClear()),
            DeferredRenderManager.of("NoiseSphere", e -> NoiseSphereRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("MagicRing", e -> MagicRingRenderManager.renderAllAndClear()),
            DeferredRenderManager.of("SphericalShield", e -> SphericalShieldRenderManager.renderAllAndClear()),
            BillboardEffects.FRENZIED_FLAME,
            BillboardEffects.LINEAR_FRENZIED_FLAME,
            TrailEffects.FRENZIED,
            BillboardEffects.BLOOD_DROP,
            BillboardEffects.TINY_BLOOM,
            TrailEffects.RADIANT,
            BillboardEffects.FRENZIED_EXPLOSION,
            DeferredRenderManager.of("BloodBlob", BloodBlobRenderManager::renderAll),
            OrbEffects.BLOOD,
            DeferredRenderManager.of("MiniBlob", MiniBlobRenderManager::renderAll),
            OrbEffects.GOLDEN_SORROW,
            OrbEffects.SCARLET_CINDER,
            DeferredRenderManager.of("BloodStainDecal", BloodStainDecalRenderManager::renderAllAndClear),
            DeferredRenderManager.of("OmnitomeBook", e -> OmnitomeBookRenderManager.INSTANCE.renderAllAndClear()),
            DeferredRenderManager.of("OmnitomeClip", e -> OmnitomeClipRenderManager.INSTANCE.renderAllAndClear()),
            DeferredRenderManager.of("OmnitomeVoid", e -> OmnitomeVoidRenderManager.INSTANCE.renderAllAndClear())
    );

    private static DeferredRenderObserver observer;

    private DeferredRenderRegistry() {}

    public static void setObserver(DeferredRenderObserver value) {
        observer = value;
    }

    public static void renderAll(FrameContext frame) {
        SceneColorCopy.invalidate();
        DeferredRenderObserver current = observer;
        if (current != null) current.begin();
        for (DeferredRenderManager manager : ORDERED) {
            manager.renderAndClear(frame);
            if (current != null) current.mark(manager.name());
        }
    }
}
