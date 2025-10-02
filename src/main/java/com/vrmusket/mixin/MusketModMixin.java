package com.vrmusket.mixin;


import ewewukek.musketmod.MusketMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = MusketMod.class)
public class MusketModMixin {

//    @ModifyConstant(method = "onInitialize", constant = @Constant(floatValue = 0.5F), remap = false)
//    private float replaceBulletSize(float original) {
//        return 0.1F;
//    }
}