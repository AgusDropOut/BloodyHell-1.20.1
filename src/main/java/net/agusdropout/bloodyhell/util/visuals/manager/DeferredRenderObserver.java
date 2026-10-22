package net.agusdropout.bloodyhell.util.visuals.manager;

public interface DeferredRenderObserver {
    void begin();

    void mark(String label);
}
