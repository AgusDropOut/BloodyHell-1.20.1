package net.agusdropout.bloodyhell.util.bones;

import net.minecraft.client.model.geom.ModelPart;

public record PoseSnapshot(
        float x, float y, float z,
        float xRot, float yRot, float zRot,
        float xScale, float yScale, float zScale
) {
    public PoseSnapshot(ModelPart part) {
        this(
                part.x, part.y, part.z,
                part.xRot, part.yRot, part.zRot,
                part.xScale, part.yScale, part.zScale
        );
    }

    public void restore(ModelPart part) {
        part.x = this.x;
        part.y = this.y;
        part.z = this.z;
        part.xRot = this.xRot;
        part.yRot = this.yRot;
        part.zRot = this.zRot;
        part.xScale = this.xScale;
        part.yScale = this.yScale;
        part.zScale = this.zScale;
    }
}