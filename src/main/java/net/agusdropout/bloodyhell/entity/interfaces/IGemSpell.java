package net.agusdropout.bloodyhell.entity.interfaces;

import net.agusdropout.bloodyhell.config.ModCommonConfig;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.item.custom.base.GemType;
import net.agusdropout.bloodyhell.item.custom.base.SpellType;

import java.util.List;

public interface IGemSpell {
    default void configureSpell(List<Gem> gems) {
        if (gems == null) return;
        for (Gem gem : gems) {
            String statKey = gem.getStat();
            double value = gem.getValue();
            switch (statKey) {
                case GemType.TYPE_DAMAGE -> increaseSpellDamage(value);
                case GemType.TYPE_SIZE -> increaseSpellSize(value);
                case GemType.TYPE_DURATION -> increaseSpellDuration((int)value*20);
                case GemType.TYPE_QUANTITY -> increaseSpellQuantity(value);
            }
        }
    }

    default void applyConfigScaling(SpellType spellType) {
        double finalDamageMulti = ModCommonConfig.getFinalDamageMultiplier(spellType.getId());
        if (finalDamageMulti != 1.0) {
            setBaseDamage((float) (getBaseDamage() * finalDamageMulti));
        }
    }

    float getBaseDamage();
    void setBaseDamage(float damage);

    void increaseSpellDamage(double amount);
    void increaseSpellSize(double amount);
    void increaseSpellDuration(int amount);

    default void increaseSpellQuantity(double amount) {

    }

}
