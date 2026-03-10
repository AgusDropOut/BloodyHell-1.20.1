package net.agusdropout.bloodyhell.item.custom.altar;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.minecraft.world.level.block.Block;

public class BlasphemousBloodAltarItem extends BaseGeckoBlockItem  {

    public BlasphemousBloodAltarItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getId() {
        return "blasphemous_blood_altar";
    }
}
