package net.agusdropout.bloodyhell.item.custom.staffs;

import net.agusdropout.bloodyhell.item.client.staffs.StaffOfTheGoldenSorrowRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class StaffOfTheGoldenSorrowItem extends BaseStaffItem {

    public StaffOfTheGoldenSorrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getId() {
        return "staff_of_the_golden_sorrow";
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private StaffOfTheGoldenSorrowRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new StaffOfTheGoldenSorrowRenderer();
                }
                return this.renderer;
            }
        });
    }

}