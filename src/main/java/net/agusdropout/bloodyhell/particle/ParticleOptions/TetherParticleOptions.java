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
import java.util.UUID;

public class TetherParticleOptions implements ParticleOptions {
    private final UUID targetUUID;
    private final float r, g, b, a;
    private final int lifetime;

    public TetherParticleOptions(UUID targetUUID, float r, float g, float b, float a, int lifetime) {
        this.targetUUID = targetUUID;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.lifetime = lifetime;
    }

    public UUID targetUUID() { return this.targetUUID; }
    public float r() { return this.r; }
    public float g() { return this.g; }
    public float b() { return this.b; }
    public float a() { return this.a; }
    public int lifetime() { return this.lifetime; }

    public static final Codec<TetherParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("target_uuid").forGetter(TetherParticleOptions::targetUUID),
            Codec.FLOAT.fieldOf("r").forGetter(TetherParticleOptions::r),
            Codec.FLOAT.fieldOf("g").forGetter(TetherParticleOptions::g),
            Codec.FLOAT.fieldOf("b").forGetter(TetherParticleOptions::b),
            Codec.FLOAT.fieldOf("a").forGetter(TetherParticleOptions::a),
            Codec.INT.fieldOf("lifetime").forGetter(TetherParticleOptions::lifetime)
    ).apply(instance, TetherParticleOptions::new));

    public static final ParticleOptions.Deserializer<TetherParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public TetherParticleOptions fromCommand(ParticleType<TetherParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' '); UUID uuid = UUID.fromString(reader.readString());
            reader.expect(' '); float r = reader.readFloat();
            reader.expect(' '); float g = reader.readFloat();
            reader.expect(' '); float b = reader.readFloat();
            reader.expect(' '); float a = reader.readFloat();
            reader.expect(' '); int life = reader.readInt();
            return new TetherParticleOptions(uuid, r, g, b, a, life);
        }

        @Override
        public TetherParticleOptions fromNetwork(ParticleType<TetherParticleOptions> type, FriendlyByteBuf buf) {
            return new TetherParticleOptions(buf.readUUID(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }
    };

    @Override
    public ParticleType<?> getType() { return ModParticles.TETHER_PARTICLE.get(); }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeUUID(targetUUID);
        buf.writeFloat(r).writeFloat(g).writeFloat(b).writeFloat(a).writeInt(lifetime);
    }

    @Override
    public String writeToString() {
        return String.format("%s %s %.2f %.2f %.2f %.2f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), targetUUID, r, g, b, a, lifetime);
    }
}