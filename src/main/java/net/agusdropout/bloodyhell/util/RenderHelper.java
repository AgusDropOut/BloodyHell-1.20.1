package net.agusdropout.bloodyhell.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A centralized utility for rendering complex procedural shapes in 3D space.
 * <p>
 * Updates:
 * <ul>
 * <li><b>Spheres:</b> Added Slice support (min/max Theta) for wave effects.</li>
 * <li><b>Icosahedrons:</b> Added standard and stellated (spiked) variants.</li>
 * <li><b>Billboards:</b> Added camera-facing quad support.</li>
 * </ul>
 */
public class RenderHelper {

    private static final float PHI = 1.618034f; // Golden Ratio

    // =========================================================================================
    //                                      SPHERES
    // =========================================================================================

    /**
     * Standard full sphere.
     */
    public static void renderSphere(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                    float radius, int latSegs, int lonSegs,
                                    float r, float g, float b, float a, int light) {
        renderSphereSlice(consumer, pose, normal, radius, latSegs, lonSegs,
                -Math.PI / 2, Math.PI / 2, // Full Latitude
                r, g, b, a, light);
    }

    /**
     * VARIANT: Renders a "Slice" of a sphere (e.g. a band or ring-like sphere).
     * Used by: <b>MagicWaveParticle</b>.
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

            // Interpolate between minTheta and maxTheta
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

    // =========================================================================================
    //                                      ICOSAHEDRONS
    // =========================================================================================

    /**
     * Renders a standard Icosahedron (20-sided die shape).
     * Used by: <b>RitekeeperHeartLayer</b>.
     */
    public static void renderIcosahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                         float scale, float r, float g, float b, float a, int light) {
        renderStellatedIcosahedron(consumer, pose, normal, scale, scale, r, g, b, a, light);
    }

    /**
     * VARIANT: Renders a "Stellated" (Spiked) Icosahedron.
     * Used by: <b>StarLampRenderer</b>.
     *
     * @param baseScale Radius of the inner valleys.
     * @param tipScale  Radius of the outer spikes.
     */
    public static void renderStellatedIcosahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                                  float baseScale, float tipScale,
                                                  float r, float g, float b, float a, int light) {

        // 1. Generate Base Vertices (Unit Icosahedron)
        Vec3[] baseVerts = getIcosahedronVertices(baseScale);

        // 2. Generate Tip Vertices (Scaled up)
        Vec3[] tipVerts = (baseScale == tipScale) ? baseVerts : getIcosahedronVertices(tipScale);

        // 3. Faces
        int[][] faces = getIcosahedronFaces();

        for (int[] f : faces) {
            Vec3 v1 = baseVerts[f[0]];
            Vec3 v2 = baseVerts[f[1]];
            Vec3 v3 = baseVerts[f[2]];

            // For stellation (StarLamp), we render "Pyramids" on the faces?
            // Actually, your StarLamp code replaced the "v3" of the triangle with the "tip" version.
            // Let's replicate that exact logic: V1(Base) -> V2(Base) -> V3(Tip).
            // This twists the face outward.

            if (baseScale != tipScale) {
                // Star Lamp Mode: Faceted Spike
                // We construct a pyramid on the face. Center = Tip.
                // V1_base -> V2_base -> V3_tip
                // NOTE: To match your specific loop structure (v1_base, v2_base, v3_tip), we need flexible logic.
                // But for a utility, let's assume "Stellated" means drawing the 3 faces of the spike.

                Vec3 t1 = tipVerts[f[0]];
                Vec3 t2 = tipVerts[f[1]];
                Vec3 t3 = tipVerts[f[2]];

                // Draw 3 facets per face to make a spike, OR replicate the specific StarLamp twist?
                // Replicating StarLamp "Twist":
                addTri(consumer, pose, normal, v1, v2, t3, r, g, b, a, light);
                addTri(consumer, pose, normal, v2, v3, t1, r, g, b, a, light);
                addTri(consumer, pose, normal, v3, v1, t2, r, g, b, a, light);
            } else {
                // Standard Mode
                addTri(consumer, pose, normal, v1, v2, v3, r, g, b, a, light);
            }
        }
    }

    /**
     * Renders a cylinder where the radius follows a power curve (Trumpet shape).
     * <br> Used by: <b>BloodNovaRenderer</b> (Jets), <b>EnergyVortexParticle</b>.
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

            // Apply Power Curve to radius interpolation to create the "Flare"
            float t1 = (float) Math.pow(progress1, shapePower);
            float t2 = (float) Math.pow(progress2, shapePower);

            float r1 = Mth.lerp(t1, radBase, radTop);
            float r2 = Mth.lerp(t2, radBase, radTop);

            // Twist is linear along height
            float twist1 = rotation + (progress1 * totalTwist);
            float twist2 = rotation + (progress2 * totalTwist);

            float a1 = Mth.lerp(progress1, alphaBase, alphaTop);
            float a2 = Mth.lerp(progress2, alphaBase, alphaTop);

            for (int j = 0; j < segments; j++) {
                float ang1 = (float) j / segments * Mth.TWO_PI;
                float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;

                float x1a = Mth.cos(ang1 + twist1) * r1; float z1a = Mth.sin(ang1 + twist1) * r1;
                float x1b = Mth.cos(ang2 + twist1) * r1; float z1b = Mth.sin(ang2 + twist1) * r1;
                float x2a = Mth.cos(ang1 + twist2) * r2; float z2a = Mth.sin(ang1 + twist2) * r2;
                float x2b = Mth.cos(ang2 + twist2) * r2; float z2b = Mth.sin(ang2 + twist2) * r2;

                vertex(consumer, pose, normal, x1a, y1, z1a, new float[]{r,g,b,a1}, light, 0, 0, 0, 0, 0);
                vertex(consumer, pose, null, x1b, y1, z1b, new float[]{r,g,b,a1}, light, 1, 0, 0, 0, 0);
                vertex(consumer, pose, null, x2b, y2, z2b, new float[]{r,g,b,a2}, light, 1, 1, 0, 0, 0);
                vertex(consumer, pose, null, x2a, y2, z2a, new float[]{r,g,b,a2}, light, 0, 1, 0, 0, 0);
            }
        }
    }

    // =========================================================================================
    //                                      BILLBOARDS
    // =========================================================================================

    /**
     * Renders a camera-facing quad at a specific position.
     * Used by: <b>BlackHoleParticle</b> (Sparkles).
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
            v.rotate(camRot); // Rotate to face camera
            v.add(x, y, z);   // Translate to position

            // Standard quad UVs
            float u = (v.x() > x) ? 1 : 0; // Simple UV mapping based on corner
            float v_uv = (v.y() > y) ? 0 : 1;

            vertex(consumer, pose, null, v.x(), v.y(), v.z(), new float[]{r,g,b,a}, light, u, v_uv);
        }
    }

    // =========================================================================================
    //                                      CRESCENT / SLASH
    // =========================================================================================

    /**
     * Renders the crescent slash shape.
     * Used by: <b>BloodSlashEntityRenderer</b>, <b>SpecialSlashRenderer</b>.
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

            vertex(consumer, pose, normal, xInner, yInner, 0, new float[]{r,g,b,alpha}, light, 0, 0);
            vertex(consumer, pose, normal, xOuter, yOuter, 0, new float[]{r,g,b,alpha}, light, 1, 1);
        }
    }

    // --- INTERNAL GEOMETRY HELPERS ---

    private static void sphereVertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                     float rad, double theta, double phi,
                                     float r, float g, float b, float a, int light) {
        float x = (float) (rad * Math.cos(theta) * Math.cos(phi));
        float y = (float) (rad * Math.sin(theta));
        float z = (float) (rad * Math.cos(theta) * Math.sin(phi));
        float u = (float) (phi / (2 * Math.PI));
        float v = (float) ((theta + Math.PI / 2) / Math.PI);

        // Normalize for normals
        float nx = x/rad; float ny = y/rad; float nz = z/rad;
        vertex(consumer, pose, normal, x, y, z, new float[]{r,g,b,a}, light, u, v, nx, ny, nz);
    }

    private static void addTri(VertexConsumer c, Matrix4f p, Matrix3f n, Vec3 v1, Vec3 v2, Vec3 v3, float r, float g, float b, float a, int light) {
        // Compute Face Normal
        Vec3 edge1 = v2.subtract(v1);
        Vec3 edge2 = v3.subtract(v1);
        Vec3 norm = edge1.cross(edge2).normalize();

        vertex(c, p, n, (float)v1.x, (float)v1.y, (float)v1.z, new float[]{r,g,b,a}, light, 0, 0, (float)norm.x, (float)norm.y, (float)norm.z);
        vertex(c, p, n, (float)v2.x, (float)v2.y, (float)v2.z, new float[]{r,g,b,a}, light, 1, 0, (float)norm.x, (float)norm.y, (float)norm.z);
        vertex(c, p, n, (float)v3.x, (float)v3.y, (float)v3.z, new float[]{r,g,b,a}, light, 0.5f, 1, (float)norm.x, (float)norm.y, (float)norm.z);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z, float[] color, int light, float u, float v) {
        vertex(consumer, pose, normal, x, y, z, color, light, u, v, 0, 1, 0);
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

    // --- ICOSAHEDRON MATH ---
    private static class Vec3 {
        public float x, y, z;
        public Vec3(float x, float y, float z) { this.x=x; this.y=y; this.z=z; }
        public Vec3 subtract(Vec3 v) { return new Vec3(x - v.x, y - v.y, z - v.z); }
        public Vec3 cross(Vec3 v) { return new Vec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x); }
        public Vec3 normalize() { float l = (float)Math.sqrt(x*x + y*y + z*z); return new Vec3(x/l, y/l, z/l); }
    }

    private static Vec3[] getIcosahedronVertices(float scale) {
        float t = (float)((1.0 + Math.sqrt(5.0))/2.0);
        float s = 1.0f / (float)Math.sqrt(1 + t*t) * scale;
        t *= (s / scale) * scale; // Keep proportion
        return new Vec3[]{
                new Vec3(-s,t,0), new Vec3(s,t,0), new Vec3(-s,-t,0), new Vec3(s,-t,0),
                new Vec3(0,-s,t), new Vec3(0,s,t), new Vec3(0,-s,-t), new Vec3(0,s,-t),
                new Vec3(t,0,-s), new Vec3(t,0,s), new Vec3(-t,0,-s), new Vec3(-t,0,s)
        };
    }

    private static int[][] getIcosahedronFaces() {
        return new int[][]{
                {0,11,5},{0,5,1},{0,1,7},{0,7,10},{0,10,11},
                {1,5,9},{5,11,4},{11,10,2},{10,7,6},{7,1,8},
                {3,9,4},{3,4,2},{3,2,6},{3,6,8},{3,8,9},
                {4,9,5},{2,4,11},{6,2,10},{8,6,7},{9,8,1}
        };
    }

    @FunctionalInterface
    public interface RadiusModifier {
        float apply(double theta, double phi);
    }

    /**
     * Renders a sphere where the radius is determined dynamically per vertex.
     * Used by: <b>BlackHoleParticle</b> (Gravitational Lens).
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

                // Calculate vertices with modified radius
                procSphereVert(consumer, pose, normal, theta1, phi1, radiusFunc, r, g, b, a, light);
                procSphereVert(consumer, pose, normal, theta2, phi1, radiusFunc, r, g, b, a, light);
                procSphereVert(consumer, pose, normal, theta2, phi2, radiusFunc, r, g, b, a, light);
                procSphereVert(consumer, pose, normal, theta1, phi2, radiusFunc, r, g, b, a, light);
            }
        }
    }

    private static void procSphereVert(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                       double theta, double phi, RadiusModifier func,
                                       float r, float g, float b, float a, int light) {

        float rad = func.apply(theta, phi); // Calculate dynamic radius

        float x = (float) (rad * Math.cos(theta) * Math.cos(phi));
        float y = (float) (rad * Math.sin(theta));
        float z = (float) (rad * Math.cos(theta) * Math.sin(phi));

        // Simplified normal (pointing out from center)
        // Note: For extreme distortion, you'd calculate true normals via cross-product,
        // but for particles/energy, center-normals are visually sufficient and faster.
        float nx = x; float ny = y; float nz = z;
        float len = Mth.sqrt(nx*nx + ny*ny + nz*nz);
        if (len > 0) { nx /= len; ny /= len; nz /= len; }

        float u = (float) (phi / (2 * Math.PI));
        float v = (float) ((theta + Math.PI / 2) / Math.PI);

        vertex(consumer, pose, normal, x, y, z, new float[]{r, g, b, a}, light, u, v, nx, ny, nz);
    }

    // =========================================================================================
    //                                      SPHERES
    // =========================================================================================



    // =========================================================================================
    //                                      DISKS & RINGS
    // =========================================================================================

    public static void renderDisk(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                  float innerRad, float outerRad, int segments, float rotation,
                                  float[] cInner, float[] cOuter, int light) {

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

    // =========================================================================================
    //                                JETS & CYLINDERS
    // =========================================================================================

    public static void renderTaperedCylinder(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                             float height, float radBase, float radTop,
                                             float rotation, float twist,
                                             int layers, int segments,
                                             float r, float g, float b, float alphaBase, float alphaTop, int light) {

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

                float x1a = Mth.cos(ang1 + twist1) * r1; float z1a = Mth.sin(ang1 + twist1) * r1;
                float x1b = Mth.cos(ang2 + twist1) * r1; float z1b = Mth.sin(ang2 + twist1) * r1;
                float x2a = Mth.cos(ang1 + twist2) * r2; float z2a = Mth.sin(ang1 + twist2) * r2;
                float x2b = Mth.cos(ang2 + twist2) * r2; float z2b = Mth.sin(ang2 + twist2) * r2;

                vertex(consumer, pose, normal, x1a, y1, z1a, new float[]{r,g,b,a1}, light);
                vertex(consumer, pose, null, x1b, y1, z1b, new float[]{r,g,b,a1}, light);
                vertex(consumer, pose, null, x2b, y2, z2b, new float[]{r,g,b,a2}, light);
                vertex(consumer, pose, null, x2a, y2, z2a, new float[]{r,g,b,a2}, light);
            }
        }
    }

    // =========================================================================================
    //                                CRESCENT / SLASH
    // =========================================================================================


    /**
     * Renders a crescent with a color gradient from the inner edge to the outer edge.
     * Useful for slashes with glowing borders.
     *
     * @param cInner Array {R, G, B, A} for the inner vertices.
     * @param cOuter Array {R, G, B, A} for the outer vertices.
     */
    public static void renderCrescentGradient(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                              float radius, float maxThickness, float arcAngle,
                                              float[] cInner, float[] cOuter, int light) {
        int segments = 20;

        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            float angle = -arcAngle / 2.0f + t * arcAngle;

            // Shape factor: 0 at ends, 1 in middle
            float shapeFactor = Mth.sin(t * (float) Math.PI);
            float currentThickness = maxThickness * shapeFactor;

            // Apply shape factor to Alpha so tips fade out
            float alphaInner = cInner[3] * shapeFactor;
            float alphaOuter = cOuter[3] * shapeFactor;

            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);

            float xInner = (radius - currentThickness / 2) * sin;
            float yInner = (radius - currentThickness / 2) * cos;
            float xOuter = (radius + currentThickness / 2) * sin;
            float yOuter = (radius + currentThickness / 2) * cos;

            // Inner Vertex
            vertex(consumer, pose, normal, xInner, yInner, 0,
                    new float[]{cInner[0], cInner[1], cInner[2], alphaInner}, light, 0, 0, 0, 0, 1);

            // Outer Vertex
            vertex(consumer, pose, normal, xOuter, yOuter, 0,
                    new float[]{cOuter[0], cOuter[1], cOuter[2], alphaOuter}, light, 1, 1, 0, 0, 1);
        }
    }

    // =========================================================================================
    //                                      VOXELS
    // =========================================================================================

    public static void renderPixel(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                   float x, float y, float z, float size,
                                   float r, float g, float b, float a, int light) {
        float[] color = {r, g, b, a};
        vertex(consumer, pose, normal, x, y, z, color, light);
        vertex(consumer, pose, normal, x, y + size, z, color, light);
        vertex(consumer, pose, normal, x + size, y + size, z, color, light);
        vertex(consumer, pose, normal, x + size, y, z, color, light);
    }

    // --- INTERNAL HELPERS ---

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

    // =========================================================================================
    //                                STAR LAMP SPECIFIC
    // =========================================================================================

    /**
     * Renders a stellated icosahedron with double-sided faces for the Star Lamp.
     * Replicates the original manual logic: Base Vertices -> Tip Vertex.
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

            // Render the 3 facets of the spike (Double Sided)
            addDoubleSidedTri(consumer, pose, normal, v1, v2, t3, r, g, b, a, light);
            addDoubleSidedTri(consumer, pose, normal, v2, v3, t1, r, g, b, a, light);
            addDoubleSidedTri(consumer, pose, normal, v3, v1, t2, r, g, b, a, light);

            // Render the base face (inverted/inside) to close the shape
            addDoubleSidedTri(consumer, pose, normal, v1, v2, v3, r, g, b, a, light);
        }
    }

    private static void addDoubleSidedTri(VertexConsumer c, Matrix4f p, Matrix3f n, Vec3 v1, Vec3 v2, Vec3 v3, float r, float g, float b, float a, int light) {
        // Front Face
        addTri(c, p, n, v1, v2, v3, r, g, b, a, light);
        // Back Face (Swap v1 and v3 to flip normal)
        addTri(c, p, n, v3, v2, v1, r, g, b, a, light);
    }
}