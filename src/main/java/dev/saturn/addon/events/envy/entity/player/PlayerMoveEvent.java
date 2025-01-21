package dev.saturn.addon.events.envy.entity.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PlayerMoveEvent {

    private static final PlayerMoveEvent INSTANCE = new PlayerMoveEvent();
    public PlayerMoveC2SPacket type;
    public Vec3d movement;
    private double x;
    private double y;
    private double z;

    // Singleton pattern to reuse the instance
    public static PlayerMoveEvent get(PlayerMoveC2SPacket type, Vec3d movement) {
        INSTANCE.type = type;
        INSTANCE.movement = movement;
        return INSTANCE;
    }

    // Getter and Setter methods for x, y, z coordinates
    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}