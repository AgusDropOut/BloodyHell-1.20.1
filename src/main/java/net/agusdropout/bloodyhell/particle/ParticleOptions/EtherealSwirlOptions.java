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
    private final int targetId;

    public static final Codec<EtherealSwirlOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(EtherealSwirlOptions::getR),
            Codec.FLOAT.fieldOf("g").forGetter(EtherealSwirlOptions::getG),
            Codec.FLOAT.fieldOf("b").forGetter(EtherealSwirlOptions::getB),
            Codec.INT.fieldOf("maxLifetime").forGetter(EtherealSwirlOptions::getMaxLifetime),
            Codec.FLOAT.fieldOf("size").forGetter(EtherealSwirlOptions::getSize),
            Codec.INT.fieldOf("target_id").forGetter(EtherealSwirlOptions::getTargetId)
    ).apply(instance, EtherealSwirlOptions::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<EtherealSwirlOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
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
            stringReader.expect(' ');
            int targetId = stringReader.readInt();
            return new EtherealSwirlOptions(r, g, b, life, size, targetId);
        }

        @Override
        public EtherealSwirlOptions fromNetwork(ParticleType<EtherealSwirlOptions> particleType, FriendlyByteBuf buf) {
            return new EtherealSwirlOptions(
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readFloat(),
                    buf.readInt()
            );
        }
    };

    public EtherealSwirlOptions(float r, float g, float b, int maxLifetime, float size, int targetId) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.maxLifetime = maxLifetime;
        this.size = size;
        this.targetId = targetId;
    }

    public EtherealSwirlOptions(Vector3f color, int maxLifetime, float size, int targetId) {
        this(color.x(), color.y(), color.z(), maxLifetime, size, targetId);
    }

    public EtherealSwirlOptions(float r, float g, float b, int maxLifetime, float size) {
        this(r, g, b, maxLifetime, size, -1);
    }

    public EtherealSwirlOptions(Vector3f color, int maxLifetime, float size) {
        this(color.x(), color.y(), color.z(), maxLifetime, size, -1);
    }

    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
    public int getMaxLifetime() { return maxLifetime; }
    public float getSize() { return size; }
    public int getTargetId() { return targetId; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.ETHEREAL_SWIRL_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.r);
        buffer.writeFloat(this.g);
        buffer.writeFloat(this.b);
        buffer.writeInt(this.maxLifetime);
        buffer.writeFloat(this.size);
        buffer.writeInt(this.targetId);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d %.2f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.r, this.g, this.b, this.maxLifetime, this.size, this.targetId);
    }
}