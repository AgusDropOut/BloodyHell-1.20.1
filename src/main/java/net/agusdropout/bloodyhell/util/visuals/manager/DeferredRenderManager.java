package net.agusdropout.bloodyhell.util.visuals.manager;

import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.function.Consumer;

public interface DeferredRenderManager {
    String name();

    void renderAndClear(RenderLevelStageEvent event);

    static DeferredRenderManager of(String name, Consumer<RenderLevelStageEvent> action) {
        return new DeferredRenderManager() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public void renderAndClear(RenderLevelStageEvent event) {
                action.accept(event);
            }
        };
    }
}
