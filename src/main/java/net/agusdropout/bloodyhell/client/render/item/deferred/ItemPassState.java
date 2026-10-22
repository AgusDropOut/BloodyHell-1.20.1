package net.agusdropout.bloodyhell.client.render.item.deferred;

import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.world.item.ItemDisplayContext;

public final class ItemPassState {
    private static boolean managerRendering = false;
    private static boolean childMorphing = false;

    private ItemPassState() {}

    public static boolean isManagerRendering() {
        return managerRendering;
    }

    public static boolean isChildMorphing() {
        return childMorphing;
    }

    public static void managerPass(Runnable pass) {
        managerRendering = true;
        try {
            pass.run();
        } finally {
            managerRendering = false;
        }
    }

    public static void childPass(Runnable pass) {
        childMorphing = true;
        try {
            pass.run();
        } finally {
            childMorphing = false;
        }
    }

    public static VertexSorting sortingFor(ItemDisplayContext transform) {
        return (transform == ItemDisplayContext.GUI) ? VertexSorting.ORTHOGRAPHIC_Z : VertexSorting.DISTANCE_TO_ORIGIN;
    }
}
