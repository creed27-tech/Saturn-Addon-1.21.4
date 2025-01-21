package dev.saturn.addon.utils.envy;

import meteordevelopment.meteorclient.MeteorClient;

public class EnvyUtils {

    // Teleports the player to the current position (or resets position)
    public static void pos() {
        // Using LivingEntity's teleport method with the required boolean flag
        MeteorClient.mc.player.teleport(MeteorClient.mc.player.getX(), MeteorClient.mc.player.getY(), MeteorClient.mc.player.getZ(), true);
    }

    // Applies player velocity (based on your custom setup)
    public static void velocity() {
        MeteorClient.mc.player.setVelocity(0, 0, 0); // Reset velocity or apply custom changes
    }

    // Makes the player jump
    public static void jump() {
        MeteorClient.mc.player.jump();
    }

    // Custom fall method to manipulate fall speed or gravity
    public static void fall() {
        MeteorClient.mc.player.setVelocity(MeteorClient.mc.player.getVelocity().x, -0.5D, MeteorClient.mc.player.getVelocity().z);
    }

    // Makes the player sprint
    public static void sprint() {
        MeteorClient.mc.player.setSprinting(true);
    }
}