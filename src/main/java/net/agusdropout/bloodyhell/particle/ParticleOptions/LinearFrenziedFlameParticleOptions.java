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

public class LinearFrenziedFlameParticleOptions implements ParticleOptions {

    public static final Codec<LinearFrenziedFlameParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(LinearFrenziedFlameParticleOptions::getR),
            Codec.FLOAT.fieldOf("g").forGetter(LinearFrenziedFlameParticleOptions::getG),
            Codec.FLOAT.fieldOf("b").forGetter(LinearFrenziedFlameParticleOptions::getB),
            Codec.FLOAT.fieldOf("scale").forGetter(LinearFrenziedFlameParticleOptions::getScale),
            Codec.INT.fieldOf("lifetime").forGetter(LinearFrenziedFlameParticleOptions::getLifetime)
    ).apply(instance, LinearFrenziedFlameParticleOptions::new));

    public static final ParticleOptions.Deserializer<LinearFrenziedFlameParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public LinearFrenziedFlameParticleOptions fromCommand(ParticleType<LinearFrenziedFlameParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            int lifetime = reader.readInt();
            return new LinearFrenziedFlameParticleOptions(r, g, b, scale, lifetime);
        }

        @Override
        public LinearFrenziedFlameParticleOptions fromNetwork(ParticleType<LinearFrenziedFlameParticleOptions> type, FriendlyByteBuf buf) {
            return new LinearFrenziedFlameParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }
    };

    private final float r;
    private final float g;
    private final float b;
    private final float scale;
    private final int lifetime;

    public LinearFrenziedFlameParticleOptions(float r, float g, float b, float scale, int lifetime) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.scale = scale;
        this.lifetime = lifetime;
    }

    public float getR() { return this.r; }
    public float getG() { return this.g; }
    public float getB() { return this.b; }
    public float getScale() { return this.scale; }
    public int getLifetime() { return this.lifetime; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.LINEAR_FRENZIED_FLAME.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.r);
        buf.writeFloat(this.g);
        buf.writeFloat(this.b);
        buf.writeFloat(this.scale);
        buf.writeInt(this.lifetime);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d",
                net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.r, this.g, this.b, this.scale, this.lifetime);
    }
}