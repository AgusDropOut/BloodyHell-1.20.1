package net.agusdropout.bloodyhell.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModShaders {

    public static ShaderInstance DISTORTION_SHADER;
    public static ShaderInstance RADIAL_DISTORTION_SHADER;
    public static ShaderInstance ENTITY_GLITTER_SHADER;
    public static ShaderInstance SHAPE_GLITTER_SHADER;
    public static ShaderInstance SHAPE_UNKNOWN_FIRE_SHADER;
    public static ShaderInstance SHAPE_SPHERICAL_RIM_SHADER;
    public static ShaderInstance ETHEREAL_SWIRL_SHADER;
    public static ShaderInstance INSIGHT_DISTORTION_SHADER;
    public static ShaderInstance BLACK_HOLE_SHADER;
    public static ShaderInstance FRENZY_TENTACLES;
    public static ShaderInstance FRENZIED_FLAME_SHADER;
    public static ShaderInstance LINEAR_FRENZIED_FLAME_SHADER;
    public static ShaderInstance TINY_BLOOM_SHADER;
    public static ShaderInstance RADIANT_ENERGY_SHADER;
    public static ShaderInstance FRENZIED_EXPLOSION_SHADER;
    public static ShaderInstance BLOOD_BLOB_SHADER;
    public static ShaderInstance BLOOD_DROP_SHADER;
    public static ShaderInstance BLOOD_ORB_SHADER;
    public static ShaderInstance MINI_BLOOD_BLOB_SHADER;
    public static ShaderInstance GOLDEN_SORROW_ORB_SHADER;
    public static ShaderInstance SCARLET_CINDER_ORB_SHADER;
    public static ShaderInstance CRIMSON_VEIL_GUI_SHADER;
    public static ShaderInstance SPELL_WHEEL_BG_SHADER;
    public static ShaderInstance OMNITOME_VOID_SHADER;
    public static ShaderInstance EMERGE_CLIP_SHADER;
    public static ShaderInstance BLOOD_STAIN_SHADER;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        // POSITION_TEX_COLOR
        register(event, "distortion", DefaultVertexFormat.POSITION_TEX_COLOR, s -> DISTORTION_SHADER = s);
        register(event, "radial_distortion", DefaultVertexFormat.POSITION_TEX_COLOR, s -> RADIAL_DISTORTION_SHADER = s);
        register(event, "ethereal_swirl", DefaultVertexFormat.POSITION_TEX_COLOR, s -> ETHEREAL_SWIRL_SHADER = s);
        register(event, "frenzy_tentacles", DefaultVertexFormat.POSITION_TEX_COLOR, s -> FRENZY_TENTACLES = s);
        register(event, "frenzied_flame", DefaultVertexFormat.POSITION_TEX_COLOR, s -> FRENZIED_FLAME_SHADER = s);
        register(event, "linear_frenzied_flame", DefaultVertexFormat.POSITION_TEX_COLOR, s -> LINEAR_FRENZIED_FLAME_SHADER = s);
        register(event, "tiny_bloom", DefaultVertexFormat.POSITION_TEX_COLOR, s -> TINY_BLOOM_SHADER = s);
        register(event, "radiant_energy", DefaultVertexFormat.POSITION_TEX_COLOR, s -> RADIANT_ENERGY_SHADER = s);
        register(event, "frenzied_explosion", DefaultVertexFormat.POSITION_TEX_COLOR, s -> FRENZIED_EXPLOSION_SHADER = s);
        register(event, "blood_drop", DefaultVertexFormat.POSITION_TEX_COLOR, s -> BLOOD_DROP_SHADER = s);

        // NEW_ENTITY
        register(event, "entity_glitter", DefaultVertexFormat.NEW_ENTITY, s -> ENTITY_GLITTER_SHADER = s);
        register(event, "insight_distortion", DefaultVertexFormat.NEW_ENTITY, s -> INSIGHT_DISTORTION_SHADER = s);
        register(event, "omnitome_void", DefaultVertexFormat.NEW_ENTITY, s -> OMNITOME_VOID_SHADER = s);
        register(event, "omnitome_emerge_clip", DefaultVertexFormat.NEW_ENTITY, s -> EMERGE_CLIP_SHADER = s);

        // POSITION_COLOR
        register(event, "shape_glitter", DefaultVertexFormat.POSITION_COLOR, s -> SHAPE_GLITTER_SHADER = s);
        register(event, "shape_unknown_fire", DefaultVertexFormat.POSITION_COLOR, s -> SHAPE_UNKNOWN_FIRE_SHADER = s);
        register(event, "spherical_rim", DefaultVertexFormat.POSITION_COLOR, s -> SHAPE_SPHERICAL_RIM_SHADER = s);

        // POSITION_TEX
        register(event, "black_hole", DefaultVertexFormat.POSITION_TEX, s -> BLACK_HOLE_SHADER = s);
        register(event, "blood_blob", DefaultVertexFormat.POSITION_TEX, s -> BLOOD_BLOB_SHADER = s);
        register(event, "blood_orb", DefaultVertexFormat.POSITION_TEX, s -> BLOOD_ORB_SHADER = s);
        register(event, "crimson_veil_gui", DefaultVertexFormat.POSITION_TEX, s -> CRIMSON_VEIL_GUI_SHADER = s);
        register(event, "spell_wheel_bg", DefaultVertexFormat.POSITION_TEX, s -> SPELL_WHEEL_BG_SHADER = s);

        // POSITION
        register(event, "mini_blood_blob", DefaultVertexFormat.POSITION, s -> MINI_BLOOD_BLOB_SHADER = s);
        register(event, "golden_sorrow_orb", DefaultVertexFormat.POSITION, s -> GOLDEN_SORROW_ORB_SHADER = s);
        register(event, "scarlet_cinder_orb", DefaultVertexFormat.POSITION, s -> SCARLET_CINDER_ORB_SHADER = s);

        // POSITION_COLOR_TEX
        register(event, "blood_stain", DefaultVertexFormat.POSITION_COLOR_TEX, s -> BLOOD_STAIN_SHADER = s);
    }

    private static void register(RegisterShadersEvent event, String name, VertexFormat format, Consumer<ShaderInstance> onLoaded) {
        try {
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), new ResourceLocation(BloodyHell.MODID, name), format),
                    onLoaded
            );
        } catch (IOException e) {
            BloodyHell.LOGGER.error("Failed to load shader: {}:{}", BloodyHell.MODID, name, e);
        }
    }
}