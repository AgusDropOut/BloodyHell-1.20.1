package net.agusdropout.bloodyhell.client.render.item.deferred.api;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;

@FunctionalInterface
public interface VoidDraw {
    void draw(Matrix4f poseMatrix, Matrix3f normalMatrix, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);
}
