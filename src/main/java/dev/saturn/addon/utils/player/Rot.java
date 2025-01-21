package dev.saturn.addon.utils.player;

public record Rot(double yaw, double pitch) {
    public Rot(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double yaw() {
        return this.yaw;
    }

    public double pitch() {
        return this.pitch;
    }
}