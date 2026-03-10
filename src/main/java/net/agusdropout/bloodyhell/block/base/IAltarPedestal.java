package net.agusdropout.bloodyhell.block.base;

import net.minecraft.world.item.Item;
import java.util.List;

public interface IAltarPedestal {
    List<Item> getItemsInside();
    boolean clearItemsInside();
}