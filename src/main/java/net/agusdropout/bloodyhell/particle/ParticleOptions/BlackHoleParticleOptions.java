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

public class BlackHoleParticleOptions implements ParticleOptions {
    public static final Codec<BlackHoleParticleOptions> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.FLOAT.fieldOf("size").forGetter((opt) -> opt.size),
                    Codec.FLOAT.fieldOf("r").forGetter((opt) -> opt.r),
                    Codec.FLOAT.fieldOf("g").forGetter((opt) -> opt.g),
                    Codec.FLOAT.fieldOf("b").forGetter((opt) -> opt.b)
            ).apply(instance, BlackHoleParticleOptions::new)
    );

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<BlackHoleParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public BlackHoleParticleOptions fromCommand(ParticleType<BlackHoleParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            return new BlackHoleParticleOptions(size, r, g, b);
        }

        @Override
        public BlackHoleParticleOptions fromNetwork(ParticleType<BlackHoleParticleOptions> type, FriendlyByteBuf buf) {
            return new BlackHoleParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    };

    private final float size;
    private final float r, g, b;

    public BlackHoleParticleOptions(float size, float r, float g, float b) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeFloat(this.r);
        buf.writeFloat(this.g);
        buf.writeFloat(this.b);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", ModParticles.BLACK_HOLE_PARTICLE.getId(), this.size, this.r, this.g, this.b);
    }

    @Override
    public ParticleType<BlackHoleParticleOptions> getType() {
        return ModParticles.BLACK_HOLE_PARTICLE.get();
    }

    public float getSize() { return size; }
    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
}