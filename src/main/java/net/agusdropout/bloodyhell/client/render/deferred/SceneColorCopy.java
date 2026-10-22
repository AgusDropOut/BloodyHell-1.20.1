package net.agusdropout.bloodyhell.client.render.deferred;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

public final class SceneColorCopy {
    private static int texture = -1;
    private static int width = 0;
    private static int height = 0;
    private static boolean copied = false;

    private SceneColorCopy() {}

    public static void invalidate() {
        copied = false;
    }

    public static int acquire(RenderTarget mainTarget) {
        if (texture == -1 || width != mainTarget.width || height != mainTarget.height) {
            if (texture != -1) {
                TextureUtil.releaseTextureId(texture);
            }
            texture = TextureUtil.generateTextureId();
            width = mainTarget.width;
            height = mainTarget.height;
            copied = false;

            RenderSystem.bindTexture(texture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }

        RenderSystem.bindTexture(texture);
        if (!copied) {
            GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, mainTarget.width, mainTarget.height, 0);
            copied = true;
        }
        return texture;
    }
}
