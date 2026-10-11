package net.agusdropout.bloodyhell.item.custom.staffs;

import net.agusdropout.bloodyhell.item.client.staffs.IncompleteStaffOfThePureBloodedRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class IncompleteStaffOfThePureBloodedItem extends BaseStaffItem {
    public IncompleteStaffOfThePureBloodedItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getId() {
        return "incomplete_staff_of_the_pure_blooded";
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private IncompleteStaffOfThePureBloodedRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new IncompleteStaffOfThePureBloodedRenderer();
                }
                return this.renderer;
            }
        });
    }
}