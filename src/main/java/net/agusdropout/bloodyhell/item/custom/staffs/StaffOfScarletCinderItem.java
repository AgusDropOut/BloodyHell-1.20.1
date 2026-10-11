package net.agusdropout.bloodyhell.item.custom.staffs;

import net.agusdropout.bloodyhell.item.client.staffs.StaffOfScarletCinderRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class StaffOfScarletCinderItem extends BaseStaffItem {
    public StaffOfScarletCinderItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getId() {
        return "staff_of_scarlet_cinder";
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private StaffOfScarletCinderRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new StaffOfScarletCinderRenderer();
                }
                return this.renderer;
            }
        });
    }
}