package net.agusdropout.bloodyhell.util.visuals;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class InsightDistortingVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;
    private final float time;
    private final float baseAlpha;

    private double currentX, currentY, currentZ;

    public InsightDistortingVertexConsumer(VertexConsumer delegate, float time, float alphaMultiplier) {
        this.delegate = delegate;
        this.time = time;
        this.baseAlpha = alphaMultiplier;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        this.currentX = x;
        this.currentY = y;
        this.currentZ = z;
        return this.delegate.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        float noise = calculateNoise(this.currentX, this.currentY, this.currentZ, this.time);

        int degradedRed = (int) (red * 0.35f * noise);
        int degradedGreen = (int) (green * 0.35f * noise);
        int degradedBlue = (int) (blue * 0.45f * noise);

        float dynamicAlpha = this.baseAlpha * (0.1f + 0.9f * noise);
        int newAlpha = (int) (alpha * dynamicAlpha);

        return this.delegate.color(degradedRed, degradedGreen, degradedBlue, newAlpha);
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        float noise = calculateNoise(this.currentX, this.currentZ, this.currentY, this.time * 0.8f);

        float distortedU = u + (float) (Math.sin(v * 35.0f + this.time * 2.5f) * 0.02f) + (noise * 0.02f);
        float distortedV = v + (float) (Math.cos(u * 35.0f + this.time * 2.5f) * 0.02f) + (noise * 0.02f);

        return this.delegate.uv(distortedU, distortedV);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return this.delegate.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return this.delegate.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return this.delegate.normal(x, y, z);
    }

    @Override
    public void endVertex() {
        this.delegate.endVertex();
    }

    @Override
    public void defaultColor(int red, int green, int blue, int alpha) {
        this.delegate.defaultColor(red, green, blue, alpha);
    }

    @Override
    public void unsetDefaultColor() {
        this.delegate.unsetDefaultColor();
    }

    private float calculateNoise(double x, double y, double z, float t) {
        float wave1 = (float) Math.sin((x + t) * 4.1f);
        float wave2 = (float) Math.cos((y - t * 1.2f) * 3.7f);
        float wave3 = (float) Math.sin((z + t * 0.8f) * 5.3f);
        return (wave1 + wave2 + wave3 + 3.0f) / 6.0f;
    }
}