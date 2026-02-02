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

    // =========================================================================================
    //                                JETS & CYLINDERS
    // =========================================================================================

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

                float cos1a = Mth.cos(ang1 + twist1); float sin1a = Mth.sin(ang1 + twist1);
                float cos1b = Mth.cos(ang2 + twist1); float sin1b = Mth.sin(ang2 + twist1);
                float cos2a = Mth.cos(ang1 + twist2); float sin2a = Mth.sin(ang1 + twist2);
                float cos2b = Mth.cos(ang2 + twist2); float sin2b = Mth.sin(ang2 + twist2);

                float x1a = cos1a * r1; float z1a = sin1a * r1;
                float x1b = cos1b * r1; float z1b = sin1b * r1;
                float x2a = cos2a * r2; float z2a = sin2a * r2;
                float x2b = cos2b * r2; float z2b = sin2b * r2;

                // FIX: Calculate Smooth Normals (Pointing Outward)
                // If normals are UP (0,1,0), shaders make the side walls look dark/flat.
                // We use the cosine/sine directly as the normal X/Z components.

                float n1ax = cos1a; float n1az = sin1a;
                float n1bx = cos1b; float n1bz = sin1b;
                float n2ax = cos2a; float n2az = sin2a;
                float n2bx = cos2b; float n2bz = sin2b;

                // Pass normals to the vertex consumer
                vertex(consumer, pose, normal, x1a, y1, z1a, new float[]{r,g,b,a1}, light, 0, 0, n1ax, 0, n1az);
                vertex(consumer, pose, null, x1b, y1, z1b, new float[]{r,g,b,a1}, light, 1, 0, n1bx, 0, n1bz);
                vertex(consumer, pose, null, x2b, y2, z2b, new float[]{r,g,b,a2}, light, 1, 1, n2bx, 0, n2bz);
                vertex(consumer, pose, null, x2a, y2, z2a, new float[]{r,g,b,a2}, light, 0, 1, n2ax, 0, n2az);
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

    // =========================================================================================
    //                                  PROCEDURAL RINGS (New)
    // =========================================================================================

    @FunctionalInterface
    public interface RingNoiseProvider {
        float getNoise(double angle);
    }

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

            float x1_in = cos1 * rInner1; float z1_in = sin1 * rInner1;
            float x1_out = cos1 * rOuter1; float z1_out = sin1 * rOuter1;
            float x2_out = cos2 * rOuter2; float z2_out = sin2 * rOuter2;
            float x2_in = cos2 * rInner2; float z2_in = sin2 * rInner2;

            float u1_in = (x1_in / maxR + 1) * 0.5f; float v1_in = (z1_in / maxR + 1) * 0.5f;
            float u1_out = (x1_out / maxR + 1) * 0.5f; float v1_out = (z1_out / maxR + 1) * 0.5f;
            float u2_out = (x2_out / maxR + 1) * 0.5f; float v2_out = (z2_out / maxR + 1) * 0.5f;
            float u2_in = (x2_in / maxR + 1) * 0.5f; float v2_in = (z2_in / maxR + 1) * 0.5f;

            vertex(consumer, pose, normal, x1_in, 0, z1_in, cInner, light, u1_in, v1_in, 0, 1, 0);
            vertex(consumer, pose, normal, x1_out, 0, z1_out, cOuter, light, u1_out, v1_out, 0, 1, 0);
            vertex(consumer, pose, normal, x2_out, 0, z2_out, cOuter, light, u2_out, v2_out, 0, 1, 0);
            vertex(consumer, pose, normal, x2_in, 0, z2_in, cInner, light, u2_in, v2_in, 0, 1, 0);
        }
    }
    // =========================================================================================
    //                            SIMPLE SHADER-PROOF RENDERING
    // =========================================================================================

    // =========================================================================================
    //                            SMOOTH SHADING CYLINDER (Shader Compatible)
    // =========================================================================================

    /**
     * Renders a Cylinder using POSITION_COLOR only.
     * <br>
     * <b>Spike Fix:</b> If shaders are active, this method multiplies segments by 6.
     * Since we cannot use Smooth Normals with POSITION_COLOR, we use high-density geometry
     * to force the shader to render a smooth curve.
     */
    public static void renderSimpleGradientCylinder(VertexConsumer consumer, Matrix4f pose,
                                                    float height, float radBase, float radTop,
                                                    int segments, float rotation,
                                                    float r, float g, float b, float alphaBase, float alphaTop) {

        // 1. BRUTE FORCE SMOOTHNESS
        if (ShaderUtils.areShadersActive()) {
            segments *= 12; // 32 -> 192 segments. Smooths out the "face normals".

            // 2. ALPHA COMPENSATION
            // Shaders often render untextured transparency too faintly. We boost it.
            alphaBase = Math.min(1.0f, alphaBase * 1.5f);
            if (alphaTop > 0) alphaTop = Math.min(1.0f, alphaTop * 1.5f);
        }

        for (int j = 0; j < segments; j++) {
            float ang1 = (float) j / segments * Mth.TWO_PI;
            float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;

            // Apply Rotation
            float a1 = ang1 + rotation;
            float a2 = ang2 + rotation;

            float cos1 = Mth.cos(a1); float sin1 = Mth.sin(a1);
            float cos2 = Mth.cos(a2); float sin2 = Mth.sin(a2);

            // Positions
            float x1_base = cos1 * radBase; float z1_base = sin1 * radBase;
            float x2_base = cos2 * radBase; float z2_base = sin2 * radBase;
            float x1_top  = cos1 * radTop;  float z1_top  = sin1 * radTop;
            float x2_top  = cos2 * radTop;  float z2_top  = sin2 * radTop;

            // Render Quad (Double Sided to ensure visibility)
            // Note: We DO NOT pass 'normal' here. We only pass Pos + Color.

            // Side A (Outside)
            simpleVertex(consumer, pose, x1_base, 0, z1_base, r, g, b, alphaBase);
            simpleVertex(consumer, pose, x1_top, height, z1_top, r, g, b, alphaTop);
            simpleVertex(consumer, pose, x2_top, height, z2_top, r, g, b, alphaTop);
            simpleVertex(consumer, pose, x2_base, 0, z2_base, r, g, b, alphaBase);

            // Side B (Inside - to fix holes)
            simpleVertex(consumer, pose, x2_base, 0, z2_base, r, g, b, alphaBase);
            simpleVertex(consumer, pose, x2_top, height, z2_top, r, g, b, alphaTop);
            simpleVertex(consumer, pose, x1_top, height, z1_top, r, g, b, alphaTop);
            simpleVertex(consumer, pose, x1_base, 0, z1_base, r, g, b, alphaBase);
        }
    }

    // INTERNAL HELPER (Full Vertex Format)
    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z,
                               float r, float g, float b, float a,
                               float u, float v,
                               float nx, float ny, float nz) {
        consumer.vertex(pose, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880) // Full Brightness
                .normal(normal, nx, ny, nz) // TRANSFORM NORMAL
                .endVertex();
    }

    /**
     * Renders a noise-distorted ring using POSITION_COLOR.
     * Safe for shaders (no spike artifacts).
     */
    public static void renderSimpleProceduralRing(VertexConsumer consumer, Matrix4f pose,
                                                  float baseRadius, float width, int segments, float rotation,
                                                  float r, float g, float b, float alphaInner, float alphaOuter,
                                                  RingNoiseProvider noiseFunc, float innerNoiseStrength, float outerNoiseStrength) {

        if (ShaderUtils.areShadersActive()) {
            segments *= 12;
            // Boost alpha for rings too
            alphaOuter = Math.min(1.0f, alphaOuter * 1.5f);
        }

        for (int j = 0; j < segments; j++) {
            float ang1 = (float) j / segments * Mth.TWO_PI;
            float ang2 = (float) (j + 1) / segments * Mth.TWO_PI;

            float a1 = ang1 + rotation;
            float a2 = ang2 + rotation;

            float cos1 = Mth.cos(a1); float sin1 = Mth.sin(a1);
            float cos2 = Mth.cos(a2); float sin2 = Mth.sin(a2);

            float n1 = noiseFunc.getNoise(ang1);
            float n2 = noiseFunc.getNoise(ang2);

            float rInner1 = (baseRadius - width) + (n1 * innerNoiseStrength);
            float rOuter1 = baseRadius + (n1 * outerNoiseStrength);
            float rInner2 = (baseRadius - width) + (n2 * innerNoiseStrength);
            float rOuter2 = baseRadius + (n2 * outerNoiseStrength);

            float x1_in = cos1 * rInner1; float z1_in = sin1 * rInner1;
            float x1_out = cos1 * rOuter1; float z1_out = sin1 * rOuter1;
            float x2_out = cos2 * rOuter2; float z2_out = sin2 * rOuter2;
            float x2_in = cos2 * rInner2; float z2_in = sin2 * rInner2;

            simpleVertex(consumer, pose, x1_in, 0, z1_in, r, g, b, alphaInner);
            simpleVertex(consumer, pose, x1_out, 0, z1_out, r, g, b, alphaOuter);
            simpleVertex(consumer, pose, x2_out, 0, z2_out, r, g, b, alphaOuter);
            simpleVertex(consumer, pose, x2_in, 0, z2_in, r, g, b, alphaInner);
        }
    }

    // INTERNAL HELPER: Strictly Pos + Color
    private static void simpleVertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, float r, float g, float b, float a) {
        consumer.vertex(pose, x, y, z).color(r, g, b, a).endVertex();
    }

    // =========================================================================================
    //                                      NOISE MATH
    // =========================================================================================

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

    /**
     * Renders a pulsating, spinning "Atlas" Heart (Complex geometric artifact).
     * Consists of 3 layers of Stellated Icosahedrons rotating against each other.
     *
     * @param gameTime Used for rotation math.
     * @param partialTick For smooth animation.
     */
    // In RenderHelper.java

    public static void renderAtlasHeart(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                        long gameTime, float partialTick,
                                        float rBase, float gBase, float bBase, // NEW: Base Color
                                        float alpha, int light) {

        float time = gameTime + partialTick;
        float pulse = 1.0f + Mth.sin(time * 0.1f) * 0.1f;

        // LAYER 1: Core (Small, fast spin)
        renderRotatedLayer(consumer, pose, normal,
                0.25f * pulse, 0.35f * pulse,
                time * 3.0f,
                rBase, gBase, bBase, // Pass base color
                1.0f, light);

        // LAYER 2: Shell (Medium, reverse spin)
        renderRotatedLayer(consumer, pose, normal,
                0.4f * pulse, 0.55f * pulse,
                -time * 1.5f,
                rBase, gBase, bBase, // Pass base color
                0.7f, light);

        // LAYER 3: Outer Field (Large, slow spin)
        renderRotatedLayer(consumer, pose, normal,
                0.6f * pulse, 0.2f * pulse,
                time * 0.5f,
                rBase, gBase, bBase, // Pass base color
                0.4f, light);
    }

    private static void renderRotatedLayer(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                           float baseScale, float tipScale, float rotDegrees,
                                           float r, float g, float b, // Base Color input
                                           float alpha, int light) {

        Vec3[] baseVerts = getIcosahedronVertices(baseScale);
        Vec3[] tipVerts = (baseScale == tipScale) ? baseVerts : getIcosahedronVertices(tipScale);
        int[][] faces = getIcosahedronFaces();

        // Rotation
        Quaternionf q = new Quaternionf().rotateY(Mth.DEG_TO_RAD * rotDegrees);
        Quaternionf q2 = new Quaternionf().rotateX(Mth.DEG_TO_RAD * (rotDegrees * 0.5f));
        q.mul(q2);

        // DYNAMIC PALETTE GENERATION
        // We create variations based on the input color so it's not monotone.
        // Color 1: The Base Color (e.g. Red)
        // Color 2: Shifted slightly towards Yellow/White (Highlight)
        // Color 3: Shifted slightly towards Purple/Dark (Shadow)

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

    // =========================================================================================
    //                                  MANUAL DIAMOND (THE CHEAT)
    // =========================================================================================

    // =========================================================================================
    //                                  MANUAL DIAMOND (LAYERED & DOUBLE SIDED)
    // =========================================================================================

    // =========================================================================================
    //                                  MANUAL DIAMOND (SOLID / OPAQUE)
    // =========================================================================================

    // =========================================================================================
    //                                  MANUAL DIAMOND (DOUBLE SIDED FIX)
    // =========================================================================================

    public static void renderFloatingDiamond(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                             float x, float y, float z,
                                             float w, float h,
                                             float r, float g, float b, float a,
                                             int light) {

        // Vertices
        Vec3 vTop = new Vec3(x, y + h, z);
        Vec3 vBot = new Vec3(x, y - h, z);

        Vec3 vEast = new Vec3(x + w, y, z);
        Vec3 vSouth = new Vec3(x, y, z + w);
        Vec3 vWest = new Vec3(x - w, y, z);
        Vec3 vNorth = new Vec3(x, y, z - w);

        // --- UPPER PYRAMID ---
        addDoubleSidedTri(consumer, pose, normal, vEast, vTop, vSouth, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vSouth, vTop, vWest, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vWest, vTop, vNorth, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vNorth, vTop, vEast, r, g, b, a, light);

        // --- LOWER PYRAMID ---
        addDoubleSidedTri(consumer, pose, normal, vEast, vSouth, vBot, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vSouth, vWest, vBot, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vWest, vNorth, vBot, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, vNorth, vEast, vBot, r, g, b, a, light);
    }

    // =========================================================================================
    //                                  GEOMETRIC GEMS (ROBUST)
    // =========================================================================================

    /**
     * Renders a classic 8-sided Gem (Octahedron).
     * Guaranteed to be visible from all angles (Double Sided).
     */
    public static void renderOctahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                        float size,
                                        float r, float g, float b, float a, int light) {

        // 6 Vertices of an Octahedron
        Vec3 top = new Vec3(0, size, 0);
        Vec3 bot = new Vec3(0, -size, 0);

        Vec3 front = new Vec3(0, 0, size);
        Vec3 right = new Vec3(size, 0, 0);
        Vec3 back  = new Vec3(0, 0, -size);
        Vec3 left  = new Vec3(-size, 0, 0);

        // --- TOP PYRAMID ---
        addDoubleSidedTri(consumer, pose, normal, top, front, right, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, top, right, back, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, top, back, left, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, top, left, front, r, g, b, a, light);

        // --- BOTTOM PYRAMID ---
        addDoubleSidedTri(consumer, pose, normal, bot, right, front, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, bot, back, right, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, bot, left, back, r, g, b, a, light);
        addDoubleSidedTri(consumer, pose, normal, bot, front, left, r, g, b, a, light);
    }



    // =========================================================================================
    //                            THE DODECAHEDRON (COMPLETED)
    // =========================================================================================

    public static void renderDodecahedron(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                          float size,
                                          float r, float g, float b, float a, int light) {

        // Golden Ratio Constants
        float phi = 1.618034f;
        float invPhi = 1.0f / phi;

        // Scale (Dodecahedrons look huge compared to Octahedrons, so we scale down slightly)
        float s = size * 0.5f;

        // --- VERTEX GENERATION ---

        // Group A: Cube Corners (±1, ±1, ±1)
        // 0:(+,+,+), 1:(+,+,-), 2:(+,-,+), 3:(+,-,-)
        // 4:(-,+,+), 5:(-,+,-), 6:(-,-,+), 7:(-,-,-)
        Vec3[] aV = {
                new Vec3(s, s, s),   new Vec3(s, s, -s),   new Vec3(s, -s, s),   new Vec3(s, -s, -s),
                new Vec3(-s, s, s),  new Vec3(-s, s, -s),  new Vec3(-s, -s, s),  new Vec3(-s, -s, -s)
        };

        // Group B: YZ Rect (0, ±invPhi, ±phi)
        // 0:(Top-Front), 1:(Top-Back), 2:(Bot-Front), 3:(Bot-Back)
        Vec3[] bV = {
                new Vec3(0, s*invPhi, s*phi),  new Vec3(0, s*invPhi, -s*phi),
                new Vec3(0, -s*invPhi, s*phi), new Vec3(0, -s*invPhi, -s*phi)
        };

        // Group C: XY Rect (±invPhi, ±phi, 0)
        // 0:(Right-Top), 1:(Right-Bot), 2:(Left-Top), 3:(Left-Bot)
        Vec3[] cV = {
                new Vec3(s*invPhi, s*phi, 0),  new Vec3(s*invPhi, -s*phi, 0),
                new Vec3(-s*invPhi, s*phi, 0), new Vec3(-s*invPhi, -s*phi, 0)
        };

        // Group D: XZ Rect (±phi, 0, ±invPhi)
        // 0:(Right-Front), 1:(Right-Back), 2:(Left-Front), 3:(Left-Back)
        Vec3[] dV = {
                new Vec3(s*phi, 0, s*invPhi),  new Vec3(s*phi, 0, -s*invPhi),
                new Vec3(-s*phi, 0, s*invPhi), new Vec3(-s*phi, 0, -s*invPhi)
        };

        // --- FACE RENDERING (12 Pentagons) ---
        // We render each pentagon Double-Sided so it never disappears.

        // 1. Front (+Z, +X side)
        renderPentagon(consumer, pose, normal, bV[0], aV[0], dV[0], aV[2], bV[2], r, g, b, a, light);
        // 2. Front (+Z, -X side)
        renderPentagon(consumer, pose, normal, bV[2], aV[6], dV[2], aV[4], bV[0], r, g, b, a, light);

        // 3. Back (-Z, +X side)
        renderPentagon(consumer, pose, normal, bV[1], bV[3], aV[3], dV[1], aV[1], r, g, b, a, light);
        // 4. Back (-Z, -X side)
        renderPentagon(consumer, pose, normal, bV[3], bV[1], aV[5], dV[3], aV[7], r, g, b, a, light);

        // 5. Top (+Y, Front-ish)
        renderPentagon(consumer, pose, normal, cV[0], aV[0], bV[0], aV[4], cV[2], r, g, b, a, light);
        // 6. Top (+Y, Back-ish)
        renderPentagon(consumer, pose, normal, cV[2], aV[5], bV[1], aV[1], cV[0], r, g, b, a, light);

        // 7. Bottom (-Y, Front-ish)
        renderPentagon(consumer, pose, normal, cV[1], aV[2], bV[2], aV[6], cV[3], r, g, b, a, light);
        // 8. Bottom (-Y, Back-ish)
        renderPentagon(consumer, pose, normal, cV[3], aV[7], bV[3], aV[3], cV[1], r, g, b, a, light);

        // 9. Right (+X, Top-ish)
        renderPentagon(consumer, pose, normal, dV[0], aV[0], cV[0], aV[1], dV[1], r, g, b, a, light);
        // 10. Right (+X, Bot-ish)
        renderPentagon(consumer, pose, normal, dV[1], aV[3], cV[1], aV[2], dV[0], r, g, b, a, light);

        // 11. Left (-X, Top-ish)
        renderPentagon(consumer, pose, normal, dV[2], dV[3], aV[5], cV[2], aV[4], r, g, b, a, light);
        // 12. Left (-X, Bot-ish)
        renderPentagon(consumer, pose, normal, dV[3], dV[2], aV[6], cV[3], aV[7], r, g, b, a, light);
    }

    /**
     * Helper to draw a Pentagon as a Triangle Fan (3 Triangles).
     * Draws Double-Sided (6 calls total) to prevent culling.
     */
    private static void renderPentagon(VertexConsumer c, Matrix4f p, Matrix3f n,
                                       Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, Vec3 v5,
                                       float r, float g, float b, float a, int light) {
        // Triangle 1 (v1-v2-v3)
        addDoubleSidedTri(c, p, n, v1, v2, v3, r, g, b, a, light);
        // Triangle 2 (v1-v3-v4)
        addDoubleSidedTri(c, p, n, v1, v3, v4, r, g, b, a, light);
        // Triangle 3 (v1-v4-v5)
        addDoubleSidedTri(c, p, n, v1, v4, v5, r, g, b, a, light);
    }





}