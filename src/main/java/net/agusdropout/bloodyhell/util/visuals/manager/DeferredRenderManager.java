package net.agusdropout.bloodyhell.util.visuals.manager;

import java.util.function.Consumer;

public interface DeferredRenderManager {
    String name();

    void renderAndClear(FrameContext frame);

    static DeferredRenderManager of(String name, Consumer<FrameContext> action) {
        return new DeferredRenderManager() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public void renderAndClear(FrameContext frame) {
                action.accept(frame);
            }
        };
    }
}
