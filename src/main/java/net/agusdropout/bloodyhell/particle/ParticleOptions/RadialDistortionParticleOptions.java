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

public class RadialDistortionParticleOptions implements ParticleOptions {

    private final float pitch;
    private final float yaw;
    private final int lifeTicks;

    public static final Codec<RadialDistortionParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("pitch").forGetter(RadialDistortionParticleOptions::getPitch),
                    Codec.FLOAT.fieldOf("yaw").forGetter(RadialDistortionParticleOptions::getYaw),
                    Codec.INT.fieldOf("lifeTicks").forGetter(RadialDistortionParticleOptions::getLifeTicks)
            ).apply(instance, RadialDistortionParticleOptions::new)
    );

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<RadialDistortionParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public RadialDistortionParticleOptions fromCommand(ParticleType<RadialDistortionParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float pitch = (float) reader.readDouble();
            reader.expect(' ');
            float yaw = (float) reader.readDouble();
            reader.expect(' ');
            int lifeTicks = reader.readInt();
            return new RadialDistortionParticleOptions(pitch, yaw, lifeTicks);
        }

        @Override
        public RadialDistortionParticleOptions fromNetwork(ParticleType<RadialDistortionParticleOptions> type, FriendlyByteBuf buffer) {
            return new RadialDistortionParticleOptions(buffer.readFloat(), buffer.readFloat(), buffer.readInt());
        }
    };

    public RadialDistortionParticleOptions(float pitch, float yaw, int lifeTicks) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.lifeTicks = lifeTicks;
    }

    public float getPitch() { return this.pitch; }
    public float getYaw() { return this.yaw; }
    public int getLifeTicks() { return this.lifeTicks; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.RADIAL_DISTORION_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.pitch);
        buffer.writeFloat(this.yaw);
        buffer.writeInt(this.lifeTicks);
    }

    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + this.pitch + " " + this.yaw + " " + this.lifeTicks;
    }
}