package net.agusdropout.bloodyhell.entity.base;

import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.minecraft.world.level.Level;

public interface InsightEntity {

    float getMinimumInsight();

    Level level();

    default boolean hasSufficientClientInsight() {
        return !level().isClientSide || ClientInsightData.canSee(this);
    }
}
