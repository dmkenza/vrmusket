
package com.vrmusket.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.vrmusket.VRMusketClient;
import ewewukek.musketmod.GunItem;
import net.minecraft.Util;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.render.VivecraftItemRendering;

import static com.vrmusket.VRMusketClient.globalArmSwingProgress;
import static ewewukek.musketmod.Items.MUSKET_WITH_BAYONET;

@Mixin(VivecraftItemRendering.class)
public class VivecraftItemRenderingMixin {

    private static final ClientDataHolderVR DH = ClientDataHolderVR.getInstance();

    @Inject(
            method = "getTransformType",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injectMusketAsSpear(ItemStack itemStack, AbstractClientPlayer player, ItemRenderer itemRenderer, CallbackInfoReturnable<VivecraftItemRendering.VivecraftItemTransformType> cir) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof GunItem) {
            cir.setReturnValue(VivecraftItemRendering.VivecraftItemTransformType.Spear);
        }
    }


    /**
     * Buttstock swing animation and correct musket positioning
     */

    @Inject( method = "applyFirstPersonItemTransforms", at = @At("HEAD"), cancellable = true )
    private static void applyMusketTransforms(
            PoseStack poseStack, VivecraftItemRendering.VivecraftItemTransformType itemTransformType, boolean mainHand,
            AbstractClientPlayer player, float equippedProgress, float partialTick, ItemStack itemStack,
            InteractionHand hand, CallbackInfo ci) {

        if (itemStack.isEmpty()) {
            return;
        }
        boolean isMusket = itemStack.getItem() instanceof GunItem;

        if (itemTransformType == VivecraftItemRendering.VivecraftItemTransformType.Spear && isMusket) {

            float gunAngle = DH.vr.getGunAngle();
            float scale = 0.75F;;
            float translateX = -0.05F;
            float translateY = 0.005F;
            float translateZ = 0.0F;

            Quaternionf rotation = new Quaternionf();
            Quaternionf preRotation = new Quaternionf();
            rotation.mul(Axis.XP.rotationDegrees(-110.0F + gunAngle));

            if (globalArmSwingProgress > 0f) {
                if (itemStack.getItem() != MUSKET_WITH_BAYONET) {
                    rotation.mul(Axis.XP.rotationDegrees(145.0F));
                    rotation.mul(Axis.YP.rotationDegrees(180));
                }
                translateZ -= 0.2F * Mth.sin(VRMusketClient.globalArmSwingProgress * Mth.PI);
            }


            rotation.mul(Axis.XP.rotationDegrees(45.0F + gunAngle));
            translateY += -0.24F + 0.06F * gunAngle / 40.0F;
            translateZ += 0.75F;

            float progress = 0.0F;
            boolean charging = false;
            int riptideLevel = 0;

            if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand) {
                charging = true;
                riptideLevel = EnchantmentHelper.getRiptide(itemStack);

                if (riptideLevel <= 0 || player.isInWaterOrRain()) {
                    progress = itemStack.getUseDuration() - (player.getUseItemRemainingTicks() - partialTick + 1.0F);
                    rotation.mul(Axis.XP.rotationDegrees(90.0F));
                    translateY += 0.05F;

                    if (progress > TridentItem.THROW_THRESHOLD_TIME) {
                        float rotationProgress = progress - TridentItem.THROW_THRESHOLD_TIME;
                        progress = TridentItem.THROW_THRESHOLD_TIME;

                        if (riptideLevel > 0 && player.isInWaterOrRain()) {
                            poseStack.mulPose(Axis.ZP.rotationDegrees(-rotationProgress * 10.0F * riptideLevel));
                        }
                        if (DH.frameIndex % 4L == 0L) {
                            DH.vr.triggerHapticPulse(mainHand ? 0 : 1, 200);
                        }

                        translateX += 0.003F * (float) Math.sin(Util.getMillis());
                    }
                }
            }

            if (player.isAutoSpinAttack()) {
                riptideLevel = 5;
                translateZ -= 0.15F;
                poseStack.mulPose(Axis.ZP.rotationDegrees(
                        (-DH.tickCounter * 10 * riptideLevel) % 360 - partialTick * 10.0F * riptideLevel));
                charging = true;
            }

            if (!charging) {
                translateY += 0.2F * gunAngle / 40.0F;
                rotation.mul(Axis.XP.rotationDegrees(gunAngle));
            }

            rotation.mul(Axis.XP.rotationDegrees(-65.0F));
            translateZ += -0.75F + progress / 10.0F * 0.25F;

            poseStack.mulPose(preRotation);
            poseStack.translate(translateX, translateY, translateZ);
            poseStack.mulPose(rotation);
            poseStack.scale(scale, scale, scale);

            ci.cancel();
        }
    }

}