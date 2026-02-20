package net.agusdropout.bloodyhell.particle;

import com.mojang.serialization.Codec;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.particle.ParticleOptions.*;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BloodyHell.MODID);

    public static final RegistryObject<SimpleParticleType> BLOOD_PARTICLES =
            PARTICLE_TYPES.register("blood_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> LIGHT_PARTICLES =
            PARTICLE_TYPES.register("light_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DIRTY_BLOOD_FLOWER_PARTICLE =
            PARTICLE_TYPES.register("dirty_blood_flower_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> BLASPHEMOUS_MAGIC_RING =
            PARTICLE_TYPES.register("blasphemous_magic_ring", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MAGIC_LINE_PARTICLE =
            PARTICLE_TYPES.register("magic_particle_line", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_SIGIL_PARTICLE =
            PARTICLE_TYPES.register("blood_sigil_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MAGIC_SIMPLE_LINE_PARTICLE =
            PARTICLE_TYPES.register("magic_simple_particle_line", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLASPHEMOUS_BIOME_PARTICLE =
            PARTICLE_TYPES.register("blasphemous_biome_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_PULSE_PARTICLE =
            PARTICLE_TYPES.register("blood_pulse", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BLOOD_RUNE_PARTICLE =
            PARTICLE_TYPES.register("blood_rune_particle", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SLASH_PARTICLE =
            PARTICLE_TYPES.register("slash_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> VICERAL_PARTICLE =
            PARTICLE_TYPES.register("viceral_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> CYLINDER_PARTICLE =
            PARTICLE_TYPES.register("cylinder_particle", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> STAR_EXPLOSION_PARTICLE =
            PARTICLE_TYPES.register("star_explosion_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MAGIC_WAVE_PARTICLE =
            PARTICLE_TYPES.register("magic_wave_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CYCLOPS_HALO_PARTICLE =
            PARTICLE_TYPES.register("cyclops_halo_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> EYE_PARTICLE =
            PARTICLE_TYPES.register("eye_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CHILL_FLAME_PARTICLE =
            PARTICLE_TYPES.register("chill_flame_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CHILL_BLACK_PARTICLE =
            PARTICLE_TYPES.register("chill_black_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleBlockParticleOptions>> SIMPLE_BLOCK_PARTICLE =
            PARTICLE_TYPES.register("simple_block_particle", () ->
                    new ParticleType<SimpleBlockParticleOptions>(false, SimpleBlockParticleOptions.DESERIALIZER) {
                        @Override
                        public Codec<SimpleBlockParticleOptions> codec() {
                            return SimpleBlockParticleOptions.CODEC;
                        }
                    });
    public static final RegistryObject<ParticleType<MagicParticleOptions>> MAGIC_PARTICLE =
            PARTICLE_TYPES.register("magic_particle",
                    () -> new ParticleType<MagicParticleOptions>(false, MagicParticleOptions.DESERIALIZER) {
                        @Override
                        public Codec<MagicParticleOptions> codec() {
                            return MagicParticleOptions.CODEC;
                        }
                    });
    public static final RegistryObject<ParticleType<MagicFloorParticleOptions>> MAGIC_FLOOR_PARTICLE =
            PARTICLE_TYPES.register("magic_floor_particle",
                    () -> new ParticleType<MagicFloorParticleOptions>(false, MagicFloorParticleOptions.DESERIALIZER) {
                        @Override
                        public Codec<MagicFloorParticleOptions> codec() {
                            return MagicFloorParticleOptions.CODEC;
                        }
                    });

    public static final RegistryObject<SimpleParticleType> SHOCKWAVE_RING = PARTICLE_TYPES.register("shockwave_ring",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_FLAME =
            PARTICLE_TYPES.register("blood_flame", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SMALL_BLOOD_FLAME_PARTICLE =
            PARTICLE_TYPES.register("small_blood_flame_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> BLOOD_DROP_PARTICLE =
            PARTICLE_TYPES.register("blood_drop_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_STAIN_PARTICLE =
            PARTICLE_TYPES.register("blood_stain_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<ParticleType<ImpactParticleOptions>> IMPACT_PARTICLE =
            PARTICLE_TYPES.register("impact_particle",
                    () -> new ParticleType<ImpactParticleOptions>(false, ImpactParticleOptions.DESERIALIZER) {
                        @Override
                        public Codec<ImpactParticleOptions> codec() {
                            return ImpactParticleOptions.CODEC;
                        }
                    });
    public static final RegistryObject<ParticleType<HollowRectangleOptions>> HOLLOW_RECTANGLE_PARTICLE =
            PARTICLE_TYPES.register("hollow_rectangle_particle",
                    () -> new ParticleType<HollowRectangleOptions>(false, HollowRectangleOptions.DESERIALIZER) {
                        @Override
                        public Codec<HollowRectangleOptions> codec() {
                            return HollowRectangleOptions.CODEC;
                        }
                    });
    public static final RegistryObject<ParticleType<BlackHoleParticleOptions>> BLACK_HOLE_PARTICLE =
            PARTICLE_TYPES.register("black_hole_particle", () -> new ParticleType<>(false, BlackHoleParticleOptions.DESERIALIZER) {
                @Override
                public Codec<BlackHoleParticleOptions> codec() {
                    return BlackHoleParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<TetherParticleOptions>> TETHER_PARTICLE =
            PARTICLE_TYPES.register("tether_particle", () -> new ParticleType<TetherParticleOptions>(false, TetherParticleOptions.DESERIALIZER) {
                @Override
                public Codec<TetherParticleOptions> codec() {
                    return TetherParticleOptions.CODEC;
                }
            });


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }


}
