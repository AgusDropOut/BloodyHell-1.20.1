package net.agusdropout.bloodyhell.fluid;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, BloodyHell.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, BloodyHell.MODID);

    // ========================================================================
    // 1. BLOOD (Red)
    // ========================================================================
    public static final RegistryObject<FluidType> BLOOD_TYPE = register("blood",
            FluidType.Properties.create()
                    .lightLevel(2)
                    .density(1100)
                    .viscosity(1500)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA),
            0xFFCC0000,
            new Vector3f(204f/255f, 0f/255f, 0f/255f));

    public static final RegistryObject<FlowingFluid> BLOOD_SOURCE = FLUIDS.register("blood_source",
            () -> new ForgeFlowingFluid.Source(ModFluids.BLOOD_PROPERTIES));
    public static final RegistryObject<FlowingFluid> BLOOD_FLOWING = FLUIDS.register("blood_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.BLOOD_PROPERTIES));

    public static final ForgeFlowingFluid.Properties BLOOD_PROPERTIES = new ForgeFlowingFluid.Properties(
            BLOOD_TYPE, BLOOD_SOURCE, BLOOD_FLOWING)
            .slopeFindDistance(2).levelDecreasePerBlock(2)
            .block(ModBlocks.BLOOD_FLUID_BLOCK)
            .bucket(ModItems.BLOOD_BUCKET);


    // ========================================================================
    // 2. CORRUPTED BLOOD (Dark Red)
    // ========================================================================
    public static final RegistryObject<FluidType> CORRUPTED_BLOOD_TYPE = register("corrupted_blood",
            FluidType.Properties.create()
                    .lightLevel(5)
                    .density(1500)
                    .viscosity(3000)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_POWDER_SNOW)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_POWDER_SNOW),
            0xFF550000,
            new Vector3f(85f/255f, 0f/255f, 0f/255f));

    public static final RegistryObject<FlowingFluid> CORRUPTED_BLOOD_SOURCE = FLUIDS.register("corrupted_blood_source",
            () -> new ForgeFlowingFluid.Source(ModFluids.CORRUPTED_BLOOD_PROPERTIES));
    public static final RegistryObject<FlowingFluid> CORRUPTED_BLOOD_FLOWING = FLUIDS.register("corrupted_blood_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.CORRUPTED_BLOOD_PROPERTIES));

    public static final ForgeFlowingFluid.Properties CORRUPTED_BLOOD_PROPERTIES = new ForgeFlowingFluid.Properties(
            CORRUPTED_BLOOD_TYPE, CORRUPTED_BLOOD_SOURCE, CORRUPTED_BLOOD_FLOWING)
            .slopeFindDistance(3).levelDecreasePerBlock(3)
            .block(ModBlocks.CORRUPTED_BLOOD_BLOCK)
            .bucket(ModItems.CORRUPTED_BLOOD_BUCKET);


    // ========================================================================
    // 3. VISCOUS BLASPHEMY (Black & Yellow)
    // ========================================================================
    public static final RegistryObject<FluidType> VISCOUS_BLASPHEMY_TYPE = register("viscous_blasphemy",
            FluidType.Properties.create()
                    .lightLevel(15)
                    .density(4000)
                    .viscosity(6000)
                    .temperature(1300)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA),
            0xFFFEE011,
            new Vector3f(254f/255f, 224f/255f, 17f/255f));

    public static final RegistryObject<FlowingFluid> VISCOUS_BLASPHEMY_SOURCE = FLUIDS.register("viscous_blasphemy_source",
            () -> new ForgeFlowingFluid.Source(ModFluids.VISCOUS_BLASPHEMY_PROPERTIES));
    public static final RegistryObject<FlowingFluid> VISCOUS_BLASPHEMY_FLOWING = FLUIDS.register("viscous_blasphemy_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.VISCOUS_BLASPHEMY_PROPERTIES));

    public static final ForgeFlowingFluid.Properties VISCOUS_BLASPHEMY_PROPERTIES = new ForgeFlowingFluid.Properties(
            VISCOUS_BLASPHEMY_TYPE, VISCOUS_BLASPHEMY_SOURCE, VISCOUS_BLASPHEMY_FLOWING)
            .slopeFindDistance(2).levelDecreasePerBlock(4)
            .block(ModBlocks.VISCOUS_BLASPHEMY_BLOCK)
            .bucket(ModItems.VISCOUS_BLASPHEMY_BUCKET);


    // ========================================================================
    // 3. VISCERAL BLOOD (Black & Yellow)
    // ========================================================================
    // ========================================================================
    // 3. VISCERAL FLUID (Yellow-Greenish / Infected)
    // ========================================================================

    // 1. Fluid Type (Physics, Sound, Tint)
    public static final RegistryObject<FluidType> VISCERAL_FLUID_TYPE = register("visceral_blood",
            FluidType.Properties.create()
                    .lightLevel(8)      // Slight sickly glow
                    .density(3500)      // Thicker than blood
                    .viscosity(5000)    // Sluggish flow
                    .temperature(310)   // Body temperature
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_TADPOLE) // Squishy sound
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_TADPOLE),
            0xFFC8E617, // Hex Color: Sickly Yellow-Green (AARRGGBB)
            new Vector3f(200f/255f, 230f/255f, 23f/255f)); // Vector Color

    // 2. Fluid Instances
    public static final RegistryObject<FlowingFluid> VISCERAL_BLOOD_SOURCE = FLUIDS.register("visceral_blood_source",
            () -> new ForgeFlowingFluid.Source(ModFluids.VISCERAL_BLOOD_PROPERTIES));

    public static final RegistryObject<FlowingFluid> VISCERAL_BLOOD_FLOWING = FLUIDS.register("visceral_blood_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.VISCERAL_BLOOD_PROPERTIES));

    // 3. Properties Bundle
    public static final ForgeFlowingFluid.Properties VISCERAL_BLOOD_PROPERTIES = new ForgeFlowingFluid.Properties(
            VISCERAL_FLUID_TYPE, VISCERAL_BLOOD_SOURCE, VISCERAL_BLOOD_FLOWING)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(ModBlocks.VISCERAL_BLOOD_BLOCK)
            .bucket(ModItems.VISCERAL_BLOOD_BUCKET);


    // --- HELPER REGISTRY METHOD ---
    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties, int tintColor, Vector3f fogColor) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(
                new ResourceLocation(BloodyHell.MODID, "block/fluid/" + name + "_still"),
                new ResourceLocation(BloodyHell.MODID, "block/fluid/" + name + "_flow"),
                new ResourceLocation(BloodyHell.MODID, "block/fluid/" + name + "_overlay"),
                tintColor,
                fogColor,
                properties));
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}