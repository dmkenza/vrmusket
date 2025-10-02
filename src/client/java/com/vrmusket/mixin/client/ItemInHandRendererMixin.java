
package com.vrmusket.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vrmusket.VRMusketClient;
import ewewukek.musketmod.GunItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.vrmusket.VivecraftCompat.isVivecraftRunning;

@Mixin(value = ItemInHandRenderer.class, priority = 998)
public abstract class ItemInHandRendererMixin {

    /**
     * Perform a forward strike with the musket
     */

    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void afterRenderItem(
            AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci) {

        if (isVivecraftRunning()) {
            if(interactionHand != InteractionHand.MAIN_HAND || !(itemStack.getItem() instanceof GunItem)){
                return;
            }

            float thrustAmount = Mth.sin(VRMusketClient.globalArmSwingProgress * Mth.PI) * 0.3F;
            poseStack.translate(0.0, 0.0, -thrustAmount);
        }
    }
}