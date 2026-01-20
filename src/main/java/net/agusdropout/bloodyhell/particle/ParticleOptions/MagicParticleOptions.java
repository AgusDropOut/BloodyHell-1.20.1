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

public class MagicParticleOptions implements ParticleOptions {

    // DATA
    private final Vector3f color;
    private final float size;
    private final boolean jitter;
    private final int lifetime; // NEW FIELD

    // --- CODEC ---
    public static final Codec<MagicParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.x()),
                    Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.y()),
                    Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.z()),
                    Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
                    Codec.BOOL.fieldOf("jitter").forGetter(d -> d.jitter),
                    Codec.INT.fieldOf("lifetime").forGetter(d -> d.lifetime) // Add Lifetime
            ).apply(instance, MagicParticleOptions::new)
    );

    // --- DESERIALIZER ---
    public static final ParticleOptions.Deserializer<MagicParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<MagicParticleOptions>() {
        @Override
        public MagicParticleOptions fromCommand(ParticleType<MagicParticleOptions> type, StringReader reader) throws CommandSyntaxException {
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
            int lifetime = reader.readInt(); // Read Lifetime
            return new MagicParticleOptions(r, g, b, size, jitter, lifetime);
        }

        @Override
        public MagicParticleOptions fromNetwork(ParticleType<MagicParticleOptions> type, FriendlyByteBuf buffer) {
            // Read individually
            return new MagicParticleOptions(
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readBoolean(),
                    buffer.readInt() // Read Lifetime
            );
        }
    };

    // --- CONSTRUCTORS ---

    // Constructor used by Codec (Takes individual floats)
    public MagicParticleOptions(float r, float g, float b, float size, boolean jitter, int lifetime) {
        this.color = new Vector3f(r, g, b);
        this.size = size;
        this.jitter = jitter;
        this.lifetime = lifetime;
    }

    // Constructor for your own code (Takes Vector3f directly)
    public MagicParticleOptions(Vector3f color, float size, boolean jitter, int lifetime) {
        this.color = color;
        this.size = size;
        this.jitter = jitter;
        this.lifetime = lifetime;
    }

    // GETTERS
    public Vector3f getColor() { return color; }
    public float getSize() { return size; }
    public boolean shouldJitter() { return jitter; }
    public int getLifetime() { return lifetime; } // Getter

    @Override
    public ParticleType<?> getType() {
        return ModParticles.MAGIC_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.color.x());
        buffer.writeFloat(this.color.y());
        buffer.writeFloat(this.color.z());
        buffer.writeFloat(this.size);
        buffer.writeBoolean(this.jitter);
        buffer.writeInt(this.lifetime); // Write Lifetime
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %s %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.color.x(), this.color.y(), this.color.z(), this.size, this.jitter, this.lifetime);
    }
}