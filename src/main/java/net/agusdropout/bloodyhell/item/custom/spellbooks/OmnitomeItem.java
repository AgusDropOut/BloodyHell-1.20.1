package net.agusdropout.bloodyhell.item.custom.spellbooks;

import net.agusdropout.bloodyhell.item.client.books.OmnitomeRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseSpellBookItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseStaffItem;
import net.agusdropout.bloodyhell.item.custom.base.StaffCastType;
import net.agusdropout.bloodyhell.screen.custom.menu.OmnitomeMenu;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class OmnitomeItem extends Item implements GeoItem {

    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenLoop("open");
    private static final RawAnimation CLOSED_ANIM = RawAnimation.begin().thenLoop("closed");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation EMERGE_ANIM = RawAnimation.begin().thenPlay("emerge").thenLoop("open");
    private static final RawAnimation ATTACK_SEQ_ANIM = RawAnimation.begin().thenLoop("attack_seq");

    private static final String NBT_OPEN_KEY = "spellbook_is_open";
    public static final String NBT_ACTIVE_SPELL = "ActiveSpell";
    public static final String NBT_SELECTED_SLOT = "SelectedSlot";
    public static final String NBT_LAST_MORPH_TIME = "LastMorphTime";
    public static final String NBT_IS_REVERTING = "IsReverting";
    public static final String NBT_PREV_SPELL = "PrevSpell";

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public OmnitomeItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> new OmnitomeItemStackHandler(stack));

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, handler);
            }
        };
    }

    public static void openGui(ServerPlayer serverPlayer, InteractionHand hand) {
        ItemStack stack = serverPlayer.getItemInHand(hand);
        if (stack.getItem() instanceof OmnitomeItem) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (id, inv, p) -> new OmnitomeMenu(id, inv, stack),
                    Component.translatable("item.bloodyhell.the_omnitome")
            ), buf -> buf.writeEnum(hand));
        }
    }

    public static void selectSlot(ItemStack omnitomeStack, int slotIndex) {
        CompoundTag tag = omnitomeStack.getOrCreateTag();
        CompoundTag previousActive = tag.contains(NBT_ACTIVE_SPELL) ? tag.getCompound(NBT_ACTIVE_SPELL).copy() : null;

        if (slotIndex < 0 || slotIndex >= 8) {
            tag.putInt(NBT_SELECTED_SLOT, -1);
            tag.remove(NBT_ACTIVE_SPELL);
        } else {
            tag.putInt(NBT_SELECTED_SLOT, slotIndex);
            omnitomeStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                syncActiveSpell(omnitomeStack, handler);
            });
        }

        boolean hasNewActive = tag.contains(NBT_ACTIVE_SPELL);

        if (!hasNewActive && previousActive != null) {
            previousActive.getCompound("tag").putBoolean(NBT_OPEN_KEY, true);
            tag.put(NBT_PREV_SPELL, previousActive);
            tag.putBoolean(NBT_IS_REVERTING, true);
        } else if (hasNewActive) {
            tag.putBoolean(NBT_IS_REVERTING, false);
            tag.remove(NBT_PREV_SPELL);
        }
    }

    public static void syncActiveSpell(ItemStack omnitomeStack, IItemHandler handler) {
        CompoundTag tag = omnitomeStack.getOrCreateTag();
        int selectedSlot = tag.contains(NBT_SELECTED_SLOT) ? tag.getInt(NBT_SELECTED_SLOT) : -1;

        if (handler instanceof OmnitomeItemStackHandler omnitomeHandler) {
            omnitomeHandler.loadFromStack();
        }

        ItemStack selectedBook = (selectedSlot >= 0 && selectedSlot < 8) ? handler.getStackInSlot(selectedSlot) : ItemStack.EMPTY;

        if (!selectedBook.isEmpty() && selectedBook.getItem() instanceof BaseSpellBookItem<?> spellBook) {
            ItemStack copy = selectedBook.copy();
            copy.getOrCreateTag().putBoolean(NBT_OPEN_KEY, tag.getBoolean(NBT_OPEN_KEY));
            tag.put(NBT_ACTIVE_SPELL, spellBook.serializeBookData(copy));
        } else {
            tag.remove(NBT_ACTIVE_SPELL);
        }
    }

    public ItemStack getActiveSpellBook(ItemStack omnitomeStack) {
        CompoundTag tag = omnitomeStack.getOrCreateTag();
        if (tag.contains(NBT_ACTIVE_SPELL)) {
            ItemStack active = BaseSpellBookItem.deserializeBookData(tag.getCompound(NBT_ACTIVE_SPELL));
            if (!active.isEmpty()) {
                active.getOrCreateTag().putBoolean(NBT_OPEN_KEY, tag.getBoolean(NBT_OPEN_KEY));
            }
            return active;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack omnitome = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                openGui(serverPlayer, hand);
            }
            return InteractionResultHolder.sidedSuccess(omnitome, level.isClientSide());
        }

        ItemStack activeSpell = getActiveSpellBook(omnitome);

        if (!activeSpell.isEmpty() && activeSpell.getItem() instanceof BaseSpellBookItem<?> spellBook) {
            if (spellBook.canStartCasting(level, player)) {
                player.startUsingItem(hand);
                if (!level.isClientSide) {

                    omnitome.getOrCreateTag().put(NBT_ACTIVE_SPELL, spellBook.serializeBookData(activeSpell));
                }
                return InteractionResultHolder.consume(omnitome);
            }
            return InteractionResultHolder.fail(omnitome);
        }
        return InteractionResultHolder.pass(omnitome);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        ItemStack activeSpell = getActiveSpellBook(stack);
        if (!activeSpell.isEmpty() && activeSpell.getItem() instanceof BaseSpellBookItem<?> spellBook) {
            spellBook.onUseTick(level, livingEntity, activeSpell, remainingUseDuration);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        ItemStack activeSpell = getActiveSpellBook(stack);
        if (!activeSpell.isEmpty() && activeSpell.getItem() instanceof BaseSpellBookItem<?> spellBook) {
            if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
                GeoItem.getOrAssignId(activeSpell, serverLevel);
                stack.getOrCreateTag().put(NBT_ACTIVE_SPELL, spellBook.serializeBookData(activeSpell));
            }

            spellBook.releaseUsing(activeSpell, level, entity, timeLeft, entity.getUsedItemHand());
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelected) {
        if (entity instanceof Player player) {
            boolean isHeld = player.getItemInHand(InteractionHand.MAIN_HAND) == itemStack || player.getItemInHand(InteractionHand.OFF_HAND) == itemStack;
            CompoundTag tag = itemStack.getOrCreateTag();
            if (tag.getBoolean(NBT_OPEN_KEY) != isHeld) {
                tag.putBoolean(NBT_OPEN_KEY, isHeld);
                if (tag.contains(NBT_ACTIVE_SPELL)) {
                    CompoundTag activeTag = tag.getCompound(NBT_ACTIVE_SPELL);
                    activeTag.getCompound("tag").putBoolean(NBT_OPEN_KEY, isHeld);
                }
            }
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        if (item.hasTag()) {
            item.getTag().putBoolean(NBT_OPEN_KEY, false);
        }
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public boolean isPerspectiveAware() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
            ItemDisplayContext context = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

            if (context != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND &&
                    context != ItemDisplayContext.FIRST_PERSON_LEFT_HAND &&
                    context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND &&
                    context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                state.getController().setAnimationSpeed(1.0f);
                return state.setAndContinue(CLOSED_ANIM);
            }

            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            ItemStack activeSpell = getActiveSpellBook(stack);

            Entity entity = state.getData(DataTickets.ENTITY);
            if (entity == null) {
                entity = net.minecraft.client.Minecraft.getInstance().player;
            }


            if (entity instanceof LivingEntity living && living.isUsingItem()) {
                Item usedItem = living.getUseItem().getItem();
                if (usedItem instanceof BaseStaffItem || usedItem instanceof BaseSpellBookItem<?> || usedItem instanceof OmnitomeItem) {
                    if (!activeSpell.isEmpty() && activeSpell.getItem() instanceof BaseSpellBookItem<?> spellBook) {
                        if (spellBook.getStaffCastType() == StaffCastType.SEQUENTIAL) {
                            int cycleLength = Math.max(1, spellBook.getMinChargeTime()) + Math.max(0, spellBook.getCooldown());
                            state.getController().setAnimationSpeed(10.0f / (float) cycleLength);
                            return state.setAndContinue(ATTACK_SEQ_ANIM);
                        }
                    }
                }
            }

            boolean isAttacking = state.getController().getCurrentAnimation() != null &&
                    state.getController().getCurrentAnimation().animation().name().equals("attack");

            if (isAttacking) {
                float speed = 1.0f;
                if (!activeSpell.isEmpty() && activeSpell.getItem() instanceof BaseSpellBookItem<?> spellBook) {
                    if (spellBook.getStaffCastType() == StaffCastType.THROW || spellBook.getStaffCastType() == StaffCastType.RISE) {
                        speed = 10.0f / (float)Math.max(1, spellBook.getMinChargeTime());
                    }
                }
                state.getController().setAnimationSpeed(Math.min(speed, 8.0f));

                if (!state.getController().hasAnimationFinished()) {
                    return software.bernie.geckolib.core.object.PlayState.CONTINUE;
                }
            } else {
                state.getController().setAnimationSpeed(1.0f);
            }

            boolean isOpen = stack != null && stack.hasTag() && stack.getTag().getBoolean(NBT_OPEN_KEY);
            return state.setAndContinue(isOpen ? OPEN_ANIM : CLOSED_ANIM);

        }).triggerableAnim("attack", ATTACK_ANIM).triggerableAnim("emerge", EMERGE_ANIM));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }

    @Override
    public int getUseDuration(ItemStack stack) { return 72000; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new OmnitomeRenderer();
                return this.renderer;
            }
        });
    }
}