package net.agusdropout.bloodyhell.util.bones;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.IdentityHashMap;
import java.util.Map;

public class BoneManipulator {
    // IdentityHashMap is faster for storing ModelParts
    private static final Map<ModelPart, PoseSnapshot> SNAPSHOT_CACHE = new IdentityHashMap<>();

    public static void applyVisceralTwitch(ModelPart part, float time, float intensity) {
        if (part == null) return;


        SNAPSHOT_CACHE.putIfAbsent(part, new PoseSnapshot(part));


        part.xRot += (float) Math.sin(time * 22.0f) * intensity;
        part.zRot += (float) Math.cos(time * 28.0f) * (intensity * 0.5f);


        part.x += (float) Math.sin(time * 35.0f) * (intensity * 4.0f);
        part.y += (float) Math.cos(time * 30.0f) * (intensity * 4.0f);


        float pulse = 1.0f + ((float) Math.sin(time * 15.0f) * 0.1f);
        part.xScale *= pulse;
        part.yScale *= pulse;


        for (ModelPart child : part.children.values()) {
            applyVisceralTwitch(child, time, intensity * 0.85f);
        }
    }

    public static void applyBoneDisplacement(ModelPart part, float ageInTicks, float power) {
        if (part == null) return;


        float offsetX = (float) Math.sin(ageInTicks * 15.0f) * power;
        float offsetY = (float) Math.cos(ageInTicks * 12.0f) * power;


        part.x += offsetX;
        part.y += offsetY;


    }

    public static void applyGeckoTwitch(GeoBone bone, float time, float intensity) {
        if (bone == null) return;


        bone.setRotX(bone.getRotX() + (float) Math.sin(time * 20.0f) * intensity);
        bone.setRotZ(bone.getRotZ() + (float) Math.cos(time * 25.0f) * intensity);


        bone.setPosX(bone.getPosX() + (float) Math.sin(time * 30.0f) * (intensity * 2.0f));
        bone.setPosY(bone.getPosY() + (float) Math.cos(time * 28.0f) * (intensity * 2.0f));


    }

    public static void applyExorcismTwist(GeoBone bone, float intensity) {
        if (bone == null) return;
        float twistAmount = 2.5f * intensity;


        float jaggedness = (float) Math.sin(System.currentTimeMillis() * 0.05) * 0.1f;

        bone.setRotY(bone.getRotY() + twistAmount + jaggedness);


        bone.setPosY(bone.getPosY() + (intensity * 2.0f));
    }

    public static void applyVanillaExorcismTwist(ModelPart part, float intensity) {
        if (part == null) return;

        SNAPSHOT_CACHE.putIfAbsent(part, new PoseSnapshot(part));

        float twistAmount = 2.5f * intensity;

        float jaggedness = (float) Math.sin(System.currentTimeMillis() * 0.05) * 0.1f;

        part.yRot += twistAmount + jaggedness;
        part.xRot += twistAmount + jaggedness;
        part.zRot += twistAmount + jaggedness;
        part.x += (intensity * 2.0f);

        part.y += (intensity * 2.0f);
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

    public static Vec3 getBoneWorldPosition(LivingEntity entity, ModelPart part) {
        if (part == null) return entity.position();


        double localX = part.x / 16.0D;
        double localY = (24.0D - part.y) / 16.0D;
        double localZ = part.z / 16.0D;

        Vec3 localOffset = new Vec3(localX, localY, localZ);


        float bodyYawRad = (float) Math.toRadians(-entity.yBodyRot);
        localOffset = localOffset.yRot(bodyYawRad);


        return entity.position().add(localOffset);
    }
}