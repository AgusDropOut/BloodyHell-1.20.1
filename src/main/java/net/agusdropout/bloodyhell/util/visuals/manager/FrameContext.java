package net.agusdropout.bloodyhell.util.visuals.manager;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public record FrameContext(Camera camera, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick) {
}
