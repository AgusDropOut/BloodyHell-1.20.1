package net.agusdropout.bloodyhell.entity.client;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation CRYSTAL_PILLAR = new ModelLayerLocation(BloodyHell.prefix("crystal_pillar"), "main");
    public static final ModelLayerLocation UNKNOWN_EYE_ENTITY = new ModelLayerLocation(BloodyHell.prefix("unknown_eye_entity"), "main");
    public static final ModelLayerLocation VESPER = new ModelLayerLocation(
            new ResourceLocation(BloodyHell.MODID, "vesper"), "main");
    public static final ModelLayerLocation BLASPHEMOUS_IMPALER_ENTITY = new ModelLayerLocation(
            BloodyHell.prefix("blasphemous_impaler_entity"), "main");
}
