package com.vrmusket.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ewewukek.musketmod.BulletEntity;
import ewewukek.musketmod.BulletRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.vrmusket.VRMusket.isDebugEnabled;

@Mixin(BulletRenderer.class)
public class BulletRendererMixin {

    // Bullet size constants
    @Unique
    private static final float SINGLE_BULLET_SCALE_X = 0.2F;
    @Unique
    private static final float SINGLE_BULLET_SCALE_Y = 0.2F;
    @Unique
    private static final float SINGLE_BULLET_SCALE_Z = 0.2F;

    @Unique
    private static final float MULTI_BULLET_SCALE_X = 0.15F;
    @Unique
    private static final float MULTI_BULLET_SCALE_Y = 0.1F;
    @Unique
    private static final float MULTI_BULLET_SCALE_Z = 0.1F;

    @ModifyArg(
            method = "render(Lewewukek/musketmod/BulletEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0),
            index = 0
    )
    private float modifyScaleX1(float original) {
        return SINGLE_BULLET_SCALE_X;
    }

    @ModifyArg(
            method = "render(Lewewukek/musketmod/BulletEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0),
            index = 1
    )
    private float modifyScaleY1(float original) {
        return SINGLE_BULLET_SCALE_Y;
    }

    @ModifyArg(
            method = "render(Lewewukek/musketmod/BulletEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0),
            index = 2
    )
    private float modifyScaleZ1(float original) {
        return SINGLE_BULLET_SCALE_Z;
    }

    @ModifyArg(
            method = "render(Lewewukek/musketmod/BulletEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 1),
            index = 0
    )
    private float modifyScaleX2(float original) {
        return MULTI_BULLET_SCALE_X;
    }

    @ModifyArg(
            method = "render(Lewewukek/musketmod/BulletEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 1),
            index = 1
    )
    private float modifyScaleY2(float original) {
        return MULTI_BULLET_SCALE_Y;
    }

    @ModifyArg(
            method = "render(Lewewukek/musketmod/BulletEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 1),
            index = 2
    )
    private float modifyScaleZ2(float original) {
        return MULTI_BULLET_SCALE_Z;
    }


    /**
     * Renders a debug cube around the bullet for trajectory visualization.
     */
    @Inject(
            method = "render(Lewewukek/musketmod/BulletEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL")
    )
    private void renderDebugCube(BulletEntity bullet, float yaw, float dt, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        // Check if debug mode is enabled
        if (!isDebugEnabled) {
            return;
        }

        poseStack.pushPose();

        // Cube size and position
        float cubeSize = 0.05f; // Small cube

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer lineBuilder = bufferSource.getBuffer(RenderType.lines());

        // Draw 12 edges of the cube
        // Bottom face
        drawLine(lineBuilder, pose, -cubeSize, -cubeSize, -cubeSize,  cubeSize, -cubeSize, -cubeSize);
        drawLine(lineBuilder, pose,  cubeSize, -cubeSize, -cubeSize,  cubeSize, -cubeSize,  cubeSize);
        drawLine(lineBuilder, pose,  cubeSize, -cubeSize,  cubeSize, -cubeSize, -cubeSize,  cubeSize);
        drawLine(lineBuilder, pose, -cubeSize, -cubeSize,  cubeSize, -cubeSize, -cubeSize, -cubeSize);

        // Top face
        drawLine(lineBuilder, pose, -cubeSize,  cubeSize, -cubeSize,  cubeSize,  cubeSize, -cubeSize);
        drawLine(lineBuilder, pose,  cubeSize,  cubeSize, -cubeSize,  cubeSize,  cubeSize,  cubeSize);
        drawLine(lineBuilder, pose,  cubeSize,  cubeSize,  cubeSize, -cubeSize,  cubeSize,  cubeSize);
        drawLine(lineBuilder, pose, -cubeSize,  cubeSize,  cubeSize, -cubeSize,  cubeSize, -cubeSize);

        // Vertical edges
        drawLine(lineBuilder, pose, -cubeSize, -cubeSize, -cubeSize, -cubeSize,  cubeSize, -cubeSize);
        drawLine(lineBuilder, pose,  cubeSize, -cubeSize, -cubeSize,  cubeSize,  cubeSize, -cubeSize);
        drawLine(lineBuilder, pose,  cubeSize, -cubeSize,  cubeSize,  cubeSize,  cubeSize,  cubeSize);
        drawLine(lineBuilder, pose, -cubeSize, -cubeSize,  cubeSize, -cubeSize,  cubeSize,  cubeSize);

        poseStack.popPose();
    }

    /**
     * Helper method for drawing a single line.
     */
    @Unique
    private void drawLine(VertexConsumer builder, PoseStack.Pose pose,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2) {
        // Red color
        int r = 255, g = 0, b = 0, a = 255;
        // Full brightness
        int light = 0xF000F0;
        builder.vertex(pose.pose(), x1, y1, z1).color(r, g, b, a)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), x2, y2, z2).color(r, g, b, a)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
    }

}
