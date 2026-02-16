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

        PartDefinition impaler = partdefinition.addOrReplaceChild("impaler", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.25F, 23.2444F, 0.25F, 1.5708F, 0.0F, 0.0F));

        PartDefinition cube_r1 = impaler.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(8, 42).addBox(-16.7773F, -39.4493F, -1.95F, 1.0F, 1.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(40, 12).addBox(-16.7773F, -38.4493F, -1.95F, 2.0F, 2.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(8, 15).addBox(-16.7773F, -36.4493F, -1.95F, 4.0F, 11.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-14.8773F, -37.5493F, 2.85F, -2.8F, -2.8F, -5.7F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-13.8773F, -35.5493F, 2.85F, -3.8F, -3.8F, -5.7F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-11.8773F, -24.5493F, 2.85F, -5.8F, -12.8F, -5.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4579F, 0.0F, 0.0F, 0.0F, -2.7489F));

        PartDefinition cube_r2 = impaler.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(22, 41).addBox(15.7773F, -39.4493F, -1.95F, 1.0F, 1.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(40, 6).addBox(14.7773F, -38.4493F, -1.95F, 2.0F, 2.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(8, 0).addBox(12.7773F, -36.4493F, -1.95F, 4.0F, 11.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(17.6773F, -37.5493F, 2.85F, -2.8F, -2.8F, -5.7F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(17.6773F, -35.5493F, 2.85F, -3.8F, -3.8F, -5.7F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(17.6773F, -24.5493F, 2.85F, -5.8F, -12.8F, -5.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4579F, 0.0F, 0.0F, 0.0F, 2.7489F));

        PartDefinition cube_r3 = impaler.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, 20.7135F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(38, 37).addBox(-2.0F, -5.2865F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 22).addBox(-2.25F, -24.2865F, -2.25F, 4.5F, 2.0F, 4.5F, new CubeDeformation(0.0F))
                .texOffs(38, 29).addBox(-2.0F, -28.2865F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-2.0F, -37.2865F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 13).addBox(-2.0F, 39.7135F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -32.2865F, -1.0F, 2.0F, 72.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.9F, 23.6135F, 2.9F, -5.8F, -3.8F, -5.8F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.9F, -2.3865F, 2.9F, -5.8F, -3.8F, -5.8F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.15F, -21.3865F, 3.15F, -6.3F, -3.8F, -6.3F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.9F, -23.3865F, 2.9F, -5.8F, -5.8F, -5.8F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.9F, -27.3865F, 2.9F, -5.8F, -10.8F, -5.8F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.9F, 45.6135F, 2.9F, -5.8F, -6.8F, -5.8F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.9F, 40.6135F, 1.9F, -3.8F, -73.8F, -3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4579F, 0.0F, 0.0F, 0.0F, 3.1416F));

        PartDefinition cube_r4 = impaler.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(8, 30).addBox(-13.7803F, -28.4571F, -1.9F, 2.5F, 7.75F, 3.8F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-10.3803F, -19.8071F, 2.8F, -4.3F, -9.55F, -5.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4579F, 0.0F, 0.0F, 0.0F, -2.5744F));

        PartDefinition cube_r5 = impaler.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(24, 29).addBox(11.2803F, -28.4571F, -1.9F, 2.5F, 7.75F, 3.8F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(14.6803F, -19.8071F, 2.8F, -4.3F, -9.55F, -5.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4579F, 0.0F, 0.0F, 0.0F, 2.5744F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(RhnullImpalerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        float baseHeight = 23.2444F;
        float bob = Mth.sin(ageInTicks * 0.1f) * 2.0f;

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