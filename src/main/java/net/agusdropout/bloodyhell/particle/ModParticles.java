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

    public static final RegistryObject<ParticleType<RadialDistortionParticleOptions>> RADIAL_DISTORION_PARTICLE =
            PARTICLE_TYPES.register("radial_distortion_particle", () -> new ParticleType<RadialDistortionParticleOptions>(false, RadialDistortionParticleOptions.DESERIALIZER) {
                @Override
                public Codec<RadialDistortionParticleOptions> codec() {
                    return RadialDistortionParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<GlitterParticleOptions>> GLITTER_PARTICLE =
            PARTICLE_TYPES.register("glitter_particle", () -> new ParticleType<GlitterParticleOptions>(false, GlitterParticleOptions.DESERIALIZER) {
                @Override
                public Codec<GlitterParticleOptions> codec() {
                    return GlitterParticleOptions.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<SmallGlitterParticleOptions>> SMALL_GLITTER_PARTICLE =
            PARTICLE_TYPES.register("small_glitter_particle", () -> new ParticleType<SmallGlitterParticleOptions>(false, SmallGlitterParticleOptions.DESERIALIZER) {
                @Override
                public Codec<SmallGlitterParticleOptions> codec() {
                    return SmallGlitterParticleOptions.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<EtherealSwirlOptions>> ETHEREAL_SWIRL_PARTICLE =
            PARTICLE_TYPES.register("etherial_swirl_particle", () -> new ParticleType<EtherealSwirlOptions>(false, EtherealSwirlOptions.DESERIALIZER) {
                @Override
                public Codec<EtherealSwirlOptions> codec() {
                    return EtherealSwirlOptions.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<ChillFallingParticleOptions>> CHILL_FALLING_PARTICLE =
            PARTICLE_TYPES.register("chill_falling_particle", () -> new ParticleType<ChillFallingParticleOptions>(false, ChillFallingParticleOptions.DESERIALIZER) {
                @Override
                public Codec<ChillFallingParticleOptions> codec() {
                    return ChillFallingParticleOptions.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<ShockwaveParticleOptions>> SHOCKWAVE_PARTICLE =
            PARTICLE_TYPES.register("shockwave_particle", () -> new ParticleType<ShockwaveParticleOptions>(false, ShockwaveParticleOptions.DESERIALIZER) {
                @Override
                public Codec<ShockwaveParticleOptions> codec() {
                    return ShockwaveParticleOptions.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<MagicalRingParticleOptions>> MAGICAL_RING_PARTICLE =
            PARTICLE_TYPES.register("magical_ring", () -> new ParticleType<MagicalRingParticleOptions>(false, MagicalRingParticleOptions.DESERIALIZER) {
                @Override
                public Codec<MagicalRingParticleOptions> codec() {
                    return MagicalRingParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<NoiseSphereParticleOptions>> NOISE_SPHERE_PARTICLE =
            PARTICLE_TYPES.register("noise_sphere_particle", () -> new ParticleType<NoiseSphereParticleOptions>(false, NoiseSphereParticleOptions.DESERIALIZER) {
                @Override
                public Codec<NoiseSphereParticleOptions> codec() {
                    return NoiseSphereParticleOptions.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<SphericalShieldParticleOptions>> SPHERICAL_SHIELD_PARTICLE =
            PARTICLE_TYPES.register("spherical_shield_particle", () -> new ParticleType<SphericalShieldParticleOptions>(false, SphericalShieldParticleOptions.DESERIALIZER) {
                @Override
                public Codec<SphericalShieldParticleOptions> codec() {
                    return SphericalShieldParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<FrenziedFlameParticleOptions>> FRENZIED_FLAME_PARTICLE =
            PARTICLE_TYPES.register("frenzied_flame_particle", () -> new ParticleType<FrenziedFlameParticleOptions>(false, FrenziedFlameParticleOptions.DESERIALIZER) {
                @Override
                public com.mojang.serialization.Codec<FrenziedFlameParticleOptions> codec() {
                    return FrenziedFlameParticleOptions.CODEC;
                }
            });


    public static final RegistryObject<SimpleParticleType> FRENZIED_TRAIL_PARTICLE =
            PARTICLE_TYPES.register("franzied_trail_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> RADIANT_ENERGY_PARTICLE =
            PARTICLE_TYPES.register("radiant_energy_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> ORBITAL_FRENZIED_PARTICLE =
            PARTICLE_TYPES.register("orbital_frenzied_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<ParticleType<TinyBloomParticleOptions>> TINY_BLOOM =
            PARTICLE_TYPES.register("tiny_bloom", () -> new ParticleType<>(false, TinyBloomParticleOptions.DESERIALIZER) {
                @Override
                public Codec<TinyBloomParticleOptions> codec() {
                    return TinyBloomParticleOptions.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<FrenziedExplosionParticleOptions>> FRENZIED_EXPLOSION =
            PARTICLE_TYPES.register("frenzied_explosion", () ->
                    new ParticleType<FrenziedExplosionParticleOptions>(false, FrenziedExplosionParticleOptions.DESERIALIZER) {
                        @Override
                        public Codec<FrenziedExplosionParticleOptions> codec() {
                            return FrenziedExplosionParticleOptions.CODEC;
                        }
                    });

    public static final RegistryObject<ParticleType<LinearFrenziedFlameParticleOptions>> LINEAR_FRENZIED_FLAME =
            PARTICLE_TYPES.register("linear_frenzied_flame_particle", () ->
                    new ParticleType<LinearFrenziedFlameParticleOptions>(false, LinearFrenziedFlameParticleOptions.DESERIALIZER) {
                        @Override
                        public Codec<LinearFrenziedFlameParticleOptions> codec() {
                            return LinearFrenziedFlameParticleOptions.CODEC;
                        }
                    });





    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }


}
