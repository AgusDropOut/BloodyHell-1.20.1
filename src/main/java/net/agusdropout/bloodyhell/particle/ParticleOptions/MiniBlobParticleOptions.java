package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3f;

import java.util.Locale;

public class MiniBlobParticleOptions implements ParticleOptions {
    public static final Codec<MiniBlobParticleOptions> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.FLOAT.fieldOf("size").forGetter((opt) -> opt.size),
                    Codec.FLOAT.fieldOf("r").forGetter((opt) -> opt.r),
                    Codec.FLOAT.fieldOf("g").forGetter((opt) -> opt.g),
                    Codec.FLOAT.fieldOf("b").forGetter((opt) -> opt.b),
                    Codec.BOOL.fieldOf("has_gravity").forGetter((opt) -> opt.hasGravity)
            ).apply(instance, MiniBlobParticleOptions::new)
    );

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<MiniBlobParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public MiniBlobParticleOptions fromCommand(ParticleType<MiniBlobParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            boolean hasGravity = reader.readBoolean();
            return new MiniBlobParticleOptions(size, r, g, b, hasGravity);
        }

        @Override
        public MiniBlobParticleOptions fromNetwork(ParticleType<MiniBlobParticleOptions> type, FriendlyByteBuf buf) {
            return new MiniBlobParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean());
        }
    };

    private final float size;
    private final float r, g, b;
    private final boolean hasGravity;

    public MiniBlobParticleOptions(float size, float r, float g, float b, boolean hasGravity) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.hasGravity = hasGravity;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeFloat(this.r);
        buf.writeFloat(this.g);
        buf.writeFloat(this.b);
        buf.writeBoolean(this.hasGravity);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %b", ModParticles.MINI_BLOB_PARTICLE.getId(), this.size, this.r, this.g, this.b, this.hasGravity);
    }

    @Override
    public ParticleType<MiniBlobParticleOptions> getType() {
        return ModParticles.MINI_BLOB_PARTICLE.get();
    }

    public float getSize() { return size; }
    public Vector3f getColor() { return new Vector3f(r, g, b); }
    public boolean hasGravity() { return hasGravity; }
}