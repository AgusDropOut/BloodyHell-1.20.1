package net.agusdropout.bloodyhell.item.client;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;

public class ClientItemHooks {

    // Safe way to check camera without importing Minecraft in Item classes
    public static boolean isFirstPerson() {
        return Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    // Safe way to trigger GeckoLib animations
    public static void triggerGeckoAnim(Player player, ItemStack stack, String controller, String animName) {
        if (stack.getItem() instanceof GeoItem geoItem) {
            geoItem.triggerAnim(player, GeoItem.getId(stack), controller, animName);
        }
    }

    public static void stopGeckoAnim(Player player, ItemStack stack, String controller, String animName) {
        if (stack.getItem() instanceof GeoItem geoItem) {
            geoItem.stopTriggeredAnim(player, GeoItem.getId(stack), controller, animName );
        }
    }

    public static LocalPlayer getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    // Safe way to play PlayerAnimator animations
    public static void playPlayerAnimatorAnim(Player player, String animName) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

        var animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                .getPlayerAssociatedData(clientPlayer)
                .get(new ResourceLocation(BloodyHell.MODID, "animation"));

        if (animationLayer != null) {
            var anim = PlayerAnimationRegistry.getAnimation(new ResourceLocation(BloodyHell.MODID, animName));
            if (anim != null) {
                // Prevent restarting the animation if it is already playing
                if (animationLayer.getAnimation() instanceof KeyframeAnimationPlayer current && current.getData().equals(anim)) {
                    return;
                }
                animationLayer.setAnimation(new KeyframeAnimationPlayer(anim));
            }
        }
    }
}