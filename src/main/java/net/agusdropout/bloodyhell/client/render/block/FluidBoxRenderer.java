package net.agusdropout.bloodyhell.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public final class FluidBoxRenderer {

    private FluidBoxRenderer() {
    }

    public static TextureAtlasSprite blockSprite(ResourceLocation texture) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
    }

    public static void drawBox(VertexConsumer builder, PoseStack.Pose pose, TextureAtlasSprite sprite, int argb,
                               float xMin, float yMin, float zMin, float xMax, float yMax, float zMax,
                               int light, int overlay) {
        float a = ((argb >> 24) & 0xFF) / 255f;
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        Face face = new Face(builder, pose, sprite, r, g, b, a, light, overlay);

        face.quad(0, 1, 0, xMin, yMax, zMin, xMin, yMax, zMax, xMax, yMax, zMax, xMax, yMax, zMin);
        face.quad(0, -1, 0, xMin, yMin, zMax, xMin, yMin, zMin, xMax, yMin, zMin, xMax, yMin, zMax);
        face.quad(0, 0, -1, xMax, yMax, zMin, xMax, yMin, zMin, xMin, yMin, zMin, xMin, yMax, zMin);
        face.quad(0, 0, 1, xMin, yMax, zMax, xMin, yMin, zMax, xMax, yMin, zMax, xMax, yMax, zMax);
        face.quad(-1, 0, 0, xMin, yMax, zMin, xMin, yMin, zMin, xMin, yMin, zMax, xMin, yMax, zMax);
        face.quad(1, 0, 0, xMax, yMax, zMax, xMax, yMin, zMax, xMax, yMin, zMin, xMax, yMax, zMin);
    }

    private record Face(VertexConsumer builder, PoseStack.Pose pose, TextureAtlasSprite sprite,
                        float r, float g, float b, float a, int light, int overlay) {

        void quad(float nx, float ny, float nz,
                  float x0, float y0, float z0, float x1, float y1, float z1,
                  float x2, float y2, float z2, float x3, float y3, float z3) {
            vertex(x0, y0, z0, nx, ny, nz, sprite.getU0(), sprite.getV0());
            vertex(x1, y1, z1, nx, ny, nz, sprite.getU0(), sprite.getV1());
            vertex(x2, y2, z2, nx, ny, nz, sprite.getU1(), sprite.getV1());
            vertex(x3, y3, z3, nx, ny, nz, sprite.getU1(), sprite.getV0());
        }

        private void vertex(float x, float y, float z, float nx, float ny, float nz, float u, float v) {
            builder.vertex(pose.pose(), x, y, z)
                    .color(r, g, b, a)
                    .uv(u, v)
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(pose.normal(), nx, ny, nz)
                    .endVertex();
        }
    }
}
