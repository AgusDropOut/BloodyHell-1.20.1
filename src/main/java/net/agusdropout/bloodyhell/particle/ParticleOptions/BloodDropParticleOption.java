package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import java.util.Locale;

public class BloodDropParticleOption implements ParticleOptions {


    public static final Vector3f DEFAULT_COLOR = new Vector3f(0.8f, 0.05f, 0.05f);

    private final Vector3f color;

    public static final Codec<BloodDropParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(o -> o.getColor().x()),
            Codec.FLOAT.fieldOf("g").forGetter(o -> o.getColor().y()),
            Codec.FLOAT.fieldOf("b").forGetter(o -> o.getColor().z())
    ).apply(instance, (r, g, b) -> new BloodDropParticleOption(new Vector3f(r, g, b))));

    public static final ParticleOptions.Deserializer<BloodDropParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public BloodDropParticleOption fromCommand(ParticleType<BloodDropParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = (float) reader.readDouble();
            reader.expect(' ');
            float g = (float) reader.readDouble();
            reader.expect(' ');
            float b = (float) reader.readDouble();
            return new BloodDropParticleOption(new Vector3f(r, g, b));
        }

        @Override
        public BloodDropParticleOption fromNetwork(ParticleType<BloodDropParticleOption> type, FriendlyByteBuf buf) {
            return new BloodDropParticleOption(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()));
        }
    };

    public BloodDropParticleOption(Vector3f color) {
        this.color = color;
    }

    public Vector3f getColor() {
        return this.color;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.BLOOD_DROP_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.color.x());
        buf.writeFloat(this.color.y());
        buf.writeFloat(this.color.z());
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f", ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z());
    }
}