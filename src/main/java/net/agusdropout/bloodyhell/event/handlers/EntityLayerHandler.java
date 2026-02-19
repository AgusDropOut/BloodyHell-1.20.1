package net.agusdropout.bloodyhell.event.handlers;

import com.google.common.collect.ImmutableList;
import net.agusdropout.bloodyhell.entity.client.layer.BloodFireLayer;
import net.agusdropout.bloodyhell.entity.client.layer.PainThroneLayer;
import net.agusdropout.bloodyhell.entity.client.layer.VisceralLayer; // Ensure this import matches your package
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;
import java.util.stream.Collectors;

public class EntityLayerHandler {

    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // 1. PLAYERS
        for (String skinType : event.getSkins()) {
            LivingEntityRenderer<Player, EntityModel<Player>> renderer = event.getSkin(skinType);
            if (renderer != null) {
                renderer.addLayer(new BloodFireLayer<>(renderer));
                renderer.addLayer(new VisceralLayer<>(renderer));
            }
        }

        // 2. MOBS
        List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
                ForgeRegistries.ENTITY_TYPES.getValues().stream()
                        .filter(DefaultAttributes::hasSupplier)
                        .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                        .collect(Collectors.toList())
        );

        // 3. GECKO ENTITIES
        List<EntityType<? extends GeoEntity>> entityGeoTypes = ImmutableList.copyOf(
                ForgeRegistries.ENTITY_TYPES.getValues().stream()
                        .filter(DefaultAttributes::hasSupplier)
                        .map(entityType -> (EntityType<? extends GeoEntity>) entityType)
                        .collect(Collectors.toList())
        );

        entityTypes.forEach(entityType -> addLayerIfApplicable(entityType, event));
        entityGeoTypes.forEach(entityType -> addGeoLayerIfApplicable(entityType, event));

    }

    private static void addLayerIfApplicable(EntityType<? extends LivingEntity> entityType, EntityRenderersEvent.AddLayers event) {
        if (entityType == EntityType.ENDER_DRAGON) return;

        LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer = null;

        try {
            renderer = event.getRenderer(entityType);
        } catch (Exception e) {
            System.err.println("Failed to get renderer for entity: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
        }

        if (renderer != null) {
            try {
                renderer.addLayer(new BloodFireLayer<>(renderer));
                renderer.addLayer(new VisceralLayer<>(renderer));
            } catch (Exception e) {
                System.err.println("Failed to add layers to entity: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
            }
        }
    }

    private static void addGeoLayerIfApplicable(EntityType<? extends GeoEntity> entityGeoType, EntityRenderersEvent.AddLayers event) {

        Object renderer = event.getEntityRenderer(entityGeoType);

        if (renderer != null) {

            if (renderer instanceof GeoEntityRenderer<?> geoEntityRenderer) {
                try {

                    geoEntityRenderer.addRenderLayer(new PainThroneLayer(geoEntityRenderer));
                    System.out.println("Added PainThroneLayer to: " + entityGeoType.getDescriptionId());
                } catch (Exception e) {
                    System.err.println("Failed to add layer to Gecko entity: " + entityGeoType.getDescriptionId());
                }
            }
        }
    }


}