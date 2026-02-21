package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.entity.client.animations.RhnullPainThroneAnimations;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullPainThroneEntity;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class RhnullPainThroneModel extends HierarchicalModel<RhnullPainThroneEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("bloodyhell", "rhnull_pain_throne"), "main");

    private final ModelPart root;
    private final ModelPart rhnull_pain_throne;

    public RhnullPainThroneModel(ModelPart root) {
        this.root = root;
        this.rhnull_pain_throne = root.getChild("rhnull_pain_throne");
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition rhnull_pain_throne = partdefinition.addOrReplaceChild("rhnull_pain_throne", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0946F, 31.0261F, -14.096F, 26.0F, 2.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(104, 0).addBox(-6.0946F, -19.9739F, -8.096F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(106, 98).addBox(-5.0946F, -28.9739F, -7.096F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(104, 15).addBox(-2.0946F, -30.9739F, -5.096F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(120, 60).addBox(4.9054F, -25.9739F, -3.096F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(120, 66).addBox(-10.0946F, -25.9739F, -3.096F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0946F, -29.0261F, 1.096F));

        PartDefinition cube_r1 = rhnull_pain_throne.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(116, 25).addBox(-2.5F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.8446F, -28.2239F, -1.596F, 0.0F, 0.0F, 1.1781F));

        PartDefinition cube_r2 = rhnull_pain_throne.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(120, 77).addBox(-1.5F, -1.5F, -1.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.3446F, -25.4739F, -1.596F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r3 = rhnull_pain_throne.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(104, 25).addBox(-2.5F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.6554F, -28.2239F, -1.596F, 0.0F, 0.0F, -1.1781F));

        PartDefinition cube_r4 = rhnull_pain_throne.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(120, 72).addBox(-2.5F, -1.5F, -1.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.1554F, -25.4739F, -1.596F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r5 = rhnull_pain_throne.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(120, 50).addBox(-2.0F, -1.0F, -4.0F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0946F, -29.9739F, -5.096F, 0.3927F, 0.0F, 0.0F));

        PartDefinition cube_r6 = rhnull_pain_throne.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 72).addBox(-8.5F, -46.98F, -1.0F, 17.0F, 2.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5946F, 30.0261F, -9.096F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r7 = rhnull_pain_throne.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 51).addBox(-8.5F, -45.0F, -1.0F, 17.0F, 2.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.4054F, 28.0261F, -9.096F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r8 = rhnull_pain_throne.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(116, 117).addBox(-9.5F, -45.0F, -1.0F, 3.0F, 46.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0946F, 30.0261F, 10.904F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r9 = rhnull_pain_throne.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(106, 117).addBox(-9.5F, -45.0F, -1.0F, 3.0F, 46.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.4054F, 30.0261F, 6.904F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r10 = rhnull_pain_throne.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(110, 50).addBox(-9.5F, -45.0F, -1.0F, 3.0F, 46.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.5946F, 30.0261F, 6.904F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r11 = rhnull_pain_throne.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(72, 28).addBox(-8.5F, -46.96F, -19.0F, 15.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(126, 137).addBox(2.5F, -36.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 137).addBox(-1.5F, -36.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 141).addBox(-5.5F, -36.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 141).addBox(2.5F, -41.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 141).addBox(-1.5F, -41.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 141).addBox(-5.5F, -41.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 64).addBox(2.5F, -26.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 68).addBox(-1.5F, -26.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 80).addBox(-5.5F, -26.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 84).addBox(2.5F, -31.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 88).addBox(-1.5F, -31.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 92).addBox(-5.5F, -31.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 19).addBox(2.5F, -21.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 23).addBox(-1.5F, -21.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 60).addBox(-5.5F, -21.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 141).addBox(2.5F, -11.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(40, 141).addBox(-1.5F, -11.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 141).addBox(-5.5F, -11.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(120, 90).addBox(2.5F, -16.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(120, 86).addBox(-1.5F, -16.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(120, 82).addBox(-5.5F, -16.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(72, 98).addBox(-8.5F, -45.0F, -1.0F, 15.0F, 46.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5946F, 30.0261F, 6.904F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r12 = rhnull_pain_throne.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(134, 129).addBox(3.5F, -31.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 133).addBox(-0.5F, -31.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(136, 15).addBox(-4.5F, -31.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 117).addBox(3.5F, -61.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 121).addBox(-0.5F, -61.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 125).addBox(-4.5F, -61.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(126, 133).addBox(3.5F, -56.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 72).addBox(-0.5F, -56.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 76).addBox(-4.5F, -56.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(128, 90).addBox(3.5F, -51.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(128, 94).addBox(-0.5F, -51.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(126, 129).addBox(-4.5F, -51.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(128, 23).addBox(3.5F, -46.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(128, 82).addBox(-0.5F, -46.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(128, 86).addBox(-4.5F, -46.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(126, 125).addBox(3.5F, -41.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(128, 15).addBox(-0.5F, -41.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(128, 19).addBox(-4.5F, -41.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(120, 94).addBox(3.5F, -36.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(126, 117).addBox(-0.5F, -36.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(126, 121).addBox(-4.5F, -36.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(-6.5F, -66.99F, -20.0F, 15.0F, 2.0F, 21.0F, new CubeDeformation(0.0F))
                .texOffs(38, 93).addBox(-6.5F, -65.0F, -1.0F, 15.0F, 46.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.4054F, 50.0261F, 6.904F, 0.0F, 0.7854F, 0.0F));

        PartDefinition outline = rhnull_pain_throne.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, 3.5F, 21.5F, -27.0F, -3.0F, -27.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(0.0F, -46.5F, 13.5F, -13.0F, -4.0F, -13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -49.5F, 12.5F, -11.0F, -10.0F, -11.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-4.0F, -58.5F, 12.5F, -5.0F, -3.0F, -9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(4.0F, -52.5F, 9.5F, -6.0F, -4.0F, -4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-11.0F, -52.5F, 9.5F, -6.0F, -4.0F, -4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.4054F, 30.0261F, -9.096F));

        PartDefinition cube_outline_r1 = outline.addOrReplaceChild("cube_outline_r1", CubeListBuilder.create().texOffs(0, 0).addBox(3.0F, 1.0F, 1.0F, -6.0F, -2.0F, -2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.25F, -58.25F, 7.5F, 0.0F, 0.0F, 1.1781F));

        PartDefinition cube_outline_r2 = outline.addOrReplaceChild("cube_outline_r2", CubeListBuilder.create().texOffs(0, 0).addBox(3.0F, 1.0F, 2.0F, -5.0F, -3.0F, -4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-18.75F, -55.5F, 7.5F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_outline_r3 = outline.addOrReplaceChild("cube_outline_r3", CubeListBuilder.create().texOffs(0, 0).addBox(3.0F, 1.0F, 1.0F, -6.0F, -2.0F, -2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.25F, -58.25F, 7.5F, 0.0F, 0.0F, -1.1781F));

        PartDefinition cube_outline_r4 = outline.addOrReplaceChild("cube_outline_r4", CubeListBuilder.create().texOffs(0, 0).addBox(2.0F, 1.0F, 2.0F, -5.0F, -3.0F, -4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.75F, -55.5F, 7.5F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_outline_r5 = outline.addOrReplaceChild("cube_outline_r5", CubeListBuilder.create().texOffs(0, 0).addBox(2.5F, 1.5F, 4.5F, -5.0F, -3.0F, -9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, -60.0F, 4.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition cube_outline_r6 = outline.addOrReplaceChild("cube_outline_r6", CubeListBuilder.create().texOffs(0, 0).addBox(9.0F, -44.48F, 18.5F, -18.0F, -3.0F, -20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_outline_r7 = outline.addOrReplaceChild("cube_outline_r7", CubeListBuilder.create().texOffs(0, 0).addBox(9.0F, -42.5F, 18.5F, -18.0F, -3.0F, -20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_outline_r8 = outline.addOrReplaceChild("cube_outline_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 1.5F, 1.5F, -4.0F, -47.0F, -3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.5F, 0.0F, 20.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_outline_r9 = outline.addOrReplaceChild("cube_outline_r9", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 1.5F, 1.5F, -4.0F, -47.0F, -3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.0F, 16.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_outline_r10 = outline.addOrReplaceChild("cube_outline_r10", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 1.5F, 1.5F, -4.0F, -47.0F, -3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-18.0F, 0.0F, 16.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_outline_r11 = outline.addOrReplaceChild("cube_outline_r11", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -44.46F, 1.5F, -16.0F, -3.0F, -21.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.0F, -33.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -33.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -33.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.0F, -38.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -38.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -38.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.0F, -23.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -23.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -23.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.0F, -28.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -28.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -28.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.0F, -18.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -18.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -18.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.0F, -8.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -8.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -8.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.0F, -13.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -13.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -13.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(7.0F, 1.5F, 1.5F, -16.0F, -47.0F, -3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, 0.0F, 16.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_outline_r12 = outline.addOrReplaceChild("cube_outline_r12", CubeListBuilder.create().texOffs(0, 0).addBox(6.0F, -28.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.0F, -28.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -28.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(6.0F, -58.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.0F, -58.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -58.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(6.0F, -53.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.0F, -53.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -53.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(6.0F, -48.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.0F, -48.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -48.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(6.0F, -43.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.0F, -43.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -43.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(6.0F, -38.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.0F, -38.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -38.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(6.0F, -33.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.0F, -33.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -33.5F, -0.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(9.0F, -64.49F, 1.5F, -16.0F, -3.0F, -22.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(9.0F, -18.5F, 1.5F, -16.0F, -47.0F, -3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.0F, 16.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition rightDoor = rhnull_pain_throne.addOrReplaceChild("rightDoor", CubeListBuilder.create(), PartPose.offset(12.393F, 5.1625F, -2.4086F));

        PartDefinition cube_r13 = rightDoor.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(56, 141).addBox(3.5F, -31.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(64, 141).addBox(-0.5F, -31.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(126, 141).addBox(-4.5F, -31.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 141).addBox(-4.5F, -26.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 27).addBox(-0.5F, -26.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 31).addBox(3.5F, -26.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 35).addBox(3.5F, -21.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 39).addBox(-0.5F, -21.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 43).addBox(-4.5F, -21.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 72).addBox(-4.5F, -16.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 76).addBox(-0.5F, -16.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 117).addBox(3.5F, -16.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 121).addBox(3.5F, -11.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 125).addBox(-0.5F, -11.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 129).addBox(-4.5F, -11.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 133).addBox(-4.5F, -6.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 137).addBox(-0.5F, -6.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(142, 141).addBox(3.5F, -6.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 15).addBox(3.5F, -1.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 19).addBox(-0.5F, -1.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 23).addBox(-4.5F, -1.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 93).addBox(-8.5F, -35.0F, -1.0F, 17.0F, 46.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.9876F, 14.8636F, -6.6874F, 0.0F, -0.7854F, 0.0F));

        PartDefinition leftDoor = rhnull_pain_throne.addOrReplaceChild("leftDoor", CubeListBuilder.create(), PartPose.offset(-12.5821F, 5.1625F, -2.4086F));

        PartDefinition cube_r14 = leftDoor.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(144, 47).addBox(2.5F, -16.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 51).addBox(-5.5F, -16.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 55).addBox(-1.5F, -16.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 59).addBox(2.5F, -11.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 63).addBox(-1.5F, -11.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 67).addBox(-5.5F, -11.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 80).addBox(-5.5F, -6.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 84).addBox(-1.5F, -6.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 88).addBox(2.5F, -6.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(144, 92).addBox(2.5F, -1.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 145).addBox(-1.5F, -1.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 145).addBox(-5.5F, -1.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 145).addBox(-5.5F, 4.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 145).addBox(-1.5F, 4.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 145).addBox(2.5F, 4.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(40, 145).addBox(2.5F, 9.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 145).addBox(-1.5F, 9.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(56, 145).addBox(-5.5F, 9.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(64, 145).addBox(2.5F, 14.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(126, 145).addBox(-1.5F, 14.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 145).addBox(-5.5F, 14.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(72, 50).addBox(-8.5F, -20.0F, -1.0F, 17.0F, 46.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.9876F, -0.1364F, -6.6874F, 0.0F, 0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(RhnullPainThroneEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);


        this.animate(entity.grabAnimationState, RhnullPainThroneAnimations.grabing, ageInTicks, 1.0f);
        this.animate(entity.closeAnimationState, RhnullPainThroneAnimations.close, ageInTicks, 1.0f);
        this.animate(entity.damageAnimationState, RhnullPainThroneAnimations.damaging, ageInTicks, 1.0f);
    }


}