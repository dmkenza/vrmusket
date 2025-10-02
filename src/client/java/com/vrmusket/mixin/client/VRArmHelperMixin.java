package com.vrmusket.mixin.client;


import com.vrmusket.VRMusketClient;
import ewewukek.musketmod.GunItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.vivecraft.client_vr.render.helpers.VRArmHelper;

@Mixin(VRArmHelper.class)
public class VRArmHelperMixin {

    /**
     * Block the default swing/attack animation for a Musket
     */
    @Redirect(
            method = "renderVRHand_Main(Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getAttackAnim(F)F"
            )
    )
    private static float redirectGetAttackAnim(LocalPlayer player, float partialTick) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof GunItem) {
            VRMusketClient.globalArmSwingProgress = player.getAttackAnim(partialTick);
            return 0f;
        }

        return player.getAttackAnim(partialTick);
    }
}
