package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousImpalerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class BlasphemousImpalerEntityModel extends EntityModel<BlasphemousImpalerEntity> {
    // Usamos tu LayerLocation centralizado
    public static final ModelLayerLocation LAYER_LOCATION = ModModelLayers.BLASPHEMOUS_IMPALER_ENTITY;

    private final ModelPart blasphemous_impaler;
    private final ModelPart upper_anim;
    private final ModelPart a4;
    private final ModelPart a3;
    private final ModelPart a2;
    private final ModelPart a1;
    private final ModelPart lower_anim;

    // CORRECCIÓN: Renombradas variables numéricas (1 -> part1)
    private final ModelPart part1;
    private final ModelPart part2;
    private final ModelPart part3;
    private final ModelPart part4;

    private final ModelPart eye1;
    private final ModelPart b2;
    private final ModelPart b1;
    private final ModelPart eye2;
    private final ModelPart b3;
    private final ModelPart b4;

    public BlasphemousImpalerEntityModel(ModelPart root) {
        this.blasphemous_impaler = root.getChild("blasphemous_impaler");
        this.upper_anim = this.blasphemous_impaler.getChild("upper_anim");
        this.a4 = this.upper_anim.getChild("a4");
        this.a3 = this.upper_anim.getChild("a3");
        this.a2 = this.upper_anim.getChild("a2");
        this.a1 = this.upper_anim.getChild("a1");
        this.lower_anim = this.blasphemous_impaler.getChild("lower_anim");

        // CORRECCIÓN: Mapeamos los nombres originales ("1") a variables válidas (part1)
        this.part1 = this.lower_anim.getChild("1");
        this.part2 = this.lower_anim.getChild("2");
        this.part3 = this.lower_anim.getChild("3");
        this.part4 = this.lower_anim.getChild("4");

        this.eye1 = this.blasphemous_impaler.getChild("eye1");
        this.b2 = this.eye1.getChild("b2");
        this.b1 = this.eye1.getChild("b1");
        this.eye2 = this.blasphemous_impaler.getChild("eye2");
        this.b3 = this.eye2.getChild("b3");
        this.b4 = this.eye2.getChild("b4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition blasphemous_impaler = partdefinition.addOrReplaceChild("blasphemous_impaler", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -41.0F, -1.0F, 2.0F, 72.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 13).addBox(-2.0F, 31.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-2.0F, -46.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(38, 29).addBox(-2.0F, -37.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 22).addBox(-2.25F, -33.0F, -2.25F, 4.5F, 2.0F, 4.5F, new CubeDeformation(0.0F))
                .texOffs(38, 37).addBox(-2.0F, -14.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 0).addBox(-2.0F, 12.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(38, 53).addBox(-1.0F, -35.5F, -2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(54, 38).addBox(-1.0F, -35.5F, 0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 1.5708F, 0.0F, 0.0F));;

        PartDefinition cube_r1 = blasphemous_impaler.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(52, 23).addBox(-2.0F, -9.0F, -1.95F, 1.0F, 1.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(52, 17).addBox(-2.0F, -8.0F, -1.95F, 2.0F, 2.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(8, 15).addBox(-2.0F, -6.0F, -1.95F, 4.0F, 11.0F, 3.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -42.5F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r2 = blasphemous_impaler.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(28, 53).addBox(1.0F, -9.0F, -1.95F, 1.0F, 1.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(8, 52).addBox(0.0F, -8.0F, -1.95F, 2.0F, 2.0F, 3.9F, new CubeDeformation(0.0F))
                .texOffs(8, 0).addBox(-2.0F, -6.0F, -1.95F, 4.0F, 11.0F, 3.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -42.5F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r3 = blasphemous_impaler.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(8, 30).addBox(-0.5F, -5.75F, -1.9F, 2.5F, 7.75F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -35.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        PartDefinition cube_r4 = blasphemous_impaler.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 29).addBox(-2.0F, -5.75F, -1.9F, 2.5F, 7.75F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -35.0F, 0.0F, 0.0F, 0.0F, -0.5672F));

        PartDefinition upper_anim = blasphemous_impaler.addOrReplaceChild("upper_anim", CubeListBuilder.create(), PartPose.offset(0.0F, -28.0F, 0.0F));

        PartDefinition a4 = upper_anim.addOrReplaceChild("a4", CubeListBuilder.create().texOffs(44, 16).addBox(0.0F, 0.0F, -4.0F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition a3 = upper_anim.addOrReplaceChild("a3", CubeListBuilder.create().texOffs(46, 43).addBox(0.0F, 0.0F, 0.0F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));

        PartDefinition a2 = upper_anim.addOrReplaceChild("a2", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, 0.0F));

        PartDefinition cube_r5 = a2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(20, 51).addBox(0.0F, -3.0F, -2.5F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition a1 = upper_anim.addOrReplaceChild("a1", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, 0.0F));

        PartDefinition cube_r6 = a1.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(52, 6).addBox(0.0F, -3.0F, -1.5F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition lower_anim = blasphemous_impaler.addOrReplaceChild("lower_anim", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9413F, 35.0F, 0.9795F, 0.0F, -0.6981F, 0.0F));

        // CORRECCIÓN: Nombres "1", "2"... cambiados a "1", "2" (como String está bien, pero variable no)
        PartDefinition part1 = lower_anim.addOrReplaceChild("1", CubeListBuilder.create().texOffs(40, 6).addBox(3.0F, -5.0F, 0.0F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        PartDefinition part2 = lower_anim.addOrReplaceChild("2", CubeListBuilder.create().texOffs(22, 41).addBox(-9.0F, -5.0F, 0.0F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition part3 = lower_anim.addOrReplaceChild("3", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, 1.0F));

        PartDefinition cube_r7 = part3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(8, 42).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition part4 = lower_anim.addOrReplaceChild("4", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, 5.0F));

        PartDefinition cube_r8 = part4.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(34, 43).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition eye1 = blasphemous_impaler.addOrReplaceChild("eye1", CubeListBuilder.create(), PartPose.offset(0.0F, -32.75F, -1.75F));

        PartDefinition b2 = eye1.addOrReplaceChild("b2", CubeListBuilder.create().texOffs(40, 17).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 0.5F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.75F, -0.8F));

        PartDefinition b1 = eye1.addOrReplaceChild("b1", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 0.5F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.75F, -0.8F));

        PartDefinition eye2 = blasphemous_impaler.addOrReplaceChild("eye2", CubeListBuilder.create(), PartPose.offset(0.0F, -32.75F, 3.5F));

        PartDefinition b3 = eye2.addOrReplaceChild("b3", CubeListBuilder.create().texOffs(40, 18).addBox(-1.0F, -0.25F, -0.15F, 2.0F, 0.5F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.75F, -0.8F));

        PartDefinition b4 = eye2.addOrReplaceChild("b4", CubeListBuilder.create().texOffs(40, 19).addBox(-1.0F, -0.25F, -0.15F, 2.0F, 0.5F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.75F, -0.8F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(BlasphemousImpalerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Deja esto vacío ya que rotamos el modelo entero en el Renderer
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        blasphemous_impaler.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}