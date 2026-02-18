package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class HollowRectangleOptions implements ParticleOptions {

    public static final Codec<HollowRectangleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(o -> o.color.x),
            Codec.FLOAT.fieldOf("g").forGetter(o -> o.color.y),
            Codec.FLOAT.fieldOf("b").forGetter(o -> o.color.z),
            Codec.FLOAT.fieldOf("width").forGetter(HollowRectangleOptions::getWidth),
            Codec.FLOAT.fieldOf("height").forGetter(HollowRectangleOptions::getHeight),
            Codec.INT.fieldOf("life").forGetter(HollowRectangleOptions::getLife),
            Codec.FLOAT.fieldOf("yaw").forGetter(HollowRectangleOptions::getYaw),
            Codec.FLOAT.fieldOf("jitter").forGetter(HollowRectangleOptions::getJitter)
    ).apply(instance, (r, g, b, width, height, life, yaw, jitter) ->
            new HollowRectangleOptions(new Vector3f(r, g, b), width, height, life, yaw, jitter)));

    public static final Deserializer<HollowRectangleOptions> DESERIALIZER = new Deserializer<>() {
        public @NotNull HollowRectangleOptions fromCommand(@NotNull ParticleType<HollowRectangleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat(); reader.expect(' ');
            float g = reader.readFloat(); reader.expect(' ');
            float b = reader.readFloat(); reader.expect(' ');
            float width = reader.readFloat(); reader.expect(' ');
            float height = reader.readFloat(); reader.expect(' ');
            int life = reader.readInt(); reader.expect(' ');
            float yaw = reader.readFloat(); reader.expect(' ');
            float jitter = reader.readFloat();
            return new HollowRectangleOptions(new Vector3f(r, g, b), width, height, life, yaw, jitter);
        }
        public @NotNull HollowRectangleOptions fromNetwork(@NotNull ParticleType<HollowRectangleOptions> type, @NotNull FriendlyByteBuf buffer) {
            return new HollowRectangleOptions(
                    new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readInt(),
                    buffer.readFloat(),
                    buffer.readFloat()
            );
        }
    };

    private final Vector3f color;
    private final float width;
    private final float height;
    private final int life;
    private final float yaw;
    private final float jitter;

    public HollowRectangleOptions(Vector3f color, float width, float height, int life, float yaw, float jitter) {
        this.color = color;
        this.width = width;
        this.height = height;
        this.life = life;
        this.yaw = yaw;
        this.jitter = jitter;
    }

    // Backwards compatibility constructor if needed, or remove
    public HollowRectangleOptions(Vector3f color, float width, float height, int life) {
        this(color, width, height, life, 0.0f, 0.0f);
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buffer) {
        buffer.writeFloat(color.x());
        buffer.writeFloat(color.y());
        buffer.writeFloat(color.z());
        buffer.writeFloat(width);
        buffer.writeFloat(height);
        buffer.writeInt(life);
        buffer.writeFloat(yaw);
        buffer.writeFloat(jitter);
    }

    @Override
    public @NotNull String writeToString() {
        return String.format(Locale.ROOT, "%f %f %f %f %f %d %f %f",
                color.x, color.y, color.z, width, height, life, yaw, jitter);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ModParticles.HOLLOW_RECTANGLE_PARTICLE.get();
    }

    public Vector3f getColor() { return color; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getLife() { return life; }
    public float getYaw() { return yaw; }
    public float getJitter() { return jitter; }
}