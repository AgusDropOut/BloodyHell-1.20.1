package net.agusdropout.bloodyhell.util.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;

public class ShaderUtils {

    private static Boolean hasIris = null;
    private static Boolean hasOptifine = null;
    private static Field ofShadersField = null;

    public static boolean areShadersActive() {
        return isIrisShaderActive() || isOptifineShaderActive();
    }

    // ==========================================
    //           RENDER TYPE PROXY
    // ==========================================

    /**
     * PROXY METHOD: Acts as a switch.
     * Use this in your EntityRenderers when selecting a RenderType.
     * * @param texture The texture you want to use.
     * @param original The custom RenderType you normally use (e.g., your ADDITIVE blend type).
     * @return The original type if Vanilla, or a "Shader-Safe" type (Translucent Emissive) if Shaders are on.
     */
    public static RenderType getRenderType(ResourceLocation texture, RenderType original) {
        if (areShadersActive()) {
            // "entity_translucent_emissive" is the Gold Standard for shaders.
            // Shaders recognize it as: "This object has texture + transparency + ignores light map".
            return RenderType.entityTranslucentEmissive(texture);
        }
        return original;
    }

    /**
     * PROXY METHOD: For untextured shapes (Beams, Lightning).
     */
    public static RenderType getShapeRenderType(RenderType original) {
        if (areShadersActive()) {
            // "lightning" is the safest bet for untextured glowing shapes in shaders.
            return RenderType.lightning();
        }
        return original;
    }

    // ==========================================
    //                 IRIS
    // ==========================================
    private static boolean isIrisShaderActive() {
        if (hasIris == null) {
            hasIris = ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus");
        }
        if (hasIris) {
            try {
                Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                Object instance = apiClass.getMethod("getInstance").invoke(null);
                return (boolean) apiClass.getMethod("isShaderPackInUse").invoke(instance);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    // ==========================================
    //               OPTIFINE
    // ==========================================
    private static boolean isOptifineShaderActive() {
        if (hasOptifine == null) {
            try {
                Class.forName("net.optifine.Config");
                hasOptifine = true;
            } catch (ClassNotFoundException e) {
                hasOptifine = false;
            }
        }
        if (hasOptifine) {
            try {
                if (ofShadersField == null) {
                    Class<?> shadersClass = Class.forName("net.optifine.shaders.Shaders");
                    ofShadersField = shadersClass.getDeclaredField("shaderPackLoaded");
                    ofShadersField.setAccessible(true);
                }
                return (boolean) ofShadersField.get(null);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static void renderGravitationalLens(PoseStack poseStack, int captureTextureId, float size, Vector3f color, float alpha, float time) {
        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getWidth();
        int screenH = mc.getWindow().getHeight();

        // 1. Pass Time Uniform
        if (ModShaders.DISTORTION_SHADER.getUniform("GameTime") != null) {
            ModShaders.DISTORTION_SHADER.getUniform("GameTime").set(time);
        }

        // 2. Capture Screen
        RenderSystem.bindTexture(captureTextureId);
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, screenW, screenH, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // 3. Setup Shader
        RenderSystem.setShader(() -> ModShaders.DISTORTION_SHADER);
        if (ModShaders.DISTORTION_SHADER.getUniform("ScreenSize") != null) {
            ModShaders.DISTORTION_SHADER.getUniform("ScreenSize").set((float)screenW, (float)screenH);
        }
        RenderSystem.setShaderTexture(0, captureTextureId);

        // 4. Drawing Logic
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        Quaternionf camRot = mc.gameRenderer.getMainCamera().rotation();

        // --- POSITIONS ---
        // These match your old method: -1 to 1 scaled by 'size'
        Vector3f[] corners = {
                new Vector3f(-size, -size, 0),
                new Vector3f(-size, size, 0),
                new Vector3f(size, size, 0),
                new Vector3f(size, -size, 0)
        };

        // --- UVS ---
        // These are absolute: always 0 to 1 so the shader knows where the center is
        float[][] uvs = {
                {0.0f, 0.0f},
                {0.0f, 1.0f},
                {1.0f, 1.0f},
                {1.0f, 0.0f}
        };

        for (int i = 0; i < 4; i++) {
            Vector3f posVec = new Vector3f(corners[i]);
            posVec.rotate(camRot); // Face the camera

            // Multiply by the PoseStack matrix to move it to the particle's world position
            Vector4f finalPos = new Vector4f(posVec.x(), posVec.y(), posVec.z(), 1.0f);
            finalPos.mul(poseStack.last().pose());

            buffer.vertex(finalPos.x(), finalPos.y(), finalPos.z())
                    .uv(uvs[i][0], uvs[i][1])
                    .color(color.x(), color.y(), color.z(), alpha)
                    .endVertex();
        }

        tess.end();
    }
}