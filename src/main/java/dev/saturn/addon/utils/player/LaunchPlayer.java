package dev.saturn.addon.utils.player;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.MinecraftClient;

public class LaunchPlayer {
    public static void MatrixElytra(double BoostValue) {
        MinecraftClient mc = MeteorClient.mc;  // Accessing MinecraftClient instance

        // Getting player's current rotation values
        float rotationYaw = mc.player.getYaw();
        float rotationPitch = mc.player.getPitch();

        // Convert yaw and pitch to radians
        double yawRadians = Math.toRadians(rotationYaw);
        double pitchRadians = Math.toRadians(rotationPitch);

        // Calculate movement vector based on yaw and pitch
        double x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        double y = -Math.sin(pitchRadians);
        double z = Math.cos(yawRadians) * Math.cos(pitchRadians);

        // Normalize the vector and apply BoostValue
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;

        x *= BoostValue;
        y *= BoostValue;
        z *= BoostValue;

        // Apply the boost to the player movement
        mc.player.setVelocity(x, y, z);
    }
}
