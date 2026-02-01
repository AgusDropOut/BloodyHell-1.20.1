package net.agusdropout.bloodyhell.block.client;



import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.custom.mechanism.SanguinitePipeBlock;
import net.agusdropout.bloodyhell.block.entity.custom.SanguinitePipeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

public class SanguinitePipeModel extends GeoModel<SanguinitePipeBlockEntity> {

    @Override
    public ResourceLocation getModelResource(SanguinitePipeBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        boolean n = state.getValue(SanguinitePipeBlock.NORTH);
        boolean s = state.getValue(SanguinitePipeBlock.SOUTH);
        boolean e = state.getValue(SanguinitePipeBlock.EAST);
        boolean w = state.getValue(SanguinitePipeBlock.WEST);
        boolean u = state.getValue(SanguinitePipeBlock.UP);
        boolean d = state.getValue(SanguinitePipeBlock.DOWN);

        int connections = (n?1:0) + (s?1:0) + (e?1:0) + (w?1:0) + (u?1:0) + (d?1:0);

        // --- CHECK: IS IT A STRAIGHT LINE? ---
        boolean isVerticalLine = (u || d) && !n && !s && !e && !w;
        boolean isZLine = (n || s) && !u && !d && !e && !w;
        boolean isXLine = (e || w) && !u && !d && !n && !s;

        // --- LOGIC: SIMPLE VS COMPLEX ---

        // Case A: Simple Vertical (Original)
        if (isVerticalLine && connections <= 2) {
            return new ResourceLocation(BloodyHell.MODID, "geo/sanguinite_pipe_vertical.geo.json");
        }

        // Case B: Simple Horizontal (Original)
        // We use the same model for Z and X, the renderer rotates X.
        if ((isZLine || isXLine) && connections <= 2) {
            return new ResourceLocation(BloodyHell.MODID, "geo/sanguinite_pipe.geo.json");
        }

        // Case C: Complex / Connector (New)
        // If we are here, it's a corner, T-junction, or cross.

        // Decide Base for Connector:
        // Priority: Vertical Base if we have ANY vertical connection?
        // Or strictly if we have Up AND Down?
        // Let's stick to your previous logic: Vertical Base if Up+Down or isolated vertical elements.
        boolean verticalBase = (u && d) || (!n && !s && !e && !w && (u || d));

        if (verticalBase) {
            return new ResourceLocation(BloodyHell.MODID, "geo/sanguinite_pipe_vertical_con.geo.json");
        } else {
            return new ResourceLocation(BloodyHell.MODID, "geo/sanguinite_pipe_horizontal_con.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(SanguinitePipeBlockEntity animatable) {
        // Use "Con" texture if we are using "Con" models, otherwise use standard.
        // We can reuse the exact same logic or just check the path string if we want to be lazy,
        // but robust logic is better:

        // (Repeat boolean logic or helper method)
        // For simplicity here:
        String modelPath = getModelResource(animatable).getPath();
        if (modelPath.contains("_con")) {
            return new ResourceLocation(BloodyHell.MODID, "textures/block/sanguinite_pipe_con.png");
        }
        return new ResourceLocation(BloodyHell.MODID, "textures/block/sanguinite_pipe.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SanguinitePipeBlockEntity animatable) {
        return new ResourceLocation(BloodyHell.MODID, "animations/sanguinite_pipe.animation.json");
    }
}