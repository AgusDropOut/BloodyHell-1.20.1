package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.entity.projectile.ViscousProjectileEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class ViscousProjectileModel<T extends ViscousProjectileEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("bloodyhell", "viscous_projectile"), "main");
    private final ModelPart root;
    private final ModelPart c1;
    private final ModelPart c2;
    private final ModelPart c3;
    private final ModelPart c4;
    private final ModelPart c5;
    private final ModelPart c6;
    private final ModelPart c7;

    public ViscousProjectileModel(ModelPart root) {
        this.root = root.getChild("viscous_projectile");
        this.c1 = this.root.getChild("c1");
        this.c2 = this.root.getChild("c2");
        this.c3 = this.root.getChild("c3");
        this.c4 = this.root.getChild("c4");
        this.c5 = this.root.getChild("c5");
        this.c6 = this.root.getChild("c6");
        this.c7 = this.root.getChild("c7");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();


        PartDefinition viscous_projectile = partdefinition.addOrReplaceChild("viscous_projectile", CubeListBuilder.create(), PartPose.offset(0.9286F, 17.2143F, 1.0357F));

        PartDefinition c2 = viscous_projectile.addOrReplaceChild("c2", CubeListBuilder.create().texOffs(0, 21).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.6786F, 2.2857F, -4.0357F));

        PartDefinition c3 = viscous_projectile.addOrReplaceChild("c3", CubeListBuilder.create().texOffs(0, 38).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.6786F, 3.2857F, 3.9643F));

        PartDefinition c4 = viscous_projectile.addOrReplaceChild("c4", CubeListBuilder.create().texOffs(33, 36).addBox(-3.5F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.1786F, -3.2143F, 2.2143F));

        PartDefinition c5 = viscous_projectile.addOrReplaceChild("c5", CubeListBuilder.create().texOffs(41, 0).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.8214F, -3.2143F, 4.2143F));

        PartDefinition c1 = viscous_projectile.addOrReplaceChild("c1", CubeListBuilder.create().texOffs(33, 21).addBox(-3.5F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(2.8214F, -2.7143F, -4.5357F));

        PartDefinition c6 = viscous_projectile.addOrReplaceChild("c6", CubeListBuilder.create().texOffs(0, 51).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.8214F, 3.7857F, -0.7857F));

        PartDefinition c7 = viscous_projectile.addOrReplaceChild("c7", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.9286F, -0.2143F, -1.0357F));

        return LayerDefinition.create(meshdefinition, 64, 64);

    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float speed = ageInTicks * 0.3F;

        this.root.xRot = ageInTicks * 0.05f;
        this.root.yRot = ageInTicks * 0.06f;
        this.root.zRot = ageInTicks * 0.07f;

        // Cube 1 (Core)
        this.c1.xScale = 1.0F + (float) Math.sin(speed) * 0.1F;
        this.c1.yScale = 1.0F + (float) Math.cos(speed) * 0.1F;
        this.c1.zScale = 1.0F + (float) Math.sin(speed + 1.57F) * 0.1F;

        // Cube 2
        float speed2 = ageInTicks * 0.35F;
        this.c2.xScale = 1.0F + (float) Math.sin(speed2 + 0.78F) * 0.2F;
        this.c2.yScale = 1.0F + (float) Math.cos(speed2 + 0.78F) * 0.2F;
        this.c2.zScale = 1.0F + (float) Math.sin(speed2 + 2.35F) * 0.2F;

        // Cube 3
        float speed3 = ageInTicks * 0.28F;
        this.c3.xScale = 1.0F + (float) Math.cos(speed3 + 1.57F) * 0.15F;
        this.c3.yScale = 1.0F + (float) Math.sin(speed3 + 1.57F) * 0.15F;
        this.c3.zScale = 1.0F + (float) Math.cos(speed3 + 3.14F) * 0.15F;

        // Cube 4
        float speed4 = ageInTicks * 0.40F;
        this.c4.xScale = 1.0F + (float) Math.sin(speed4 + 2.35F) * 0.25F;
        this.c4.yScale = 1.0F + (float) Math.cos(speed4 + 2.35F) * 0.25F;
        this.c4.zScale = 1.0F + (float) Math.sin(speed4 + 3.92F) * 0.25F;

        // Cube 5
        float speed5 = ageInTicks * 0.32F;
        this.c5.xScale = 1.0F + (float) Math.cos(speed5 + 3.14F) * 0.2F;
        this.c5.yScale = 1.0F + (float) Math.sin(speed5 + 3.14F) * 0.2F;
        this.c5.zScale = 1.0F + (float) Math.cos(speed5 + 4.71F) * 0.2F;

        // Cube 6
        float speed6 = ageInTicks * 0.37F;
        this.c6.xScale = 1.0F + (float) Math.sin(speed6 + 3.92F) * 0.15F;
        this.c6.yScale = 1.0F + (float) Math.cos(speed6 + 3.92F) * 0.15F;
        this.c6.zScale = 1.0F + (float) Math.sin(speed6 + 5.49F) * 0.15F;

        // Cube 7
        float speed7 = ageInTicks * 0.42F;
        this.c7.xScale = 1.0F + (float) Math.cos(speed7 + 4.71F) * 0.2F;
        this.c7.yScale = 1.0F + (float) Math.sin(speed7 + 4.71F) * 0.2F;
        this.c7.zScale = 1.0F + (float) Math.cos(speed7) * 0.2F;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}