package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.spell.SmallCrimsonDagger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SmallCrimsonDaggerRenderer extends EntityRenderer<SmallCrimsonDagger> {

    private final SmallCrimsonDaggerModel<SmallCrimsonDagger> model;
    private static final ResourceLocation TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/small_crimson_dagger.png");
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/effect/blood_trail.png");

    // --- CONFIGURATION ---
    private static final float STUCK_DEPTH_OFFSET = 0.0f; // Keep 0 if you fixed the model Z-offset

    // CHANGE THIS NUMBER TO RESIZE THE DAGGER
    private static final float SCALE_MODIFIER = 2.5f;

    public SmallCrimsonDaggerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SmallCrimsonDaggerModel<>(context.bakeLayer(SmallCrimsonDaggerModel.LAYER_LOCATION));
    }

    @Override
    public void render(SmallCrimsonDagger entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // 1. Interpolated Rotation
        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        // 2. Apply Rotation
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

        // 3. Stuck Offset
        if (entity.isStuckInGround()) {
            poseStack.translate(0, 0, STUCK_DEPTH_OFFSET);
        }

        // 4. SCALE & FADE (UPDATED)
        float scale = SCALE_MODIFIER; // Use our new modifier
        float alpha = entity.getFadeAlpha(partialTicks);
        if (alpha < 1.0f) scale *= alpha;

        poseStack.scale(scale, scale, scale);

        // 5. Render Model
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

        // 6. RENDER TRAIL
        if (!entity.isStuckInGround() && !entity.isInvisible()) {
            renderTrail(entity, partialTicks, poseStack, buffer);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderTrail(SmallCrimsonDagger entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        List<Vec3> history = new ArrayList<>(entity.getTrailHistory());
        if (history.size() < 2) return;

        double lerpX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double lerpY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double lerpZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        history.add(0, new Vec3(lerpX, lerpY, lerpZ));

        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));

        poseStack.pushPose();
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // We scale the trail width too, so it matches the bigger dagger
        float width = 0.35f * SCALE_MODIFIER;

        for (int i = 0; i < history.size() - 1; i++) {
            Vec3 p1 = history.get(i);
            Vec3 p2 = history.get(i + 1);

            Vector3f start = new Vector3f((float) (p1.x - lerpX), (float) (p1.y - lerpY), (float) (p1.z - lerpZ));
            Vector3f end   = new Vector3f((float) (p2.x - lerpX), (float) (p2.y - lerpY), (float) (p2.z - lerpZ));

            Vector3f dir = new Vector3f(end).sub(start).normalize();
            Vector3f toCam = new Vector3f((float)(camPos.x - p1.x), (float)(camPos.y - p1.y), (float)(camPos.z - p1.z)).normalize();
            Vector3f right = new Vector3f(dir).cross(toCam).normalize().mul(width * 0.5f);

            float progress1 = (float) i / history.size();
            float progress2 = (float) (i + 1) / history.size();
            float alpha1 = 1.0f - progress1;
            float alpha2 = 1.0f - progress2;

            consumer.vertex(pose, start.x - right.x, start.y - right.y, start.z - right.z).color(1.0f, 0.0f, 0.0f, alpha1 * 0.7f).uv(0.0f, progress1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(pose, start.x + right.x, start.y + right.y, start.z + right.z).color(1.0f, 0.0f, 0.0f, alpha1 * 0.7f).uv(1.0f, progress1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(pose, end.x + right.x, end.y + right.y, end.z + right.z).color(1.0f, 0.0f, 0.0f, alpha2 * 0.7f).uv(1.0f, progress2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(pose, end.x - right.x, end.y - right.y, end.z - right.z).color(1.0f, 0.0f, 0.0f, alpha2 * 0.7f).uv(0.0f, progress2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SmallCrimsonDagger entity) {
        return TEXTURE;
    }
}