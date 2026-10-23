package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public record SpellCastContext(Level level, Player player, BaseSpellBookItem<?> book, ItemStack stack, InteractionHand hand, SpellModifiers modifiers) {
    public List<Gem> gems() {
        return GemType.getGemsFromWeapon(stack);
    }

    public <T extends Entity & IGemSpell> void spawn(T spell) {
        if (modifiers.damage() != 1.0f) {
            spell.setBaseDamage(spell.getBaseDamage() * modifiers.damage());
        }
        level.addFreshEntity(spell);
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
    }
}
