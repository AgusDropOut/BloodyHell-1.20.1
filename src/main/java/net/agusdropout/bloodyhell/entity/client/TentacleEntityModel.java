package net.agusdropout.bloodyhell.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.entity.client.animations.TentacleAnimations;
import net.agusdropout.bloodyhell.entity.custom.TentacleEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class TentacleEntityModel extends HierarchicalModel<TentacleEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(BloodyHell.MODID, "entity_tentacles"), "main");

    private final ModelPart root;
    public final ModelPart v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12;
    public final ModelPart v13, v14, v15, v16, v17;
    public final ModelPart locator;
    public final ModelPart finger1, finger2, finger3, finger4;

    public TentacleEntityModel(ModelPart root) {
        this.root = root;
        this.v1 = root.getChild("v1");
        this.v2 = this.v1.getChild("v2");
        this.v3 = this.v2.getChild("v3");
        this.v4 = this.v3.getChild("v4");
        this.v5 = this.v4.getChild("v5");
        this.v6 = this.v5.getChild("v6");
        this.v7 = this.v6.getChild("v7");
        this.v8 = this.v7.getChild("v8");
        this.v9 = this.v8.getChild("v9");
        this.v10 = this.v9.getChild("v10");
        this.v11 = this.v10.getChild("v11");
        this.v12 = this.v11.getChild("v12");
        this.v13 = this.v12.getChild("v13");
        this.v14 = this.v13.getChild("v14");
        this.v15 = this.v14.getChild("v15");
        this.v16 = this.v15.getChild("v16");
        this.v17 = this.v16.getChild("v17");

        this.locator = this.v17.getChild("locator");

        this.finger2 = this.v17.getChild("finger2");
        this.finger3 = this.v17.getChild("finger3");
        this.finger4 = this.v17.getChild("finger4");
        this.finger1 = this.v17.getChild("finger1");
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition v1 = partdefinition.addOrReplaceChild("v1", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -30.0F, 0.0F));

        PartDefinition v2 = v1.addOrReplaceChild("v2", CubeListBuilder.create().texOffs(16, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v3 = v2.addOrReplaceChild("v3", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v4 = v3.addOrReplaceChild("v4", CubeListBuilder.create().texOffs(24, 8).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v5 = v4.addOrReplaceChild("v5", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v6 = v5.addOrReplaceChild("v6", CubeListBuilder.create().texOffs(0, 10).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(16, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v7 = v6.addOrReplaceChild("v7", CubeListBuilder.create().texOffs(32, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v8 = v7.addOrReplaceChild("v8", CubeListBuilder.create().texOffs(32, 24).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v9 = v8.addOrReplaceChild("v9", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v10 = v9.addOrReplaceChild("v10", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v11 = v10.addOrReplaceChild("v11", CubeListBuilder.create().texOffs(0, 36).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v12 = v11.addOrReplaceChild("v12", CubeListBuilder.create().texOffs(16, 36).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v13 = v12.addOrReplaceChild("v13", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v14 = v13.addOrReplaceChild("v14", CubeListBuilder.create().texOffs(40, 8).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v15 = v14.addOrReplaceChild("v15", CubeListBuilder.create().texOffs(32, 40).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v16 = v15.addOrReplaceChild("v16", CubeListBuilder.create().texOffs(40, 40).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition v17 = v16.addOrReplaceChild("v17", CubeListBuilder.create().texOffs(0, 44).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition locator = v17.addOrReplaceChild("locator", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition finger2 = v17.addOrReplaceChild("finger2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.25F, 1.75F, -0.5F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r1 = finger2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(20, 44).addBox(-0.5F, -1.9693F, 3.1955F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition finger1_tip2 = finger2.addOrReplaceChild("finger1_tip2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.1262F, 2.8087F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r2 = finger1_tip2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(28, 44).addBox(-0.5F, -3.0307F, 3.1955F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.3665F, -2.5198F, 0.3927F, 0.0F, 0.0F));

        PartDefinition finger3 = v17.addOrReplaceChild("finger3", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5F, 1.75F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r3 = finger3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 44).addBox(-1.5F, -3.1173F, 0.4239F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 3.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition finger1_tip3 = finger3.addOrReplaceChild("finger1_tip3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.25F, 1.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r4 = finger1_tip3.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(32, 46).addBox(-1.5F, -1.8827F, 0.4239F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.75F, 0.25F, 0.3927F, 0.0F, 0.0F));

        PartDefinition finger4 = v17.addOrReplaceChild("finger4", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5F, 1.75F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r5 = finger4.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(16, 44).addBox(0.5F, -3.1173F, 0.4239F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 3.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition finger1_tip4 = finger4.addOrReplaceChild("finger1_tip4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.1262F, 0.8087F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r6 = finger1_tip4.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(36, 46).addBox(0.5F, -1.8827F, 0.4239F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.7452F, 0.6015F, 0.3927F, 0.0F, 0.0F));

        PartDefinition finger1 = v17.addOrReplaceChild("finger1", CubeListBuilder.create(), PartPose.offset(0.25F, 1.9131F, 1.3087F));

        PartDefinition cube_r7 = finger1.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(8, 44).addBox(-0.5F, -3.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.8369F, 1.1913F, 0.3927F, 0.0F, 0.0F));

        PartDefinition finger1_tip = finger1.addOrReplaceChild("finger1_tip", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0869F, 1.1913F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r8 = finger1_tip.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(24, 44).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, 0.3927F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(TentacleEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entity.getInitialDelay() > 0) return;

        float realAge = ageInTicks;
        this.root().getAllParts().forEach(ModelPart::resetPose);

        if (entity.getTargetAltar() != null) {

            // 1. PROCEDURAL SPINE (Cuerpo)
            applyProceduralSpine(entity, realAge);

            // 2. KEYFRAME ANIMATION (Dedos)
            // Empieza en tick 20 para anticipar el agarre
            if (realAge >= 20f) {
                this.animate(entity.grabAnimationState, TentacleAnimations.GRAB, realAge);
            }
        }
    }

    private void applyProceduralSpine(TentacleEntity entity, float realAge) {
        float seed = entity.getRandomSeed();
        float speedRandom = 1.0f + (((int)seed % 20) / 100.0f);
        float directionRandom = ((int)seed % 2 == 0) ? 1.0f : -1.0f;

        float rawProgress = calculateLifecycleProgress(realAge);
        float recoil = calculateSoftRecoil(realAge);
        float progress = Mth.clamp(rawProgress - recoil, 0f, 1f);

        Vec3 origin = entity.position().add(0, 1.5, 0);
        Vec3 target = Vec3.atCenterOf(entity.getTargetAltar());
        double dx = target.x - origin.x;
        double dy = target.y - origin.y;
        double dz = target.z - origin.z;
        double dh = Math.sqrt(dx * dx + dz * dz);

        // CORRECCIÓN DE ORIENTACIÓN (SUMAR en vez de restar para girar 180 si es necesario)
        float targetYaw = (float) (Math.atan2(dz, dx)) + 1.5707F;
        float targetPitch = (float) (-(Math.atan2(dy, dh)));

        this.v1.yRot = targetYaw;
        this.v1.xRot = targetPitch + 0.8f;

        float baseCurl = Mth.lerp(progress * progress, 1.5f, -0.1f);

        float accumulatedRotX = this.v1.xRot;
        float accumulatedRotZ = this.v1.zRot;
        float waveIntensity = 1.0f - progress;

        // --- APLICACIÓN DEL CAOS A TODOS LOS HUESOS (v2 a v15) ---
        accumulatedRotX += applyChaosMotion(this.v2, baseCurl, realAge, 1, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v3, baseCurl, realAge, 2, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v4, baseCurl, realAge, 3, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v5, baseCurl, realAge, 4, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v6, baseCurl, realAge, 5, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v7, baseCurl, realAge, 6, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v8, baseCurl, realAge, 7, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v9, baseCurl, realAge, 8, waveIntensity, speedRandom, seed, directionRandom).x;
        Vec3 rotV10 = applyChaosMotion(this.v10, baseCurl,realAge, 9, waveIntensity, speedRandom, seed, directionRandom);
        accumulatedRotX += rotV10.x;
        accumulatedRotZ += rotV10.z;

        accumulatedRotX += applyChaosMotion(this.v11, baseCurl, realAge, 10, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v12, baseCurl, realAge, 11, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v13, baseCurl, realAge, 12, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v14, baseCurl, realAge, 13, waveIntensity, speedRandom, seed, directionRandom).x;
        accumulatedRotX += applyChaosMotion(this.v15, baseCurl, realAge, 14, waveIntensity, speedRandom, seed, directionRandom).x;

        // --- NUEVO ESTABILIZADOR (v16 y v17) ---
        float desiredTotalX = targetPitch;
        float correctionNeededX = desiredTotalX - accumulatedRotX;

        // v16 absorbe el 60% del error
        this.v16.xRot = correctionNeededX * 0.6f;
        this.v16.zRot = -accumulatedRotZ * 0.6f;
        accumulatedRotX += this.v16.xRot;
        accumulatedRotZ += this.v16.zRot;

        // v17 (Mano/Garra) hace la corrección final (Gimbal)
        this.v17.xRot = (desiredTotalX - accumulatedRotX);
        this.v17.zRot = -accumulatedRotZ;

        // Respiración final en v17
        if (realAge > 45 && realAge < 80) {
            this.v17.xRot += Math.sin((realAge + seed) * 0.1f) * 0.05f;
        }
    }

    private float calculateLifecycleProgress(float ageInTicks) {
        float extendDuration = 45f;
        float retractStart = 80f;
        float deathTime = 120f;
        if (ageInTicks < extendDuration) {
            float t = ageInTicks / extendDuration;
            return (float) (1.0f - Math.pow(1.0f - t, 3));
        } else if (ageInTicks < retractStart) {
            return 1.0f;
        } else {
            float t = (ageInTicks - retractStart) / (deathTime - retractStart);
            t = Mth.clamp(t, 0f, 1f);
            float smoothT = t * t * (3.0f - 2.0f * t);
            return 1.0f - smoothT;
        }
    }

    private float calculateSoftRecoil(float age) {
        float impactTick = 45f;
        float dist = age - impactTick;
        if (dist >= 0 && dist < 6) {
            return (float) (Math.sin(dist * 0.8f) * Math.exp(-dist * 0.5f) * 0.15f);
        }
        return 0f;
    }

    private Vec3 applyChaosMotion(ModelPart part, float baseAngle, float time, int offset, float intensityMult, float speedMult, float seed, float direction) {
        float speed = 0.04f * speedMult;
        float frequency = 0.3f;
        float intensityX = 0.04f * intensityMult;
        float intensityZ = 0.04f * intensityMult;
        float waveInput = ((time + seed) * speed) + (offset * frequency);
        float waveX = (float) Math.sin(waveInput) * intensityX;
        float waveZ = (float) Math.cos(waveInput) * intensityZ * direction;
        part.xRot = baseAngle + waveX;
        part.zRot = waveZ;
        return new Vec3(part.xRot, 0, part.zRot);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        v1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}