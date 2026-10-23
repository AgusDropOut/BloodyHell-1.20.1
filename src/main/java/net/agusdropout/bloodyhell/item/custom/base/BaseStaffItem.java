package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.client.render.item.generic.GenericStaffRenderer;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BaseStaffItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation THROW_CASTING_ANIM = RawAnimation.begin().thenLoop("throw_casting");
    protected static final RawAnimation CIRCLE_CASTING_ANIM = RawAnimation.begin().thenLoop("circle_casting");
    protected static final RawAnimation HOLD_CASTING_ANIM = RawAnimation.begin().thenLoop("hold_casting");
    protected static final RawAnimation RISE_CASTING_ANIM = RawAnimation.begin().thenPlay("rise_casting").thenLoop("rise_casting_hold");

    protected static final RawAnimation SEQ_1_ANIM = RawAnimation.begin().thenLoop("seq_casting_1");
    protected static final RawAnimation SEQ_2_ANIM = RawAnimation.begin().thenLoop("seq_casting_2");

    public static final double BONUS_RADIUS = 16.0;
    public static final int MARKED_FOR_FULL_BONUS = 5;
    public static final int BONUS_REFRESH_TICKS = 10;
    public static final float MAX_DAMAGE_BONUS = 0.30f;
    public static final float MAX_COST_REDUCTION = 0.25f;
    public static final float MAX_COOLDOWN_REDUCTION = 0.25f;
    public static final float OFF_AFFINITY_SHARE = 0.4f;
    private static final String NBT_MARKED_TARGETS = "MarkedTargets";

    private final SpellAffinity affinity;
    @Nullable
    private final Supplier<MobEffect> marker;

    public BaseStaffItem(Properties properties, SpellAffinity affinity, @Nullable Supplier<MobEffect> marker) {
        super(properties);
        this.affinity = affinity;
        this.marker = marker;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public SpellAffinity getAffinity() {
        return affinity;
    }

    public int getMarkedTargets(ItemStack staff) {
        return staff.hasTag() ? staff.getTag().getInt(NBT_MARKED_TARGETS) : 0;
    }

    public float getBonusFill(ItemStack staff) {
        return Math.min(getMarkedTargets(staff), MARKED_FOR_FULL_BONUS) / (float) MARKED_FOR_FULL_BONUS;
    }

    public SpellModifiers modifiersFor(ItemStack staff, BaseSpellBookItem<?> book) {
        float power = getBonusFill(staff) * (book.getAffinity() == affinity ? 1.0f : OFF_AFFINITY_SHARE);
        return new SpellModifiers(1.0f + MAX_DAMAGE_BONUS * power, 1.0f - MAX_COST_REDUCTION * power, 1.0f - MAX_COOLDOWN_REDUCTION * power);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slot, isSelected);
        if (level.isClientSide || marker == null || !(entity instanceof Player player)) return;
        if (!isSelected) {
            storeMarkedTargets(stack, 0);
        } else if (!player.isUsingItem() && player.tickCount % BONUS_REFRESH_TICKS == 0) {
            refreshMarkedTargets(stack, player);
        }
    }

    private void refreshMarkedTargets(ItemStack staff, Player player) {
        if (marker != null) {
            storeMarkedTargets(staff, countMarkedTargets(player, marker.get()));
        }
    }

    private static int countMarkedTargets(Player player, MobEffect effect) {
        AABB area = player.getBoundingBox().inflate(BONUS_RADIUS);
        double maxDistanceSqr = BONUS_RADIUS * BONUS_RADIUS;
        List<LivingEntity> marked = player.level().getEntitiesOfClass(LivingEntity.class, area,
                target -> target != player && target.isAlive() && target.hasEffect(effect) && target.distanceToSqr(player) <= maxDistanceSqr);
        return Math.min(marked.size(), MARKED_FOR_FULL_BONUS);
    }

    private void storeMarkedTargets(ItemStack staff, int count) {
        if (getMarkedTargets(staff) != count) {
            staff.getOrCreateTag().putInt(NBT_MARKED_TARGETS, count);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.bloodyhell.staff.affinity", Component.translatable("spell_affinity.bloodyhell." + affinity.name().toLowerCase(Locale.ROOT))).withStyle(ChatFormatting.GOLD));
        if (marker == null) return;
        tooltip.add(Component.translatable("tooltip.bloodyhell.staff.marker", marker.get().getDisplayName(), MARKED_FOR_FULL_BONUS).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.bloodyhell.staff.bonus", "+" + Math.round(MAX_DAMAGE_BONUS * 100) + "%", "-" + Math.round(MAX_COST_REDUCTION * 100) + "%", "-" + Math.round(MAX_COOLDOWN_REDUCTION * 100) + "%").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.bloodyhell.staff.off_affinity", Math.round(OFF_AFFINITY_SHARE * 100) + "%").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.bloodyhell.staff.charge", Math.min(getMarkedTargets(stack), MARKED_FOR_FULL_BONUS), MARKED_FOR_FULL_BONUS).withStyle(ChatFormatting.RED));
    }

    protected ItemStack getActualSpellStack(ItemStack offhandStack) {
        if (offhandStack.getItem() instanceof BaseSpellBookItem) {
            return offhandStack;
        } else if (offhandStack.getItem() instanceof OmnitomeItem omnitome) {
            return omnitome.getActiveSpellBook(offhandStack);
        }
        return ItemStack.EMPTY;
    }

    protected void syncSpellStackIfOmnitome(ItemStack offhandStack, ItemStack activeSpellStack) {
        if (offhandStack.getItem() instanceof OmnitomeItem && activeSpellStack.getItem() instanceof BaseSpellBookItem spellBook) {
            offhandStack.getOrCreateTag().put(OmnitomeItem.NBT_ACTIVE_SPELL, spellBook.serializeBookData(activeSpellStack));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
        return net.minecraft.world.item.UseAnim.NONE;
    }

    @Override
    public boolean isPerspectiveAware() {
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GenericStaffRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new GenericStaffRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "base_controller", 2, this::basePredicate));

        controllers.add(new AnimationController<>(this, "action_controller", 2, state -> PlayState.STOP)
                .triggerableAnim("inspect_1", RawAnimation.begin().thenPlay("inspect_1"))
                .triggerableAnim("inspect_2", RawAnimation.begin().thenPlay("inspect_2"))
                .triggerableAnim("inspect_3", RawAnimation.begin().thenPlay("inspect_3"))
                .triggerableAnim("rise_finish", RawAnimation.begin().thenPlay("rise_casting_finish")));
    }

    protected <T extends GeoAnimatable> PlayState basePredicate(AnimationState<T> event) {
        ItemDisplayContext perspective = event.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

        if (perspective != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND && perspective != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            event.getController().setAnimationSpeed(1.0f);
            event.getController().setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        }

        Entity entity = event.getData(DataTickets.ENTITY);
        if (entity == null) entity = net.agusdropout.bloodyhell.client.ClientItemHooks.getLocalPlayerAsEntity();

        if (entity instanceof LivingEntity livingEntity && livingEntity.isUsingItem() && livingEntity.getUseItem().getItem() == this) {
            ItemStack stack = livingEntity.getUseItem();
            ItemStack offhandStack = livingEntity.getItemInHand(InteractionHand.OFF_HAND);
            ItemStack actualSpellStack = getActualSpellStack(offhandStack);

            String castTypeStr = stack.getOrCreateTag().getString("CastType");
            StaffCastType castType = StaffCastType.HOLD;
            try { castType = StaffCastType.valueOf(castTypeStr); } catch (IllegalArgumentException ignored) {}

            float animSpeed = 1.0f;
            int chargeTime = 20;
            int cooldown = 0;

            if (!actualSpellStack.isEmpty() && actualSpellStack.getItem() instanceof BaseSpellBookItem spellBook) {
                chargeTime = Math.max(1, spellBook.getMinChargeTime());
                cooldown = modifiersFor(stack, spellBook).scaleCooldown(spellBook.getCooldown());
            }

            switch (castType) {
                case THROW:
                    animSpeed = castType.getBaseTicks() / (float) chargeTime;
                    event.getController().setAnimationSpeed(animSpeed);
                    event.getController().setAnimation(THROW_CASTING_ANIM);
                    break;
                case CIRCLE:
                    event.getController().setAnimationSpeed(1.0f);
                    event.getController().setAnimation(CIRCLE_CASTING_ANIM);
                    break;
                case RISE:
                    animSpeed = castType.getBaseTicks() / (float) chargeTime;
                    event.getController().setAnimationSpeed(animSpeed);
                    event.getController().setAnimation(RISE_CASTING_ANIM);
                    break;
                case HOLD:
                    event.getController().setAnimationSpeed(1.0f);
                    event.getController().setAnimation(HOLD_CASTING_ANIM);
                    break;
                case SEQUENTIAL:
                    int cycleLength = chargeTime + cooldown;
                    animSpeed = castType.getBaseTicks() / (float) cycleLength;
                    event.getController().setAnimationSpeed(animSpeed);

                    int ticksUsed = stack.getUseDuration() - livingEntity.getUseItemRemainingTicks();

                    if ((ticksUsed / cycleLength) % 2 == 0) {
                        event.getController().setAnimation(SEQ_1_ANIM);
                    } else {
                        event.getController().setAnimation(SEQ_2_ANIM);
                    }
                    break;
            }
        } else {
            event.getController().setAnimationSpeed(1.0f);
            event.getController().setAnimation(IDLE_ANIM);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        ItemStack actualSpellStack = getActualSpellStack(offhandStack);

        if (!actualSpellStack.isEmpty() && actualSpellStack.getItem() instanceof BaseSpellBookItem spellBook) {
            if (!level.isClientSide) {
                refreshMarkedTargets(itemStack, player);
            }

            if (!spellBook.canStartCasting(level, player, modifiersFor(itemStack, spellBook))) {
                return InteractionResultHolder.fail(itemStack);
            }

            if (!level.isClientSide) {
                itemStack.getOrCreateTag().putString("CastType", spellBook.getStaffCastType().name());
                syncSpellStackIfOmnitome(offhandStack, actualSpellStack);
            }

            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            ItemStack actualSpellStack = getActualSpellStack(offhandStack);

            if (!actualSpellStack.isEmpty() && actualSpellStack.getItem() instanceof BaseSpellBookItem<?> spellBook) {
                SpellCaster.tick(level, player, spellBook, actualSpellStack, InteractionHand.MAIN_HAND, getUseDuration(stack) - remainingUseDuration, stack);
                syncSpellStackIfOmnitome(offhandStack, actualSpellStack);
            }
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            ItemStack actualSpellStack = getActualSpellStack(offhandStack);

            if (!actualSpellStack.isEmpty() && actualSpellStack.getItem() instanceof BaseSpellBookItem<?> spellBook) {
                SpellCaster.release(level, player, spellBook, actualSpellStack, InteractionHand.MAIN_HAND, getUseDuration(stack) - timeLeft, stack);
                syncSpellStackIfOmnitome(offhandStack, actualSpellStack);
            }

            if (!level.isClientSide) {
                String castTypeStr = stack.getOrCreateTag().getString("CastType");
                if (StaffCastType.RISE.name().equals(castTypeStr)) {
                    triggerAnim(player, GeoItem.getId(stack), "action_controller", "rise_finish");
                }
            }
        }
        super.releaseUsing(stack, level, entity, timeLeft);
    }

    public void triggerInspect(Player player, ItemStack stack) {
        int randomInspect = player.level().random.nextInt(3) + 1;
        triggerAnim(player, GeoItem.getId(stack), "action_controller", "inspect_" + randomInspect);
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object itemStack) { return RenderUtils.getCurrentTick(); }
    public abstract String getId();
}