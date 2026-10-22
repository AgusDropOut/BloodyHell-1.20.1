package net.agusdropout.bloodyhell.item;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class ModToolTiers {
    public static final Tier SANGUINITE = TierSortingRegistry.registerTier(
            new ForgeTier(5, 1500, 9f, 5f, 25,
                    ModTags.Blocks.NEEDS_SANGUINITE_TOOL, () -> Ingredient.of(ModItems.SANGUINITE.get())),
            new ResourceLocation(BloodyHell.MODID, "sanguinite"), List.of(Tiers.NETHERITE), List.of());
    public static final Tier BLASPHEMITE = TierSortingRegistry.registerTier(
            new ForgeTier(6, 1700, 9.5f, 5.5f, 26,
                    ModTags.Blocks.NEEDS_BLASPHEMITE_TOOL, () -> Ingredient.of(ModItems.BLASPHEMITE.get())),
            new ResourceLocation(BloodyHell.MODID, "blasphemite"), List.of(ModToolTiers.SANGUINITE), List.of());
    public static final Tier RHNULL = TierSortingRegistry.registerTier(
            new ForgeTier(7, 3000, 10f, 7f, 28,
                    ModTags.Blocks.NEEDS_RHNULL_TOOL, () -> Ingredient.of(ModItems.RHNULL.get())),
            new ResourceLocation(BloodyHell.MODID, "rhnull"), List.of(ModToolTiers.BLASPHEMITE), List.of());



}
