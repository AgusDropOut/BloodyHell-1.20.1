package net.agusdropout.bloodyhell.devtools;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.agusdropout.bloodyhell.client.render.deferred.api.DeferredRenderObserver;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

public final class GlStateProbe implements DeferredRenderObserver {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Set<String> seen = new HashSet<>();
    private String previous;

    @Override
    public void begin() {
        previous = capture();
    }

    @Override
    public void mark(String label) {
        String current = capture();
        if (previous != null) {
            String line = label + " | " + previous + " -> " + current;
            if (seen.add(line)) {
                LOGGER.info("[GLPROBE] {}", line);
            }
        }
        previous = current;
    }

    private String capture() {
        StringBuilder sb = new StringBuilder();
        sb.append("blend=").append(GL11.glIsEnabled(GL11.GL_BLEND))
                .append(':').append(GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB))
                .append(':').append(GL11.glGetInteger(GL14.GL_BLEND_DST_RGB))
                .append(':').append(GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA))
                .append(':').append(GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA))
                .append(':').append(GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB));
        sb.append(" depth=").append(GL11.glIsEnabled(GL11.GL_DEPTH_TEST))
                .append(':').append(GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK))
                .append(':').append(GL11.glGetInteger(GL11.GL_DEPTH_FUNC));
        sb.append(" cull=").append(GL11.glIsEnabled(GL11.GL_CULL_FACE));
        sb.append(" scissor=").append(GL11.glIsEnabled(GL11.GL_SCISSOR_TEST));
        sb.append(" polyOffset=").append(GL11.glIsEnabled(GL11.GL_POLYGON_OFFSET_FILL));
        sb.append(" program=").append(GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM) != 0 ? "set" : "none");
        sb.append(" tex2d=").append(GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D) != 0 ? "set" : "none");
        sb.append(" activeTex=").append(GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) - GL13.GL_TEXTURE0);
        sb.append(" fbo=").append(GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING) != 0 ? "set" : "none");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer colorMask = stack.malloc(4);
            GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, colorMask);
            sb.append(" colorMask=");
            for (int i = 0; i < 4; i++) sb.append(colorMask.get(i) != 0 ? '1' : '0');
            IntBuffer viewport = stack.mallocInt(4);
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            sb.append(" viewport=").append(viewport.get(2)).append('x').append(viewport.get(3));
        }
        float m33 = RenderSystem.getProjectionMatrix().m33();
        sb.append(" projection=").append(Math.abs(m33 - 1.0f) < 1e-4f ? "ortho" : "perspective");
        return sb.toString();
    }
}
