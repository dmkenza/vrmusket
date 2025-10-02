package com.vrmusket.mixin;


import ewewukek.musketmod.BulletEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHurtingProjectile.class)
public abstract class AbstractHurtingProjectileMixin {

    /**
     *  Removes the bullet when the player attacks a stopped bullet.
     */

    @Inject(
            method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof BulletEntity bullet) {
            bullet.discard();
        }
    }
}