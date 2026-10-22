package net.agusdropout.bloodyhell.client.render.deferred.api;

public interface DeferredRenderObserver {
    void begin();

    void mark(String label);
}
