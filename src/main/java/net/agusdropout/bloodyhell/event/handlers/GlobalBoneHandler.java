package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.client.PainThroneRegistry;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TetherParticleOptions;
import net.agusdropout.bloodyhell.util.bones.BoneManipulator;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalBoneHandler {

    @SubscribeEvent
    public static void onPreRender(RenderLivingEvent.Pre<?, ?> event) {


        LivingEntity entity = event.getEntity();
        if(PainThroneRegistry.hasJitterInfo(entity.getUUID())) {
           applyJitterLogic(event,entity);
        } else if(PainThroneRegistry.hasBrokenBone(entity.getUUID())) {
            applyBoneDisplacement(event,entity);
        }
    }

    public static void applyJitterLogic(RenderLivingEvent.Pre<?, ?> event, LivingEntity entity) {
        float intensity = PainThroneRegistry.getIntensity(entity.getUUID());
        if (intensity > 0) {
            float time = entity.tickCount + event.getPartialTick();
            if (event.getRenderer().getModel() instanceof AgeableListModel<?> ageable) {
                ageable.headParts().forEach(p -> BoneManipulator.applyVisceralTwitch(p, time, 0.2f * intensity));
                ageable.bodyParts().forEach(p -> BoneManipulator.applyVisceralTwitch(p, time, 0.15f * intensity));
            } else if (event.getRenderer().getModel() instanceof HierarchicalModel<?> genericModel) {
                genericModel.root().getAllParts().forEach(p -> BoneManipulator.applyVisceralTwitch(p, time, 0.1f * intensity));
            }
        }
    }

    public static void applyBoneDisplacement(RenderLivingEvent.Pre<?, ?> event, LivingEntity entity) {
        List<PainThroneRegistry.BrokenBoneInfo> activeBreaks = PainThroneRegistry.getActiveBreaks(entity.getUUID());

        if (!activeBreaks.isEmpty()) {
            List<ModelPart> breakableParts = getBreakableParts(event.getRenderer().getModel());
            if (!breakableParts.isEmpty()) {
                for (PainThroneRegistry.BrokenBoneInfo info : activeBreaks) {
                    RandomSource randomSource = RandomSource.create(info.seed);
                    int targetIndex = randomSource.nextInt(breakableParts.size());
                    ModelPart victimPart = breakableParts.get(targetIndex);
                    BoneManipulator.applyVanillaExorcismTwist(victimPart, info.progress);

                }
            }
        }
    }



    public static List<ModelPart> getBreakableParts(EntityModel<?> model) {
        List<ModelPart> breakableParts = new ArrayList<>();

        if (model instanceof AgeableListModel<?> ageable) {
            ageable.headParts().forEach(breakableParts::add);
            ageable.bodyParts().forEach(breakableParts::add);
        } else if (model instanceof HierarchicalModel<?> genericModel) {
            ModelPart root = genericModel.root();
            root.getAllParts().forEach(breakableParts::add);
            breakableParts.remove(root);
        } else if (model instanceof net.minecraft.client.model.ListModel<?> listModel) {
            listModel.parts().forEach(breakableParts::add);
        } else {
            System.out.println("WARNING: Unhandled Model Type Detected!");
            System.out.println("Model Class: " + model.getClass().getName());
        }

        breakableParts.removeIf(Objects::isNull);

        return breakableParts;
    }




    @SubscribeEvent
    public static void onRenderPost(RenderLivingEvent.Post<?, ?> event) {
        BoneManipulator.restoreAll(); //Restore all bones to their original state after rendering to prevent permanent deformation
    }
}