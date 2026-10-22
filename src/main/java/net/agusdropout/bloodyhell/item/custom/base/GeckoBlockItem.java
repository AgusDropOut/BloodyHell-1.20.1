package net.agusdropout.bloodyhell.item.custom.base;

import net.minecraft.world.level.block.Block;

public class GeckoBlockItem extends BaseGeckoBlockItem {
    private final String id;
    private final String textureName;
    private final boolean glowingLayer;

    public GeckoBlockItem(Block block, Properties properties, String id, String textureName, boolean glowingLayer) {
        super(block, properties);
        this.id = id;
        this.textureName = textureName;
        this.glowingLayer = glowingLayer;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTextureName() {
        return textureName;
    }

    @Override
    public boolean hasGlowingLayer() {
        return glowingLayer;
    }
}
