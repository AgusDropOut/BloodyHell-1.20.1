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
        boolean isGlitching = Math.sin(this.time * 0.5f) > 0.95f;

        float tintR = 0.5f;
        float tintG = 0.6f;
        float tintB = 0.8f;

        float brightness = 0.6f + (noise * 0.4f);
        if (isGlitching) {
            brightness += 0.4f;
        }

        int finalRed = (int) Math.min(255, (red * tintR * brightness));
        int finalGreen = (int) Math.min(255, (green * tintG * brightness));
        int finalBlue = (int) Math.min(255, (blue * tintB * brightness));

        float dynamicAlpha = this.baseAlpha * (0.35f + 0.65f * noise);
        if (isGlitching) {
            dynamicAlpha *= 0.5f;
        }
        int newAlpha = (int) (alpha * dynamicAlpha);

        return this.delegate.color(finalRed, finalGreen, finalBlue, newAlpha);
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        float fastTime = this.time * 1.5f;
        boolean isGlitching = Math.sin(this.time * 0.5f) > 0.95f;

        float warpX = Math.abs((float) Math.sin(v * 12.0f + fastTime)) * (float) Math.cos(u * 18.0f - fastTime * 0.8f);
        float warpY = Math.abs((float) Math.cos(u * 12.0f + fastTime * 1.2f)) * (float) Math.sin(v * 18.0f - fastTime);

        warpX = Math.signum(warpX) * (warpX * warpX) * 0.15f;
        warpY = Math.signum(warpY) * (warpY * warpY) * 0.15f;

        if (isGlitching) {
            warpX += (float) Math.sin(v * 50.0f) * 0.05f;
        }

        return this.delegate.uv(u + warpX, v + warpY);
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