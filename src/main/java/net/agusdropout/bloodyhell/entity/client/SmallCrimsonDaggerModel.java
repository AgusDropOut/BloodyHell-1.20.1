package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class SmallCrimsonDaggerModel<T extends Entity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(BloodyHell.MODID, "small_crimson_dagger"), "main");

    private final ModelPart small_crimson_dagger;

    public SmallCrimsonDaggerModel(ModelPart root) {
        this.small_crimson_dagger = root.getChild("small_crimson_dagger");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // --- BURROWING FIX ---
        // 1. Y = 0.0F: Centers the model vertically on the hitbox (no flying above/below).
        // 2. Z = -6.0F: Moves the model BACKWARDS by 6 pixels.
        //    Since the dagger is 11 pixels long, shifting back ~5.5 pixels aligns the tip
        //    with the entity's location (0,0,0). The body will now trail behind the impact.

        partdefinition.addOrReplaceChild("small_crimson_dagger",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, -5.5F, 0.0F, 4.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, -1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Rotation is handled by the Renderer via PoseStack
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        small_crimson_dagger.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}