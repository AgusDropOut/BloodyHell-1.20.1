package net.agusdropout.bloodyhell.client.render.item.deferred.data;

import net.agusdropout.bloodyhell.client.render.deferred.MatrixSnapshot;
import net.agusdropout.bloodyhell.client.render.item.deferred.api.VoidDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;

public record VoidTask(VoidDraw draw, ItemDisplayContext transform, Matrix4f pose, Matrix3f normal, MatrixSnapshot matrices, GeoBone bone, ResourceLocation texture, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
}
