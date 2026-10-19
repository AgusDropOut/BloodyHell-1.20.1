package net.agusdropout.bloodyhell.item.client.books;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.item.client.layer.OmnitomeVoidLayer;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class OmnitomeRenderer extends GeoItemRenderer<OmnitomeItem> {

    public static boolean isManagerRendering = false;
    public static boolean isChildMorphing = false;

    public Matrix4f leftPageMatrix = null;
    public Matrix3f leftPageNormal = null;
    public Matrix4f rightPageMatrix = null;
    public Matrix3f rightPageNormal = null;

    private static final float MORPH_DURATION = 0.95f;
    private static final float OMNITOME_COLLAPSE_START = 0.18f;
    private static final float OMNITOME_COLLAPSE_END = 0.62f;
    private static final float CHILD_SPAWN_START = 0.26f;
    private static final float CHILD_SPAWN_END = 0.95f;
    private static final float BOOK_CENTER_Y = 0.10f;

    public ItemDisplayContext currentTransform = ItemDisplayContext.NONE;
    private float morphElapsed = MORPH_DURATION + 1.0f;

    private ItemStack activeSpell = ItemStack.EMPTY;
    private ItemStack prevSpell = ItemStack.EMPTY;
    private ItemStack renderingSpell = ItemStack.EMPTY;
    private boolean isReverting = false;
    private boolean renderingChild = false;
    private float virtualElapsed = 0.0f;

    public OmnitomeRenderer() {
        super(new OmnitomeModel());
        this.addRenderLayer(new OmnitomeVoidLayer(this));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        if (!isManagerRendering && (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)) {
            net.agusdropout.bloodyhell.util.visuals.manager.OmnitomeBookRenderManager.INSTANCE.queueBook(this, stack, transformType, poseStack, packedLight, packedOverlay);


            if (bufferSource instanceof MultiBufferSource.BufferSource immediate) {
                immediate.endBatch();
            }

            return;
        }

        this.currentTransform = transformType;
        this.leftPageMatrix = null;
        this.leftPageNormal = null;
        this.rightPageMatrix = null;
        this.rightPageNormal = null;
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, OmnitomeItem animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (currentItemStack != null) {
            if (currentItemStack.getItem() instanceof OmnitomeItem omnitome) {
                this.activeSpell = omnitome.getActiveSpellBook(currentItemStack);
            } else {
                this.activeSpell = ItemStack.EMPTY;
            }

            Minecraft mc = Minecraft.getInstance();
            CompoundTag tag = currentItemStack.getTag();

            this.isReverting = tag != null && tag.getBoolean(OmnitomeItem.NBT_IS_REVERTING);
            if (this.isReverting && tag != null && tag.contains(OmnitomeItem.NBT_PREV_SPELL)) {
                this.prevSpell = BaseSpellBookItem.deserializeBookData(tag.getCompound(OmnitomeItem.NBT_PREV_SPELL));
                if (!this.prevSpell.isEmpty()) {
                    this.prevSpell.getOrCreateTag().putBoolean("spellbook_is_open", true);
                }
            } else {
                this.prevSpell = ItemStack.EMPTY;
            }

            if (mc.level != null && tag != null && tag.contains(OmnitomeItem.NBT_LAST_MORPH_TIME)) {
                long lastMorphTick = tag.getLong(OmnitomeItem.NBT_LAST_MORPH_TIME);
                float elapsedTicks = (mc.level.getGameTime() - lastMorphTick) + mc.getFrameTime();
                this.morphElapsed = Math.max(0.0f, elapsedTicks / 20.0f);
            } else {
                this.morphElapsed = MORPH_DURATION + 1.0f;
            }

            if (this.isReverting && this.morphElapsed > MORPH_DURATION && !mc.isPaused() && mc.level != null && tag != null) {
                if (!mc.level.isClientSide()) {
                    tag.remove(OmnitomeItem.NBT_IS_REVERTING);
                    tag.remove(OmnitomeItem.NBT_PREV_SPELL);
                }
                this.isReverting = false;
            }

            boolean isInHand = this.currentTransform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                    this.currentTransform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                    this.currentTransform == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND ||
                    this.currentTransform == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

            boolean hasActiveSpell = !this.activeSpell.isEmpty() && isInHand;
            boolean isRevertingVisual = this.isReverting && !this.prevSpell.isEmpty() && isInHand && this.morphElapsed <= MORPH_DURATION;

            this.renderingSpell = isRevertingVisual ? this.prevSpell : this.activeSpell;
            this.renderingChild = hasActiveSpell || isRevertingVisual;

            this.virtualElapsed = isRevertingVisual ? (MORPH_DURATION - this.morphElapsed) : this.morphElapsed;
        }

        if (!this.renderingChild || this.virtualElapsed < OMNITOME_COLLAPSE_END) {
            poseStack.pushPose();
            if (this.renderingChild) {
                applyOmnitomeMorphDeformation(poseStack, this.virtualElapsed);
            }
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }

        if (this.renderingChild && this.virtualElapsed >= CHILD_SPAWN_START) {
            poseStack.pushPose();
            poseStack.translate(-0.5f, -0.51f, -0.5f);
            applyChildMorphDeformation(poseStack, this.virtualElapsed);

            float currentMorphProgress = Mth.clamp((this.virtualElapsed - CHILD_SPAWN_START) / (CHILD_SPAWN_END - CHILD_SPAWN_START), 0.0f, 1.0f);

            net.agusdropout.bloodyhell.util.visuals.manager.OmnitomeClipRenderManager.INSTANCE.queueClipBook(
                    this.renderingSpell,
                    this.currentTransform,
                    poseStack,
                    packedLight,
                    packedOverlay,
                    currentMorphProgress
            );

            poseStack.popPose();
        }
    }

    @Override
    public void renderRecursively(PoseStack poseStack, OmnitomeItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();

        if (name.startsWith("tentacle") || name.equals("eye")) {
            if (this.renderingChild && this.virtualElapsed < OMNITOME_COLLAPSE_END) {
                float tentacleProgress = Mth.clamp(this.virtualElapsed / (OMNITOME_COLLAPSE_END * 0.85f), 0.0f, 1.0f);
                float s = 1.0f - easeInBack(tentacleProgress);
                bone.updateScale(s, s, s);
            } else {
                bone.updateScale(1.0f, 1.0f, 1.0f);
            }
        }

        if (name.equals("lunderPage") || name.equals("runderPage")) {
            return;
        }

        if (name.equals("lupperPage") || name.equals("rupperPage")) {
            poseStack.pushPose();
            RenderUtils.prepMatrixForBone(poseStack, bone);

            if (name.equals("lupperPage")) {
                this.leftPageMatrix = new Matrix4f(poseStack.last().pose());
                this.leftPageNormal = new Matrix3f(poseStack.last().normal());
            } else {
                this.rightPageMatrix = new Matrix4f(poseStack.last().pose());
                this.rightPageNormal = new Matrix3f(poseStack.last().normal());
            }

            for (GeoBone childBone : bone.getChildBones()) {
                super.renderRecursively(poseStack, animatable, childBone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            }

            poseStack.popPose();
            return;
        }

        VertexConsumer safeBuffer = bufferSource.getBuffer(renderType);
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, safeBuffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void renderVoidCubesDirect(Matrix4f poseMatrix, Matrix3f normalMatrix, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);
        poseStack.last().normal().set(normalMatrix);



        for (GeoCube cube : bone.getCubes()) {
            poseStack.pushPose();
            this.renderCube(poseStack, cube, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }

    private void applyOmnitomeMorphDeformation(PoseStack poseStack, float elapsed) {
        float fullProgress = Mth.clamp(elapsed / OMNITOME_COLLAPSE_END, 0.0f, 1.0f);
        float collapseProgress = Mth.clamp((elapsed - OMNITOME_COLLAPSE_START) / (OMNITOME_COLLAPSE_END - OMNITOME_COLLAPSE_START), 0.0f, 1.0f);

        float tremor = (float) Math.sin(elapsed * 75.0f) * 0.012f * (1.0f - collapseProgress);
        float swell = (float) Math.sin(fullProgress * Math.PI) * 0.22f * (1.0f - collapseProgress);
        float shrink = 1.0f - easeInCubic(collapseProgress);
        float scaleXZ = Math.max(0.001f, (shrink + swell) * (1.0f - 0.35f * collapseProgress));
        float scaleY = Math.max(0.001f, shrink + swell * 1.6f);

        float twistY = easeInCubic(fullProgress) * 160.0f;
        float tiltZ = (float) Math.sin(fullProgress * Math.PI) * 18.0f;

        poseStack.translate(tremor, BOOK_CENTER_Y, 0.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(twistY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(tiltZ));
        poseStack.scale(scaleXZ, scaleY, scaleXZ);
        poseStack.translate(0.0f, -BOOK_CENTER_Y, 0.0f);
    }

    private void applyChildMorphDeformation(PoseStack poseStack, float elapsed) {
        if (elapsed >= CHILD_SPAWN_END) return;

        float p = Mth.clamp((elapsed - CHILD_SPAWN_START) / (CHILD_SPAWN_END - CHILD_SPAWN_START), 0.0f, 1.0f);
        float elastic = easeOutBack(p);
        float smooth = easeOutQuint(p);

        float twistY = (1.0f - smooth) * -140.0f;
        float pitchX = (1.0f - smooth) * 22.0f;
        float rollZ = (float) Math.sin((1.0f - p) * Math.PI) * -14.0f;

        Vector3f scale = new Vector3f(
                Mth.lerp(elastic, 0.08f, 1.0f),
                Mth.lerp(smooth, 1.35f, 1.0f) * Mth.clamp(p * 2.5f, 0.1f, 1.0f),
                Mth.lerp(elastic, 0.08f, 1.0f)
        );

        float liftY = (1.0f - elastic) * -0.06f;

        poseStack.translate(0.5f, 0.51f + BOOK_CENTER_Y + liftY, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(twistY));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitchX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rollZ));
        poseStack.scale(scale.x(), scale.y(), scale.z());
        poseStack.translate(-0.5f, -(0.51f + BOOK_CENTER_Y), -0.5f);
    }

    private static float easeInCubic(float x) { return x * x * x; }
    private static float easeOutQuint(float x) { float inv = 1.0f - x; return 1.0f - inv * inv * inv * inv * inv; }
    private static float easeOutBack(float x) { float c1 = 2.1f; float c3 = c1 + 1.0f; float xm1 = x - 1.0f; return 1.0f + c3 * xm1 * xm1 * xm1 + c1 * xm1 * xm1; }
    private static float easeInBack(float x) { float c1 = 1.70158f; float c3 = c1 + 1.0f; return c3 * x * x * x - c1 * x * x; }
}