package net.agusdropout.bloodyhell.entity.interfaces;

import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.GemType;

import java.util.List;

public interface IGemSpell {
    default void configureSpell(List<Gem> gems) {
        for (Gem gem : gems) {
            String statKey = gem.getStat(); // e.g. "damage"
            double value = gem.getValue();
            switch (statKey) {
                case GemType.TYPE_DAMAGE -> increaseSpellDamage(value);
                case GemType.TYPE_SIZE -> increaseSpellSize(value);
                case GemType.TYPE_DURATION -> increaseSpellDuration((int)value*20); // converting seconds to ticks
                case GemType.TYPE_QUANTITY -> increaseSpellQuantity(value);
            }
        }
    }
    void increaseSpellDamage(double amount);
    void increaseSpellSize(double amount);
    void increaseSpellDuration(int amount);
    /* Optional method, could be handled by the book itself */
    default void increaseSpellQuantity(double amount) {

    }

}
