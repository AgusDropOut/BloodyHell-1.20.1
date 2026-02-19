package net.agusdropout.bloodyhell.util.bones;

import net.minecraft.client.model.geom.ModelPart;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.IdentityHashMap;
import java.util.Map;

public class BoneManipulator {
    // IdentityHashMap is faster for storing ModelParts
    private static final Map<ModelPart, PoseSnapshot> SNAPSHOT_CACHE = new IdentityHashMap<>();

    public static void applyVisceralTwitch(ModelPart part, float time, float intensity) {
        if (part == null) return;

        // 1. Snapshot the current state BEFORE we touch it
        SNAPSHOT_CACHE.putIfAbsent(part, new PoseSnapshot(part));

        // 2. Apply Jitter (Rotation)
        part.xRot += (float) Math.sin(time * 22.0f) * intensity;
        part.zRot += (float) Math.cos(time * 28.0f) * (intensity * 0.5f);

        // 3. Apply Violent Displacement (Translation)
        // This makes the bones look like they are vibrating out of their sockets
        part.x += (float) Math.sin(time * 35.0f) * (intensity * 4.0f);
        part.y += (float) Math.cos(time * 30.0f) * (intensity * 4.0f);

        // 4. Apply Bone "Pulsing" (Scale)
        float pulse = 1.0f + ((float) Math.sin(time * 15.0f) * 0.1f);
        part.xScale *= pulse;
        part.yScale *= pulse;

        // 5. Recursively apply to all child parts (made public via AT)
        for (ModelPart child : part.children.values()) {
            applyVisceralTwitch(child, time, intensity * 0.85f);
        }
    }

    public static void applyBoneDisplacement(ModelPart part, float ageInTicks, float power) {
        if (part == null) return;

        // Use Sine to create a "vibration" offset.
        // Because sin(x) goes from -1 to 1, the bone always returns to its original spot.
        float offsetX = (float) Math.sin(ageInTicks * 15.0f) * power;
        float offsetY = (float) Math.cos(ageInTicks * 12.0f) * power;

        // Apply the displacement
        part.x += offsetX;
        part.y += offsetY;

        // IMPORTANT: We must subtract this exact same amount in the 'Post' render event,
        // OR use a Mixin to store the original 'initialPose' and reset to it.
        // A simpler way for a "Pain Throne" effect is to just use Rotations,
        // as Minecraft resets them for you.
    }

    public static void applyGeckoTwitch(GeoBone bone, float time, float intensity) {
        if (bone == null) return;

        // GeckoLib rotations are usually additive to the current animation
        bone.setRotX(bone.getRotX() + (float) Math.sin(time * 20.0f) * intensity);
        bone.setRotZ(bone.getRotZ() + (float) Math.cos(time * 25.0f) * intensity);

        // Displacement
        bone.setPosX(bone.getPosX() + (float) Math.sin(time * 30.0f) * (intensity * 2.0f));
        bone.setPosY(bone.getPosY() + (float) Math.cos(time * 28.0f) * (intensity * 2.0f));

        // GeckoLib hierarchy is internal, so we don't need manual recursion here
        // if we iterate over 'getRegisteredBones()' in the event handler.
    }

    /**
     * Resets all modified parts to their original pose.
     * MUST be called in RenderLivingEvent.Post
     */
    public static void restoreAll() {
        for(Map.Entry<ModelPart, PoseSnapshot> entry : SNAPSHOT_CACHE.entrySet()) {
            ModelPart part = entry.getKey();
            PoseSnapshot snapshot = entry.getValue();
            snapshot.restore(part);
        }

        SNAPSHOT_CACHE.clear();
    }
}