package net.agusdropout.bloodyhell.util.recipe;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class RecipeUtils {
    public RecipeUtils() {
    }

    public boolean isSetEqual(List<Item> setA, List<Item> setB) {
        if (setA.size() != setB.size()) return false;
        List<Item> copyB = new ArrayList<Item>(setB);
        for (Item item : setA) {
            if (!copyB.remove(item)) return false;
        }
        return copyB.isEmpty();
    }
}