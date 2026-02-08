package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;

import net.agusdropout.bloodyhell.entity.projectile.spell.BloodFireMeteorEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class BloodFireMeteorModel<T extends BloodFireMeteorEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(BloodyHell.MODID, "blood_fire_meteor_projectile"), "main");
    private final ModelPart bloodFireMeteor;

    public BloodFireMeteorModel(ModelPart root) {
        this.bloodFireMeteor = root.getChild("bloodFireMeteor");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bloodFireMeteor = partdefinition.addOrReplaceChild("bloodFireMeteor", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 62).addBox(-6.0F, -6.0F, -11.0F, 12.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(48, 32).addBox(-11.0F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(48, 56).addBox(8.0F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(-6.0F, -6.0F, 8.0F, 12.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-6.0F, 8.0F, -6.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 47).addBox(-6.0F, -11.0F, -6.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Continuous Rotation based on age
        float speed = 0.2f;
        this.bloodFireMeteor.xRot = ageInTicks * speed;
        this.bloodFireMeteor.yRot = ageInTicks * speed * 0.7f;
        this.bloodFireMeteor.zRot = ageInTicks * speed * 0.5f;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bloodFireMeteor.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}