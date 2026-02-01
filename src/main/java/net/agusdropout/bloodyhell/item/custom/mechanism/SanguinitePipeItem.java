package net.agusdropout.bloodyhell.item.custom.mechanism;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.minecraft.world.level.block.Block;

public class SanguinitePipeItem extends BaseGeckoBlockItem {
    public SanguinitePipeItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getId() {
        return "sanguinite_pipe";
    }
}
