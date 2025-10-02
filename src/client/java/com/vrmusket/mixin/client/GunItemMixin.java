package com.vrmusket.mixin.client;

import com.vrmusket.VivecraftCompat;
import ewewukek.musketmod.GunItem;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.vrmusket.VRMusket.isDebugEnabled;
import static com.vrmusket.VivecraftCompat.playGunEffect;

@Mixin(GunItem.class)
public abstract class GunItemMixin {

    private Vec3 capturedDirection;

    /**
     * Support BHaptics
     */

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void triggerHapticsBeforeSound(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (level.isClientSide && VivecraftCompat.isVivecraftRunning()) {
            playGunEffect(player, hand, player.getItemInHand(hand));
        }
    }


    /**
     * Disable smoke for debugging/
     */

    @Inject(
            method = "fire(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lewewukek/musketmod/MusketMod;sendSmokeEffect(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)V"
            ),
            cancellable = true
    )
    private void disableSmoke(
            LivingEntity entity,
            ItemStack stack,
            Vec3 direction,
            Vec3 smokeOffset,
            CallbackInfo ci
    ) {
        if (isDebugEnabled) {
            ci.cancel();
        }
    }


    /**
     * Use the musket's orientation for the firing vector
     */

    @Redirect(
            method = "fire(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/world/phys/Vec3"
            )
    )
    private Vec3 redirectFireOrigin(double x, double y, double z, LivingEntity entity, ItemStack stack, Vec3 direction, Vec3 smokeOffset) {
        if (y == entity.getEyeY()) {
            InteractionHand hand = entity.getUsedItemHand();
            return VivecraftCompat.getFireOrigin(entity, hand);
        }
        return new Vec3(x, y, z);
    }

    /**
     * Override the aiming direction for VR (so that the musket's orientation is used)
     */

    @ModifyVariable(
            method = "fire(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private Vec3 modifyFireDirection(Vec3 originalDirection, LivingEntity entity) {
        if(!VivecraftCompat.isVivecraftRunning()){
            return originalDirection;
        }

        InteractionHand hand = entity.getUsedItemHand();
        capturedDirection = VivecraftCompat.getFireDirection(entity, hand);
        return capturedDirection;
    }

    /**
     * Smoke offset for VR
     */

    @ModifyVariable(
            method = "fire(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At("HEAD"),
            argsOnly = true,
            index = 4
    )
    private Vec3 replaceSmokeOffset(Vec3 original) {
        if(VivecraftCompat.isVivecraftRunning()){
            return capturedDirection.normalize().scale(-1.4);
        }else {
            return original;
        }
    }

}
