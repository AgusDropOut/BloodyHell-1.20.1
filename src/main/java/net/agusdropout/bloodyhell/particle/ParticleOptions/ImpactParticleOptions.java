package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class ImpactParticleOptions implements ParticleOptions {
    // 1. DATA FIELDS (Individual RGB)
    private final float r;
    private final float g;
    private final float b;
    private final float size;
    private final int lifetime;
    private final boolean jitter;
    private final float expansionSpeed;

    // 2. CODEC
    public static final Codec<ImpactParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(o -> o.r),
            Codec.FLOAT.fieldOf("g").forGetter(o -> o.g),
            Codec.FLOAT.fieldOf("b").forGetter(o -> o.b),
            Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
            Codec.INT.fieldOf("lifetime").forGetter(o -> o.lifetime),
            Codec.BOOL.fieldOf("jitter").forGetter(o -> o.jitter),
            Codec.FLOAT.fieldOf("expansion_speed").forGetter(o -> o.expansionSpeed)
    ).apply(instance, ImpactParticleOptions::new));

    // 3. CONSTRUCTOR
    public ImpactParticleOptions(float r, float g, float b, float size, int lifetime, boolean jitter, float expansionSpeed) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.size = size;
        this.lifetime = lifetime;
        this.jitter = jitter;
        this.expansionSpeed = expansionSpeed;
    }

    // Helper: Create using 0-255 Integers
    public static ImpactParticleOptions create(int r, int g, int b, float size, int lifetime, boolean jitter, float expansion) {
        return new ImpactParticleOptions(
                r / 255.0f, g / 255.0f, b / 255.0f,
                size, lifetime, jitter, expansion
        );
    }

    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
    public float getSize() { return size; }
    public int getLifetime() { return lifetime; }
    public boolean shouldJitter() { return jitter; }
    public float getExpansionSpeed() { return expansionSpeed; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.IMPACT_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(r);
        buffer.writeFloat(g);
        buffer.writeFloat(b);
        buffer.writeFloat(size);
        buffer.writeInt(lifetime);
        buffer.writeBoolean(jitter);
        buffer.writeFloat(expansionSpeed);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d %b %.2f",
                this.getType().toString(), r, g, b, size, lifetime, jitter, expansionSpeed);
    }

    // 4. DESERIALIZER
    public static final Deserializer<ImpactParticleOptions> DESERIALIZER = new Deserializer<ImpactParticleOptions>() {
        @Override
        public ImpactParticleOptions fromCommand(ParticleType<ImpactParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            int life = reader.readInt();
            reader.expect(' ');
            boolean jitter = reader.readBoolean();
            reader.expect(' ');
            float speed = reader.readFloat();
            return new ImpactParticleOptions(r, g, b, size, life, jitter, speed);
        }

        @Override
        public ImpactParticleOptions fromNetwork(ParticleType<ImpactParticleOptions> type, FriendlyByteBuf buffer) {
            return new ImpactParticleOptions(
                    buffer.readFloat(), buffer.readFloat(), buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readInt(),
                    buffer.readBoolean(),
                    buffer.readFloat()
            );
        }
    };
}