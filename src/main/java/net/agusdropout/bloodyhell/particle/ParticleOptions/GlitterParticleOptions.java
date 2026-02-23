package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3f;

import java.util.Locale;

public class GlitterParticleOptions implements ParticleOptions {


    private final Vector3f color;
    private final float size;
    private final boolean jitter;
    private final int lifetime;
    private final boolean whiteCore;


    public static final Codec<GlitterParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.x()),
                    Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.y()),
                    Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.z()),
                    Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
                    Codec.BOOL.fieldOf("jitter").forGetter(d -> d.jitter),
                    Codec.INT.fieldOf("lifetime").forGetter(d -> d.lifetime),
                    Codec.BOOL.fieldOf("whiteCore").forGetter(d -> d.whiteCore)
            ).apply(instance, GlitterParticleOptions::new)
    );


    public static final Deserializer<GlitterParticleOptions> DESERIALIZER = new Deserializer<GlitterParticleOptions>() {
        @Override
        public GlitterParticleOptions fromCommand(ParticleType<GlitterParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            boolean jitter = reader.readBoolean();
            reader.expect(' ');
            int lifetime = reader.readInt();
            reader.expect(' ');
            boolean whiteCore = reader.readBoolean();
            return new GlitterParticleOptions(r, g, b, size, jitter, lifetime, whiteCore);
        }

        @Override
        public GlitterParticleOptions fromNetwork(ParticleType<GlitterParticleOptions> type, FriendlyByteBuf buffer) {
            return new GlitterParticleOptions(
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readBoolean(),
                    buffer.readInt(),
                    buffer.readBoolean()
            );
        }
    };


    public GlitterParticleOptions(float r, float g, float b, float size, boolean jitter, int lifetime, boolean whiteCore) {
        this.color = new Vector3f(r, g, b);
        this.size = size;
        this.jitter = jitter;
        this.lifetime = lifetime;
        this.whiteCore = whiteCore;
    }

    public GlitterParticleOptions(Vector3f color, float size, boolean jitter, int lifetime, boolean whiteCore) {
        this.color = color;
        this.size = size;
        this.jitter = jitter;
        this.lifetime = lifetime;
        this.whiteCore = whiteCore;
    }


    public GlitterParticleOptions(Vector3f color, float size, boolean jitter, int lifetime) {
        this.color = color;
        this.size = size;
        this.jitter = jitter;
        this.lifetime = lifetime;
        this.whiteCore = false;
    }

    // GETTERS
    public Vector3f getColor() { return color; }
    public float getSize() { return size; }
    public boolean shouldJitter() { return jitter; }
    public int getLifetime() { return lifetime; }
    public boolean hasWhiteCore() { return whiteCore; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.GLITTER_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.color.x());
        buffer.writeFloat(this.color.y());
        buffer.writeFloat(this.color.z());
        buffer.writeFloat(this.size);
        buffer.writeBoolean(this.jitter);
        buffer.writeInt(this.lifetime);
        buffer.writeBoolean(this.whiteCore);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %s %d %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.color.x(), this.color.y(), this.color.z(), this.size, this.jitter, this.lifetime, this.whiteCore);
    }


}