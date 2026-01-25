package net.agusdropout.bloodyhell.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
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
}