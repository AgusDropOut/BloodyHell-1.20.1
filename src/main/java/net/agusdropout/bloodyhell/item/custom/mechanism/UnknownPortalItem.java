package net.agusdropout.bloodyhell.item.custom.mechanism;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.minecraft.world.level.block.Block;

public class UnknownPortalItem extends BaseGeckoBlockItem {
    public UnknownPortalItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getId() {
        return "unknown_portal_block";
    }

    @Override
    public boolean hasGlowingLayer() {
        return true;
    }

}
