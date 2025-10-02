package com.vrmusket.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ewewukek.musketmod.Config;

@Mixin(value = Config.class)
public class ConfigMixin {

    @Inject(method = "load", at = @At("RETURN"), remap = false)
    private static void afterLoad(CallbackInfo ci) {
        // Reduce the standard deviation of bullets (makes bullets more accurate)
//        Config.musketBulletStdDev *= 0.0f;
//        Config.blunderbussBulletStdDev *= 1f;
//        Config.pistolBulletStdDev *= 0.00f;
//        Config.dispenserBulletStdDev *= 0.00f;

        // Increase the reload time (loadingStageDuration)
        Config.loadingStageDuration *= 1.2f;

       // Increase the damage
        Config.musketDamage *= 1.2f;
        Config.blunderbussDamage *= 1.2f;
        Config.pistolDamage *= 1.2f;
        Config.dispenserDamage *= 1.2f;

//        Config.musketBulletSpeed = 1f;
//        Config.blunderbussBulletSpeed =1f;
//        Config.pistolBulletSpeed = 1f;;
//        Config.dispenserBulletSpeed = 1f;;
    }
}
