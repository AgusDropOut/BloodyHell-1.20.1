package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RhnullImpalerModel extends EntityModel<RhnullImpalerEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(BloodyHell.MODID, "rhnull_impaler_entity"), "main");

    private final ModelPart impaler;

    public RhnullImpalerModel(ModelPart root) {
        this.impaler = root.getChild("impaler");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Root Bone 'impaler'
        PartDefinition impaler = partdefinition.addOrReplaceChild("impaler", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -32.7444F, -1.0F, 2.0F, 72.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 13).addBox(-2.0F, 39.2556F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-2.0F, -37.7444F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(38, 29).addBox(-2.0F, -28.7444F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 22).addBox(-2.25F, -24.7444F, -2.25F, 4.5F, 2.0F, 4.5F, new CubeDeformation(0.0F))
                .texOffs(38, 37).addBox(-2.0F, -5.7444F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 0).addBox(-2.0F, 20.2556F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 23.2444F, 0.25F, 1.5708F, 0.0F, 0.0F));

        // Child cubes
        PartDefinition cube_r1 = impaler.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(8, 42).addBox(-2.0F, -9.0F, 20.05F, 1.0F, 1.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(40, 12).addBox(-2.0F, -8.0F, 20.05F, 2.0F, 2.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(8, 15).addBox(-2.0F, -6.0F, 20.05F, 4.0F, 11.0F, 3.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -34.2444F, -22.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r2 = impaler.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(22, 41).addBox(1.0F, -9.0F, 20.05F, 1.0F, 1.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(40, 6).addBox(0.0F, -8.0F, 20.05F, 2.0F, 2.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(8, 0).addBox(-2.0F, -6.0F, 20.05F, 4.0F, 11.0F, 3.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -34.2444F, -22.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r3 = impaler.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(8, 30).addBox(-0.5F, -5.75F, 20.1F, 2.5F, 7.75F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -26.7444F, -22.0F, 0.0F, 0.0F, 0.5672F));

        PartDefinition cube_r4 = impaler.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 29).addBox(-2.0F, -5.75F, 20.1F, 2.5F, 7.75F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -26.7444F, -22.0F, 0.0F, 0.0F, -0.5672F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(RhnullImpalerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // --- LEVITATION ANIMATION ---
        // Your model's base Y is 23.2444F.
        float baseHeight = 23.2444F;
        float bob = Mth.sin(ageInTicks * 0.1f) * 2.0f; // Speed 0.1, Amplitude 2.0

        if (!entity.isLaunched()) {
            this.impaler.y = baseHeight + bob;
        } else {
            this.impaler.y = baseHeight;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        impaler.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}