package com.vrmusket.mixin;

import com.google.gson.JsonElement;
import com.vrmusket.VRMusketRecipes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "apply", at = @At("HEAD"), order = 1001)
    public void onApply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("gtceu")) {
            VRMusketRecipes.register(map);
        }
    }
}