package net.agusdropout.bloodyhell.client.render.entity.custom.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.render.entity.animation.CrystalPillarAnimations;
import net.agusdropout.bloodyhell.entity.custom.CrystalPillar;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrystalPillarModel <T extends CrystalPillar> extends HierarchicalModel<T> {
    private final ModelPart root;

    public CrystalPillarModel(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -16.0F, -4.0F, 8.0F, 16.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(26, 24).addBox(4.0F, -9.0F, -3.0F, 3.0F, 9.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(-3.5F, -18.0F, -3.0F, 7.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-7.0F, -5.0F, -3.0F, 3.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(32, 4).addBox(-1.0F, -5.0F, -6.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-5.0F, -2.0F, 4.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.emergeAnimationState, CrystalPillarAnimations.CRYSTAL_PILLAR_EMERGE, ageInTicks);
        this.animate(entity.retractAnimationState, CrystalPillarAnimations.CRYSTAL_PILLAR_RETRACT, ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
