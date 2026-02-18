package net.agusdropout.bloodyhell.block.entity.custom.plant;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGemSproutBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class BloodGemSproutBlockEntity extends BaseGemSproutBlockEntity {

    // --- CONFIG ---
    private static final int BLOOD_PER_STAGE = 250;
    private static final int MAX_GROWTH_TOME = 500;
    private static final float GROWTH_CHANCE = 0.35f;



    public BloodGemSproutBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.BLOOD_GEM_SPROUT_BE.get(), pPos, pState,
                MAX_GROWTH_TOME, BLOOD_PER_STAGE, GROWTH_CHANCE);
    }







    @Override
    public Fluid getValidFluid() {
        return ModFluids.BLOOD_SOURCE.get();
    }

    @Override
    public void getRenderingGemShape(VertexConsumer consumer, PoseStack poseStack) {
        int c = getGemColor();
        float r = ((c >> 16) & 0xFF) / 255f;
        float g = ((c >> 8) & 0xFF) / 255f;
        float b = (c & 0xFF) / 255f;

        // 5. Render Octahedron
        // Outer Shell
        RenderHelper.renderOctahedron(
                consumer,
                poseStack.last().pose(),
                poseStack.last().normal(),
                0.25f,
                r, g, b, 1.0f,
                15728880
        );
    }


}