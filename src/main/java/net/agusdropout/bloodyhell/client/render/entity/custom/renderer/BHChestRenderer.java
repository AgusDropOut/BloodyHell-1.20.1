package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;

import net.agusdropout.bloodyhell.client.render.entity.custom.model.*;

import com.google.common.collect.ImmutableMap;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.EnumMap;
import java.util.Map;

public class BHChestRenderer <T extends ChestBlockEntity> extends ChestRenderer<T> {
    public static final Map<Block, EnumMap<ChestType, Material>> MATERIALS;

    static {
        ImmutableMap.Builder<Block, EnumMap<ChestType, Material>> builder = ImmutableMap.builder();

        builder.put(ModBlocks.BLOOD_WOOD_CHEST.get(), chestMaterial("blood_wood"));
        MATERIALS = builder.build();
    }

    public BHChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Material getMaterial(T entity, ChestType chestType) {
        EnumMap<ChestType, Material> b = MATERIALS.get(entity.getBlockState().getBlock());

        if (b == null) return super.getMaterial(entity, chestType);

        Material material = b.get(chestType);

        return material != null ? material : super.getMaterial(entity, chestType);
    }

    private static EnumMap<ChestType, Material> chestMaterial(String type) {
        EnumMap<ChestType, Material> map = new EnumMap<>(ChestType.class);

        map.put(ChestType.SINGLE, new Material(Sheets.CHEST_SHEET, BloodyHell.prefix("entity/chest/" + type + "/" + "single")));
        map.put(ChestType.LEFT, new Material(Sheets.CHEST_SHEET, BloodyHell.prefix("entity/chest/" + type + "/" +  "left")));
        map.put(ChestType.RIGHT, new Material(Sheets.CHEST_SHEET, BloodyHell.prefix("entity/chest/" + type + "/" +  "right")));

        return map;
    }
}
