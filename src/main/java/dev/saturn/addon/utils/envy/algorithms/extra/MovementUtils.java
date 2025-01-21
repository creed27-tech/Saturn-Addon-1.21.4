package dev.saturn.addon.utils.envy.algorithms.extra;

import dev.saturn.addon.Saturn;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class MovementUtils {
    public static double getSpeed() {
        return Math.sqrt(Saturn.mc.player.getVelocity().x * Saturn.mc.player.getVelocity().x + Saturn.mc.player.getVelocity().z * Saturn.mc.player.getVelocity().z);
    }

    public static void strafe(float speed) {
        double yaw = direction();
        double sin = -Math.sin(yaw) * (double)speed;
        double cos = Math.cos(yaw) * (double)speed;
        Saturn.mc.player.setVelocity(cos, 0.0D, sin);
    }

    public static void VulcanMoveStrafe(float speed) {
        if (isMoving()) {
            double direction = getDirection();
            double x = -Math.sin(direction) * (double)speed;
            double z = Math.cos(direction) * (double)speed;
            Vec3d motion = new Vec3d(x, Saturn.mc.player.getVelocity().y, z);
            Saturn.mc.player.setVelocity(motion);
        }
    }

    public static boolean isMoving() {
        return Saturn.mc.player != null && test();
    }

    public static boolean test() {
        return Saturn.mc.options.forwardKey.isPressed() || Saturn.mc.options.backKey.isPressed() || Saturn.mc.options.leftKey.isPressed() || Saturn.mc.options.rightKey.isPressed();
    }

    public static void Vulcanstrafe() {
        strafe(getSpeed());
    }

    public static void strafe(double speed) {
        double yaw = direction();
        double sin = -Math.sin(yaw) * speed;
        double cos = Math.cos(yaw) * speed;
        Saturn.mc.player.setVelocity(cos, 0.0D, sin);
    }

    public static double direction() {
        float yaw = Saturn.mc.player.getYaw();
        if (Saturn.mc.player.getPitch() < 0.0F) {
            yaw += 180.0F;
        }

        float forward = 1.0F;
        if (Saturn.mc.player.getPitch() < 0.0F) {
            forward = (float)((double)forward - 0.5D);
        } else if (Saturn.mc.player.getPitch() > 0.0F) {
            forward = (float)((double)forward + 0.5D);
        }

        if (Saturn.mc.player.getYaw() > 0.0F) {
            yaw -= 90.0F * forward;
        }

        if (Saturn.mc.player.getYaw() < 0.0F) {
            yaw += 90.0F * forward;
        }

        return Math.toRadians((double)yaw);
    }

    private static double getDirection() {
        double rotationYaw = (double)MinecraftClient.getInstance().player.getYaw();
        if (MinecraftClient.getInstance().player.getPitch() < 0.0F) {
            rotationYaw += 180.0D;
        }

        double forward = 1.0D;
        if (MinecraftClient.getInstance().player.getPitch() < 0.0F) {
            forward = -0.5D;
        } else if (MinecraftClient.getInstance().player.getPitch() > 0.0F) {
            forward = 0.5D;
        }

        if (MinecraftClient.getInstance().player.getYaw() > 0.0F) {
            rotationYaw -= 90.0D * forward;
        }

        if (MinecraftClient.getInstance().player.getYaw() < 0.0F) {
            rotationYaw += 90.0D * forward;
        }

        return Math.toRadians(rotationYaw);
    }
}