package net.agusdropout.bloodyhell.fluid;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("bloodyhell:block/blood_fluid_still_texture");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("bloodyhell:block/blood_fluid_flowing_texture");
    @Deprecated
    public static final ResourceLocation RHNULL_WATER_STILL_RL = new ResourceLocation("bloodyhell:block/rhnull_blood_fluid_still_texture");
    @Deprecated
    public static final ResourceLocation RHNULL_WATER_FLOWING_RL = new ResourceLocation("bloodyhell:block/rhnull_blood_fluid_flowing_texture");
    public static final ResourceLocation BLOOD_OVERLAY_RL = new ResourceLocation(BloodyHell.MODID, "misc/in_soap_water");
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, BloodyHell.MODID);

    public static final RegistryObject<FluidType> BLOOD_FLUID_TYPE = register("blood_fluid",
            FluidType.Properties.create().lightLevel(2).density(15).viscosity(5).sound(SoundAction.get("drink"),
                    SoundEvents.HONEY_DRINK));
    @Deprecated
    public static final RegistryObject<FluidType> RHNULL_BLOOD_FLUID_TYPE = registerRhnull("rhnull_blood_fluid",
            FluidType.Properties.create().lightLevel(2).density(15).viscosity(5).sound(SoundAction.get("drink"),
                    SoundEvents.HONEY_DRINK));



    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, BLOOD_OVERLAY_RL,
                0xFFFF0000, new Vector3f(1.0f, 0.0f, 0.0f), properties));
    }
    @Deprecated
    private static RegistryObject<FluidType> registerRhnull(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(RHNULL_WATER_STILL_RL, RHNULL_WATER_FLOWING_RL, BLOOD_OVERLAY_RL,
                0xFFBF00, new Vector3f(255.0f/255f, 191.0f/255f, 0.0f/255f), properties));
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
