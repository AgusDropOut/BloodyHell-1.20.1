package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.SpecialSlashEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class SpecialSlashRenderer extends EntityRenderer<SpecialSlashEntity> {

    private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/entity/blank.png");

    public SpecialSlashRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SpecialSlashEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // 1. Orientación Global (Hacia donde mira la entidad)
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        // =============================================================
        // CÁLCULO DE EFECTOS (Edad, Escala, Fade)
        // =============================================================
        float age = entity.tickCount + partialTicks;
        float maxLife = 20.0f;

        // 1. CRECIMIENTO
        float growthDuration = 3.5f;
        float rawScale = Mth.clamp(age / growthDuration, 0.0f, 1.0f);
        float scale = 0.1f + 0.9f * (float) Math.pow(rawScale, 0.5);

        // 2. DESVANECIMIENTO GLOBAL
        float fadeStart = maxLife - 4.0f;
        float mainAlpha = 1.0f;
        if (age > fadeStart) {
            mainAlpha = 1.0f - Mth.clamp((age - fadeStart) / 4.0f, 0.0f, 1.0f);
        }

        if (mainAlpha <= 0.01f) {
            poseStack.popPose();
            return;
        }

        // =============================================================
        // CONFIGURACIÓN RENDER
        // =============================================================
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // =============================================================
        // BUCLE DE TRAZO (GHOST TRAIL)
        // =============================================================

        // Cantidad de "fantasmas" detrás del tajo principal.
        // 5 es un buen número: suave y no consume recursos.
        int trailCount = 5;

        // Distancia entre cada fantasma (hacia atrás). Si la entidad es muy rápida, aumenta esto.
        float trailSpacing = 0.3f;

        for (int i = 0; i < trailCount; i++) {
            poseStack.pushPose();

            // Desplazamiento hacia atrás:
            // i=0 es el principal (0 desplazamiento)
            // i=1 es el primer fantasma, etc.
            // Movemos en X negativo porque tras rotar -90 grados en Y, el eje X es la profundidad local (o Z, depende de tu setup, probamos X negativo que suele ser "atrás" visualmente tras la rotación inicial).
            // NOTA: En tu rotación inicial, -90 en Y suele alinear X con la vista. Si ves que el trazo sale de lado, cambia esto a 'translate(0, 0, -offset)'
            float offset = i * trailSpacing;

            // IMPORTANTE: Tras tus rotaciones (YP -90), el eje "hacia adelante" suele ser X.
            // Vamos a movernos en X negativo para dejar el rastro atrás.
            poseStack.translate(-offset, 0, 0);

            // Cálculo de Alpha para el fantasma
            // El principal (i=0) tiene alpha 100%. Los de atrás se desvanecen rápido.
            float trailFactor = 1.0f - ((float) i / trailCount);
            float currentAlpha = mainAlpha * trailFactor * trailFactor; // Cuadrático para que desaparezca rápido

            // Escalado del fantasma (opcional: que el rastro se haga más pequeño)
            float ghostScale = scale * (1.0f - (i * 0.1f));

            if (currentAlpha > 0.05f && ghostScale > 0) {
                float currentRadius = 3.5f * ghostScale;
                float currentWidth = 0.8f * ghostScale;

                // DIBUJAR LOS DOS TAJOS (La X)

                // Slash 1
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.mulPose(Axis.XN.rotationDegrees(180));
                poseStack.mulPose(Axis.YN.rotationDegrees(45));
                drawSlash(poseStack, buffer, currentRadius, currentWidth, currentAlpha, age);
                poseStack.popPose();

                // Slash 2
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.mulPose(Axis.YN.rotationDegrees(-45));
                drawSlash(poseStack, buffer, currentRadius, currentWidth, currentAlpha, age);
                poseStack.popPose();
            }

            poseStack.popPose(); // Fin del push del fantasma actual
        }

        // Restaurar estado
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private void drawSlash(PoseStack poseStack, BufferBuilder buffer, float radius, float width, float alphaMult, float age) {
        Matrix4f matrix = poseStack.last().pose();

        // --- CAPA 1: AURA (NEGRO) ---
        // Alpha base 0.8f * alphaMult del fade out
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        buildCrescentMesh(buffer, matrix, radius, width, 0.0f, 0.0f, 0.0f, 0.8f * alphaMult);
        Tesselator.getInstance().end();

        // --- CAPA 2: NÚCLEO (AMARILLO BRILLANTE + EFECTO PULSO) ---

        // EFECTO PULSO: El núcleo cambia sutilmente de grosor usando seno del tiempo
        // Esto hace que la energía parezca vibrar.
        float pulse = 1.0f + 0.1f * Mth.sin(age * 1.5f);
        float coreWidth = (width * 0.5f) * pulse;
        float coreRadius = (radius * 0.9f);

        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        buildCrescentMesh(buffer, matrix, coreRadius, coreWidth, 1.0f, 1.0f, 0.0f, 1.0f * alphaMult);
        Tesselator.getInstance().end();
    }

    private void buildCrescentMesh(BufferBuilder buffer, Matrix4f matrix, float radius, float maxThickness, float r, float g, float b, float maxAlpha) {
        int segments = 20;
        float arcAngle = (float) Math.PI / 1.2f;

        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            float angle = -arcAngle / 2.0f + t * arcAngle;

            float currentThickness = maxThickness * Mth.sin(t * (float) Math.PI);
            float alpha = maxAlpha * Mth.sin(t * (float) Math.PI);

            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);

            float xInner = (radius - currentThickness / 2) * sin;
            float yInner = (radius - currentThickness / 2) * cos;

            float xOuter = (radius + currentThickness / 2) * sin;
            float yOuter = (radius + currentThickness / 2) * cos;

            buffer.vertex(matrix, xInner, yInner, 0).color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, xOuter, yOuter, 0).color(r, g, b, alpha).endVertex();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SpecialSlashEntity entity) {
        return BLANK_TEXTURE;
    }
}