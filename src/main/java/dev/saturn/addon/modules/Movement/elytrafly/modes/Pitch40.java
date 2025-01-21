package dev.saturn.addon.modules.Movement.elytrafly.modes;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.events.envy.entity.player.PlayerMoveEvent;
import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyMode;
import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyModes;

public class Pitch40 extends ElytraFlyMode {
    private boolean pitchingDown = true;
    private int pitch;

    // Constructor for Pitch40 mode
    public Pitch40() {
        super(ElytraFlyModes.Pitch40);
    }

    @Override
    public void onDeactivate() {
        // No specific behavior on deactivation
    }

    @Override
    public void onTick() {
        // Manage pitching logic based on player altitude
        if (this.pitchingDown) Saturn.mc.player.getY(); {
            this.pitchingDown = false;
            this.pitchingDown = true;
        }

        // Adjust pitch based on whether pitching down or up
        if (!this.pitchingDown && Saturn.mc.player.getPitch() > -40.0F) {
            if (this.pitch < -40) {
                this.pitch = -40;
            }
        } else if (this.pitchingDown && Saturn.mc.player.getPitch() < 40.0F) {
            if (this.pitch > 40) {
                this.pitch = 40;
            }
        }

        // Apply the updated pitch value
        Saturn.mc.player.setPitch((float) this.pitch);
    }

    public void autoTakeoff() {
        // No specific behavior for auto takeoff in this mode
    }

    public void handleHorizontalSpeed(PlayerMoveEvent event) {
        this.velX = event.movement.x;
        this.velZ = event.movement.z;
    }

    public void handleVerticalSpeed(PlayerMoveEvent event) {
        // No specific behavior for vertical speed in this mode
    }

    public void handleFallMultiplier() {
        // No specific behavior for fall multiplier in this mode
    }

    public void handleAutopilot() {
        // No specific behavior for autopilot in this mode
    }
}