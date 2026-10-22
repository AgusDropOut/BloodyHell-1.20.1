package net.agusdropout.bloodyhell.client.render.deferred;


import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderManager;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderObserver;
import net.agusdropout.bloodyhell.client.render.deferred.api.FrameContext;
import net.agusdropout.bloodyhell.client.render.effect.Effects;
import net.agusdropout.bloodyhell.client.render.item.deferred.ItemEffects;
import java.util.List;

public final class DeferredRenderRegistry {
    private static final List<DeferredRenderManager> ORDERED = List.of(
            Effects.SWIRL,
            Effects.BLACK_HOLE,
            Effects.RADIAL_DISTORTION,
            DeferredTaskQueues.ENTITY_GLITTER,
            DeferredTaskQueues.SHAPE_GLITTER,
            Effects.BLACK_HOLE_ENTITY,
            DeferredTaskQueues.INSIGHT,
            Effects.NOISE_SPHERE,
            Effects.MAGIC_RING,
            Effects.SPHERICAL_SHIELD,
            Effects.FRENZIED_FLAME,
            Effects.LINEAR_FRENZIED_FLAME,
            Effects.FRENZIED_TRAIL,
            Effects.BLOOD_DROP,
            Effects.TINY_BLOOM,
            Effects.RADIANT_TRAIL,
            Effects.FRENZIED_EXPLOSION,
            Effects.BLOOD_BLOB,
            Effects.BLOOD_ORB,
            Effects.MINI_BLOB,
            Effects.GOLDEN_SORROW_ORB,
            Effects.SCARLET_CINDER_ORB,
            Effects.BLOOD_STAIN_DECAL,
            ItemEffects.OMNITOME_BOOK,
            ItemEffects.OMNITOME_CLIP,
            ItemEffects.OMNITOME_VOID
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
