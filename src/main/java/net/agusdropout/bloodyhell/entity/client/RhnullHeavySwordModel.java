package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.client.animations.RhnullHeavySwordAnimations;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullHeavySwordEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.ParticleHelper;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class RhnullHeavySwordModel<T extends RhnullHeavySwordEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(BloodyHell.MODID, "rhnull_heavy_sword_entity"), "main");
    private static final Vector3f COLOR_CORE = new Vector3f(1f, 0.9f, 0.0f);

    private final ModelPart root;
    private final ModelPart impaler;

    // New Locator Parts
    private final ModelPart emitterStart;
    private final ModelPart emitterEnd;

    public RhnullHeavySwordModel(ModelPart root) {
        this.root = root;
        this.impaler = root.getChild("rhnull_heavy_sword_entity"); // Matches your blockbench name

        // Get the locators (Children of the sword bone)
        this.emitterStart = this.impaler.getChild("particle_emitter_start");
        this.emitterEnd = this.impaler.getChild("particle_emitter_end");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Main Sword Bone
        PartDefinition rhnull_heacy_sword_entity = partdefinition.addOrReplaceChild("rhnull_heavy_sword_entity", CubeListBuilder.create().texOffs(36, 41).addBox(-2.0F, -5.8333F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 0).addBox(-9.0F, 6.1667F, -3.0F, 18.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(52, 41).addBox(-12.0F, 3.1667F, -3.0F, 3.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(52, 53).addBox(8.0F, 3.1667F, -3.0F, 3.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-7.0F, 9.1667F, -2.0F, 14.0F, 47.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 9).addBox(-6.0F, 56.1667F, -2.0F, 12.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 20).addBox(-5.0F, 63.1667F, -2.0F, 10.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 51).addBox(-4.0F, 68.1667F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 57).addBox(-3.0F, 71.1667F, -2.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 58).addBox(-2.0F, 76.1667F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(60, 29).addBox(-1.0F, 79.1667F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 29).addBox(-3.0F, -11.8333F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 44.5833F, 0.0F, 1.5708F, -0.7854F, -1.5708F));

        // Outline
        PartDefinition outline = rhnull_heacy_sword_entity.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(0, 0).addBox(2.8F, 12.8F, 2.8F, -5.6F, -13.6F, -5.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(9.8F, 15.8F, 3.8F, -19.6F, -4.6F, -7.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.2F, 15.8F, 3.8F, -4.6F, -7.6F, -7.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(11.8F, 15.8F, 3.8F, -4.6F, -7.6F, -7.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(7.8F, 62.8F, 2.8F, -15.6F, -48.6F, -5.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(6.8F, 69.8F, 2.8F, -13.6F, -8.6F, -5.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.8F, 74.8F, 2.8F, -11.6F, -6.6F, -5.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(4.8F, 77.8F, 2.8F, -9.6F, -4.6F, -5.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.8F, 82.8F, 2.8F, -7.6F, -6.6F, -5.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.8F, 85.8F, 2.8F, -5.6F, -4.6F, -5.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.8F, 88.8F, 2.8F, -3.6F, -4.6F, -5.6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.8F, 0.8F, 3.8F, -7.6F, -7.6F, -7.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.8333F, 0.0F, 0.0F, 3.1416F, 0.0F));

        // NEW LOCATORS (Empty Cubes)
        rhnull_heacy_sword_entity.addOrReplaceChild("particle_emitter_start", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));
        rhnull_heacy_sword_entity.addOrReplaceChild("particle_emitter_end", CubeListBuilder.create(), PartPose.offset(0.0F, 74.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(RhnullHeavySwordEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.fallAnimationState, RhnullHeavySwordAnimations.fall, ageInTicks);
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        impaler.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}