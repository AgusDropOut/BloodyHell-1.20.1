package net.agusdropout.bloodyhell.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A centralized utility for rendering complex procedural shapes in 3D space.
 * Includes support for Spheres, Icosahedrons, Cylinders, Billboards, and procedural noise geometry.
 */
public class RenderHelper {

    private static final float PHI = 1.618034f;

    /**
     * Renders a standard full sphere.
     *
     * @param radius  The radius of the sphere.
     * @param latSegs Vertical segments (Latitude).
     * @param lonSegs Horizontal segments (Longitude).
     */
    public static void renderSphere(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                    float radius, int latSegs, int lonSegs,
                                    float r, float g, float b, float a, int light) {
        renderSphereSlice(consumer, pose, normal, radius, latSegs, lonSegs,
                -Math.PI / 2, Math.PI / 2,
                r, g, b, a, light);
    }

    /**
     * Renders a "Slice" of a sphere (e.g., a band or ring-like sphere).
     *
     * @param minTheta Starting vertical angle (-PI/2 is bottom).
     * @param maxTheta Ending vertical angle (PI/2 is top).
     */
    public static void renderSphereSlice(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                         float radius, int latSegs, int lonSegs,
                                         double minTheta, double maxTheta,
                                         float r, float g, float b, float a, int light) {
        for (int i = 0; i < latSegs; i++) {
            double t1 = (double) i / latSegs;
            double t2 = (double) (i + 1) / latSegs;
            double theta1 = minTheta + (maxTheta - minTheta) * t1;
            double theta2 = minTheta + (maxTheta - minTheta) * t2;

            for (int j = 0; j < lonSegs; j++) {
                double phi1 = 2 * Math.PI * j / lonSegs;
                double phi2 = 2 * Math.PI * (j + 1) / lonSegs;

                sphereVertex(consumer, pose, normal, radius, theta1, phi1, r, g, b, a, light);
                sphereVertex(consumer, pose, normal, radius, theta2, phi1, r, g, b, a, light);
                sphereVertex(consumer, pose, normal, radius, theta2, phi2, r, g, b, a, light);
                sphereVertex(consumer, pose, normal, radius, theta1, phi2, r, g, b, a, light);
            }
        }
    }

    /**
     * Renders a sphere where the radius is determined dynamically per vertex via a function.
     */
    public static void renderProceduralSphere(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                              int latSegs, int lonSegs,
                                              RadiusModifier radiusFunc,
                                              float r, float g, float b, float a, int light) {
        for (int i = 0; i < latSegs; i++) {
            double theta1 = Math.PI * i / latSegs - Math.PI / 2;
            double theta2 = Math.PI * (i + 1) / latSegs - Math.PI / 2;

            for (int j = 0; j < lonSegs; j++) {
                double phi1 = 2 * Math.PI * j / lonSegs;
                double phi2 = 2 * Math.PI * (j + 1) / lonSegs;

                procSphereVert(consumer, pose, normal, theta1, phi1, radiusFunc, r, g, b, a, light);
                procSphereVert(consumer, pose, normal, theta2, phi1, radiusFunc, r, g, b, a, light);
                procSphereVert(consumer, pose, normal, theta2, phi2, radiusFunc, r, g, b, a, light);
                procSphereVert(consumer, pose, normal, theta1, phi2, radiusFunc, r, g, b, a, light);
            }
        }
    }

