package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.render.entity.animation.CrystalPillarAnimations;
import net.agusdropout.bloodyhell.client.render.entity.animation.VesperAnimations;
import net.agusdropout.bloodyhell.entity.custom.VesperEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VesperModel<T extends VesperEntity> extends HierarchicalModel<T>{


    private final ModelPart root;

    public VesperModel(ModelPart root) {
        this.root = root.getChild("vesper");
    }



    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition vesper = partdefinition.addOrReplaceChild("vesper", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -22.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(28, 0).addBox(-4.0F, -21.0F, 3.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 20).addBox(-4.0F, -17.0F, 7.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 49).addBox(-3.0F, -23.0F, 5.0F, 6.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition legs = vesper.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftLeg = legs.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(14, 32).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -9.0F, 0.0F));

        PartDefinition rightLeg = legs.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 32).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -9.0F, 0.0F));

        PartDefinition head = vesper.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -8.2F, -3.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(44, 41).addBox(4.0F, -6.2F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 46).addBox(-8.0F, -6.2F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -21.8F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 46).addBox(-3.0F, 0.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -1.2F, 0.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(44, 45).addBox(-1.0F, -2.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -9.2F, 0.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition skirt = vesper.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(28, 42).addBox(-4.0F, -10.0F, -3.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(44, 12).addBox(-4.0F, -10.0F, 3.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r3 = skirt.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(44, 33).addBox(-3.0F, -2.0F, -1.0F, 6.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -8.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r4 = skirt.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(44, 25).addBox(-3.0F, -2.0F, -1.0F, 6.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -8.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition arms = vesper.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rightArm = arms.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(28, 12).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -20.5F, 0.0F));

        PartDefinition leftArm = arms.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(28, 27).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -20.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(VesperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.attackAnimationState, VesperAnimations.VesperAttack,ageInTicks, 1f);
        this.animate(entity.idleAnimationState, VesperAnimations.VesperIdle,ageInTicks, 1f);
        this.animateWalk(VesperAnimations.VesperWalk, limbSwing, limbSwingAmount, 2f, 2.5f);
        ModelPart head = this.root.getChild("head");
        head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        head.xRot = headPitch * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
    @Override
    public ModelPart root() {
        return this.root;
    }
}
