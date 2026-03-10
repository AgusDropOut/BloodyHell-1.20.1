package net.agusdropout.bloodyhell.item.custom.altar;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.minecraft.world.level.block.Block;

public class MainBlasphemousBloodAltarItem extends BaseGeckoBlockItem {

    public MainBlasphemousBloodAltarItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getId() {
        return "main_blasphemous_blood_altar";
    }
}