    /**
     * Renders a standard Icosahedron (20-sided shape).
     */
    public static void renderIcosahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                         float scale, float r, float g, float b, float a, int light) {
        renderStellatedIcosahedron(consumer, pose, normal, scale, scale, r, g, b, a, light);
    }

    /**
     * Renders a "Stellated" (Spiked) Icosahedron.
     *
     * @param baseScale Radius of the inner valleys.
     * @param tipScale  Radius of the outer spikes.
     */
    public static void renderStellatedIcosahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                                  float baseScale, float tipScale,
                                                  float r, float g, float b, float a, int light) {
        Vec3[] baseVerts = getIcosahedronVertices(baseScale);
        Vec3[] tipVerts = (baseScale == tipScale) ? baseVerts : getIcosahedronVertices(tipScale);
        int[][] faces = getIcosahedronFaces();

        for (int[] f : faces) {
            Vec3 v1 = baseVerts[f[0]];
            Vec3 v2 = baseVerts[f[1]];
            Vec3 v3 = baseVerts[f[2]];

            if (baseScale != tipScale) {
                Vec3 t1 = tipVerts[f[0]];
                Vec3 t2 = tipVerts[f[1]];
                Vec3 t3 = tipVerts[f[2]];

                addTri(consumer, pose, normal, v1, v2, t3, r, g, b, a, light);
                addTri(consumer, pose, normal, v2, v3, t1, r, g, b, a, light);
                addTri(consumer, pose, normal, v3, v1, t2, r, g, b, a, light);
            } else {
                addTri(consumer, pose, normal, v1, v2, v3, r, g, b, a, light);
            }
        }
    }

    /**
     * Renders a stellated icosahedron with double-sided faces for lamps or glowing artifacts.
     */
    public static void renderStarLampIcosahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                                 float baseScale, float tipScale,
                                                 float r, float g, float b, float a, int light) {
        Vec3[] baseVerts = getIcosahedronVertices(baseScale);
        Vec3[] tipVerts = getIcosahedronVertices(tipScale);
        int[][] faces = getIcosahedronFaces();

        for (int[] f : faces) {
            Vec3 v1 = baseVerts[f[0]];
            Vec3 v2 = baseVerts[f[1]];
            Vec3 v3 = baseVerts[f[2]];

            Vec3 t1 = tipVerts[f[0]];
            Vec3 t2 = tipVerts[f[1]];
            Vec3 t3 = tipVerts[f[2]];

            addDoubleSidedTri(consumer, pose, normal, v1, v2, t3, r, g, b, a, light);
            addDoubleSidedTri(consumer, pose, normal, v2, v3, t1, r, g, b, a, light);
            addDoubleSidedTri(consumer, pose, normal, v3, v1, t2, r, g, b, a, light);
            addDoubleSidedTri(consumer, pose, normal, v1, v2, v3, r, g, b, a, light);
        }
    }



    /**
     * Renders a cylinder where the radius follows a power curve (Trumpet/Flare shape).
     *
     * @param shapePower 1.0 = Cone (Linear), 2.0 = Trumpet (Quadratic), 0.5 = Dome.
     */
    public static void renderFlaredCylinder(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                            float height, float radBase, float radTop,
                                            float rotation, float totalTwist,
                                            int layers, int segments,
                                            float shapePower,
                                            float r, float g, float b, float alphaBase, float alphaTop, int light) {
        for (int i = 0; i < layers; i++) {
            float progress1 = (float) i / layers;
            float progress2 = (float) (i + 1) / layers;

            float y1 = progress1 * height;
            float y2 = progress2 * height;

            float t1 = (float) Math.pow(progress1, shapePower);
            float t2 = (float) Math.pow(progress2, shapePower);

            float r1 = Mth.lerp(t1, radBase, radTop);
            float r2 = Mth.lerp(t2, radBase, radTop);

            float twist1 = rotation + (progress1 * totalTwist);
            float twist2 = rotation + (progress2 * totalTwist);

            float a1 = Mth.lerp(progress1, alphaBase, alphaTop);
            float a2 = Mth.lerp(progress2, alphaBase, alphaTop);

            for (int j = 0; j < segments; j++) {
                float ang1 = (float) j / segments * Mth.TWO_PI;
                float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;

                float x1a = Mth.cos(ang1 + twist1) * r1;
                float z1a = Mth.sin(ang1 + twist1) * r1;
                float x1b = Mth.cos(ang2 + twist1) * r1;
                float z1b = Mth.sin(ang2 + twist1) * r1;
                float x2a = Mth.cos(ang1 + twist2) * r2;
                float z2a = Mth.sin(ang1 + twist2) * r2;
                float x2b = Mth.cos(ang2 + twist2) * r2;
                float z2b = Mth.sin(ang2 + twist2) * r2;

                vertex(consumer, pose, normal, x1a, y1, z1a, new float[]{r, g, b, a1}, light, 0, 0, 0, 0, 0);
                vertex(consumer, pose, null, x1b, y1, z1b, new float[]{r, g, b, a1}, light, 1, 0, 0, 0, 0);
                vertex(consumer, pose, null, x2b, y2, z2b, new float[]{r, g, b, a2}, light, 1, 1, 0, 0, 0);
                vertex(consumer, pose, null, x2a, y2, z2a, new float[]{r, g, b, a2}, light, 0, 1, 0, 0, 0);
            }
        }
    }

    /**
     * Renders a tapered cylinder with smooth normals for shaders.
     */
    public static void renderTaperedCylinder(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                             float height, float radBase, float radTop,
                                             float rotation, float twist,
                                             int layers, int segments,
                                             float r, float g, float b, float alphaBase, float alphaTop, int light) {
        if (ShaderUtils.areShadersActive()) {
            segments *= 6;
        }

        for (int i = 0; i < layers; i++) {
            float progress1 = (float) i / layers;
            float progress2 = (float) (i + 1) / layers;

            float y1 = progress1 * height;
            float y2 = progress2 * height;

            float r1 = Mth.lerp(progress1, radBase, radTop);
            float r2 = Mth.lerp(progress2, radBase, radTop);

            float twist1 = rotation + (progress1 * twist);
            float twist2 = rotation + (progress2 * twist);

            float a1 = Mth.lerp(progress1, alphaBase, alphaTop);
            float a2 = Mth.lerp(progress2, alphaBase, alphaTop);

            for (int j = 0; j < segments; j++) {
                float ang1 = (float) j / segments * Mth.TWO_PI;
                float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;

                float cos1a = Mth.cos(ang1 + twist1);
                float sin1a = Mth.sin(ang1 + twist1);
                float cos1b = Mth.cos(ang2 + twist1);
                float sin1b = Mth.sin(ang2 + twist1);
                float cos2a = Mth.cos(ang1 + twist2);
                float sin2a = Mth.sin(ang1 + twist2);
                float cos2b = Mth.cos(ang2 + twist2);
                float sin2b = Mth.sin(ang2 + twist2);

                float x1a = cos1a * r1;
                float z1a = sin1a * r1;
                float x1b = cos1b * r1;
                float z1b = sin1b * r1;
                float x2a = cos2a * r2;
                float z2a = sin2a * r2;
                float x2b = cos2b * r2;
                float z2b = sin2b * r2;

                float n1ax = cos1a;
                float n1az = sin1a;
                float n1bx = cos1b;
                float n1bz = sin1b;
                float n2ax = cos2a;
                float n2az = sin2a;
                float n2bx = cos2b;
                float n2bz = sin2b;

                vertex(consumer, pose, normal, x1a, y1, z1a, new float[]{r, g, b, a1}, light, 0, 0, n1ax, 0, n1az);
                vertex(consumer, pose, null, x1b, y1, z1b, new float[]{r, g, b, a1}, light, 1, 0, n1bx, 0, n1bz);
                vertex(consumer, pose, null, x2b, y2, z2b, new float[]{r, g, b, a2}, light, 1, 1, n2bx, 0, n2bz);
                vertex(consumer, pose, null, x2a, y2, z2a, new float[]{r, g, b, a2}, light, 0, 1, n2ax, 0, n2az);
            }
        }
    }


    /**
     * Renders a hollow rectangle box (walls only, no top/bottom caps).
     * Useful for magical barriers or highlighting areas.
     *
     * @param width   Half-width of the rectangle (extends +/- width from center).
     * @param height  The vertical extent of the walls (extends +/- height from center).
     * @param yOffset vertical shift applied to the top/bottom edges (e.g. for animations).
     * @param upAlpha Alpha transparency for the top edge of the walls.
     */
    public static void renderHollowRectangle(VertexConsumer consumer, Matrix4f pose,
                                             float width, float height, float yOffset,
                                             float r, float g, float b, float alpha, float upAlpha,float jitterIntensity) {

        float upRedColor = r;
        float upGreenColor = g;
        float upBlueColor = b;


        RandomSource random = RandomSource.create();

        if (jitterIntensity > 0) {
            // Apply random offset based on intensity (e.g., -0.1 to +0.1)
            upRedColor = Mth.clamp(r + (random.nextFloat() - 0.5f) * jitterIntensity, 0.0f, 1.0f);
            upGreenColor = Mth.clamp(g + (random.nextFloat() - 0.5f) * jitterIntensity, 0.0f, 1.0f);
            upBlueColor = Mth.clamp(b + (random.nextFloat() - 0.5f) * jitterIntensity, 0.0f, 1.0f);
        }


        // Face 1: EAST (+X)
        simpleVertex(consumer, pose, width, -yOffset, -height, r, g, b, alpha);
        simpleVertex(consumer, pose, width, -yOffset,  height, r, g, b, alpha);
        simpleVertex(consumer, pose, width,  yOffset,  height, upRedColor, upGreenColor, upBlueColor, upAlpha);
        simpleVertex(consumer, pose, width,  yOffset, -height, upRedColor, upGreenColor, upBlueColor, upAlpha);

        // Face 2: WEST (-X)
        simpleVertex(consumer, pose, -width, -yOffset,  height, r, g, b, alpha);
        simpleVertex(consumer, pose, -width, -yOffset, -height, r, g, b, alpha);
        simpleVertex(consumer, pose, -width,  yOffset, -height, upRedColor, upGreenColor, upBlueColor, upAlpha);
        simpleVertex(consumer, pose, -width,  yOffset,  height, upRedColor, upGreenColor, upBlueColor, upAlpha);

        // Face 3: NORTH (-Z)
        simpleVertex(consumer, pose, -width, -yOffset, -height, r, g, b, alpha);
        simpleVertex(consumer, pose,  width, -yOffset, -height, r, g, b, alpha);
        simpleVertex(consumer, pose,  width,  yOffset, -height, upRedColor, upGreenColor, upBlueColor, upAlpha);
        simpleVertex(consumer, pose, -width,  yOffset, -height, upRedColor, upGreenColor, upBlueColor, upAlpha);

        // Face 4: SOUTH (+Z)
        simpleVertex(consumer, pose,  width, -yOffset,  height, r, g, b, alpha);
        simpleVertex(consumer, pose, -width, -yOffset,  height, r, g, b, alpha);
        simpleVertex(consumer, pose, -width,  yOffset,  height, upRedColor, upGreenColor, upBlueColor, upAlpha);
        simpleVertex(consumer, pose,  width,  yOffset,  height, upRedColor, upGreenColor, upBlueColor, upAlpha);
    }


    public static void renderPureMathToroid(VertexConsumer consumer, Matrix4f pose,
                                            float R, float r, float rCol, float gCol, float bCol, float alpha) {
        float step = (float) Math.PI / 8;

        for (float phi = 0; phi < Mth.TWO_PI; phi += step) {
            for (float theta = 0; theta < Mth.TWO_PI; theta += step) {

                drawFormulaVertex(consumer, pose, R, r, phi, theta, rCol, gCol, bCol, alpha);
                drawFormulaVertex(consumer, pose, R, r, phi + step, theta, rCol, gCol, bCol, alpha);
                drawFormulaVertex(consumer, pose, R, r, phi + step/2, theta + step, rCol, gCol, bCol, alpha);
                drawFormulaVertex(consumer, pose, R, r, phi, theta + step, rCol, gCol, bCol, alpha);
            }
        }
    }

    private static void drawFormulaVertex(VertexConsumer consumer, Matrix4f pose, float R, float r,
                                          float phi, float theta, float rCol, float gCol, float bCol, float alpha) {
        // x = (R + r * sin(theta)) * cos(phi)
        // z = (R + r * sin(theta)) * sin(phi)
        // y = r * cos(theta)
        float x = (R + r * Mth.sin(theta)) * Mth.cos(phi);
        float z = (R + r * Mth.sin(theta)) * Mth.sin(phi);
        float y = r * Mth.cos(theta);

        consumer.vertex(pose, x, y, z).color(rCol, gCol, bCol, alpha).endVertex();
    }


    /**
     * Renders a horizontal cylinder (lying on its side).
     * * @param radius The radius of the circular face.
     * @param length The total length of the cylinder.
     * @param segments How many quads to use for the roundness (Resolution).
     */
    public static void renderSideCylinder(VertexConsumer consumer, Matrix4f pose,
                                          float radius, float length, int segments,
                                          float rCol, float gCol, float bCol, float alpha) {

        float angleStep = (float) (Math.PI * 2) / segments;

        float x = 0;
        float z = 0;
        float nextx = 0;
        float nextz = 0;
        float y=10;

        for (int i = 0; i < segments; i++) {
            float theta = i * angleStep;
            float nextTheta = (i + 1) * angleStep;

            // --- YOUR TURN ---
            // 1. Calculate the 4 corners of a "strip" of the cylinder.
            // 2. Front corners will have z = 0.
            // 3. Back corners will have z = length.
            // 4. Use Mth.cos(theta) and Mth.sin(theta) for X and Y.
            x = (radius * Mth.cos(theta));
            z = (radius * Mth.sin(theta));
            nextx = (radius * Mth.cos(nextTheta));
            nextz = (radius * Mth.sin(nextTheta));

            consumer.vertex(pose, x, y, z).color(rCol, gCol, 1.0f, alpha).endVertex();
            consumer.vertex(pose, nextx, y, nextz).color(1.0f, gCol, bCol, alpha).endVertex();
            consumer.vertex(pose, nextx, y+10, nextz).color(rCol, 0.5f, bCol, alpha).endVertex();
            consumer.vertex(pose, x, y+10, z).color(rCol, gCol, 1.0f, alpha).endVertex();

            //invertido
            consumer.vertex(pose, x, y+10, z).color(rCol, gCol, 1.0f, alpha).endVertex();
            consumer.vertex(pose, nextx, y+10, nextz).color(rCol, 0.5f, bCol, alpha).endVertex();
            consumer.vertex(pose, nextx, y, nextz).color(1.0f, gCol, bCol, alpha).endVertex();
            consumer.vertex(pose, x, y, z).color(rCol, gCol, 1.0f, alpha).endVertex();
        }
    }

    /**
     * Renders a Cylinder using POSITION_COLOR only.
     * Increase segments if shaders are active to force smoothness without normals.
     */
    public static void renderSimpleGradientCylinder(VertexConsumer consumer, Matrix4f pose,
                                                    float height, float radBase, float radTop,
                                                    int segments, float rotation,
                                                    float r, float g, float b, float alphaBase, float alphaTop) {
        if (ShaderUtils.areShadersActive()) {
            segments *= 12;
            alphaBase = Math.min(1.0f, alphaBase * 1.5f);
            if (alphaTop > 0) alphaTop = Math.min(1.0f, alphaTop * 1.5f);
        }

        for (int j = 0; j < segments; j++) {
            float ang1 = (float) j / segments * Mth.TWO_PI;
            float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;

            float a1 = ang1 + rotation;
            float a2 = ang2 + rotation;

            float cos1 = Mth.cos(a1);
            float sin1 = Mth.sin(a1);
            float cos2 = Mth.cos(a2);
            float sin2 = Mth.sin(a2);

            float x1_base = cos1 * radBase;
            float z1_base = sin1 * radBase;
            float x2_base = cos2 * radBase;
            float z2_base = sin2 * radBase;
            float x1_top = cos1 * radTop;
            float z1_top = sin1 * radTop;
            float x2_top = cos2 * radTop;
            float z2_top = sin2 * radTop;

            simpleVertex(consumer, pose, x1_base, 0, z1_base, r, g, b, alphaBase);
            simpleVertex(consumer, pose, x1_top, height, z1_top, r, g, b, alphaTop);
            simpleVertex(consumer, pose, x2_top, height, z2_top, r, g, b, alphaTop);
            simpleVertex(consumer, pose, x2_base, 0, z2_base, r, g, b, alphaBase);

            simpleVertex(consumer, pose, x2_base, 0, z2_base, r, g, b, alphaBase);
            simpleVertex(consumer, pose, x2_top, height, z2_top, r, g, b, alphaTop);
            simpleVertex(consumer, pose, x1_top, height, z1_top, r, g, b, alphaTop);
            simpleVertex(consumer, pose, x1_base, 0, z1_base, r, g, b, alphaBase);
        }
    }

    /**
     * Renders a camera-facing quad at a specific position.
     */
    public static void renderBillboardQuad(VertexConsumer consumer, Matrix4f pose,
                                           float x, float y, float z, float size,
                                           float r, float g, float b, float a,
                                           Quaternionf camRot, int light) {
        Vector3f[] vertices = {
                new Vector3f(-size, -size, 0),
                new Vector3f(-size, size, 0),
                new Vector3f(size, size, 0),
                new Vector3f(size, -size, 0)
        };

        for (Vector3f v : vertices) {
            v.rotate(camRot);
            v.add(x, y, z);

            float u = (v.x() > x) ? 1 : 0;
            float v_uv = (v.y() > y) ? 0 : 1;

            vertex(consumer, pose, null, v.x(), v.y(), v.z(), new float[]{r, g, b, a}, light, u, v_uv);
        }
    }

    /**
     * Renders a flat disk or ring.
     */
    public static void renderDisk(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                  float innerRad, float outerRad, int segments, float rotation,
                                  float[] cInner, float[] cOuter, int light) {
        if (ShaderUtils.areShadersActive()) {
            segments *= 6;
        }
        for (int i = 0; i < segments; i++) {
            float a1 = (float) (Math.PI * 2 * i / segments) + rotation;
            float a2 = (float) (Math.PI * 2 * (i + 1) / segments) + rotation;

            float cos1 = Mth.cos(a1), sin1 = Mth.sin(a1);
            float cos2 = Mth.cos(a2), sin2 = Mth.sin(a2);

            vertex(consumer, pose, normal, cos1 * innerRad, 0, sin1 * innerRad, cInner, light);
            vertex(consumer, pose, normal, cos1 * outerRad, 0, sin1 * outerRad, cOuter, light);
            vertex(consumer, pose, normal, cos2 * outerRad, 0, sin2 * outerRad, cOuter, light);
            vertex(consumer, pose, normal, cos2 * innerRad, 0, sin2 * innerRad, cInner, light);
        }
    }

    /**
     * Renders a noise-distorted ring using POSITION_COLOR.
     */
    public static void renderSimpleProceduralRing(VertexConsumer consumer, Matrix4f pose,
                                                  float baseRadius, float width, int segments, float rotation,
                                                  float r, float g, float b, float alphaInner, float alphaOuter,
                                                  RingNoiseProvider noiseFunc, float innerNoiseStrength, float outerNoiseStrength) {
        if (ShaderUtils.areShadersActive()) {
            segments *= 12;
            alphaOuter = Math.min(1.0f, alphaOuter * 1.5f);
        }

        for (int j = 0; j < segments; j++) {
            float ang1 = (float) j / segments * Mth.TWO_PI;
            float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;

            float a1 = ang1 + rotation;
            float a2 = ang2 + rotation;

            float cos1 = Mth.cos(a1), sin1 = Mth.sin(a1);
            float cos2 = Mth.cos(a2), sin2 = Mth.sin(a2);

            float n1 = noiseFunc.getNoise(ang1);
            float n2 = noiseFunc.getNoise(ang2);

            float rInner1 = (baseRadius - width) + (n1 * innerNoiseStrength);
            float rOuter1 = baseRadius + (n1 * outerNoiseStrength);
            float rInner2 = (baseRadius - width) + (n2 * innerNoiseStrength);
            float rOuter2 = baseRadius + (n2 * outerNoiseStrength);

            float x1_in = cos1 * rInner1;
            float z1_in = sin1 * rInner1;
            float x1_out = cos1 * rOuter1;
            float z1_out = sin1 * rOuter1;
            float x2_out = cos2 * rOuter2;
            float z2_out = sin2 * rOuter2;
            float x2_in = cos2 * rInner2;
            float z2_in = sin2 * rInner2;

            simpleVertex(consumer, pose, x1_in, 0, z1_in, r, g, b, alphaInner);
            simpleVertex(consumer, pose, x1_out, 0, z1_out, r, g, b, alphaOuter);
            simpleVertex(consumer, pose, x2_out, 0, z2_out, r, g, b, alphaOuter);
            simpleVertex(consumer, pose, x2_in, 0, z2_in, r, g, b, alphaInner);
        }
    }

    /**
     * Renders a ring with procedural noise affecting the radius at each vertex.
     */
    public static void renderProceduralRing(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                            float baseRadius, float width, int segments, float rotation,
                                            float[] cInner, float[] cOuter, int light,
                                            RingNoiseProvider noiseFunc,
                                            float innerNoiseStrength, float outerNoiseStrength) {
        if (ShaderUtils.areShadersActive()) {
            segments *= 6;
        }

        float maxR = baseRadius + Math.max(width, Math.abs(outerNoiseStrength));

        for (int i = 0; i < segments; i++) {
            double ang1_raw = (2 * Math.PI * i) / segments;
            double ang2_raw = (2 * Math.PI * (i + 1)) / segments;

            float a1 = (float) (ang1_raw + rotation);
            float a2 = (float) (ang2_raw + rotation);

            float cos1 = Mth.cos(a1), sin1 = Mth.sin(a1);
            float cos2 = Mth.cos(a2), sin2 = Mth.sin(a2);

            float n1 = noiseFunc.getNoise(ang1_raw);
            float n2 = noiseFunc.getNoise(ang2_raw);

            float rInner1 = (baseRadius - width) + (n1 * innerNoiseStrength);
            float rOuter1 = baseRadius + (n1 * outerNoiseStrength);
            float rInner2 = (baseRadius - width) + (n2 * innerNoiseStrength);
            float rOuter2 = baseRadius + (n2 * outerNoiseStrength);

            float x1_in = cos1 * rInner1;
            float z1_in = sin1 * rInner1;
            float x1_out = cos1 * rOuter1;
            float z1_out = sin1 * rOuter1;
            float x2_out = cos2 * rOuter2;
            float z2_out = sin2 * rOuter2;
            float x2_in = cos2 * rInner2;
            float z2_in = sin2 * rInner2;

            float u1_in = (x1_in / maxR + 1) * 0.5f;
            float v1_in = (z1_in / maxR + 1) * 0.5f;
            float u1_out = (x1_out / maxR + 1) * 0.5f;
            float v1_out = (z1_out / maxR + 1) * 0.5f;
            float u2_out = (x2_out / maxR + 1) * 0.5f;
            float v2_out = (z2_out / maxR + 1) * 0.5f;
            float u2_in = (x2_in / maxR + 1) * 0.5f;
            float v2_in = (z2_in / maxR + 1) * 0.5f;

            vertex(consumer, pose, normal, x1_in, 0, z1_in, cInner, light, u1_in, v1_in, 0, 1, 0);
            vertex(consumer, pose, normal, x1_out, 0, z1_out, cOuter, light, u1_out, v1_out, 0, 1, 0);
            vertex(consumer, pose, normal, x2_out, 0, z2_out, cOuter, light, u2_out, v2_out, 0, 1, 0);
            vertex(consumer, pose, normal, x2_in, 0, z2_in, cInner, light, u2_in, v2_in, 0, 1, 0);
        }
    }

    /**
     * Renders the crescent slash shape.
     */
    public static void renderCrescent(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                      float radius, float maxThickness, float arcAngle,
                                      float r, float g, float b, float maxAlpha, int light) {
        int segments = 20;

        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            float angle = -arcAngle / 2.0f + t * arcAngle;

            float shapeFactor = Mth.sin(t * (float) Math.PI);
            float currentThickness = maxThickness * shapeFactor;
            float alpha = maxAlpha * shapeFactor;

            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);

            float xInner = (radius - currentThickness / 2) * sin;
            float yInner = (radius - currentThickness / 2) * cos;
            float xOuter = (radius + currentThickness / 2) * sin;
            float yOuter = (radius + currentThickness / 2) * cos;

            vertex(consumer, pose, normal, xInner, yInner, 0, new float[]{r, g, b, alpha}, light, 0, 0);
            vertex(consumer, pose, normal, xOuter, yOuter, 0, new float[]{r, g, b, alpha}, light, 1, 1);
        }
    }

    /**
     * Renders a crescent with a color gradient from the inner edge to the outer edge.
     */
    public static void renderCrescentGradient(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                              float radius, float maxThickness, float arcAngle,
                                              float[] cInner, float[] cOuter, int light) {
        int segments = 20;

        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            float angle = -arcAngle / 2.0f + t * arcAngle;

            float shapeFactor = Mth.sin(t * (float) Math.PI);
            float currentThickness = maxThickness * shapeFactor;

            float alphaInner = cInner[3] * shapeFactor;
            float alphaOuter = cOuter[3] * shapeFactor;

            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);

            float xInner = (radius - currentThickness / 2) * sin;
            float yInner = (radius - currentThickness / 2) * cos;
            float xOuter = (radius + currentThickness / 2) * sin;
            float yOuter = (radius + currentThickness / 2) * cos;

            vertex(consumer, pose, normal, xInner, yInner, 0,
                    new float[]{cInner[0], cInner[1], cInner[2], alphaInner}, light, 0, 0, 0, 0, 1);

            vertex(consumer, pose, normal, xOuter, yOuter, 0,
                    new float[]{cOuter[0], cOuter[1], cOuter[2], alphaOuter}, light, 1, 1, 0, 0, 1);
        }
    }

    /**
     * Renders a double-sided floating diamond shape.
     */
    public static void renderFloatingDiamond(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                             float x, float y, float z,
                                             float w, float h,
                                             float r, float g, float b, float a,
                                             int light) {
        Vec3 vTop = new Vec3(x, y + h, z);
        Vec3 vBot = new Vec3(x, y - h, z);

        Vec3 vEast = new Vec3(x + w, y, z);
        Vec3 vSouth = new Vec3(x, y, z + w);
        Vec3 vWest = new Vec3(x - w, y, z);
        Vec3 vNorth = new Vec3(x, y, z - w);

        addDoubleSidedTri(consumer, pose, normal, vEast, vTop, vSouth, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vSouth, vTop, vWest, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vWest, vTop, vNorth, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vNorth, vTop, vEast, r, g, b, a, light);

        addDoubleSidedTri(consumer, pose, normal, vEast, vSouth, vBot, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vSouth, vWest, vBot, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vWest, vNorth, vBot, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vNorth, vEast, vBot, r, g, b, a, light);
    }

    /**
     * Renders a classic 8-sided Gem (Octahedron). Guaranteed to be visible from all angles.
     */
    public static void renderOctahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                        float size,
                                        float r, float g, float b, float a, int light) {
        Vec3 top = new Vec3(0, size, 0);
        Vec3 bot = new Vec3(0, -size, 0);

        Vec3 front = new Vec3(0, 0, size);
        Vec3 right = new Vec3(size, 0, 0);
        Vec3 back = new Vec3(0, 0, -size);
        Vec3 left = new Vec3(-size, 0, 0);

        addDoubleSidedTri(consumer, pose, normal, top, front, right, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, top, right, back, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, top, back, left, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, top, left, front, r, g, b, a, light);

        addDoubleSidedTri(consumer, pose, normal, bot, right, front, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, bot, back, right, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, bot, left, back, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, bot, front, left, r, g, b, a, light);
    }

    /**
     * Renders a Dodecahedron (12 pentagonal faces).
     */
    public static void renderDodecahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                          float size,
                                          float r, float g, float b, float a, int light) {
        float phi = 1.618034f;
        float invPhi = 1.0f / phi;
        float s = size * 0.5f;

        Vec3[] aV = {
                new Vec3(s, s, s), new Vec3(s, s, -s), new Vec3(s, -s, s), new Vec3(s, -s, -s),
                new Vec3(-s, s, s), new Vec3(-s, s, -s), new Vec3(-s, -s, s), new Vec3(-s, -s, -s)
        };

        Vec3[] bV = {
                new Vec3(0, s * invPhi, s * phi), new Vec3(0, s * invPhi, -s * phi),
                new Vec3(0, -s * invPhi, s * phi), new Vec3(0, -s * invPhi, -s * phi)
        };

        Vec3[] cV = {
                new Vec3(s * invPhi, s * phi, 0), new Vec3(s * invPhi, -s * phi, 0),
                new Vec3(-s * invPhi, s * phi, 0), new Vec3(-s * invPhi, -s * phi, 0)
        };

        Vec3[] dV = {
                new Vec3(s * phi, 0, s * invPhi), new Vec3(s * phi, 0, -s * invPhi),
                new Vec3(-s * phi, 0, s * invPhi), new Vec3(-s * phi, 0, -s * invPhi)
        };

        renderPentagon(consumer, pose, normal, bV[0], aV[0], dV[0], aV[2], bV[2], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, bV[2], aV[6], dV[2], aV[4], bV[0], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, bV[1], bV[3], aV[3], dV[1], aV[1], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, bV[3], bV[1], aV[5], dV[3], aV[7], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, cV[0], aV[0], bV[0], aV[4], cV[2], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, cV[2], aV[5], bV[1], aV[1], cV[0], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, cV[1], aV[2], bV[2], aV[6], cV[3], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, cV[3], aV[7], bV[3], aV[3], cV[1], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, dV[0], aV[0], cV[0], aV[1], dV[1], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, dV[1], aV[3], cV[1], aV[2], dV[0], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, dV[2], dV[3], aV[5], cV[2], aV[4], r, g, b, a, light);
        renderPentagon(consumer, pose, normal, dV[3], dV[2], aV[6], cV[3], aV[7], r, g, b, a, light);
    }

    /**
     * Renders a pixel-like voxel box.
     */
    public static void renderPixel(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                   float x, float y, float z, float size,
                                   float r, float g, float b, float a, int light) {
        float[] color = {r, g, b, a};

        vertexForPixel(consumer, pose, normal, x, y, z, 0.0f, 1.0f, color, light);
        vertexForPixel(consumer, pose, normal, x + size, y, z, 1.0f, 1.0f, color, light);
        vertexForPixel(consumer, pose, normal, x + size, y + size, z, 1.0f, 0.0f, color, light);
        vertexForPixel(consumer, pose, normal, x, y + size, z, 0.0f, 0.0f, color, light);
    }






    /**
     * Renders a pulsating, spinning "Atlas" Heart (Complex geometric artifact).
     * Consists of 3 layers of Stellated Icosahedrons rotating against each other.
     */
    public static void renderAtlasHeart(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                        long gameTime, float partialTick,
                                        float rBase, float gBase, float bBase,
                                        float alpha, int light) {
        float time = gameTime + partialTick;
        float pulse = 1.0f + Mth.sin(time * 0.1f) * 0.1f;

        renderRotatedLayer(consumer, pose, normal,
                0.25f * pulse, 0.35f * pulse,
                time * 3.0f,
                rBase, gBase, bBase,
                1.0f, light);

        renderRotatedLayer(consumer, pose, normal,
                0.4f * pulse, 0.55f * pulse,
                -time * 1.5f,
                rBase, gBase, bBase,
                0.7f, light);

        renderRotatedLayer(consumer, pose, normal,
                0.6f * pulse, 0.2f * pulse,
                time * 0.5f,
                rBase, gBase, bBase,
                0.4f, light);
    }

    private static void renderRotatedLayer(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                           float baseScale, float tipScale, float rotDegrees,
                                           float r, float g, float b,
                                           float alpha, int light) {
        Vec3[] baseVerts = getIcosahedronVertices(baseScale);
        Vec3[] tipVerts = (baseScale == tipScale) ? baseVerts : getIcosahedronVertices(tipScale);
        int[][] faces = getIcosahedronFaces();

        Quaternionf q = new Quaternionf().rotateY(Mth.DEG_TO_RAD * rotDegrees);
        Quaternionf q2 = new Quaternionf().rotateX(Mth.DEG_TO_RAD * (rotDegrees * 0.5f));
        q.mul(q2);

        float[] c1 = {r, g, b};
        float[] c2 = {Math.min(1f, r + 0.2f), Math.min(1f, g + 0.2f), Math.min(1f, b + 0.2f)};
        float[] c3 = {Math.max(0f, r - 0.2f), Math.max(0f, g - 0.1f), Math.max(0f, b - 0.1f)};

        for (int i = 0; i < faces.length; i++) {
            int[] f = faces[i];

            Vec3 v1 = rotateVec(baseVerts[f[0]], q);
            Vec3 v2 = rotateVec(baseVerts[f[1]], q);
            Vec3 v3 = rotateVec(baseVerts[f[2]], q);
            Vec3 tTip = rotateVec(tipVerts[f[0]], q);

            float[] c;
            int colorIndex = i % 3;
            if (colorIndex == 0) c = c1;
            else if (colorIndex == 1) c = c2;
            else c = c3;

            addTri(consumer, pose, normal, v1, v2, tTip, c[0], c[1], c[2], alpha, light);
            addTri(consumer, pose, normal, v2, v3, tTip, c[0], c[1], c[2], alpha, light);
            addTri(consumer, pose, normal, v3, v1, tTip, c[0], c[1], c[2], alpha, light);
        }
    }

    private static Vec3 rotateVec(Vec3 v, Quaternionf q) {
        Vector3f temp = new Vector3f(v.x, v.y, v.z);
        temp.rotate(q);
        return new Vec3(temp.x, temp.y, temp.z);
    }

    private static void renderPentagon(VertexConsumer c, Matrix4f p, Matrix3f n,
                                       Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, Vec3 v5,
                                       float r, float g, float b, float a, int light) {
        addDoubleSidedTri(c, p, n, v1, v2, v3, r, g, b, a, light);
        addDoubleSidedTri(c, p, n, v1, v3, v4, r, g, b, a, light);
        addDoubleSidedTri(c, p, n, v1, v4, v5, r, g, b, a, light);
    }

    private static void addDoubleSidedTri(VertexConsumer c, Matrix4f p, Matrix3f n, Vec3 v1, Vec3 v2, Vec3 v3, float r, float g, float b, float a, int light) {
        addTri(c, p, n, v1, v2, v3, r, g, b, a, light);
        addTri(c, p, n, v3, v2, v1, r, g, b, a, light);
    }

    private static void addTri(VertexConsumer c, Matrix4f p, Matrix3f n, Vec3 v1, Vec3 v2, Vec3 v3, float r, float g, float b, float a, int light) {
        Vec3 edge1 = v2.subtract(v1);
        Vec3 edge2 = v3.subtract(v1);
        Vec3 norm = edge1.cross(edge2).normalize();

        vertex(c, p, n, (float) v1.x, (float) v1.y, (float) v1.z, new float[]{r, g, b, a}, light, 0, 0, (float) norm.x, (float) norm.y, (float) norm.z);
        vertex(c, p, n, (float) v2.x, (float) v2.y, (float) v2.z, new float[]{r, g, b, a}, light, 1, 0, (float) norm.x, (float) norm.y, (float) norm.z);
        vertex(c, p, n, (float) v3.x, (float) v3.y, (float) v3.z, new float[]{r, g, b, a}, light, 0.5f, 1, (float) norm.x, (float) norm.y, (float) norm.z);
    }

    private static void sphereVertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                     float rad, double theta, double phi,
                                     float r, float g, float b, float a, int light) {
        float x = (float) (rad * Math.cos(theta) * Math.cos(phi));
        float y = (float) (rad * Math.sin(theta));
        float z = (float) (rad * Math.cos(theta) * Math.sin(phi));
        float u = (float) (phi / (2 * Math.PI));
        float v = (float) ((theta + Math.PI / 2) / Math.PI);

        float nx = x / rad;
        float ny = y / rad;
        float nz = z / rad;
        vertex(consumer, pose, normal, x, y, z, new float[]{r, g, b, a}, light, u, v, nx, ny, nz);
    }

    private static void procSphereVert(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                       double theta, double phi, RadiusModifier func,
                                       float r, float g, float b, float a, int light) {
        float rad = func.apply(theta, phi);

        float x = (float) (rad * Math.cos(theta) * Math.cos(phi));
        float y = (float) (rad * Math.sin(theta));
        float z = (float) (rad * Math.cos(theta) * Math.sin(phi));

        float nx = x;
        float ny = y;
        float nz = z;
        float len = Mth.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0) {
            nx /= len;
            ny /= len;
            nz /= len;
        }

        float u = (float) (phi / (2 * Math.PI));
        float v = (float) ((theta + Math.PI / 2) / Math.PI);

        vertex(consumer, pose, normal, x, y, z, new float[]{r, g, b, a}, light, u, v, nx, ny, nz);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z,
                               float[] color, int light,
                               float u, float v,
                               float nx, float ny, float nz) {
        consumer.vertex(pose, x, y, z)
                .color(color[0], color[1], color[2], color[3])
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light);
        if (normal != null) consumer.normal(normal, nx, ny, nz);
        else consumer.normal(nx, ny, nz);
        consumer.endVertex();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z, float[] color, int light) {
        consumer.vertex(pose, x, y, z)
                .color(color[0], color[1], color[2], color[3])
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light);

        if (normal != null) {
            consumer.normal(normal, 0, 1, 0);
        } else {
            consumer.normal(0, 1, 0);
        }
        consumer.endVertex();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z, float[] color, int light, float u, float v) {
        vertex(consumer, pose, normal, x, y, z, color, light, u, v, 0, 1, 0);
    }

    private static void simpleVertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, float r, float g, float b, float a) {
        consumer.vertex(pose, x, y, z).color(r, g, b, a).endVertex();
    }

    private static void vertexForPixel(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                       float x, float y, float z, float u, float v, float[] color, int light) {
        consumer.vertex(pose, x, y, z)
                .color(color[0], color[1], color[2], color[3])
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light);

        if (normal != null) {
            consumer.normal(normal, 0, 1, 0);
        } else {
            consumer.normal(0, 0, 1);
        }

        consumer.endVertex();
    }

    @FunctionalInterface
    public interface RadiusModifier {
        float apply(double theta, double phi);
    }

    @FunctionalInterface
    public interface RingNoiseProvider {
        float getNoise(double angle);
    }

    private static class Vec3 {
        public float x, y, z;

        public Vec3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3 subtract(Vec3 v) {
            return new Vec3(x - v.x, y - v.y, z - v.z);
        }

        public Vec3 cross(Vec3 v) {
            return new Vec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
        }

        public Vec3 normalize() {
            float l = (float) Math.sqrt(x * x + y * y + z * z);
            return new Vec3(x / l, y / l, z / l);
        }
    }

    private static Vec3[] getIcosahedronVertices(float scale) {
        float t = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);
        float s = 1.0f / (float) Math.sqrt(1 + t * t) * scale;
        t *= (s / scale) * scale;
        return new Vec3[]{
                new Vec3(-s, t, 0), new Vec3(s, t, 0), new Vec3(-s, -t, 0), new Vec3(s, -t, 0),
                new Vec3(0, -s, t), new Vec3(0, s, t), new Vec3(0, -s, -t), new Vec3(0, s, -t),
                new Vec3(t, 0, -s), new Vec3(t, 0, s), new Vec3(-t, 0, -s), new Vec3(-t, 0, s)
        };
    }

    private static int[][] getIcosahedronFaces() {
        return new int[][]{
                {0, 11, 5}, {0, 5, 1}, {0, 1, 7}, {0, 7, 10}, {0, 10, 11},
                {1, 5, 9}, {5, 11, 4}, {11, 10, 2}, {10, 7, 6}, {7, 1, 8},
                {3, 9, 4}, {3, 4, 2}, {3, 2, 6}, {3, 6, 8}, {3, 8, 9},
                {4, 9, 5}, {2, 4, 11}, {6, 2, 10}, {8, 6, 7}, {9, 8, 1}
        };
    }

    public static class Perlin {
        private static final int[] perm = new int[512];
        private static final int[] p = new int[256];

        static {
            for (int i = 0; i < 256; i++) p[i] = i;
            java.util.Random rand = new java.util.Random(1234);
            for (int i = 0; i < 256; i++) {
                int j = rand.nextInt(256 - i) + i;
                int tmp = p[i];
                p[i] = p[j];
                p[j] = tmp;
                perm[i] = perm[i + 256] = p[i];
            }
        }

        private static double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private static double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private static double grad(int hash, double x, double y) {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }

        public static double noise(double x, double y) {
            int X = (int) Math.floor(x) & 255;
            int Y = (int) Math.floor(y) & 255;
            x -= Math.floor(x);
            y -= Math.floor(y);
            double u = fade(x);
            double v = fade(y);
            int A = perm[X] + Y, B = perm[X + 1] + Y;
            return lerp(v, lerp(u, grad(perm[A], x, y), grad(perm[B], x - 1, y)),
                    lerp(u, grad(perm[A + 1], x, y - 1), grad(perm[B + 1], x - 1, y - 1)));
        }
    }
}