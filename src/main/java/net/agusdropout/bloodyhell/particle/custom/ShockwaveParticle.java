package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShockwaveParticle extends TextureSheetParticle {

    protected ShockwaveParticle(ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);

        this.xd = xDir;
        this.yd = yDir;
        this.zd = zDir;

        this.lifetime = 10;
        this.quadSize = 0.5f;
        this.alpha = 1.0f;

        this.pickSprite(spriteSet);
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.quadSize += 0.35f; // Expansión
            this.alpha = 1.0f - ((float)this.age / (float)this.lifetime); // Fade out
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        // --- CÁLCULO DE ROTACIÓN BASE ---
        Quaternionf baseRotation = new Quaternionf();

        double horizontalDist = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
        float yRot = (float) (Mth.atan2(this.xd, this.zd));
        float xRot = (float) (Mth.atan2(this.yd, horizontalDist));

        // Alineamos perpendicular al movimiento
        baseRotation.rotateY(yRot);
        // IMPORTANTE: Volví a poner PI/2 (90 grados) porque es lo estándar para que sea "transversal"
        // Si con esto queda de canto, cámbialo a Math.PI, pero PI/2 es lo geométricamente correcto para "cortar" la lanza.
        baseRotation.rotateX(-xRot + (float)(Math.PI));

        // Definimos los 4 vértices del cuadrado
        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        float size = this.getQuadSize(partialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);

        // --- DOBLE RENDERIZADO (Cara A y Cara B) ---
        for (int i = 0; i < 2; i++) {

            // Copiamos la rotación base para no modificar la original
            Quaternionf currentRot = new Quaternionf(baseRotation);

            // Si es la segunda pasada (i=1), rotamos 180 grados para hacer la "espalda"
            if (i == 1) {
                currentRot.rotateY((float) Math.PI);
            }

            // Dibujamos los 4 vértices
            for (Vector3f baseVertex : vertices) {
                // Copia del vértice para transformar
                Vector3f vertex = new Vector3f(baseVertex);

                vertex.rotate(currentRot); // Rotar
                vertex.mul(size);          // Escalar
                vertex.add(x, y, z);       // Trasladar

                // Escribir en el buffer
                // Nota: Usamos las UVs mapeadas a los vértices correspondientes
                // (Aquí simplificado, el orden del loop coincide con el orden de vértices 0-3)
                float u = (baseVertex.x() < 0) ? u1 : u0; // Mapeo simple basado en posición X local
                float v = (baseVertex.y() < 0) ? v1 : v0; // Mapeo simple basado en posición Y local

                // NOTA: Para texturas complejas, el mapeo UV manual es mejor, pero este truco funciona para quads centrados.
                // Si la textura se ve espejada en un lado, es normal en este método simple.
            }

            // Escribir manualmente al buffer para tener control total
            // Vértice 0 (Abajo-Izq)
            Vector3f v_0 = transform(vertices[0], currentRot, size, x, y, z);
            buffer.vertex(v_0.x(), v_0.y(), v_0.z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

            // Vértice 1 (Arriba-Izq)
            Vector3f v_1 = transform(vertices[1], currentRot, size, x, y, z);
            buffer.vertex(v_1.x(), v_1.y(), v_1.z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

            // Vértice 2 (Arriba-Der)
            Vector3f v_2 = transform(vertices[2], currentRot, size, x, y, z);
            buffer.vertex(v_2.x(), v_2.y(), v_2.z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

            // Vértice 3 (Abajo-Der)
            Vector3f v_3 = transform(vertices[3], currentRot, size, x, y, z);
            buffer.vertex(v_3.x(), v_3.y(), v_3.z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        }
    }

    // Método auxiliar para limpiar el código de renderizado
    private Vector3f transform(Vector3f original, Quaternionf rot, float scale, float x, float y, float z) {
        Vector3f v = new Vector3f(original);
        v.rotate(rot);
        v.mul(scale);
        v.add(x, y, z);
        return v;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new ShockwaveParticle(level, x, y, z, xd, yd, zd, this.spriteSet);
        }
    }
}