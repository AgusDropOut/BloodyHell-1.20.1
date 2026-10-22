package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.minecraft.world.level.block.Block;

public class SelioraRestingItem extends BaseGeckoBlockItem {
    public SelioraRestingItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getId() {
        return "seliora_resting_block";
    }

    @Override
    public String getTextureName() {
        return "seliora_resting_block_texture";
    }

    @Override
    public boolean hasGlowingLayer() {
        return false;
    }
}
