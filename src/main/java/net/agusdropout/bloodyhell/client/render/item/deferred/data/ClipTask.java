package net.agusdropout.bloodyhell.client.render.item.deferred.data;

import net.agusdropout.bloodyhell.client.render.deferred.MatrixSnapshot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public record ClipTask(GeoItemRenderer<?> childRenderer, ItemStack stack, ItemDisplayContext transform, Matrix4f pose, Matrix3f normal, MatrixSnapshot matrices, int packedLight, int packedOverlay, float morphProgress) {
}
