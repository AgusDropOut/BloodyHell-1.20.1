package net.agusdropout.bloodyhell.client.render.deferred.api;

public record EffectState(Runnable begin, Runnable end) {
    public static final EffectState NONE = new EffectState(() -> {}, () -> {});
}
