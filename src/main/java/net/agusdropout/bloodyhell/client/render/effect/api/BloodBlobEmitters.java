package net.agusdropout.bloodyhell.client.render.effect.api;

import java.util.HashSet;
import java.util.Set;

public final class BloodBlobEmitters {
    private static final Set<IBloodBlobEmitter> ACTIVE = new HashSet<>();

    private BloodBlobEmitters() {}

    public static void register(IBloodBlobEmitter emitter) {
        ACTIVE.add(emitter);
    }

    public static void unregister(IBloodBlobEmitter emitter) {
        ACTIVE.remove(emitter);
    }

    public static Set<IBloodBlobEmitter> active() {
        return ACTIVE;
    }
}
