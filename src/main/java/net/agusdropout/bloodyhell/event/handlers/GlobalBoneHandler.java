package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.client.PainThroneRegistry;
import net.agusdropout.bloodyhell.util.bones.BoneManipulator;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
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

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalBoneHandler {

    @SubscribeEvent
    public static void onPreRender(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        float intensity = PainThroneRegistry.getIntensity(entity.getUUID());
        if (intensity > 0) {
            float time = entity.tickCount + event.getPartialTick();
            if (event.getRenderer().getModel() instanceof AgeableListModel<?> ageable) {
                ageable.headParts().forEach(p -> BoneManipulator.applyVisceralTwitch(p, time, 0.2f * intensity));
                ageable.bodyParts().forEach(p -> BoneManipulator.applyVisceralTwitch(p, time, 0.15f * intensity));
            }
            else if (event.getRenderer().getModel() instanceof HierarchicalModel<?> genericModel) {
                genericModel.root().getAllParts().forEach(p -> BoneManipulator.applyVisceralTwitch(p, time, 0.1f * intensity));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPost(RenderLivingEvent.Post<?, ?> event) {
        BoneManipulator.restoreAll();
    }
}