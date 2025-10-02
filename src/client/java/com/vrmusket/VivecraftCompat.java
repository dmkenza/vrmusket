package com.vrmusket;

import ewewukek.musketmod.PistolItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.VRData.VRDevicePose;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.vivecraft.client_vr.provider.ControllerType;
import org.vivecraft.client_vr.provider.MCVR;

import static com.mojang.text2speech.Narrator.LOGGER;
import org.vivecraft.client_vr.VRState;

public class VivecraftCompat {

    public static boolean isVivecraftLoaded() {
        return FabricLoader.getInstance().isModLoaded("vivecraft");
    }

    public static boolean isVivecraftRunning() {
        if (!isVivecraftLoaded()) {
            return false;
        }
        MCVR vr = MCVR.get();
        return vr != null && vr.initialized && VRState.VR_RUNNING;
    }

    /**
     * Plays a recoil effect on the VR controller when shooting.
     */
    public static void playGunEffect(Player player, InteractionHand hand, ItemStack weapon) {
        MCVR vr = MCVR.get();
        if (vr == null || !vr.initialized) {
            return;
        }

        ControllerType controllerType = getControllerTypeForHand(player, hand);
        if (controllerType == null) {
            return;
        }

        float frequency = 160.0f;
        float amplitude = 1.0f;
        float durationSeconds;

        if (weapon.getItem() instanceof PistolItem) {
            durationSeconds = 0.25f;
        } else {
            durationSeconds = 0.5f;
        }

        vr.triggerHapticPulse(controllerType, durationSeconds, frequency, amplitude);
    }


    private static ControllerType getControllerTypeForHand(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            return player.getMainArm() == HumanoidArm.RIGHT ? ControllerType.RIGHT : ControllerType.LEFT;
        } else if (hand == InteractionHand.OFF_HAND) {
            return player.getMainArm() == HumanoidArm.RIGHT ? ControllerType.LEFT : ControllerType.RIGHT;
        }
        return null;
    }


    public static Vec3 getFireOrigin(LivingEntity entity, InteractionHand hand) {
        if (shouldUseVivecraft(entity)) {
            try {
                VRDevicePose controllerPose = getControllerPose(hand);
                if (controllerPose != null) {
                    Vec3 controllerPos = controllerPose.getPosition();
                    Matrix4f controllerMatrix = controllerPose.getMatrix();

                    Vector3f muzzleOffsetLocal = new Vector3f(
                            -0.03f,
                            0.03f,
                            -1.2f
                    );

                    Vector3f worldMuzzleOffset = new Vector3f(muzzleOffsetLocal);
                    controllerMatrix.transformDirection(worldMuzzleOffset);

                    return controllerPos.add(worldMuzzleOffset.x, worldMuzzleOffset.y, worldMuzzleOffset.z);
                }
            } catch (Throwable e) {
                LOGGER.error("VRMusket: Failed to get Vivecraft muzzle position. Falling back to default.", e);
            }
        }
        return getDefaultOrigin(entity);
    }

    public static Vec3 getFireDirection(LivingEntity entity, InteractionHand hand) {
        if (shouldUseVivecraft(entity)) {
            try {
                VRDevicePose controllerPose = getControllerPose(hand);
                if (controllerPose != null) {
                    Matrix4f controllerMatrix = controllerPose.getMatrix();

                    Vector3f muzzleDirLocal = new Vector3f(
                            0.008f,
                            -0.025f,
                            -1.0f
                    );

                    controllerMatrix.transformDirection(muzzleDirLocal);
                    muzzleDirLocal.normalize();

                    return new Vec3(muzzleDirLocal.x, muzzleDirLocal.y, muzzleDirLocal.z);
                }
            } catch (Throwable e) {
                LOGGER.error("VRMusket: Failed to get Vivecraft fire direction. Falling back to default.", e);
            }
        }
        return getDefaultDirection(entity);
    }

    private static boolean shouldUseVivecraft(LivingEntity entity) {
        return isVivecraftRunning()
                && entity instanceof Player
                && entity.equals(Minecraft.getInstance().player);
    }

    private static VRData.VRDevicePose getControllerPose(InteractionHand hand) {
        ClientDataHolderVR dataHolder = ClientDataHolderVR.getInstance();
        if (dataHolder == null
                || dataHolder.vrPlayer == null
                || dataHolder.vrPlayer.getVRDataWorld() == null
                || !dataHolder.vrSettings.vrEnabled) {
            return null;
        }
        VRData vrData = dataHolder.vrPlayer.getVRDataWorld();
        int controllerIndex = (hand == InteractionHand.MAIN_HAND) ? 0 : 1;
        return vrData.getController(controllerIndex);
    }

    private static Vec3 getDefaultOrigin(LivingEntity entity) {
        return new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
    }

    private static Vec3 getDefaultDirection(LivingEntity entity) {
        return entity.getViewVector(1.0f);
    }
}
