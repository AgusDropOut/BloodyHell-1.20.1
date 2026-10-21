package net.agusdropout.bloodyhell.client.render.entity.custom.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.client.render.entity.generic.GenericMinionModel;
import net.agusdropout.bloodyhell.client.render.entity.generic.GenericMinionRenderer;
import net.agusdropout.bloodyhell.entity.minions.custom.FailedSonOfTheUnknown;
import net.agusdropout.bloodyhell.entity.minions.custom.WeepingOcularEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class FailedSonOfTheUnknownRenderer extends GenericMinionRenderer<FailedSonOfTheUnknown> {

    private static final float MODEL_SCALE = 1.0f;
    private static final String[] LOCATORS = {"particle", "particle1", "particle2"};

    public FailedSonOfTheUnknownRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GenericMinionModel<>());
    }

    @Override
    public void render(FailedSonOfTheUnknown entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (!entity.isRemoved() && !entity.isInvisible()) {
            super.spawnLocatorParticles(entity);
        }
    }


}