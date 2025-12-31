package net.agusdropout.bloodyhell.sound;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BloodyHell.MODID);

    public static final RegistryObject<SoundEvent> GRAWL_DROWN = registerSoundEvents("grawl_drown");
    public static final RegistryObject<SoundEvent> GRAWL_HURT = registerSoundEvents("grawl_hurt");
    public static final RegistryObject<SoundEvent> GRAWL_ATTACK = registerSoundEvents("grawl_attack");
    public static final RegistryObject<SoundEvent> GRAWL_DEATH = registerSoundEvents("grawl_death");
    public static final RegistryObject<SoundEvent> VISCERAL_EXPLOSION = registerSoundEvents("visceral_explosion");
    public static final RegistryObject<SoundEvent> VEINRAVER_AMBIENT = registerSoundEvents("veinraver_ambient");
    public static final RegistryObject<SoundEvent> VEINRAVER_HURT = registerSoundEvents("veinraver_hurt");
    public static final RegistryObject<SoundEvent> VEINRAVER_DEATH = registerSoundEvents("veinraver_death");
    public static final RegistryObject<SoundEvent> VEINRAVER_ATTACK = registerSoundEvents("veinraver_attack");
    public static final RegistryObject<SoundEvent> VEINRAVER_STEP = registerSoundEvents("veinraver_step");
    public static final RegistryObject<SoundEvent> VEINRAVER_SLAM = registerSoundEvents("veinraver_slam");
    public static final RegistryObject<SoundEvent> VEINRAVER_SLASH = registerSoundEvents("veinraver_slash");
    public static final RegistryObject<SoundEvent> OFFSPRING_ATTACK = registerSoundEvents("offspring_attack");
    public static final RegistryObject<SoundEvent> OFFSPRING_HURT = registerSoundEvents("offspring_hurt");
    public static final RegistryObject<SoundEvent> OFFSPRING_AMBIENT = registerSoundEvents("offspring_ambient");
    public static final RegistryObject<SoundEvent> OFFSPRING_STEP = registerSoundEvents("offspring_step");
    public static final RegistryObject<SoundEvent> STARFALL_AMBIENT_SOUND = registerSoundEvents("starfall_ambient_sound");
    public static final RegistryObject<SoundEvent> STARFALL_EXPLOSION_SOUND = registerSoundEvents("starfall_explosion_sound");
    public static final RegistryObject<SoundEvent> WHIRLWIND_FLYING_SOUND = registerSoundEvents("whirldind_flying_sound");
    public static final RegistryObject<SoundEvent> WHIRLWIND_CUT_SOUND = registerSoundEvents("whirldind_cut_sound");
    public static final RegistryObject<SoundEvent> SELIORA_HURT1_SOUND = registerSoundEvents("seliora_hurt1_sound");
    public static final RegistryObject<SoundEvent> SELIORA_HURT2_SOUND = registerSoundEvents("seliora_hurt2_sound");
    public static final RegistryObject<SoundEvent> SELIORA_LULLABY_SOUND = registerSoundEvents("seliora_lullaby_sound");
    public static final RegistryObject<SoundEvent> SELIORA_THROW_SOUND = registerSoundEvents("seliora_throw_sound");
    public static final RegistryObject<SoundEvent> SELIORA_JUMP_ATTACK_SOUND = registerSoundEvents("seliora_jump_attack_sound");
    public static final RegistryObject<SoundEvent> SELIORA_CHARGE_ATTACK_SOUND = registerSoundEvents("seliora_charge_attack_sound");
    public static final RegistryObject<SoundEvent> SELIORA_SECOND_PHASE_AMBIENT_SOUND = registerSoundEvents("seliora_second_phase_ambient_sound");
    public static final RegistryObject<SoundEvent> SELIORA_AMBIENT_SOUND = registerSoundEvents("seliora_ambient_sound");
    public static final RegistryObject<SoundEvent> NECK_SNAP_SOUND = registerSoundEvents("neck_snap_sound");
    public static final RegistryObject<SoundEvent> HORNED_WORM_BURROWED = registerSoundEvents("horned_worm_burrowed");
    public static final RegistryObject<SoundEvent> SONG3 = registerSoundEvents("song3");
    public static final RegistryObject<SoundEvent> DAGGER_ATTACK_1 = registerSoundEvents("dagger_attack_1");
    public static final RegistryObject<SoundEvent> DAGGER_ATTACK_2 = registerSoundEvents("dagger_attack_2");
    public static final RegistryObject<SoundEvent> DAGGER_ATTACK_3 = registerSoundEvents("dagger_attack_3");







    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BloodyHell.MODID, name)));
    }


    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
