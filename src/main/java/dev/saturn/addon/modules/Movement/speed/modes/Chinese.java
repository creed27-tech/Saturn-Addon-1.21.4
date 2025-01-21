package dev.saturn.addon.modules.Movement.speed.modes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.systems.modules.Modules;
import dev.saturn.addon.modules.Movement.speed.SpeedPlus;
import dev.saturn.addon.modules.Movement.speed.SpeedMode;
import dev.saturn.addon.modules.Movement.speed.SpeedModes;
import dev.saturn.addon.utils.envy.algorithms.extra.MovementUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;

public class Chinese extends SpeedMode {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Chinese() {
        super(SpeedModes.Chinese);
    }

    @EventHandler
    public boolean onTick() {
        PlayerEntity player = mc.player;
        if (player != null && PlayerUtils.isMoving()) {
            if (player.isSprinting()) {
                player.setSprinting(false); // Disable sprinting if needed
            }

            if (player.getVelocity().length() > 0.2D) {
                player.setVelocity(player.getVelocity().multiply(0.1F)); // Adjust velocity if speed exceeds threshold
            }

            if (player.getVelocity().length() == 0.4D) {
                MovementUtils.strafe(7.0F); // Apply custom strafe movement
            }
        }

        return false;
        }
    }