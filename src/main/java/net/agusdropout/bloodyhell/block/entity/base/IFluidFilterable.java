package net.agusdropout.bloodyhell.block.entity.base;

import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public interface IFluidFilterable {

    // --- METHODS TO BE IMPLEMENTED BY BE ---
    Fluid getFilter();
    void setFilter(Fluid fluid);

    // --- DEFAULT METHODS (LOGIC) ---

    default boolean isFluidAllowed(FluidStack stack) {
        // If filter is Empty (or null), allow EVERYTHING (No filter)
        if (getFilter() == null || getFilter() == Fluids.EMPTY) {
            return true;
        }
        // Otherwise, allow only exact matches
        return stack.getFluid().isSame(getFilter());
    }

    default void cycleFilter(Level level, BlockPos pos, Player player) {
        List<Fluid> allowedFluids = getAllowedFluids();
        Fluid current = getFilter();

        // Find current index
        int index = allowedFluids.indexOf(current);
        if (index == -1) index = 0; // Default to start if unknown

        // Calculate next index
        int nextIndex = (index + 1) % allowedFluids.size();
        Fluid nextFluid = allowedFluids.get(nextIndex);

        // Update BE
        setFilter(nextFluid);

        // Play Sound
        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.BLOCKS, 0.5f, 1.2f);

        // Send Message
        if (!level.isClientSide) {
            String fluidName = (nextFluid == Fluids.EMPTY) ? "None (Allow All)" : ForgeRegistries.FLUIDS.getKey(nextFluid).getPath();
            player.displayClientMessage(Component.literal("§6[Filter Set]: §f" + fluidName), true);
        }
    }

    // Centralized List of Filterable Fluids
    default List<Fluid> getAllowedFluids() {
        return List.of(
                Fluids.EMPTY, // Index 0: No Filter
                Fluids.WATER,
                Fluids.LAVA,
                ModFluids.BLOOD_SOURCE.get(),
                ModFluids.CORRUPTED_BLOOD_SOURCE.get(),
                ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(),
                ModFluids.VISCERAL_BLOOD_SOURCE.get()
        );
    }
}