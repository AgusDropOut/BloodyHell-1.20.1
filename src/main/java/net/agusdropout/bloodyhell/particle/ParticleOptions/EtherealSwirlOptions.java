package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import org.joml.Vector3f;

import java.util.Locale;

public class EtherealSwirlOptions implements ParticleOptions {

    private final float r;
    private final float g;
    private final float b;
    private final int maxLifetime;
    private final float size;

    public static final Codec<EtherealSwirlOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(EtherealSwirlOptions::getR),
            Codec.FLOAT.fieldOf("g").forGetter(EtherealSwirlOptions::getG),
            Codec.FLOAT.fieldOf("b").forGetter(EtherealSwirlOptions::getB),
            Codec.INT.fieldOf("maxLifetime").forGetter(EtherealSwirlOptions::getMaxLifetime),
            Codec.FLOAT.fieldOf("size").forGetter(EtherealSwirlOptions::getSize)
    ).apply(instance, EtherealSwirlOptions::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<EtherealSwirlOptions> DESERIALIZER = new ParticleOptions.Deserializer<EtherealSwirlOptions>() {
        @Override
        public EtherealSwirlOptions fromCommand(ParticleType<EtherealSwirlOptions> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float r = stringReader.readFloat();
            stringReader.expect(' ');
            float g = stringReader.readFloat();
            stringReader.expect(' ');
            float b = stringReader.readFloat();
            stringReader.expect(' ');
            int life = stringReader.readInt();
            stringReader.expect(' ');
            float size = stringReader.readFloat();
            return new EtherealSwirlOptions(r, g, b, life, size);
        }

        @Override
        public EtherealSwirlOptions fromNetwork(ParticleType<EtherealSwirlOptions> particleType, FriendlyByteBuf friendlyByteBuf) {
            return new EtherealSwirlOptions(
                    friendlyByteBuf.readFloat(),
                    friendlyByteBuf.readFloat(),
                    friendlyByteBuf.readFloat(),
                    friendlyByteBuf.readInt(),
                    friendlyByteBuf.readFloat()

            );
        }
    };

    public EtherealSwirlOptions(float r, float g, float b, int maxLifetime, float size) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.maxLifetime = maxLifetime;
        this.size = size;
    }

    public EtherealSwirlOptions(Vector3f color, int maxLifetime, float size) {
        this.r = color.x;
        this.g = color.y;
        this.b = color.z;
        this.maxLifetime = maxLifetime;
        this.size = size;
    }

    public float getR() {
        return this.r;
    }

    public float getG() {
        return this.g;
    }

    public float getB() {
        return this.b;
    }

    public int getMaxLifetime() {
        return this.maxLifetime;
    }
    public float getSize() {
        return this.size;
    }

    @Override
    public ParticleType<?> getType() {
        return  ModParticles.ETHEREAL_SWIRL_PARTICLE.get();
    }


    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.r);
        buffer.writeFloat(this.g);
        buffer.writeFloat(this.b);
        buffer.writeInt(this.maxLifetime);
        buffer.writeFloat(this.size);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.r, this.g, this.b, this.maxLifetime, this.size);
    }
}