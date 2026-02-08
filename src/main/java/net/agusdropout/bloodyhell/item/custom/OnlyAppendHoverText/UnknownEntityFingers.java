package net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnknownEntityFingers extends Item {
    public UnknownEntityFingers(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.literal("Obtained by sacrificing a mob with the heretic dagger").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }
}
