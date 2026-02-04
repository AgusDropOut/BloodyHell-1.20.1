package net.agusdropout.bloodyhell.item.custom.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BasePowerGemItem extends Item {
    public BasePowerGemItem(Properties properties) {
        super(properties);
    }


@Override
public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    if (GemType.hasValidStat(stack)){
         String stat = GemType.getStatfromStack(stack);
         double value = stack.getTag().getDouble(stat);
        tooltip.add(Component.literal("Spell " + stat+ ": " + GemType.getFormattedBonus(stat,value)).withStyle(GemType.getChatFormating(stat)));
    }else {
        tooltip.add(Component.literal("Unidentified (Harvest to roll stats)")
                .withStyle(ChatFormatting.GRAY));
    }



    super.appendHoverText(stack, level, tooltip, flag);
}




    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return Optional.empty();
    }
}
