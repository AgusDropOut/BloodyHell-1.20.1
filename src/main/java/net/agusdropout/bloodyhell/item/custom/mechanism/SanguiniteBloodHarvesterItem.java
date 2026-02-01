package net.agusdropout.bloodyhell.item.custom.mechanism;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.minecraft.world.level.block.Block;

public class SanguiniteBloodHarvesterItem extends BaseGeckoBlockItem {
    public SanguiniteBloodHarvesterItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getId() {
        return "sanguinite_blood_harvester";
    }
}
