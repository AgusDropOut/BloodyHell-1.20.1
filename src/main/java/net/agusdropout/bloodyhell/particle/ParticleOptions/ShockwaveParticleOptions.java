package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.Locale;

public class ShockwaveParticleOptions implements ParticleOptions {

    public static final Codec<ShockwaveParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(o -> o.color),
            Codec.FLOAT.fieldOf("initial_size").forGetter(o -> o.initialSize),
            Codec.FLOAT.fieldOf("max_size").forGetter(o -> o.maxSize)
    ).apply(instance, ShockwaveParticleOptions::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<ShockwaveParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ShockwaveParticleOptions fromCommand(ParticleType<ShockwaveParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float initialSize = reader.readFloat();
            reader.expect(' ');
            float maxSize = reader.readFloat();
            return new ShockwaveParticleOptions(new Vector3f(r, g, b), initialSize, maxSize);
        }

        @Override
        public ShockwaveParticleOptions fromNetwork(ParticleType<ShockwaveParticleOptions> type, FriendlyByteBuf buf) {
            return new ShockwaveParticleOptions(buf.readVector3f(), buf.readFloat(), buf.readFloat());
        }
    };

    private final Vector3f color;
    private final float initialSize;
    private final float maxSize;

    public ShockwaveParticleOptions(Vector3f color, float initialSize, float maxSize) {
        this.color = color;
        this.initialSize = initialSize;
        this.maxSize = maxSize;
    }

    public Vector3f getColor() { return color; }
    public float getInitialSize() { return initialSize; }
    public float getMaxSize() { return maxSize; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SHOCKWAVE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVector3f(this.color);
        buf.writeFloat(this.initialSize);
        buf.writeFloat(this.maxSize);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f",
                net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.color.x(), this.color.y(), this.color.z(), this.initialSize, this.maxSize);
    }
}