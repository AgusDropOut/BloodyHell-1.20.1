package net.agusdropout.bloodyhell.util.visuals;

import org.joml.Vector3f;

import java.util.List;

public enum SpellPalette {


    RHNULL(List.of(new Vector3f(1.0f, 0.9f, 0.0f), new Vector3f(1f, 0.8f, 0.0f), new Vector3f(1.0f,0.7f,0.0f)));





    private final List<Vector3f> colors;

    SpellPalette(List<Vector3f> colors) {
        this.colors = colors;
    }

    public Vector3f getColor(int index) {
        return colors.get(index % colors.size());
    }
    public Vector3f getRandomColor() {
        int index = (int) (Math.random() * colors.size());
        return colors.get(index);
    }

}
